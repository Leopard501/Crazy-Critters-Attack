package main;

import main.buffs.Buff;
import main.enemies.Enemy;
import main.gui.Hand;
import main.gui.InGameGui;
import main.gui.LevelBuilderGui;
import main.gui.Selection;
import main.gui.guiObjects.GuiObject;
import main.gui.guiObjects.buttons.Button;
import main.gui.guiObjects.buttons.Play;
import main.gui.guiObjects.buttons.TileSelect;
import main.gui.guiObjects.buttons.TowerBuy;
import main.levelStructure.DesertWaves;
import main.levelStructure.ForestWaves;
import main.levelStructure.Level;
import main.misc.*;
import main.particles.Particle;
import main.pathfinding.AStar;
import main.pathfinding.HeapNode;
import main.pathfinding.Node;
import main.projectiles.Arc;
import main.projectiles.Projectile;
import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static main.misc.MiscMethods.*;
import static main.misc.SoundLoader.loadSounds;
import static main.misc.SpriteLoader.loadSprites;
import static main.misc.SpriteLoader.loadSpritesAnim;

public class Main extends PApplet {

    public static Tile.TileDS tiles;
    public static KeyDS keysPressed = new KeyDS();
    public static InputHandler inputHandler;
    public static KeyBinds keyBinds;

    public static Machine machine;
    public static ArrayList<main.towers.Tower> towers;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<main.misc.Corpse> corpses;
    public static ArrayList<main.projectiles.Projectile> projectiles;
    public static ArrayList<main.particles.Particle> particles, underParticles;
    public static ArrayList<main.projectiles.Arc> arcs;
    public static ArrayList<main.projectiles.Shockwave> shockwaves;
    public static ArrayList<TowerBuy> towerBuyButtons;
    public static ArrayList<TileSelect> tileSelectButtons;
    public static ArrayList<Buff> buffs;

    public static int currentLevel;
    public static Level[] levels;

    public static CompressArray compress;

    public static Button openMenuButton;
    public static Button wallBuyButton;
    public static Button sellButton;
    public static Button targetButton;
    public static Button upgradeButtonA, upgradeButtonB;
    public static GuiObject moneyIcon;
    public static GuiObject upgradeIconA, upgradeIconB;
    public static Play playButton;

    public static Hand hand;
    public static Selection selection;
    public static InGameGui inGameGui;
    public static LevelBuilderGui levelBuilderGui;

    public static PFont veryLargeFont;
    public static PFont largeFont;
    public static PFont mediumLargeFont;
    public static PFont mediumFont;
    public static PFont smallFont;

    public static int money = 100;
    public static boolean alive = true;
    public static boolean debug = false;
    public static boolean playingLevel = false;
    public static boolean levelBuilder = false;
    public static int connectWallQueues;
    public static float volume = 0.25f;

    public static final int BOARD_WIDTH = 900;
    public static final int BOARD_HEIGHT = 900;
    public static final int GRID_WIDTH = 1100;
    public static final int GRID_HEIGHT = 1100;

    public static final int SLINGSHOT_PRICE = 75;
    public static final int RANDOMCANNON_PRICE = 150;
    public static final int CROSSBOW_PRICE = 200;
    public static final int CANNON_PRICE = 300;
    public static final int GLUER_PRICE = 300;
    public static final int SEISMIC_PRICE = 400;

    public static HashMap<String, PImage> spritesH = new HashMap<>();
    public static HashMap<String, PImage[]> spritesAnimH = new HashMap<>();
    public static HashMap<String, SoundFile> soundsH = new HashMap<>();
    public static HashMap<String, SoundLoop> soundLoopsH = new HashMap<>();

    //pathfinding stuff
    public static int defaultSize = 1;
    public static Node[][] nodeGrid;
    public static HeapNode openNodes;
    public static Node start;
    public static Node[] end;
    public static AStar path;
    public static int nSize;
    public static float maxCost, minCost;

    public static void main(String[] args) {
        PApplet.main("main.Main", args);
    }

    public void settings() {
        size(GRID_WIDTH, BOARD_HEIGHT);
    } //todo: openGL?

