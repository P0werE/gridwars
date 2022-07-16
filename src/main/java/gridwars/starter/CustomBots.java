package gridwars.starter;

import cern.ais.gridwars.api.Coordinates;
import cern.ais.gridwars.api.UniverseView;
import cern.ais.gridwars.api.bot.PlayerBot;
import cern.ais.gridwars.api.command.MovementCommand;

import javax.swing.*;
import java.awt.event.MouseMotionAdapter;
import java.util.*;

public abstract class CustomBots implements PlayerBot {
    protected States currentState = States.EXPAND;
    protected States attackState = States.ATTACK;

    private float POPULATION = 0;
    private float lastRound = 0;

    protected final boolean isAttacked(){
        return  POPULATION <= lastRound;
    }


    protected abstract Set<MovementCommand> performStrategy(UniverseView v, Coordinates cell, int maxPop);
    protected abstract void evaluate();


    private final Set<MovementCommand.Direction> spreadDirection(UniverseView v, MovementCommand.Direction dir){
        Set<MovementCommand.Direction> directions = new HashSet<>(Arrays.asList(MovementCommand.Direction.values()));
        switch (dir){
            case DOWN:
                directions.remove(MovementCommand.Direction.UP);
                break;
            case UP:
                directions.remove(MovementCommand.Direction.DOWN);
                break;
            case LEFT:
                directions.remove(MovementCommand.Direction.RIGHT);
                break;
            case RIGHT:
                directions.remove(MovementCommand.Direction.LEFT);
                break;
            default:
        }
        return directions;
    }

    protected boolean isBorder2(UniverseView v, Coordinates c){
        for(MovementCommand.Direction dir : MovementCommand.Direction.values()){
            if(!v.belongsToMe(c.getNeighbour(dir))){
                return true;
            }
        }
        return false;
    }


    protected final boolean isBorder(UniverseView v, Coordinates c, int range) {
        int count = 0;
        for(int i = -range; i <= range; i++) {
            for(int j = -range; j <= range; j++) {
                count += v.isEmpty(v.getCoordinates(c.getX() + i, c.getY() + j)) ? 1 : 0;
            }
        }

        return count / range >= 2;
    }

    protected final MovementCommand.Direction enemySpotted(UniverseView v, Coordinates c, int range) {
        Vector a = new Vector(c.getX(), c.getY());
        double dist = -1;
        range = range % 2 == 0? 1 :0;
        Vector me = new Vector(c.getX(), c.getY());
        Vector dir = new Vector(0,0);

        for(int i = -range; i <= range; i++ ){
            for(int j = -range; j <= range; j++){
                Coordinates target = v.getCoordinates(c.getX() + i, c.getY() +j);
                if(isEnemy(v, target)){
                    Vector other = new Vector(target.getX(), target.getY());
                    double distToOther = me.distance(other);
                    if(dist < 0 && distToOther < dist){
                        dir = other;
                        dist = distToOther;
                    }
                }
            }
        }

        me.sub(dir);
        return dist < 0 ? null : vectorDir(me.angleFromNormal());
    }


    protected final boolean isEnemy(UniverseView v, Coordinates cell){
        return (!(v.belongsToMe(cell) || v.isEmpty(cell)));
    }

    private double distance(Coordinates curr, Coordinates tar){
        return Math.sqrt(curr.getX()*tar.getX() + curr.getY()*tar.getY());
    }


    protected final double getAverage(UniverseView view){
        return countPopulation(view) / view.getMyCells().size();
    }


    protected final double countPopulation(UniverseView view) {
        double val = .0;
        for(Coordinates cell : view.getMyCells()) {
            val += (double) view.getPopulation(cell);
        }
        return val;
    }

    protected final boolean enemyInDirection(UniverseView uni, Coordinates cell, MovementCommand.Direction dir){
        Coordinates target = cell.getNeighbour(dir);
        return !(uni.isEmpty(target) || uni.belongsToMe(target));
    }


    protected final boolean isReinforced(UniverseView v, Coordinates cell, int reinforcedStrength){
        int count = 0;
        for(MovementCommand.Direction dir: MovementCommand.Direction.values()){
            Coordinates relative = cell.getRelative(1, dir);
            if(v.belongsToMe(relative)) {
                if(v.getPopulation(relative) >= reinforcedStrength) {
                    count++;
                }
            }
        }
        return count >= 3;
    }

