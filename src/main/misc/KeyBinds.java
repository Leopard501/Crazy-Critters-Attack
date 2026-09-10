package main.misc;

import main.Main;
import main.enemies.*;
import main.levelStructure.Level;
import main.towers.Tower;
import main.towers.turrets.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;

import static main.Main.*;
import static main.misc.Utilities.closeSettingsMenu;
import static main.pathfinding.PathfindingUtilities.updateCombatPoints;
import static main.sound.SoundUtilities.playSound;

public class KeyBinds {

    private static PApplet p;

    public KeyBinds(PApplet p) {
        KeyBinds.p = p;
    }

    public void menuKeys() {
        boolean pause = keysPressed.getPressedPulse('|');

        if (pause) {
            switch (screen) {
                case InGame -> {

                    if (isSettings) closeSettingsMenu();
                    if (isPaused) {
                        isPaused = false;
                        playSound(sounds.get("littleButtonIn"), 1, 1);
                    } else {
                        isPaused = true;
                        playSound(sounds.get("littleButtonOut"), 1, 1);
                    }
                } case LevelSelect -> {
                    if (isSettings) {
                        playSound(sounds.get("littleButtonOut"), 1, 1);
                        isSettings = false;
                    }
                }
            }
        }
    }

    public void selectionKeys() {
        boolean upgradeTop = keysPressed.getPressedPulse('1');
        boolean upgradeBottom = keysPressed.getPressedPulse('2');
        boolean priorityRight = keysPressed.getPressedPulse('3');
        boolean delete = keysPressed.getPressedPulse('*');

        if (upgradeTop) selection.upgradeTop();
        if (upgradeBottom) selection.upgradeBottom();
        if (priorityRight) selection.changePriority();
        if (delete) selection.sell();
    }

    public void inGameKeys() {
        boolean play = keysPressed.getPressedPulse(' ');
        //hotkeys
        boolean wall =           addHotkey(new char[]{'?'}, 0),
                slingshot =      addHotkey(new char[]{'q', 'Q'}, Slingshot.price),
                luggageBlaster = addHotkey(new char[]{'a', 'A'}, RandomCannon.price),
                crossbow =       addHotkey(new char[]{'z', 'Z'}, Crossbow.price),
                cannon =         addHotkey(new char[]{'w', 'W'}, Cannon.price),
                gluer =          addHotkey(new char[]{'s', 'S'}, Gluer.price),
                seismicTower =   addHotkey(new char[]{'x', 'X'}, SeismicTower.price),
                energyBlaster =  addHotkey(new char[]{'e', 'E'}, EnergyBlaster.price),
                flamethrower =   addHotkey(new char[]{'d', 'Q'}, Flamethrower.price),
                teslaTower =     addHotkey(new char[]{'c', 'C'}, TeslaTower.price),
                booster =        addHotkey(new char[]{'v', 'V'}, Booster.price),
                iceTower =       addHotkey(new char[]{'f', 'F'}, IceTower.price),
                magicMissileer = addHotkey(new char[]{'r', 'R'}, MagicMissileer.price),
                railgun =        addHotkey(new char[]{'t', 'T'}, Railgun.price),
                nightmare =      addHotkey(new char[]{'g', 'G'}, Nightmare.price),
                waveMotion =     addHotkey(new char[]{'b', 'B'}, WaveMotion.price);

        if (play) {
            if (!isPlaying) {
                isPlaying = true;
                Level level = levels[currentLevel];
                level.currentWave = 0;
            } else {
                levels[currentLevel].advance();
            }
        }
        //hotkeys
        if (!isDev) {
            String name = "";
            Class<?> towerType = null;
            if (wall) name = "wall";
            if (slingshot) {
                name = "slingshot";
                towerType = Slingshot.class;
            } if (luggageBlaster) {
                name = "miscCannon";
                towerType = RandomCannon.class;
            } if (crossbow) {
                name = "crossbow";
                towerType = Crossbow.class;
            }
            if (currentLevel > 0) {
                if (cannon) {
                    name = "cannon";
                    towerType = Cannon.class;
                } if (gluer) {
                    name = "gluer";
                    towerType = Gluer.class;
                } if (seismicTower) {
                    name = "seismic";
                    towerType = SeismicTower.class;
                }
            } if (currentLevel > 1) {
                if (energyBlaster) {
                    name = "energyBlaster";
                    towerType = EnergyBlaster.class;
                } if (flamethrower) {
                    name = "flamethrower";
                    towerType = Flamethrower.class;
                } if (teslaTower) {
                    name = "tesla";
                    towerType = TeslaTower.class;
                }
            } if (currentLevel > 2) {
                if (booster) {
                    name = "booster";
                    towerType = Booster.class;
                } if (iceTower) {
                    name = "iceTower";
                    towerType = IceTower.class;
                } if (magicMissileer) {
                    name = "magicMissleer";
                    towerType = MagicMissileer.class;
                }
            } if (currentLevel > 3) {
                if (railgun) {
                    name = "railgun";
                    towerType = Railgun.class;
                } if (nightmare) {
                    name = "nightmare";
                    towerType = Nightmare.class;
                } if (waveMotion) {
                    name = "waveMotion";
                    towerType = WaveMotion.class;
                }
            }
            if (!name.isEmpty()) {
                if (hand.held.equals(name)) hand.setHeld("null");
                else hand.setHeld(name);
                hand.heldClass = towerType;
            }
        }
    }