    public void setup() {
        veryLargeFont = createFont("STHeitiSC-Light", 48);
        largeFont = createFont("STHeitiSC-Light", 24);
        mediumLargeFont = createFont("STHeitiSC-Light", 21);
        mediumFont = createFont("STHeitiSC-Light", 18);
        smallFont = createFont("STHeitiSC-Light", 12);
        //creates object data structures
        tiles = new Tile.TileDS();
        for (int y = 0; y <= (BOARD_HEIGHT / 50); y++) {
            for (int x = 0; x <= (BOARD_WIDTH / 50); x++) {
                tiles.add(new Tile(this, new PVector(x * 50, y * 50), tiles.size()), x, y);
            }
        }
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        corpses = new ArrayList<>();
        particles = new ArrayList<>();
        underParticles = new ArrayList<>();
        arcs = new ArrayList<>();
        shockwaves = new ArrayList<>();
        towerBuyButtons = new ArrayList<>();
        tileSelectButtons = new ArrayList<>();
        buffs = new ArrayList<>();
        //loads sprites
        loadSprites(this);
        loadSpritesAnim(this);
        //loads sounds
        loadSounds(this);
        //pathfinding stuff
        nSize = 25;
        nodeGrid = new Node[GRID_WIDTH / nSize][GRID_HEIGHT / nSize];
        for (int x = 0; x < GRID_WIDTH / nSize; x++) {
            for (int y = 0; y < GRID_HEIGHT / nSize; y++) {
                nodeGrid[x][y] = new Node(this, new PVector((nSize * x)-100, (nSize * y)-100));
            }
        }
        path = new AStar();
        openNodes = new HeapNode((int) (sq((float)GRID_WIDTH / nSize)));
        //create end nodes
        end = new Node[0];
        //create start node
        nodeGrid[1][(GRID_WIDTH / nSize) / 2].setStart(1, (GRID_HEIGHT / nSize) / 2);
        start.findGHF();
        for (Node node : end) node.findGHF();
        updateTowerArray();
        //generates levels
        currentLevel = 1; //temp
        levels = new Level[2];
        levels[0] = new Level(this, ForestWaves.genForestWaves(this), "levels/forest", 125, 50, "dirt");
        levels[1] = new Level(this, DesertWaves.genDesertWaves(this), "levels/desert", 250, 75, "sand");
        //load level data
        DataControl.load(this, levels[currentLevel].layout);
        money = levels[currentLevel].startingCash;
        updateNodes();
        //other stuff
        inputHandler = new InputHandler(this);
        keyBinds = new KeyBinds(this);
        keyBinds.loadKeyBinds();
        hand = new Hand(this);
        selection = new Selection(this);
        inGameGui = new InGameGui(this);
        levelBuilderGui = new LevelBuilderGui(this);
        //other
        connectWallQueues = 0;
    }