    protected final boolean reachedIncubationThreshhold(UniverseView v, Coordinates cell, int threshhold){
        return v.getPopulation(cell) >= threshhold;
    }


    protected MovementCommand.Direction nearestBorder(UniverseView v, Coordinates cell, int maxDir) {
        maxDir += maxDir % 2 == 0?  1: 0;
        double shortestDistance = -1;
        Vector vect = new Vector(cell.getX(),cell.getY());
        Vector dir = new Vector(0,0);
        for(int i = -maxDir; i <= maxDir; i++){
            for(int j = -maxDir; j<= maxDir; j++){
                Coordinates aimedCell = v.getCoordinates(cell.getX() + i, cell.getY() + j);
                Vector temp = new Vector(aimedCell.getX(),aimedCell.getY());
                double distToOther =  vect.distance(temp);
                if(shortestDistance < 0 && i != 0 && j != 0 && distToOther < shortestDistance){
                    dir = temp;
                    shortestDistance = distToOther;
                }
            }
        }
        vect.sub(dir);
        return vectorDir(vect.angleFromNormal());
    }


    private Collection<MovementCommand.Direction> shuffledDirections() {
        List<MovementCommand.Direction> list = new LinkedList<>(Arrays.asList(MovementCommand.Direction.values()));
        List<MovementCommand.Direction> shuffled = new LinkedList<>();
        while(!list.isEmpty()){
            int index = (int) Math.round(Math.random() * (list.size()-1));
            MovementCommand.Direction element = list.get(index);
            shuffled.add(element);
            list.remove(element);
        }


        return shuffled;
    }

    protected Set<MovementCommand> seeding(UniverseView v, Coordinates cell, int minCellSize, int seed, int dist){
        log(v, cell, "seeding");

        Set<MovementCommand> cmds = new HashSet<>();
        int pop  = v.getPopulation(cell);
        pop -= seed;
        if (minCellSize < pop) {
            dist += dist % 2 == 0 ? 1 :0;
            int averageX = 0;
            int averageY = 0;
            int total = 0;
            for (int i = -dist; i <= dist; i++){
                for(int j = - dist; j <= dist; j++){
                    Coordinates relative = v.getCoordinates(cell.getX() + i, cell.getY() + j);
                    if (relative != cell && v.belongsToMe(relative)){
                        int population = v.getPopulation(relative);
                        int distToRelative = (int) Math.round(distance(cell, relative));
                        if (distToRelative > 0){
                            averageX += population*relative.getX() / distToRelative;
                            averageY += population*relative.getY() / distToRelative;
                            total++;
                        }
                    }
                }
            }

            averageX /= dist;
            averageY /= dist;

            int diff = Math.abs(averageX) - Math.abs(averageY);
            diff = Math.abs(diff);
            boolean splitTwo = (0 <= diff && diff < 20);

            MovementCommand.Direction dirX = averageX > 0 ? MovementCommand.Direction.RIGHT: MovementCommand.Direction.LEFT;
            MovementCommand.Direction dirY = averageY > 0 ? MovementCommand.Direction.DOWN : MovementCommand.Direction.UP;

            averageX = Math.abs(averageX);
            averageY = Math.abs(averageY);


            if (total == 0) {
                int chunk = (int) Math.floor(pop / 4f);
                for(MovementCommand.Direction dr : MovementCommand.Direction.values()) {
                    if (pop > 0) {
                        cmds.add(new MovementCommand(cell, dr, chunk));
                        pop -= chunk;
                    }
                }
            } else if (splitTwo){
                log(v, cell, "Split dec");
                if( averageX > 0 ){
                    int xxPop = (int) Math.floor(pop * Math.floor((float)(averageX + averageY) / (float) averageX)) % pop;
                    if(xxPop > 0 ){
                        pop -= xxPop;
                        cmds.add(new MovementCommand(cell, dirX, xxPop));
                    }
                }
                if(averageY > 0) {
                    int yyPop = (int) Math.floor(pop * Math.floor((float)(averageX + averageY) / (float) averageY )) % pop;
                    if (yyPop > 0 ) {
                        cmds.add(new MovementCommand(cell, dirY, yyPop));
                    }
                }
            } else {
                log(v, cell, "UnSplit dec");
                if (pop > 0 ){
                    MovementCommand.Direction settedDir = Math.max(averageX, averageY) == averageX ? dirX : dirY;
                    cmds.add(new MovementCommand(cell, settedDir, pop));
                }
            }
        }
        return cmds;
    }


