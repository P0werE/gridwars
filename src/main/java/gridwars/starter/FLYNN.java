package gridwars.starter;

import cern.ais.gridwars.api.Coordinates;
import cern.ais.gridwars.api.UniverseView;
import cern.ais.gridwars.api.bot.PlayerBot;
import cern.ais.gridwars.api.command.MovementCommand;

import java.util.List;
import java.lang.*;
import java.util.List;
import java.util.Random;

public class FLYNN implements PlayerBot {
    private final long BATSIZE;
    private boolean first = true;
    private Coordinates origin;
    private boolean deathmatch = false;
    private boolean escaping = true;

    public FLYNN(int i, int i1){
        BATSIZE = 5;
        first = true;
        deathmatch = false;
        escaping = true;

    }

    /**
     *
     * @param dm
     * @param esc
     * @param unitSize
     */
    public FLYNN(boolean dm, boolean esc, long unitSize){
        BATSIZE= unitSize;
        deathmatch= dm;
        escaping =esc;
    }

    private MovementCommand.Direction[] getOrder(Coordinates me, Coordinates target, int size) {
        int delta_x;
        int delta_y;
        int order;

        Random rng = new Random();

        MovementCommand.Direction dirs[] = new MovementCommand.Direction[4];

        delta_y = target.getY() - me.getY();
        delta_x = target.getX() - me.getX();



        if ((!escaping) || deathmatch || delta_x * delta_x + delta_y * delta_y <= 128) {

            order = rng.nextInt(4);
            // Permutes directions vector depending on ORDER.

            switch (order) {
                case 1:
                    dirs[0] = MovementCommand.Direction.RIGHT;
                    dirs[1] = MovementCommand.Direction.UP;
                    dirs[2] = MovementCommand.Direction.LEFT;
                    dirs[3] = MovementCommand.Direction.DOWN;
                    break;
                case 2:
                    dirs[0] = MovementCommand.Direction.UP;
                    dirs[1] = MovementCommand.Direction.LEFT;
                    dirs[2] = MovementCommand.Direction.DOWN;
                    dirs[3] = MovementCommand.Direction.RIGHT;
                    break;
                case 3:
                    dirs[0] = MovementCommand.Direction.LEFT;
                    dirs[1] = MovementCommand.Direction.DOWN;
                    dirs[2] = MovementCommand.Direction.UP;
                    dirs[3] = MovementCommand.Direction.LEFT;
                    break;
                default:
                    dirs[0] = MovementCommand.Direction.UP;
                    dirs[1] = MovementCommand.Direction.LEFT;
                    dirs[2] = MovementCommand.Direction.DOWN;
                    dirs[3] = MovementCommand.Direction.RIGHT;
                    break;
            }
        }

        if (Math.abs(delta_x) >= Math.abs(delta_y)) {
            if ((delta_x > 0 && Math.abs(delta_x) < size / 2) || (delta_x < 0 && Math.abs(delta_x) > size / 2)) {
                dirs[0] = MovementCommand.Direction.LEFT;
                dirs[3] = MovementCommand.Direction.RIGHT;
            } else if (delta_x == 0) {
                order = rng.nextInt(2);
                if (order == 1) {
                    dirs[0] = MovementCommand.Direction.RIGHT;
                    dirs[3] = MovementCommand.Direction.LEFT;
                } else {
                    dirs[0] = MovementCommand.Direction.LEFT;
                    dirs[3] = MovementCommand.Direction.RIGHT;
                }
            } else {
                dirs[0] = MovementCommand.Direction.RIGHT;
                dirs[3] = MovementCommand.Direction.LEFT;
            }

            if ((delta_y > 0 && Math.abs(delta_y) < size / 2) || (delta_y < 0 && Math.abs(delta_y) > size / 2)) {
                dirs[1] = MovementCommand.Direction.UP;
                dirs[2] = MovementCommand.Direction.DOWN;
            } else if (delta_y == 0) {
                order = rng.nextInt(2);
                if (order == 1) {
                    dirs[1] = MovementCommand.Direction.UP;
                    dirs[2] = MovementCommand.Direction.DOWN;
                } else {
                    dirs[1] = MovementCommand.Direction.DOWN;
                    dirs[2] = MovementCommand.Direction.UP;
                }
            } else {
                dirs[1] = MovementCommand.Direction.DOWN;
                dirs[2] = MovementCommand.Direction.UP;
            }
        } else {
            if ((delta_y > 0 && Math.abs(delta_y) < size / 2) || (delta_y < 0 && Math.abs(delta_y) > size / 2)) {
                dirs[0] = MovementCommand.Direction.UP;
                dirs[3] = MovementCommand.Direction.DOWN;
            } else if (delta_y == 0) {
                order = rng.nextInt(2);
                if (order == 1) {
                    dirs[0] = MovementCommand.Direction.UP;
                    dirs[3] = MovementCommand.Direction.DOWN;
                } else {
                    dirs[0] = MovementCommand.Direction.DOWN;
                    dirs[3] = MovementCommand.Direction.UP;
                }
            } else {
                dirs[0] = MovementCommand.Direction.DOWN;
                dirs[3] = MovementCommand.Direction.UP;
            }

            if ((delta_x > 0 && Math.abs(delta_x) < size / 2) || (delta_x < 0 && Math.abs(delta_x) > size / 2)) {
                dirs[1] = MovementCommand.Direction.LEFT;
                dirs[2] = MovementCommand.Direction.RIGHT;
            } else if (delta_x == 0) {
                order = rng.nextInt(2);
                if (order == 1) {
                    dirs[1] = MovementCommand.Direction.RIGHT;
                    dirs[2] = MovementCommand.Direction.LEFT;
                } else {
                    dirs[1] = MovementCommand.Direction.LEFT;
                    dirs[2] = MovementCommand.Direction.RIGHT;
                }
            } else {
                dirs[1] = MovementCommand.Direction.RIGHT;
                dirs[2] = MovementCommand.Direction.LEFT;
            }
        }

        return dirs;
    }

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {
        long troops;
        long batallion;
        long tier;
        int order;

        boolean done;

        Random rng = new Random();

        MovementCommand.Direction dirs[];

        if ((!escaping) && (universeView.getCurrentTurn() / 25) % 4 == 3) {
            deathmatch = true;
        } else {
            deathmatch = false;
        }

        if (universeView.getCurrentTurn() > 75) {
            escaping = true;
        }

        if (universeView.getCurrentTurn() > 150) {
            escaping = false;
        }

        for (Coordinates c : universeView.getMyCells()) {

            if (first) {
                first = false;
                origin = c;
            }

            troops = universeView.getPopulation(c);

            dirs = getOrder(c, origin, universeView.getUniverseSize());
            done = false;
            if (deathmatch) {
                for (MovementCommand.Direction coiso : dirs) {
                    if (!universeView.belongsToMe(c.getRelative(1, coiso))) {
                        list.add(new MovementCommand(c, coiso, (int) troops));
                        done = true;
                        break;
                    }
                }
            }
            if (done) continue;

            // Troops Generator Base
            troops -= BATSIZE;

            tier = troops / BATSIZE;

            while (tier > 0) {
                for (MovementCommand.Direction coiso : dirs) {
                    if (tier > 0) {
                        list.add(new MovementCommand(c, coiso, Math.round(BATSIZE)));
                        tier--;
                    } else {
                        break;
                    }
                }
            }


        }
    }
}
