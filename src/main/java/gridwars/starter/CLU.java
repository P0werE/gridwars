package gridwars.starter;

import cern.ais.gridwars.api.Coordinates;
import cern.ais.gridwars.api.UniverseView;
import cern.ais.gridwars.api.bot.PlayerBot;
import cern.ais.gridwars.api.command.MovementCommand;

import java.lang.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CLU extends CustomBots {


    private MovementCommand[] generateStep(UniverseView uni, Coordinates cellUnit, int maxSeed, int unitSeed) {
        return new MovementCommand[0];
    }


    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {
        double average = getAverage(universeView);
        evaluate();



        for (Coordinates cell : universeView.getMyCells()) {
            if(universeView.getPopulation(cell) > 5){
                list.addAll(performStrategy(universeView, cell, (int) average));
            }
        }
    }

    @Override
    protected void evaluate() {
        if (isAttacked()) {

        }

        currentState = States.EXPAND;
    }

    @Override
    protected Set<MovementCommand> performStrategy(UniverseView v, Coordinates cell, int maxPop) {
        int currentPopulation = v.getPopulation(cell);
        int defendSeed = 20 ; // Min Size ??ß
        // Attack
        int attackRange = 3;
        int troopSize = 75 + (int)(maxPop * 0.1 * getAverage(v)); // Strength of each wall piece
        // Grow
        int seed = (int) Math.ceil((float) maxPop / v.getMyCells().size()); // Min seed left behind
        seed = Math.min(seed, troopSize);
        seed = Math.max(seed, defendSeed);

        // Incubation
        int incubationThreshold = 40 + (int)(maxPop * 0.1) % v.getMaximumPopulation();

        int seedMinSize = 25;
        int seedingChunk = 5;
        int seedingSamples = 30;



        return seeding3(v, cell, 10, 8);

        // return test(v, cell, new Vector(0, 1));



//        if(isBorder2(v, cell)) {
//            // seeing attacker=? -> isReinforced ? -> Attack?
//            MovementCommand.Direction enemyDirection = enemySpotted(v, cell, attackRange);
//            if (enemyDirection != null) {
//                if (isReinforced(v, cell, troopSize)) {
//                    return attack(v, cell, troopSize, enemyDirection);
//                }
//                return grow(v, cell, seed);
//            }
//
//            return seeding2(v, cell, seedMinSize, seedingChunk, seedingSamples);
//        } else {
//            if(incubationThreshold <= currentPopulation) {
//                MovementCommand.Direction dir = nearestBorder(v, cell, v.getUniverseSize());
//                if (dir != null) {
//                    return attack(v, cell, (int) getAverage(v), dir);
//                }
//               return grow(v, cell, seed);
//            }
//            return seeding2(v, cell, seedMinSize, seedingChunk, seedingSamples); // seedingChunk, seedingSAmples);
//        }
    }
}