    private boolean addHotkey(char[] keys, int price) {
        boolean pressed = false;
        //this is important, don't remove it
        for (char key : keys) if (keysPressed.getPressedPulse(key)) pressed = true;
        return pressed && alive && !isPaused && money >= price;
    }

    public void spawnKeys() {
        //projectiles
        boolean pebble = keysPressed.getPressedPulse('q') && alive;
        boolean bolt = keysPressed.getPressedPulse('w') && alive;
        boolean miscProjectile = keysPressed.getPressedPulse('e') && alive;
        boolean smallEnergyBlast = keysPressed.getPressedPulse('r') && alive;
        boolean largeEnergyBlast = keysPressed.getPressedPulse('R') && alive;
        boolean magicMissle = keysPressed.getPressedPulse('t') && alive;
        boolean arc = keysPressed.getPressedPulse('y') && alive && !enemies.isEmpty();
        boolean needle = keysPressed.getPressedPulse('u') && alive;
        boolean flame = keysPressed.getPressed('i') && alive;
        //enemies
        boolean en1 =  keysPressed.getPressedPulse('1') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en2 =  keysPressed.getPressedPulse('2') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en3 =  keysPressed.getPressedPulse('3') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en4 =  keysPressed.getPressedPulse('4') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en5 =  keysPressed.getPressedPulse('5') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en6 =  keysPressed.getPressedPulse('6') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en7 =  keysPressed.getPressedPulse('7') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en8 =  keysPressed.getPressedPulse('8') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en9 =  keysPressed.getPressedPulse('9') && alive && boardMousePosition.x < BOARD_WIDTH;
        boolean en1b = keysPressed.getPressedPulse('!') && alive && boardMousePosition.x < BOARD_WIDTH;
        //enemies
        if (en1) enemies.add(Enemy.get(p, "smolBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en2) enemies.add(Enemy.get(p, "midBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en3) enemies.add(Enemy.get(p, "bigBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en4) enemies.add(Enemy.get(p, "scorpion", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en5) enemies.add(Enemy.get(p, "emperor", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en6) enemies.add(Enemy.get(p, "smolBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en7) enemies.add(Enemy.get(p, "smolBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en8) enemies.add(Enemy.get(p, "smolBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en9) enemies.add(Enemy.get(p, "smolBug", new PVector(boardMousePosition.x, boardMousePosition.y)));
        if (en1b) enemies.add(new Dummy(p, boardMousePosition.x, boardMousePosition.y));
        if (en1 || en2 || en3 || en4 || en5 || en6 || en8 || en7 || en9 || en1b) enemies.get(enemies.size() - 1).requestPath(enemies.size() - 1);
    }

    public void debugKeys() {
        //entity stuff
        boolean deleteEnemies = keysPressed.getReleasedPulse('s') && alive;
        boolean killTowers = keysPressed.getReleasedPulse('d') && alive;
        boolean hurtTowers = keysPressed.getReleasedPulse('D') && alive;
        boolean killProjectiles = keysPressed.getReleasedPulse('f') && alive;
        boolean killEnemies = keysPressed.getPressedPulse('c') && alive;
        boolean overkillEnemies = keysPressed.getPressedPulse('C') && alive;
        //other stuff
        boolean displayDebug = keysPressed.getReleasedPulse('g');
        boolean displaySpawnAreas = keysPressed.getReleasedPulse('G');
        boolean loseMoney = keysPressed.getPressedPulse('-');
        boolean addMoney = keysPressed.getPressed('=');
        boolean levelBuilder = keysPressed.getPressedPulse('b');
        boolean saveTiles = keysPressed.getPressedPulse('z');
        boolean increaseWave = keysPressed.getPressedPulse(']'); //only works when game playing
        boolean decreaseWave = keysPressed.getPressedPulse('[');
        boolean increaseWave5 = keysPressed.getPressedPulse('}');
        boolean decreaseWave5 = keysPressed.getPressedPulse('{');
        //entity stuff
        if (deleteEnemies) {
            enemies = new ArrayList<>();
        } if (killTowers) {
            for (int i = 0; i < tiles.size(); i++) {
                Tower tower = tiles.get(i).tower;
                if (tower != null) tower.die(false);
            }
            updateCombatPoints();
            machine.die();
        } if (hurtTowers) {
            for (int i = 0; i < tiles.size(); i++) {
                Tower tower = tiles.get(i).tower;
                if (tower != null) tower.hp -= tower.maxHp / 5;
            }
            updateCombatPoints();
            machine.damage(20);
        } if (killProjectiles) projectiles = new ArrayList<>();
        if (killEnemies) {
            for (Enemy enemy : enemies) {
                enemy.damageWithoutBuff(enemy.maxHp - 1, null, null, new PVector(0,0), true);
                enemy.damageWithoutBuff(1, null, null, new PVector(0,0), true);

            }
        } if (overkillEnemies) {
            for (Enemy enemy : enemies) {
                enemy.damageWithoutBuff(enemy.maxHp + 1, null, null, new PVector(0,0), true);
            }
        }
        //other stuff
        if (displayDebug) isDebug = !isDebug;
        if (displaySpawnAreas) isShowSpawn = !isShowSpawn;
        if (addMoney) money += 1000;
        if (loseMoney) money = 0;
        if (levelBuilder) {
            Main.isLevelBuilder = !Main.isLevelBuilder;
            hand.setHeld("null");
        } if (saveTiles) {
            try {
                LayoutLoader.saveLayout();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if (increaseWave && canWave(1)) levels[currentLevel].setWave(levels[currentLevel].currentWave + 1);
        if (decreaseWave && canWave(-1)) levels[currentLevel].setWave(levels[currentLevel].currentWave - 1);
        if (increaseWave5 && canWave(5)) levels[currentLevel].setWave(levels[currentLevel].currentWave + 5);
        if (decreaseWave5 && canWave(-5)) levels[currentLevel].setWave(levels[currentLevel].currentWave - 5);
    }

    private boolean canWave(int count) {
        if (levels[currentLevel].currentWave + count < 0) return false;
        else return levels[currentLevel].currentWave + count <= levels[currentLevel].waves.length-1;
    }

    public void loadKeyBinds() {
        keysPressed.add('`');
        keysPressed.add('1');
        keysPressed.add('2');
        keysPressed.add('3');
        keysPressed.add('4');
        keysPressed.add('5');
        keysPressed.add('6');
        keysPressed.add('7');
        keysPressed.add('8');
        keysPressed.add('9');
        keysPressed.add('0');
        keysPressed.add('-');
        keysPressed.add('=');
        keysPressed.add('~');
        keysPressed.add('!');
        keysPressed.add('@');
        keysPressed.add('#');
        keysPressed.add('$');
        keysPressed.add('%');
        keysPressed.add('^');
        keysPressed.add('&');
        keysPressed.add('*');
        keysPressed.add('(');
        keysPressed.add(')');
        keysPressed.add('_');
        keysPressed.add('+');
        keysPressed.add('q');
        keysPressed.add('w');
        keysPressed.add('e');
        keysPressed.add('r');
        keysPressed.add('t');
        keysPressed.add('y');
        keysPressed.add('u');
        keysPressed.add('i');
        keysPressed.add('o');
        keysPressed.add('p');
        keysPressed.add('[');
        keysPressed.add(']');
        keysPressed.add('Q');
        keysPressed.add('W');
        keysPressed.add('E');
        keysPressed.add('R');
        keysPressed.add('T');
        keysPressed.add('Y');
        keysPressed.add('U');
        keysPressed.add('I');
        keysPressed.add('O');
        keysPressed.add('P');
        keysPressed.add('{');
        keysPressed.add('}');
        keysPressed.add('|');
        keysPressed.add('a');
        keysPressed.add('s');
        keysPressed.add('d');
        keysPressed.add('f');
        keysPressed.add('g');
        keysPressed.add('h');
        keysPressed.add('j');
        keysPressed.add('k');
        keysPressed.add('l');
        keysPressed.add(';');
        keysPressed.add('A');
        keysPressed.add('S');
        keysPressed.add('D');
        keysPressed.add('F');
        keysPressed.add('G');
        keysPressed.add('H');
        keysPressed.add('J');
        keysPressed.add('K');
        keysPressed.add('L');
        keysPressed.add(':');
        keysPressed.add('"');
        keysPressed.add('z');
        keysPressed.add('x');
        keysPressed.add('c');
        keysPressed.add('v');
        keysPressed.add('b');
        keysPressed.add('n');
        keysPressed.add('m');
        keysPressed.add(',');
        keysPressed.add('.');
        keysPressed.add('/');
        keysPressed.add('Z');
        keysPressed.add('X');
        keysPressed.add('C');
        keysPressed.add('V');
        keysPressed.add('B');
        keysPressed.add('N');
        keysPressed.add('M');
        keysPressed.add('<');
        keysPressed.add('>');
        keysPressed.add('?');
        keysPressed.add(' ');
    }
}
