package main.misc;

import main.enemies.*;
import main.levelStructure.Level;
import main.projectiles.*;
import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;

import static main.Main.*;
import static main.misc.Utilities.closeSettingsMenu;
import static main.misc.Utilities.playSound;
import static main.pathfinding.PathfindingUtilities.updateNodes;

public class KeyBinds {

    private static PApplet p;

    public KeyBinds(PApplet p) {
        KeyBinds.p = p;
    }

    public void menuKeys() {
        boolean pause = keysPressed.getPressedPulse('|');

        if (pause) {
            if (screen == 0) { //in game
                playSound(sounds.get("clickOut"), 1, 1);
                if (settings) closeSettingsMenu();
                else paused = !paused;
            } else if (screen == 1) { //level select
                if (settings) {
                    playSound(sounds.get("clickOut"), 1, 1);
                    settings = false;
                }
            }
        }
    }

    public String selectionKeys() {
        return "";
    }

    public void inGameKeys() {
        boolean play = keysPressed.getPressedPulse(' ');
        //hotkeys
        boolean wall = addHotkey(new char[]{'?'}, 0);
        boolean slingshot = addHotkey(new char[]{'q', 'Q'}, SLINGSHOT_PRICE);
        boolean luggageBlaster = addHotkey(new char[]{'a', 'A'}, RANDOMCANNON_PRICE);
        boolean crossbow = addHotkey(new char[]{'z', 'Z'}, CROSSBOW_PRICE);
        boolean cannon = addHotkey(new char[]{'w', 'W'}, CANNON_PRICE);
        boolean gluer = addHotkey(new char[]{'s', 'S'}, GLUER_PRICE);
        boolean seismicTower = addHotkey(new char[]{'x', 'X'}, SEISMIC_PRICE);
        boolean energyBlaster = addHotkey(new char[]{'e', 'E'}, ENERGYBLASTER_PRICE);
        boolean flamethrower = addHotkey(new char[]{'d', 'Q'}, FLAMETHROWER_PRICE);
        boolean teslaTower = addHotkey(new char[]{'c', 'C'}, TESLATOWER_PRICE);

        if (play) {
            if (!playingLevel) {
                playingLevel = true;
                Level level = levels[currentLevel];
                level.currentWave = 0;
            } else {
                levels[currentLevel].advance();
            }
        }
        //hotkeys
        if (!dev) {
            if (wall) {
                if (hand.held.equals("wall")) hand.setHeld("null");
                else hand.setHeld("wall");
            }
            if (slingshot) hand.setHeld("slingshot");
            if (luggageBlaster) hand.setHeld("miscCannon");
            if (crossbow) hand.setHeld("crossbow");
            if (currentLevel > 0) {
                if (cannon) hand.setHeld("cannon");
                if (gluer) hand.setHeld("gluer");
                if (seismicTower) hand.setHeld("seismic");
            } if (currentLevel > 1) {
                if (energyBlaster) hand.setHeld("energyBlaster");
                if (flamethrower) hand.setHeld("flamethrower");
                if (teslaTower) hand.setHeld("tesla");
            }
        }
    }

    private boolean addHotkey(char[] keys, int price) {
        boolean pressed = false;
        for (char key : keys) if (keysPressed.getPressedPulse(key)) pressed = true;
        return pressed && alive && !paused && money >= price;
    }