    protected  Set<MovementCommand> seeding2(UniverseView v, Coordinates cell, int minCellSize, int seed, int dist) {
        Set<MovementCommand> cmds = new HashSet<>();
        int pop = v.getPopulation(cell);
        pop -= seed;
        if(minCellSize <= pop) {
            int total = 0;
            Vector diff = new Vector(0,0);
            for(Coordinates relative : v.getMyCells()) {
                int relativePop = v.getPopulation(relative);
                if(cell != relative && v.belongsToMe(relative)) {
                    Vector me = new Vector(cell.getX(), cell.getY());
                    Vector temp = new Vector(relative.getX(), relative.getY());
                    Double distance = Math.ceil(me.distance(temp));

                    if(dist < 0 || distance <= dist) {
                        temp.mult((double) relativePop);
                        me.sub(temp);
                        total++;
                        diff.add(me);
                    }
                }
            }
            if(total > 0) {
                diff.div((double) total);
                diff.invert();

                double rad = diff.angleFromNormal();
                double puffer = Math.PI / (2 * 8);
                if (rad == 0) {
                    int chunks = (int) Math.floor(pop / 4d);
                    for(MovementCommand.Direction dir : MovementCommand.Direction.values()) {
                        if((pop - chunks) > 0) {
                            cmds.add(new MovementCommand(cell, dir, chunks));
                            pop -= chunks;
                        } else if (pop > 0){
                            cmds.add(new MovementCommand(cell, dir, pop));
                        }
                    }
                } else if ( Math.abs(rad) <= puffer && Math.abs(rad) > 0) {
                    double xx = Math.abs(diff.x);
                    double yy = Math.abs(diff.y);
                    double chunkSize = xx + yy;

                    double chunksX = xx / chunkSize;
                    double chunksY = yy / chunkSize;

                    MovementCommand.Direction dirX = diff.x > 0 ? MovementCommand.Direction.RIGHT:
                            MovementCommand.Direction.LEFT;
                    MovementCommand.Direction dirY = diff.y > 0 ? MovementCommand.Direction.UP:
                            MovementCommand.Direction.DOWN;
                    cmds.add(new MovementCommand(cell, dirX, (int) Math.floor(pop* chunksX)));
                    cmds.add(new MovementCommand(cell, dirY, (int) Math.floor(pop* chunksY)));
                } else {
                   MovementCommand.Direction dir =  vectorDir(rad);
                   cmds.add(new MovementCommand(cell, dir, pop));
                }

            } else {
                int chunks = (int) Math.floor(pop / 4d);
                for(MovementCommand.Direction dir : MovementCommand.Direction.values()) {
                    if((pop - chunks) > 0) {
                        cmds.add(new MovementCommand(cell, dir, chunks));
                        pop -= chunks;
                    } else if (pop > 0){
                        cmds.add(new MovementCommand(cell, dir, pop));
                    }
                }
            }
        }
        return cmds;
    }


    protected Set<MovementCommand> seeding3(UniverseView v, Coordinates cell, int batchSize, int seed) {
        Set<MovementCommand> mv = new HashSet<>();
        int pop = v.getPopulation(cell);
        if(pop >= batchSize) {
            pop -= seed;
            Vector me = new Vector(cell.getX(), cell.getY());
            Vector alignment = new Vector(0,0);


            for(Coordinates my: v.getMyCells()) {
                if(cell != my) {
                    Vector subs = Vector.sub(me, new Vector(my.getX(), my.getY()));
                    if(subs.mag() < 3){
                        subs.mult(v.getPopulation(my) * 1d / subs.mag());
                        alignment.add(subs);
                    }
                }
            }
            alignment.invert();
            for(MovementCommand.Direction dir: MovementCommand.Direction.values()){
                if(v.isEmpty(cell.getNeighbour(dir))) {
                        Vector subs = Vector.sub(me, new Vector(cell.getNeighbour(dir).getX(), cell.getNeighbour(dir).getY()));
                        subs.mult(v.getMaximumPopulation() * 1d);
                        alignment.add(subs);
                }
            }




            int mag = (int) Math.round(alignment.mag());
            System.out.println(mag);
            if( true) {
                int batch = (int) Math.floor(pop/ 4d);
                for(MovementCommand.Direction dir : MovementCommand.Direction.values()){
                    if(pop > 0 && pop >= batch) {
                        mv.add(new MovementCommand(cell, dir, batch));
                        pop -= batch;
                    }
                }
            } else {
                mv.add(new MovementCommand(cell, vectorDir(alignment.angleFromNormal()), pop));
            }


        }
        return mv;
    }




