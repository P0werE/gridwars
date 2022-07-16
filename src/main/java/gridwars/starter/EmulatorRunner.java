package gridwars.starter;

import cern.ais.gridwars.Emulator;
import cern.ais.gridwars.api.bot.PlayerBot;


/**
 * Instantiates the example bots and starts the game emulator.
 */
public class EmulatorRunner {

    public static void main(String[] args) {
         // PlayerBot redBot = new MovingBot();
         // PlayerBot redBot = new Blob();
         // PlayerBot redBot = new CLU(); // og  new Flynn(75, 150)para 75 / 150
          PlayerBot redBot = new FLYNN(75, 150);

        // FLYNN blueBot = new FLYNN(75, 150);
        PlayerBot blueBot = new CLU();

        Emulator.playMatch(blueBot, redBot);
    }
}
