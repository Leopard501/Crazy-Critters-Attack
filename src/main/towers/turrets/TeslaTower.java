package main.towers.turrets;

import main.enemies.Enemy;
import main.misc.Tile;
import main.projectiles.arcs.Arc;
import main.projectiles.shockwaves.LightningShockwave;
import main.sound.FadeSoundLoop;
import main.sound.SoundWithAlts;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;

import static main.Main.*;
import static main.misc.Utilities.down60ToFramerate;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class TeslaTower extends Turret {

    private static final Color SPECIAL_COLOR = new Color(0xc0f1fc);

    public static String pid = "M3-225-400-2";
    public static String description =
            "Creates electric arcs that can jump from critter to critter. " +
                    "Short range, but arcs can chain to many critters.";
    public static char shortcut = 'C';
    public static String title1 = "Tesla Tower";
    public static String title2 = null;
    public static int price = 1500;

    public int arcLength;

    private boolean lightning;
    private boolean highPower;

    private final SoundWithAlts thunderSound;

    public TeslaTower(PApplet p, Tile tile) {
        super(p,tile);
        name = "tesla";
        delay = 2f;
        damage = 400;
        arcLength = 2;
        pjSpeed = -1;
        range = 225;
        betweenIdleFrames = down60ToFramerate(3);
        material = Material.metal;
        basePrice = price;
        damageSound = sounds.get("metalDamage");
        breakSound = sounds.get("metalBreak");
        placeSound = sounds.get("metalPlace");
        fireSound = sounds.get("teslaFire");
        thunderSound = soundsWithAlts.get("thunder");
        titleLines = new String[]{"Tesla Tower"};
        extraInfo.add((arg) -> selection.displayInfoLine(arg, SPECIAL_COLOR, "Arc Chain", arcLength + ""));
    }

    @Override
    protected void checkTarget() {
        getTargetEnemy();
        if (state == State.Idle && targetEnemy != null) { //if done animating
            state = State.Fire;
            frame = 0;
            fire();
        }
    }

    @Override
    protected void spawnProjectiles(PVector position, float angle) {}

    protected void fire() {
        if (lightning) {
            thunderSound.playRandomWithRandomSpeed(1);
            PVector targetPosition = new PVector(targetEnemy.position.x, targetEnemy.position.y);
            PVector myPosition = new PVector(tile.position.x - size.x / 2, tile.position.y - size.y / 2);
            shockwaves.add(new LightningShockwave(p, targetPosition.x, targetPosition.y, 150, damage, this));
            //damaging arcs
            for (int i = 0; i < 3; i++) {
                arcs.add(new Arc(
                        p, targetPosition.copy(), this,
                        getDamage() / 2, arcLength, 600, 200,
                        Priority.values()[i],
                        25, 10, 10, 1, 1, 16,
                        new Color(215, 242, 248), Enemy.DamageType.electricity,
                        targetEnemy
                ));
            } //decorative arcs
            for (int i = 0; i < p.random(5, 10); i++) {
                arcs.add(Arc.presets(
                        "tesla",
                        p, targetPosition.copy(), this,
                        0, arcLength, 200, Priority.None
                ));
            } //decorative self arcs
            for (int i = 0; i < 3; i++) {
                arcs.add(Arc.presets(
                        "tesla",
                        p, myPosition.copy(), this,
                        0, arcLength, 100, Priority.None
                ));
            }
        } else if (!highPower) {
            playSoundRandomSpeed(p, fireSound, 1);
            PVector position = new PVector(tile.position.x - 25, tile.position.y - 25);
            arcs.add(Arc.presets(
                    "tesla",
                    p, position.copy(), this,
                    getDamage(), arcLength, getRange(), priority
            ));
        }
    }

    @Override
    public void update() {
        if (hp <= 0) {
            die(false);
            tile.tower = null;
        }
        updateBoosts();
        if (highPower && !isPaused && !machine.dead) {
            PVector position = new PVector(tile.position.x - 25, tile.position.y - 25);
            arcs.add(new Arc(
                    p, position.copy(), this,
                    getDamage(), arcLength, getRange(), getRange() / 10,
                    priority,
                    15, 30, 3, 3, 30, 200,
                    new Color(0xE12E0E), Enemy.DamageType.energy
            ));
            FadeSoundLoop electricity = fadeSoundLoops.get("electricity");
            if (electricity.targetVolume < 0.2f) electricity.setTargetVolume(0.2f);
        } else {
            if (!enemies.isEmpty() && !machine.dead && !isPaused) checkTarget();
        }
        if (p.mousePressed && boardMousePosition.x < tile.position.x && boardMousePosition.x > tile.position.x - size.x
                && boardMousePosition.y < tile.position.y
                && boardMousePosition.y > tile.position.y - size.y
                && alive && !isPaused) {
            selection.swapSelected(tile.id);
        }
    }

    @Override
    public void displayTop() {
        //shadow
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2 + 2, tile.position.y - size.y / 2 + 2);
        p.rotate(angle);
        p.tint(0,60);
        //using this sprite so that static doesn't have a shadow
        p.image(loadFrames[0],-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        //main
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2, tile.position.y - size.y / 2);
        p.rotate(angle);
        p.tint(255, tintColor, tintColor);
        if (sprite != null) p.image(sprite, -size.x / 2 - offset, -size.y / 2 - offset);
        else p.image(loadFrames[loadFrames.length - 1],-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        p.tint(255);
    }

    @Override
    protected void setUpgrades(){
        //price
        upgradePrices[0] = 800;
        upgradePrices[1] = 1000;
        upgradePrices[2] = 12_000;

        upgradePrices[3] = 600;
        upgradePrices[4] = 1200;
        upgradePrices[5] = 10_000;
        //titles
        upgradeTitles[0] = "Extra Arcing";
        upgradeTitles[1] = "Powerful Arcing";
        upgradeTitles[2] = "Call Lightning";

        upgradeTitles[3] = "Faster Recharge";
        upgradeTitles[4] = "High Voltage";
        upgradeTitles[5] = "Perma-arc";
        //description
        upgradeDescA[0] = "Increase";
        upgradeDescB[0] = "arc chain";
        upgradeDescC[0] = "& range";

        upgradeDescA[1] = "Increase";
        upgradeDescB[1] = "arc chain";
        upgradeDescC[1] = "& range";

        upgradeDescA[2] = "Calls";
        upgradeDescB[2] = "lightning";
        upgradeDescC[2] = "";


        upgradeDescA[3] = "Increase";
        upgradeDescB[3] = "recharge";
        upgradeDescC[3] = "rate";

        upgradeDescA[4] = "Increase";
        upgradeDescB[4] = "damage";
        upgradeDescC[4] = "";

        upgradeDescA[5] = "Vastly";
        upgradeDescB[5] = "increase";
        upgradeDescC[5] = "firerate";
        //icons
        upgradeIcons[0] = animatedSprites.get("upgradeIC")[1];
        upgradeIcons[1] = animatedSprites.get("upgradeIC")[2];
        upgradeIcons[2] = animatedSprites.get("upgradeIC")[33];

        upgradeIcons[3] = animatedSprites.get("upgradeIC")[7];
        upgradeIcons[4] = animatedSprites.get("upgradeIC")[34];
        upgradeIcons[5] = animatedSprites.get("upgradeIC")[59];
    }

    @Override
    protected void upgradeEffect(int id) {
        if (id == 0) {
            switch (nextLevelA) {
                case 0, 1 -> {
                    arcLength++;
                    range += 25;
                } case 2 -> {
                    range = 5000;
                    damage += 2400;
                    delay += 4;
                    arcLength++;
                    lightning = true;
                    material = Material.crystal;
                    placeSound = sounds.get("crystalPlace");
                    damageSound = sounds.get("crystalDamage");
                    breakSound = sounds.get("crystalBreak");
                    name = "teslaLightning";
                    betweenFireFrames = 2;
                    titleLines = new String[]{"Lightning Caller"};
                    extraInfo.add((arg) -> selection.displayInfoLine(arg, SPECIAL_COLOR, "Lightning", null));
                    loadSprites();
                }
            }
        } if (id == 1) {
            switch (nextLevelB) {
                case 3 -> delay -= 1;
                case 4 -> damage += 200;
                case 5 -> {
                    delay = 0;
                    highPower = true;
                    betweenIdleFrames = 3;
                    damage /= 8;
                    name = "teslaHighPower";
                    material = Material.darkMetal;
                    titleLines = new String[]{"The Demon", "Circuit"};
                    extraInfo.clear();
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            new Color(0xFD5454), "Arc Chain", arcLength + ""));
                    loadSprites();
                }
            }
        }
    }
}