    protected Set<MovementCommand> defend(UniverseView v, Coordinates cell, int seed){
        log(v, cell, "defend");

        Set<MovementCommand> movement = new HashSet<>();
        int pop = v.getPopulation(cell);

        Set<MovementCommand.Direction> directs = new HashSet<>();

        for (MovementCommand.Direction dir : MovementCommand.Direction.values()){
            if (enemyInDirection(v, cell, dir)){
                directs = spreadDirection(v, dir);
            }
        }

        for (MovementCommand.Direction dir : directs) {
            Coordinates neighbour = cell.getNeighbour(dir);
            int population = v.getPopulation(neighbour);
            int difference = pop - population;
            if (difference > 0 && (pop-difference) > seed && (population + difference <= v.getMaximumPopulation())) {
                movement.add(new MovementCommand(cell, dir, difference));
                pop -= difference;
            }

        }
        return movement;
    }

    protected Set<MovementCommand> attack(UniverseView v, Coordinates cell, int seed) {
        log(v, cell, "attack");
        MovementCommand.Direction dir = nearestBorder(v, cell, v.getUniverseSize());
        if(v.belongsToMe(cell.getNeighbour(dir))) {
            seed = (v.getPopulation(cell.getNeighbour(dir)) + seed) % v.getMaximumPopulation();
            return new HashSet<>(Arrays.asList(new MovementCommand(cell, dir, seed)));
        } else {
           return  new HashSet<>(Arrays.asList(new MovementCommand(cell, dir, seed)));
        }
    }


    protected Set<MovementCommand> attack(UniverseView v, Coordinates cell, int seed, MovementCommand.Direction dir) {
        log(v, cell, "force Attack");
        seed = (seed + v.getPopulation(cell) + 1 )% v.getPopulation(cell) + 1;
        return new HashSet<>(Arrays.asList(new MovementCommand(cell, dir, seed)));
    }



    protected Set<MovementCommand> grow(UniverseView uniView, Coordinates cell, int minSeed) {
        log(uniView, cell, "grow");
        Set<MovementCommand.Direction> direc = new HashSet<>();
        Set<MovementCommand> movements = new HashSet<>();
        int population = uniView.getPopulation(cell);
        population = population > minSeed ? population - minSeed : population;

        for(MovementCommand.Direction dir : shuffledDirections()){
            if (uniView.isEmpty(cell.getRelative(1, dir)) || uniView.getPopulation(cell.getRelative(1,dir))*2 < uniView.getPopulation(cell)) {
                direc.add(dir);
            }
        }

        for(MovementCommand.Direction dir : direc) {
            if (population > minSeed) {
                movements.add(new MovementCommand(cell, dir, minSeed));
                population -= minSeed;
            } else if (population > 0){
                movements.add(new MovementCommand(cell, dir, population));
                population = 0;
            }
        }

        return  movements;
    }


    protected Set<MovementCommand> test( UniverseView v, Coordinates cell, Vector vect) {
        Set<MovementCommand> sets = new HashSet<>();
        sets.add(new MovementCommand(cell,  vectorDir(vect.angleFromNormal()), v.getPopulation(cell)));
        return sets;
    }



    private MovementCommand.Direction vectorDir(Double radian) {

        double degree = radian *  (180/ Math.PI);
        double degreeRange = 90;
        double segment = degreeRange / 2;


        if( 315 <= degree && degree <= 360  || 0 <= degree && degree < 45) {
            return MovementCommand.Direction.RIGHT;
        }


        if(45 <= degree && degree < 135) {
            return MovementCommand.Direction.DOWN;
        }

        if(135 <= degree && degree <  225) {
            return MovementCommand.Direction.LEFT;
        }

        // 225 <= degree && degree <= 315
        return MovementCommand.Direction.UP;




            // HALF_PI -> DOWN


        // PI -> LEFT


        // PI + HALF_PI -> UP




    }




    private void log(UniverseView v, Coordinates cell, String strat) {
        v.log(String.format("%10s: %s", cell, strat.toUpperCase()));
    }
}