    public void draw() { //this will need to be change when I todo: add more menu "scenes"
        noStroke();
        fill(25, 25, 25);
        rect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        //keys
        try {
            keyBinds.debugKeys();
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyBinds.spawnKeys();
        //pathfinding
        if (path.reqQ.size() > 0) {
            path.reqQ.get(0).getPath();
            path.reqQ.remove(0);
        }
        maxCost = maxCost();
        minCost = minCost(maxCost);
        //looping sounds
        for (SoundLoop soundLoop : soundLoopsH.values()) soundLoop.continueLoop();
        //objects
        drawObjects();
        //gui stuff
        noStroke();
        if (!levelBuilder) inGameGui.display();
        else levelBuilderGui.display();
        hand.displayHeldInfo();
        //text
        textAlign(LEFT);
        if (!levelBuilder) inGameGui.drawText(this, 10);
        //levels
        if (playingLevel) levels[currentLevel].main();
        //reset mouse pulses
        inputHandler.rightMouseReleasedPulse = false;
        inputHandler.leftMouseReleasedPulse = false;
        inputHandler.rightMousePressedPulse = false;
        inputHandler.leftMousePressedPulse = false;
        for (KeyDS.KeyDSItem key : keysPressed.items) {
            key.pressedPulse = false;
            key.releasedPulse = false;
        }
    }

    private void drawObjects() {
        //tiles
        if (connectWallQueues > 0) { //else
            connectWallQueues = 0;
            updateWallTileConnections();
        }
        //main background
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).displayA();
        }
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.bgWName != null) {
                if (tile.bgWName.equals("metalWall")) {
                    tile.drawBgWICorners();
                }
            }
            tile.drawBgWOCorners("metalWall");
        }
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.bgWName != null) {
                if (tile.bgWName.equals("crystalWall")) {
                    tile.drawBgWICorners();
                }
            }
            tile.drawBgWOCorners("crystalWall");
        }
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.bgWName != null) {
                if (tile.bgWName.equals("titaniumWall")) {
                    tile.drawBgWICorners();
                }
            }
            tile.drawBgWOCorners("titaniumWall");
        }
        //obstacle shadows, background c
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).displayB();
        }
        //pathfinding debug
        if (debug) {
            for (Node[] nodes : nodeGrid) {
                for (Node node : nodes) {
                    node.display();
                }
            }
            fill(0,0,255);
            rect(start.position.x,start.position.y,nSize,nSize);
        }
        //under particle culling
        int up = underParticles.size();
        int up2 = up-800;
        if (up > 800) for (int i = 0; i < up; i++) if (random(0,up2) < 5) {
            if (i < underParticles.size()) underParticles.remove(i);
            else break;
        }
        if (up > 1200) underParticles = new ArrayList<>();
        //under particles
        for (int i = underParticles.size()-1; i >= 0; i--) {
            Particle particle = underParticles.get(i);
            particle.main(underParticles, i);
        }
        //corpses
        for (int i = corpses.size() - 1; i >= 0; i--) {
            Corpse corpse = corpses.get(i);
            corpse.main(i);
        }
        //machine
        machine.main();
        //enemies
        for (Enemy enemy : enemies) enemy.displayPassA();
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.main(i);
        }
        if (enemies.size() == 0) buffs = new ArrayList<>();
        //towers
        for (Tower tower : towers) if (tower.turret) tower.displayPassA();
        for (Tower tower : towers) if (!tower.turret) tower.displayPassA();
        for (Tower tower : towers) if (!tower.turret) tower.displayPassB();
        for (Tower tower : towers) if (tower.turret) tower.displayPassB();
        for (Tower tower : towers) tower.main();
        //projectiles
        for (Projectile projectile : projectiles) projectile.displayPassA();
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            projectile.main(projectiles, i);
        }
        //arcs
        for (int i = arcs.size()-1; i >= 0; i--) {
            Arc arc = arcs.get(i);
            arc.main();
            if (arc.alpha <= 0) arcs.remove(i);
        }
        //shockwaves
        for (int i = shockwaves.size()-1; i >= 0; i--) shockwaves.get(i).main();
        //particle culling
        int p = particles.size();
        int p2 = p-800;
        if (p > 800) for (int i = 0; i < p; i++) if (random(0,p2) < 5) {
            if (i < particles.size()) particles.remove(i);
            else break;
        }
        if (p > 1200) particles = new ArrayList<>();
        //particles
        for (int i = particles.size()-1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.main(particles, i);
        }
        //buffs
        for (int i = buffs.size() - 1; i >= 0; i--) {
            Buff buff = buffs.get(i);
            buff.main(i);
        }
        //currently held
        hand.main();
        //obstacles
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).displayC();
        }
        //enemy hp bars
        for (Enemy enemy : enemies) {
            if (enemy.hp > 0 && !enemy.stealthMode) enemy.hpBar();
        }
    }

    public void keyPressed() {
        inputHandler.key(true);
    }

    public void keyReleased() {
        inputHandler.key(false);
    }

    public void mousePressed() {
        inputHandler.mouse(true);
    }

    public void mouseReleased() {
        inputHandler.mouse(false);
    }

    public class InputHandler {

        private PApplet p;

        public boolean rightMouseReleasedPulse;
        public boolean leftMouseReleasedPulse;
        private boolean rightMousePressed;
        private boolean leftMousePressed;
        public boolean rightMousePressedPulse;
        public boolean leftMousePressedPulse;

        public InputHandler(PApplet p) {
            this.p = p;
        }

        void mouse(boolean b) {
            if (b) {
                if (p.mouseButton == RIGHT) {
                    if (!rightMousePressed) rightMousePressedPulse = true;
                    rightMousePressed = true;
                }
                if (p.mouseButton == LEFT) {
                    if (!leftMousePressed) leftMousePressedPulse = true;
                    leftMousePressed = true;
                }
            } else {
                if (rightMousePressed) {
                    rightMouseReleasedPulse = true;
                    rightMousePressed = false;
                }
                if (leftMousePressed) {
                    leftMouseReleasedPulse = true;
                    leftMousePressed = false;
                }
            }
        }

        void key(boolean b) {
            for (KeyDS.KeyDSItem item : keysPressed.items) {
                if (item.key == key) {
                    item.pressed = b;
                    item.pressedPulse = b;
                    item.releasedPulse = !b;
                }
            }
        }
    }

    public static class KeyDS {

        public KeyDSItem[] items;

        public KeyDS() {
            items = new KeyDSItem[0];
        }

        public void add(char key) {
            KeyDSItem[] newItems = new KeyDSItem[items.length + 1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = new KeyDSItem(key);
            items = newItems;
        }

        public boolean getPressed(char key) {
            boolean r = false;
            for (KeyDSItem item : items) if (item.key == key) r = item.pressed;
            return r;
        }

        public boolean getPressedPulse(char key) {
            boolean r = false;
            for (KeyDSItem item : items) if (item.key == key) r = item.pressedPulse;
            return r;
        }

        public boolean getReleasedPulse(char key) {
            boolean r = false;
            for (KeyDSItem item : items) if (item.key == key) r = item.releasedPulse;
            return r;
        }

        static class KeyDSItem {

            char key;
            boolean pressed;
            boolean pressedPulse;
            boolean releasedPulse;

            KeyDSItem(char key) {
                this.key = key;
                pressed = false;
            }
        }
    }
}
