package gridwars.starter;

import cern.ais.gridwars.api.Coordinates;
import cern.ais.gridwars.api.UniverseView;
import cern.ais.gridwars.api.command.MovementCommand;

import java.util.List;
import java.util.Set;

public class Blob extends CustomBots {
    @Override
    protected Set<MovementCommand> performStrategy(UniverseView v, Coordinates cell, int maxPop) {
        return null;
    }

    @Override
    protected void evaluate() {

    }

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {

    }
}
