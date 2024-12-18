package main.towers.turrets;

import main.buffs.Buff;
import main.enemies.Enemy;
import main.enemies.BurrowingEnemy;
import main.gui.guiObjects.PopupText;
import main.gui.inGame.Selection;
import main.misc.CompressArray;
import main.misc.Tile;
import main.particles.MiscParticle;
import main.particles.Ouch;
import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.function.Consumer;

import static main.Main.*;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.*;
import static main.misc.Tile.updateTowerArray;
import static main.pathfinding.PathfindingUtilities.updateCombatPoints;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public abstract class Turret extends Tower {

    public enum Priority {
        Close("closest"),
        Far("farthest"),
        Strong("most HP"),
        Weak("least HP"),
        Unbuffed("unbuffed"),
        None("");

        public final String text;

        Priority(String text) {
            this.text = text;
        }
    }

    public enum State {
        Idle,
        Fire,
        Load
    }

    public static String pid;
    public static String description;
    public static char shortcut;
    public static String title1;
    public static String title2;
    public static int price;

    public boolean hasPriority;
    public int pjSpeed;
    public int range;
    public int pierce;
    public int killsTotal;
    public int damageTotal;
    public int damage;
    public float effectDuration;
    public float effectLevel;
    public String effect;
    /** Seconds */
    public float delay;
    /** Radians */
    public float angle;
    public String[] upgradeDescA;
    public String[] upgradeDescB;
    public String[] upgradeDescC;
    public String[] titleLines;
    public ArrayList<Consumer<Integer>> extraInfo;
    public Runnable statsDisplay;
    public Priority priority = Priority.Close;
    public int birthday;

    protected State state = State.Idle;
    protected int offset;
    protected int frame;
    protected int frameTimer;
    protected int betweenIdleFrames;
    protected int betweenFireFrames;
    protected float targetAngle;
    protected float barrelLength;
    protected String fireParticle;
    protected PImage idleSprite;
    protected PImage sBase;
    protected PImage[] fireFrames;
    protected PImage[] loadFrames;
    protected PImage[] idleFrames;
    protected Enemy targetEnemy;
    protected SoundFile fireSound;

    protected ArrayList<Integer> compressedLoadFrames;

    protected Turret(PApplet p, Tile tile) {
        super(p, tile);
        this.p = p;
        hasPriority = true;
        size = new PVector(50, 50);
        maxHp = 20;
        hp = maxHp;
        delay = 4;
        pjSpeed = 500;
        compressedLoadFrames = new ArrayList<>();
        upgradePrices = new int[6];
        upgradeTitles = new String[6];
        upgradeDescA = new String[6];
        upgradeDescB = new String[6];
        upgradeDescC = new String[6];
        upgradeIcons = new PImage[6];
        nextLevelB = upgradeTitles.length / 2;
        extraInfo = new ArrayList<>();
        statsDisplay = () -> {
            int age = levels[currentLevel].currentWave - birthday;
            selection.displayInfoLine(-3, Selection.STAT_TEXT_COLOR, "Survived", age + "");
            selection.displayInfoLine(-2, Selection.STAT_TEXT_COLOR, "Kills", killsTotal + "");
            selection.displayInfoLine(-1, Selection.STAT_TEXT_COLOR, "Damage", nfc(damageTotal));
        };
        birthday = levels[currentLevel].currentWave;

        updateTowerArray();
    }

    @Override
    public int getValue() {
        int value = basePrice;
        for (int i = 3; i < nextLevelB; i++) {
            value += upgradePrices[i];
        } for (int i = 0; i < nextLevelA; i++) {
            value += upgradePrices[i];
        }
        return value;
    }

    @Override
    public void place(boolean quiet) {
        loadSprites();
        setUpgrades();
        if (!quiet) {
            spawnParticles();
            playSoundRandomSpeed(p, placeSound, 1);
        }
    }

    protected void checkTarget() {
        getTargetEnemy();
        if (targetEnemy != null && state != State.Fire) aim(targetEnemy);
        if (state == State.Idle &&
                targetEnemy != null &&
                abs(normalizeAngle(targetAngle) - normalizeAngle(angle)) < 0.02) {
            //if done animating and aimed
            state = State.Fire;
            frame = 0;
            fire(barrelLength, fireParticle);
        }
    }

    protected void getTargetEnemy() {
        float targetDist;
        if (priority != Priority.Far) targetDist = 1000000;
        else targetDist = 0;
        float targetHp = -1;
        boolean targetBuffed = true;
        Enemy target = null;
        for (Enemy enemy : enemies) {
            if (!enemyCanBeAttacked(enemy)) continue;
            float x = abs(tile.position.x - (size.x / 2) - enemy.position.x);
            float y = abs(tile.position.y - (size.y / 2) - enemy.position.y);
            float dist = sqrt(sq(x) + sq(y));
            if (enemy.onScreen() && dist < getRange()) {
                switch (priority) {
                    case Close -> {
                        if (dist >= targetDist) break;
                        target = enemy;
                        targetDist = dist;
                    } case Far -> {
                        if (dist <= targetDist) break;
                        target = enemy;
                        targetDist = dist;
                    } case Strong -> {
                        if (enemy.maxHp > targetHp || targetHp == -1) { //strong
                            target = enemy;
                            targetDist = dist;
                            targetHp = enemy.maxHp;
                        } else if (enemy.maxHp == targetHp && dist < targetDist) { //strong -> close
                            target = enemy;
                            targetDist = dist;
                        }
                    } case Weak -> {
                        if (enemy.maxHp < targetHp || targetHp == -1) { //weak
                            target = enemy;
                            targetDist = dist;
                            targetHp = enemy.maxHp;
                        } else if (enemy.maxHp == targetHp && dist < targetDist) { //weak -> close
                            target = enemy;
                            targetDist = dist;
                        }
                    } case Unbuffed -> {
                        boolean noMatch = true;
                        for (Buff buff : enemy.buffs) {
                            if (buff.name.equals(effect)) {
                                noMatch = false;
                                break;
                            }
                        }
                        // if the current target has the buff:
                        if (targetBuffed) {
                            // and this enemy doesn't, target it
                            if (noMatch) {
                                targetBuffed = false;
                                target = enemy;
                                targetDist = dist;
                            } // otherwise, target the enemy if it is closer
                            else if (dist < targetDist) {
                                target = enemy;
                                targetDist = dist;
                            }
                        } // if the current target does not have the buff:
                        else {
                            // and this enemy doesn't and is closer, target it
                            if (noMatch && dist < targetDist) {
                                target = enemy;
                                targetDist = dist;
                            }
                        }
                    }
                }
            }
        }
        targetEnemy = target;
    }

    protected boolean enemyCanBeAttacked(Enemy enemy) {
        return !(enemy.state == Enemy.State.Moving && enemy instanceof BurrowingEnemy);
    }

    /**
     * Sets the target angle to match the target.
     * Leads shots if enemy moving.
     * @param enemy enemy to aim at
     */
    protected void aim(Enemy enemy) {
        PVector position = new PVector(tile.position.x - 25, tile.position.y - 25);
        PVector target = enemy.position;

        if (pjSpeed > 0) { //shot leading
            float dist = PVector.sub(target, position).mag();
            float time = dist / (pjSpeed * (boostedRange() > 0 ? 1.2f : 1f));
            PVector enemyHeading = PVector.fromAngle(enemy.rotation);
            if (enemy.state == Enemy.State.Moving) enemyHeading.setMag(enemy.getActualSpeed() * time); //only lead if enemy moving
            else enemyHeading.setMag(0);
            target = new PVector(target.x + enemyHeading.x, target.y + enemyHeading.y);
        }

        targetAngle = normalizeAngle(findAngle(position, target));
        angle = normalizeAngle(angle);
        angle += getAngleDifference(targetAngle, angle) / (FRAMERATE/6f);

        if (abs(targetAngle - angle) < 0.05) angle = targetAngle; //snap to prevent getting stuck

        if (visualize && isDebug) { //cool lines
            p.stroke(255);
            p.line(position.x, position.y, target.x, target.y);
            p.stroke(255, 0, 0, 150);
            p.line(target.x, p.height, target.x, 0);
            p.stroke(0, 0, 255, 150);
            p.line(p.width, target.y, 0, target.y);
        }
    }

    protected void fire(float barrelLength, String particleType) {
        playSoundRandomSpeed(p, fireSound, 1);
        float displayAngle = angle;
        PVector projectileSpawn = new PVector(tile.position.x-size.x/2,tile.position.y-size.y/2);
        PVector barrel = PVector.fromAngle(displayAngle-HALF_PI);
        float particleCount = p.random(1,5);
        barrel.setMag(barrelLength); //barrel length
        projectileSpawn.add(barrel);
        spawnProjectiles(projectileSpawn, displayAngle);
        if (particleType != null && !particleType.equals("null")) {
            for (int i = 0; i < particleCount; i++) {
                PVector spa2 = PVector.fromAngle(displayAngle - HALF_PI + radians(p.random(-20, 20)));
                spa2.setMag(-5);
                PVector spp2 = new PVector(projectileSpawn.x, projectileSpawn.y);
                spp2.add(spa2);
                topParticles.add(new MiscParticle(p, spp2.x, spp2.y, displayAngle + radians(p.random(-45, 45)), particleType));
            }
        }
    }

    protected abstract void spawnProjectiles(PVector position, float angle);

    protected void loadSprites() {
        sBase = getResource(name + "BaseTr", staticSprites);
        fireFrames = getResource(name + "FireTR", animatedSprites);
        loadFrames = getResource(name + "LoadTR", animatedSprites);
        if (animatedSprites.containsKey(name + "IdleTR")) {
            idleFrames = getResource(name + "IdleTR", animatedSprites);
            idleSprite = idleFrames[0];
            sprite = idleSprite;
        } else {
            idleSprite = getResource(name + "IdleTr", staticSprites);
            idleFrames = new PImage[]{idleSprite};
            sprite = idleFrames[0];
        }
    }

    @Override
    public void update() {
        if (hp <= 0) {
            die(false);
            tile.tower = null;
        }
        updateBoosts();
        if (!enemies.isEmpty() && !machine.dead && !isPaused) checkTarget();
        if (p.mousePressed && boardMousePosition.x < tile.position.x && boardMousePosition.x > tile.position.x - size.x && boardMousePosition.y < tile.position.y
                && boardMousePosition.y > tile.position.y - size.y && alive && !isPaused) {
            selection.swapSelected(tile.id);
        }
    }

    @Override
    public void die(boolean isSold) {
        playSoundRandomSpeed(p, breakSound, 1);
        spawnParticles();
        tile.tower = null;
        alive = false;
        updateTowerArray();
        if (selection.turret == this) {
            selection.name = "null";
        } else if (!selection.name.equals("null")) selection.swapSelected(selection.turret);
        int moneyGain;
        if (!isSold) {
            moneyGain = (int) (getValue() * 0.4);
            tiles.get(((int)tile.position.x/50) - 1,
                    ((int)tile.position.y/50) - 1).breakableLayer.set(material + "DebrisBr_Tl");
        } else moneyGain = (int) (getValue() * 0.8);
        popupTexts.add(new PopupText(p, new PVector(tile.position.x - 25, tile.position.y - 25), moneyGain));
        money += moneyGain;
        if (hasBoostedDeathEffect()) boostedDeathEffect();
        Tile.updateFlooring();
        connectWallQueues++;
        updateCombatPoints();
    }

    @Override
    public void controlAnimation() {
        if (!isPaused) {
            if (hp < getMaxHp() && p.random(30) < 1) {
                topParticles.add(new Ouch(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(360), "greyPuff"));
            }
            if (tintColor < 255) tintColor += 20;
            switch (state) {
                case Idle -> {
                    sprite = idleSprite;
                    if (idleFrames.length > 1) {
                        if (frame < idleFrames.length) {
                            sprite = idleFrames[frame];
                            if (frameTimer >= betweenIdleFrames) {
                                frame++;
                                frameTimer = 0;
                            } else frameTimer++;
                        } else {
                            frame = 0;
                            sprite = idleFrames[frame];
                        }
                    }
                } case Fire -> {
                    if (frame < fireFrames.length - 1) { //if not done, keep going
                        if (frameTimer >= betweenFireFrames) {
                            frame++;
                            frameTimer = 0;
                            sprite = fireFrames[frame];
                        } else frameTimer++;
                    } else { //if done, switch to load
                        if (loadFrames.length > 0) {
                            int oldSize = loadFrames.length;
                            int newSize = secondsToFrames(randomizeDelay(p, getDelay()));
                            compressedLoadFrames = new ArrayList<>();
                            if (oldSize > newSize) { //decreasing size
                                //creates the new spriteArray
                                for (int i = 0; i < oldSize; i++) compressedLoadFrames.add(i);
                                //compression
                                compress = new CompressArray(oldSize, newSize, compressedLoadFrames);
                                compress.update();
                            } else { //increasing size
                                compress = new CompressArray(oldSize - 1, newSize, compressedLoadFrames);
                                compress.update();
                                compressedLoadFrames = compress.compArray;
                            }
                        }
                        frame = 0;
                        state = State.Load;
                    }
                } case Load -> {
                    frame++;
                    if (frame < compressedLoadFrames.size() && compressedLoadFrames.get(frame) < loadFrames.length) {
                        sprite = loadFrames[compressedLoadFrames.get(frame)];
                    } else { //if time runs out, switch to idle
                        frame = 0;
                        sprite = idleSprite;
                        state = State.Idle;
                    }
                }
            }
            if (hit) { //change to red if under attack
                tintColor = 0;
                hit = false;
            }
        }
    }

    public void displayUpgradePrompt() {
        // if A or B is max
        if (nextLevelB > 5 || nextLevelA > 2) return;

        if (money >= upgradePrices[nextLevelA] && money >= upgradePrices[nextLevelB]) {
            p.image(staticSprites.get("upgradePrompt2Ic"), tile.position.x - size.x, tile.position.y - size.y);
        } else if (money >= upgradePrices[nextLevelA] || money >= upgradePrices[nextLevelB]) {
            p.image(staticSprites.get("upgradePromptIc"), tile.position.x - size.x, tile.position.y - size.y);
        }
    }

    public void displayTop() {
        //shadow
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2 + 2, tile.position.y - size.y / 2 + 2);
        p.rotate(angle);
        p.tint(0, 60);
        if (sprite != null) p.image(sprite, -size.x / 2 - offset, -size.y / 2 - offset);
        p.popMatrix();
        //main
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2, tile.position.y - size.y / 2);
        p.rotate(angle);
        p.tint(255, tintColor, tintColor);
        if (sprite != null) p.image(sprite, -size.x / 2 - offset, -size.y / 2 - offset);
        p.popMatrix();
        p.tint(255);
    }

    @Override
    public void displayBase() {
        p.tint(255, tintColor, tintColor);
        p.image(sBase, tile.position.x - size.x, tile.position.y - size.y);
        p.tint(255, 255, 255);
    }

    /**
     * Upgrades the turret once.
     * @param id which track to use, 0 for A, 1 for B
     * @param quiet no particles and sound
     */
    @Override
    public void upgrade(int id, boolean quiet) {
        int price = 0;
        if (id == 0) {
            if (nextLevelA >= upgradePrices.length) return;
            price = upgradePrices[nextLevelA];
            if (price > money) return;
            if (nextLevelA > 2) return;
            if (nextLevelB == 6 && nextLevelA == 2) return;
        } else if (id == 1) {
            if (nextLevelB >= upgradePrices.length) return;
            price = upgradePrices[nextLevelB];
            if (price > money) return;
            if (nextLevelB > 5) return;
            if (nextLevelB == 5 && nextLevelA == 3) return;
        }
        money -= price;
        upgradeEffect(id);
        if (id == 0) nextLevelA++;
        else if (id == 1) nextLevelB++;
        //icons
        int maxA = upgradeTitles.length / 2;
        int maxB = upgradeTitles.length;
        if (nextLevelA < maxA && !(nextLevelB == maxB && nextLevelA == maxA - 1))
            inGameGui.upgradeIconA.sprite = upgradeIcons[nextLevelA];
        else inGameGui.upgradeIconA.sprite = animatedSprites.get("upgradeIC")[0];
        if (nextLevelB < maxB && !(nextLevelA == maxA && nextLevelB == maxB - 1))
            inGameGui.upgradeIconB.sprite = upgradeIcons[nextLevelB];
        else inGameGui.upgradeIconB.sprite = animatedSprites.get("upgradeIC")[0];

        if (!quiet) {
            playSoundRandomSpeed(p, placeSound, 1);
            spawnParticles();
        }
        //prevent having fire animations longer than delays
//        while (getDelay() <= fireFrames.length * betweenFireFrames + idleFrames.length && betweenFireFrames > 0) betweenFireFrames--;
    }

    protected abstract void setUpgrades();

    protected abstract void upgradeEffect(int id);

    //boosts

    public int boostedDamage() {
        int d = 0;
        for (Booster.Boost boost : boosts) {
            int d2 = (int) (damage * boost.damage);
            if (d2 > d) d = d2;
        }
        return d;
    }

    public int getDamage() {
        return damage + boostedDamage();
    }

    public int boostedRange() {
        int r = 0;
        for (Booster.Boost boost : boosts) {
            int r2 = (int) (range * boost.range);
            if (r2 > r) r = r2;
        }
        return r;
    }

    public int getRange() {
        return range + boostedRange();
    }

    public float boostedFirerate() {
        float f = 0;
        for (Booster.Boost boost : boosts) {
            float f2 = delay * boost.firerate;
            if (f2 > f) f = f2;
        }
        return f;
    }

    public float getDelay() {
        return delay - boostedFirerate();
    }

    public static Turret get(PApplet p, String type, Tile tile) {
        switch (type) {
            case "Booster" -> {
                return new Booster(p, tile);
            } case "Cannon" -> {
                return new Cannon(p, tile);
            } case "Crossbow" -> {
                return new Crossbow(p, tile);
            } case "EnergyBlaster" -> {
                return new EnergyBlaster(p, tile);
            } case "Flamethrower" -> {
                return new Flamethrower(p, tile);
            } case "Gluer" -> {
                return new Gluer(p, tile);
            } case "IceTower" -> {
                return new IceTower(p, tile);
            } case "MagicMissileer", "MagicMissleer" -> {
                return new MagicMissileer(p, tile);
            } case "Nightmare" -> {
                return new Nightmare(p, tile);
            } case "Railgun" -> {
                return new Railgun(p, tile);
            } case "RandomCannon", "MiscCannon" -> {
                return new RandomCannon(p, tile);
            } case "SeismicTower", "Seismic" -> {
                return new SeismicTower(p, tile);
            } case "Slingshot" -> {
                return new Slingshot(p, tile);
            } case "TeslaTower", "Tesla" -> {
                return new TeslaTower(p, tile);
            } case "WaveMotion" -> {
                return new WaveMotion(p, tile);
            } default -> {
                System.out.println("Could not get Turret of name:\n    " + type);
                return null;
            }
        }
    }
}