    public void spawnKeys() {
        //projectiles
        boolean pebble = keysPressed.getPressedPulse('q') && alive;
        boolean bolt = keysPressed.getPressedPulse('w') && alive;
        boolean miscProjectile = keysPressed.getPressedPulse('e') && alive;
        boolean smallEnergyBlast = keysPressed.getPressedPulse('r') && alive;
        boolean largeEnergyBlast = keysPressed.getPressedPulse('R') && alive;
        boolean magicMissle = keysPressed.getPressedPulse('t') && alive;
        boolean arc = keysPressed.getPressedPulse('y') && alive && enemies.size() != 0;
        boolean needle = keysPressed.getPressedPulse('u') && alive;
        boolean flame = keysPressed.getPressed('i') && alive;
        //enemies
        boolean en1 = keysPressed.getPressedPulse('1') && alive && p.mouseX < BOARD_WIDTH;
        boolean en2 = keysPressed.getPressedPulse('2') && alive && p.mouseX < BOARD_WIDTH;
        boolean en3 = keysPressed.getPressedPulse('3') && alive && p.mouseX < BOARD_WIDTH;
        boolean en4 = keysPressed.getPressedPulse('4') && alive && p.mouseX < BOARD_WIDTH;
        boolean en5 = keysPressed.getPressedPulse('5') && alive && p.mouseX < BOARD_WIDTH;
        boolean en6 = keysPressed.getPressedPulse('6') && alive && p.mouseX < BOARD_WIDTH;
        boolean en7 = keysPressed.getPressedPulse('7') && alive && p.mouseX < BOARD_WIDTH;
        boolean en8 = keysPressed.getPressedPulse('8') && alive && p.mouseX < BOARD_WIDTH;
        boolean en9 = keysPressed.getPressedPulse('9') && alive && p.mouseX < BOARD_WIDTH;
        boolean en1b = keysPressed.getPressedPulse('!') && alive && p.mouseX < BOARD_WIDTH;
        //projectiles
        if (pebble) projectiles.add(new Pebble(p, p.mouseX, p.mouseY, 0, null, 50000));
        if (bolt) projectiles.add(new Bolt(p, p.mouseX, p.mouseY, 0, null, 20, 2));
        if (miscProjectile) projectiles.add(new MiscProjectile(p, p.mouseX, p.mouseY, 0, null, round(p.random(0, 5)), 6));
        if (smallEnergyBlast) projectiles.add(new EnergyBlast(p, p.mouseX, p.mouseY, 0, null, 20, 20, false));
        if (largeEnergyBlast) projectiles.add(new EnergyBlast(p, p.mouseX, p.mouseY, 0, null, 20, 30, true));
        if (magicMissle) projectiles.add(new MagicMissile(p, p.mouseX, p.mouseY, 0, null, 5, 0, new PVector(p.mouseX,p.mouseY)));
        if (arc) arcs.add(new Arc(p, p.mouseX, p.mouseY, null, 35, 5, 500, 0));
        if (needle) projectiles.add(new Needle(p, p.mouseX, p.mouseY, 0, null, 5, 1,150));
        if (flame) projectiles.add(new Flame(p, p.mouseX, p.mouseY, 0, null, 5, 1, 300, 5, false));
        //enemies
        if (en1) enemies.add(new AlbinoBug(p, p.mouseX, p.mouseY));
        if (en2) enemies.add(new BigAlbinoBug(p, p.mouseX, p.mouseY));
        if (en3) enemies.add(new AlbinoButterfly(p, p.mouseX, p.mouseY));
        if (en4) enemies.add(new Bat(p, p.mouseX, p.mouseY));
        if (en5) enemies.add(new GiantBat(p, p.mouseX, p.mouseY));
        if (en6) enemies.add(new SmallGolem(p, p.mouseX, p.mouseY));
        if (en7) enemies.add(new Golem(p, p.mouseX, p.mouseY));
        if (en8) enemies.add(new GiantGolem(p, p.mouseX, p.mouseY));
        if (en9) enemies.add(new Wtf(p, p.mouseX, p.mouseY));
        if (en1b) enemies.add(new Dummy(p, p.mouseX, p.mouseY));
        if (en1 || en2 || en3 || en4 || en5 || en6 || en8 || en7 || en9 || en1b) enemies.get(enemies.size() - 1).requestPath(enemies.size() - 1);
    }

    public void debugKeys() throws IOException {
        //entity stuff
        boolean deleteEnemies = keysPressed.getReleasedPulse('s') && alive;
        boolean killTowers = keysPressed.getReleasedPulse('d') && alive;
        boolean hurtTowers = keysPressed.getReleasedPulse('D') && alive;
        boolean killProjectiles = keysPressed.getReleasedPulse('f') && alive;
        boolean killEnemies = keysPressed.getPressedPulse('c') && alive;
        boolean overkillEnemies = keysPressed.getPressedPulse('C') && alive;
        //other stuff
        boolean displayPathLines = keysPressed.getReleasedPulse('g');
        boolean loseMoney = keysPressed.getPressedPulse('-');
        boolean addMoney = keysPressed.getPressed('=');
        boolean switchMode = keysPressed.getPressedPulse('b');
        boolean saveTiles = keysPressed.getPressedPulse('z');
        boolean increaseWave = keysPressed.getPressedPulse(']');
        boolean decreaseWave = keysPressed.getPressedPulse('[');
        boolean increaseWave5 = keysPressed.getPressedPulse('}');
        boolean decreaseWave5 = keysPressed.getPressedPulse('{');
        //entity stuff
        if (deleteEnemies) {
            enemies = new ArrayList<>();
            buffs = new ArrayList<>();
            updateNodes();
        } if (killTowers) {
            for (int i = 0; i < tiles.size(); i++) {
                Tower tower = tiles.get(i).tower;
                if (tower != null) tower.die(false);
            }
            updateNodes();
            machine.die();
        } if (hurtTowers) {
            for (int i = 0; i < tiles.size(); i++) {
                Tower tower = tiles.get(i).tower;
                if (tower != null) tower.hp -= tower.maxHp/5;
            }
            updateNodes();
            machine.damage(20);
        } if (killProjectiles) projectiles = new ArrayList<>();
        if (killEnemies) {
            for (Enemy enemy : enemies) {
                enemy.damageWithoutBuff(enemy.maxHp - 1, null, "none", new PVector(0,0), true);
                enemy.damageWithoutBuff(1, null, "none", new PVector(0,0), true);

            }
        } if (overkillEnemies) {
            for (Enemy enemy : enemies) {
                enemy.damageWithoutBuff(enemy.maxHp + 1, null, "none", new PVector(0,0), true);
            }
        }
        //other stuff
        if (displayPathLines) debug = !debug;
        if (addMoney) money += 100;
        if (loseMoney) money = 0;
        if (switchMode) {
            levelBuilder = !levelBuilder;
            hand.setHeld("null");
        } if (saveTiles) DataControl.saveLayout();
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
