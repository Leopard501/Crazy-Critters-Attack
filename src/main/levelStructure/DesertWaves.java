package main.levelStructure;

import main.misc.Polluter;
import processing.core.PApplet;

import java.awt.*;

public class DesertWaves {

    public static Wave[] genDesertWaves(PApplet p) {
        Wave[] waves = new Wave[35];

        Color smolBugAccent = new Color(10, 10, 10);
        Color smolBugFill = new Color(255, 100, 100);
        Color smolBugText = new Color(255, 255, 255);

        Color midBugAccent = new Color(10, 10, 10);
        Color midBugFill = new Color(170, 0, 0);
        Color midBugText = new Color(25, 0, 0);

        Color bigBugAccent = new Color(170, 0, 0);
        Color bigBugFill = new Color(10, 10, 10);
        Color bigBugText = new Color(255, 0, 0);

        Color hordeAccent = new Color(89, 193, 53);
        Color hordeFill = new Color(218, 162, 106);
        Color hordeText = new Color(75, 57, 31);

        Color scorpionAccent = new Color(138, 94, 55);
        Color scorpionFill = new Color(229, 164, 75);
        Color scorpionText = new Color(71, 52, 12);

        Color wormAccent = new Color(100, 77, 110);
        Color wormFill = new Color(215, 123, 186);
        Color wormText = new Color(89, 59, 102);

        Color emperorAccent = new Color(241, 96, 255);
        Color emperorFill = new Color(0, 0,0);
        Color emperorText = new Color(241, 96, 255);

        Color midWormAccent = new Color(136, 108, 87);
        Color midWormFill = new Color(220, 133, 101);
        Color midWormText = new Color(64, 48, 32);

        Color bigWormAccent = new Color(255, 255, 255);
        Color bigWormFill = new Color(232, 106, 115);
        Color bigWormText = new Color(45, 19, 21);

        Color sidewinderAccent = new Color(0, 0,0);
        Color sidewinderFill = new Color(154, 193, 66);
        Color sidewinderText = new Color(37, 47, 14);

        waves[0] = new Wave(p, 55, 20, smolBugFill, smolBugAccent, smolBugText,
                new String[]{"Little Desert", "Beetles"});
        waves[0].addSpawns("smolBug",6);
        waves[0].polluter = new Polluter(p, 3, "desert/polluted");

        waves[1] = new Wave(p, 55, 25, midBugFill, midBugAccent, midBugText,
                new String[]{"Desert", "Beetles"});
        waves[1].addSpawns("midBug", 3);
        waves[1].addSpawns("smolBug", 5);

        waves[2] = new Wave(p, 65, 25, midBugFill, midBugAccent, midBugText,
                new String[]{"Desert", "Beetles"});
        waves[2].addSpawns("midBug", 5);
        waves[2].addSpawns("smolBug", 15);

        waves[3] = new Wave(p, 60, 25, midBugFill, midBugAccent, midBugText,
                new String[]{"Many Desert", "Beetles"});
        waves[3].addSpawns("midBug", 10);
        waves[3].addSpawns("smolBug", 5);

        waves[4] = new Wave(p, 55, 25, midBugFill, midBugAccent, midBugText,
                new String[]{"Desert", "Beetles"});
        waves[4].addSpawns("midBug", 5);
        waves[4].addSpawns("smolBug", 15);

        waves[5] = new Wave(p, 55, 25, scorpionFill, scorpionAccent, scorpionText,
                new String[]{"Few", "Scorpions"});
        waves[5].addSpawns("scorpion", 3);
        waves[5].addSpawns("smolBug", 5);

        waves[6] = new Wave(p, 60, 25, scorpionFill, scorpionAccent, scorpionText,
                new String[]{"Few", "Scorpions"});
        waves[6].addSpawns("scorpion", 3);
        waves[6].addSpawns("midBug", 3);
        waves[6].addSpawns("smolBug", 5);

        waves[7] = new Wave(p, 60, 25, scorpionFill, scorpionAccent, scorpionText,
                "Scorpions");
        waves[7].addSpawns("scorpion", 5);
        waves[7].addSpawns("midBug", 3);
        waves[7].addSpawns("smolBug", 5);

        waves[8] = new Wave(p, 65, 25, midBugFill, midBugAccent, midBugText,
                new String[]{"Various", "Beetles"});
        waves[8].addSpawns("midBug", 20);
        waves[8].addSpawns("smolBug", 5);

        waves[9] = new Wave(p, 60, 10, sidewinderFill, sidewinderAccent, sidewinderText,
                "Sidewinders");
        waves[9].addSpawns("sidewinder", 15);

        waves[10] = new Wave(p, 60, 10, sidewinderFill, sidewinderAccent, sidewinderText,
                "Sidewinders");
        waves[10].addSpawns("sidewinder", 20);
        waves[10].addSpawns("scorpion", 3);

        waves[11] = new Wave(p, 60, 25, scorpionFill, scorpionAccent, scorpionText,
                "Scorpions");
        waves[11].addSpawns("scorpion", 5);
        waves[11].addSpawns("midBug", 5);

        waves[12] = new Wave(p, 65, 25, scorpionFill, scorpionAccent, scorpionText,
                new String[]{"Many", "Scorpions"});
        waves[12].addSpawns("scorpion", 10);

        waves[13] = new Wave(p, 65, 20, wormFill, wormAccent, wormText,
                "Little Worms");
        waves[13].addSpawns("littleWorm", 5);

        waves[14] = new Wave(p, 65, 1, bigBugFill, bigBugAccent, bigBugText,
                new String[]{"Hercules", "Beetle"});
        waves[14].addSpawns("bigBug", 1);

        waves[15] = new Wave(p, 65, 30, wormFill, wormAccent, wormText,
                "Little Worms");
        waves[15].addSpawns("littleWorm", 10);
        waves[15].addSpawns("scorpion", 5);
        waves[15].addSpawns("antlion", 3);

        waves[16] = new Wave(p, 40, 1, bigBugFill, bigBugAccent, bigBugText,
                new String[]{"Hercules", "Beetle"});
        waves[16].addSpawns("bigBug", 1);

        waves[17] = new Wave(p, 65, 25, scorpionFill, scorpionAccent, scorpionText,
                new String[]{"Scorpions &", "Sidewinders"});
        waves[17].addSpawns("scorpion", 20);
        waves[17].addSpawns("sidewinder", 10);

        waves[18] = new Wave(p, 65, 20, wormFill, wormAccent, wormText,
                "Little Worms");
        waves[18].addSpawns("littleWorm", 10);
        waves[18].addSpawns("scorpion", 5);

        waves[19] = new Wave(p, 65, 20, bigBugFill, bigBugAccent, bigBugText,
                new String[]{"Hercules", "Beetles"});
        waves[19].addSpawns("bigBug", 3);
        waves[19].addSpawns("scorpion", 15);

        waves[20] = new Wave(p, 65, 5, midWormFill, midWormAccent, midWormText,
                "Sandworm");
        waves[20].addSpawns("midWorm", 1);
        waves[20].addSpawns("littleWorm", 5);

        waves[21] = new Wave(p, 65, 15, midWormFill, midWormAccent, midWormText,
                "Sandworms");
        waves[21].addSpawns("midWorm", 3);

        waves[22] = new Wave(p, 65, 25, scorpionFill, scorpionAccent, scorpionText,
                new String[]{"Desert Bug", "Swarm"});
        waves[22].addSpawns("scorpion", 30);
        waves[22].addSpawns("antlion", 5);

        waves[23] = new Wave(p, 85, 35, midWormFill, midWormAccent, midWormText,
                "Sandworms");
        waves[23].addSpawns("midWorm", 5);
        waves[23].addSpawns("scorpion", 10);

        waves[24] = new Wave(p, 65, 15, midWormFill, midWormAccent, midWormText,
                "Sandworms");
        waves[24].addSpawns("midWorm", 5);
        waves[24].addSpawns("scorpion", 10);
        waves[24].addSpawns("littleWorm", 15);

        waves[25] = new Wave(p, 85, 35, midWormFill, midWormAccent, midWormText,
                "Sandworms");
        waves[25].addSpawns("midWorm", 10);
        waves[25].addSpawns("bigBug", 5);

        waves[26] = new Wave(p, 45, 5, emperorFill, emperorAccent, emperorText,
                new String[]{"Emperor", "Scorpion"});
        waves[26].addSpawns("emperor", 1);
        waves[26].addSpawns("scorpion", 5);

        waves[27] = new Wave(p, 45, 10, sidewinderFill, sidewinderAccent, sidewinderText,
                new String[]{"Sidewinder", "Swarm"});
        waves[27].addSpawns("sidewinder", 30);
        waves[27].addSpawns("scorpion", 3);

        waves[28] = new Wave(p, 60, 25, emperorFill, emperorAccent, emperorText,
                new String[]{"Emperor", "Scorpion"});
        waves[28].addSpawns("emperor", 5);
        waves[28].addSpawns("scorpion", 15);

        waves[29] = new Wave(p, 65, 25, bigBugFill, bigBugAccent, bigBugText,
                new String[]{"Various", "Beetles"});
        waves[29].addSpawns("bigBug", 10);
        waves[29].addSpawns("midBug", 15);
        waves[29].addSpawns("smolBug", 15);

        waves[30] = new Wave(p, 65, 15, midWormFill, midWormAccent, midWormText,
                "Sandworms");
        waves[30].addSpawns("midWorm", 10);
        waves[30].addSpawns("bigBug", 5);
        waves[30].addSpawns("littleWorm", 30);

        waves[31] = new Wave(p, 65, 5, bigWormFill, bigWormAccent, bigWormText,
                new String[]{"Giant", "Sandworm"});
        waves[31].addSpawns("bigWorm", 1);

        waves[32] = new Wave(p, 60, 25, emperorFill, emperorAccent, emperorText,
                new String[]{"Emperor", "Scorpions"});
        waves[32].addSpawns("emperor", 5);
        waves[32].addSpawns("bigBug", 5);

        waves[33] = new Wave(p, 65, 20, bigWormFill, bigWormAccent, bigWormText,
                new String[]{"Giant", "Sandworms"});
        waves[33].addSpawns("bigWorm", 3);

        waves[34] = new Wave(p, 100, 40, hordeFill, hordeAccent, hordeText,
                new String[]{"Mega", "Horde"});
        waves[34].addSpawns("bigBug", 10);
        waves[34].addSpawns("emperor", 6);
        waves[34].addSpawns("bigWorm", 4);

        for (Wave wave : waves) wave.load();
        return waves;
    }
}
