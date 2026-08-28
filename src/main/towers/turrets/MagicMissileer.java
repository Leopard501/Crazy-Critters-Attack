package main.towers.turrets;

import main.projectiles.arcs.Arc;
import main.projectiles.homing.ElectricMissile;
import main.projectiles.homing.MagicMissile;
import main.misc.Tile;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;

import static main.Main.*;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.*;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class MagicMissileer extends Turret {

    private static final Color SPECIAL_COLOR = new Color(0x93f8b8);

    public static String pid = "C1-200-2000-2";
    public static String description =
            "Fires three magic missiles that home in on different critters. " +
                    "Short activation range, but missiles can travel very far.";
    public static char shortcut = 'R';
    public static String title1 = "Magic Tower";
    public static String title2 = null;
    public static int price = 5000;

    public int additionalMissiles;

    private float specialAngle;

    private enum ElectricComponent {
        OuterRing(
                getResource("magicMissleerElectricOuterRingIdleTr", staticSprites),
                getResource("magicMissleerElectricOuterRingFireTR", animatedSprites),
                getResource("magicMissleerElectricOuterRingLoadTR", animatedSprites)),
        InnerRing(
                getResource("magicMissleerElectricInnerRingIdleTr", staticSprites),
                getResource("magicMissleerElectricInnerRingFireTR", animatedSprites),
                getResource("magicMissleerElectricInnerRingLoadTR", animatedSprites)),
        Core(
                getResource("magicMissleerElectricCoreIdleTr", staticSprites),
                getResource("magicMissleerElectricCoreFireTR", animatedSprites),
                getResource("magicMissleerElectricCoreLoadTR", animatedSprites));

        final PImage idleSprite;
        final PImage[] fireAnimation;
        final PImage[] loadAnimation;

        ElectricComponent(PImage idle, PImage[] fire, PImage[] load) {
            idleSprite = idle;
            fireAnimation = fire;
            loadAnimation = load;
        }

        PImage getSprite(Turret.State state, int frame) {
           switch (state) {
               case Fire -> {
                   frame = min(frame, fireAnimation.length - 1);
                   return fireAnimation[frame];
               } case Load -> {
                    frame = min(frame, loadAnimation.length - 1);
                    return loadAnimation[frame];
               } default -> {
                   return idleSprite;
               }
           }
        }
    }

    public MagicMissileer(PApplet p, Tile tile) {
        super(p,tile);
        name = "magicMissleer";
        size = new PVector(50,50);
        hasPriority = false;
        delay = 2f;
        damage = 2000;
        pjSpeed = 300;
        range = 200;
        betweenIdleFrames = down60ToFramerate(8);
        fireSound = sounds.get("magicMissleer");
        placeSound = sounds.get("crystalPlace");
        damageSound = sounds.get("crystalDamage");
        breakSound = sounds.get("crystalBreak");
        material = Material.crystal;
        basePrice = price;
        priority = Priority.Strong;
        titleLines = new String[]{"Magic Tower"};
        extraInfo.add((arg) -> selection.displayInfoLine(arg, SPECIAL_COLOR, "Homing", null));
        extraInfo.add((arg) -> selection.displayInfoLine(arg, SPECIAL_COLOR, "Magic Missiles", 3 + additionalMissiles + ""));
    }

    @Override
    protected void loadSprites() {
        if (name.equals("magicMissleerElectric")) {
            sBase = staticSprites.get("magicMissleerBaseTR");
            idleSprite = staticSprites.get(name + "IdleTR");
            idleFrames = new PImage[]{staticSprites.get(name + "IdleTR")};

            fireFrames = animatedSprites.get(name + "CoreFireTR");
            loadFrames = animatedSprites.get(name + "CoreLoadTR");
            return;
        }
        String tempName = name;
        if (tempName.equals("magicMissleerSwarm")) tempName = "magicMissleer";
        sBase = staticSprites.get(tempName + "BaseTr");
        fireFrames = animatedSprites.get(tempName + "FireTR");
        loadFrames = animatedSprites.get(tempName + "LoadTR");
        if (animatedSprites.containsKey(tempName + "IdleTR")) {
            idleFrames = animatedSprites.get(tempName + "IdleTR");
            idleSprite = idleFrames[0];
        }
        else {
            idleSprite = staticSprites.get(tempName + "IdleTr");
            idleFrames = new PImage[]{idleSprite};
        }
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
    protected void spawnProjectiles(PVector position, float angle) {
       if (name.equals("magicMissleerElectric")) {
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Close, tile.position, effectDuration, effectLevel, pjSpeed));
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Far, tile.position, effectDuration, effectLevel, pjSpeed));
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Strong, tile.position, effectDuration, effectLevel, pjSpeed));
            for (int i = 0; i < additionalMissiles; i++) {
                projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
                  getDamage(), Priority.None, tile.position, effectDuration, effectLevel, pjSpeed));
            }
        } else {
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Close, tile.position, pjSpeed));
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Far, tile.position, pjSpeed));
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), Priority.Strong, tile.position, pjSpeed));
            for (int i = 0; i < additionalMissiles; i++) {
                projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
                  getDamage(),Priority.None, tile.position, pjSpeed));
            }
        }
    }

    private void fire() {
        playSoundRandomSpeed(p, fireSound, 1);
        spawnProjectiles(new PVector(0,0), angle);
        if (name.equals("magicMissleerElectric")) {
            for (int i = 0; i < 3; i++) {
                arcs.add(Arc.presets(
                        "electrified",
                        p, getCenter().copy(), this,
                        0, 1, (int) p.random(20, 100),
                        Priority.None
                ));
            }
        }
    }

    @Override
    public void displayBase() {
        if (name.equals("magicMissleerElectric")) return;
        p.tint(255, tintColor, tintColor);
        if (sBase != null) p.image(sBase, tile.position.x - size.x, tile.position.y - size.y);
        p.tint(255, 255, 255);
    }

    @Override
    public void displayTop() {
        int displacement = 20;
        //shadow
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2 + 2, tile.position.y - size.y / 2 + 2);
        p.rotate(angle);
        p.tint(0,60);
        if (name.equals("magicMissleerSwarm")) {
            for (int i = 0; i < 3; i++) {
                p.pushMatrix();
                p.rotate(specialAngle + (i * (TWO_PI / 3)));
                p.image(fireFrames[5],(-size.x/2-offset) + displacement,-size.y/2-offset);
                p.popMatrix();
            }
        } else if (name.equals("magicMissleerElectric")) {
            p.image(ElectricComponent.Core.idleSprite, (-size.x/2-offset),-size.y/2-offset);
            p.pushMatrix();
            p.rotate(specialAngle);
            p.image(ElectricComponent.InnerRing.idleSprite, (-size.x/2-offset),-size.y/2-offset);
            p.popMatrix();
            p.pushMatrix();
            p.rotate(-specialAngle);
            p.image(ElectricComponent.OuterRing.idleSprite, (-size.x/2-offset),-size.y/2-offset);
            p.popMatrix();
        } else p.image(fireFrames[5],-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        //main
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2, tile.position.y - size.y / 2);
        p.rotate(angle);
        p.tint(255, tintColor, tintColor);
        if (name.equals("magicMissleerSwarm") && sprite != null) {
            for (int i = 0; i < 3; i++) {
                p.pushMatrix();
                p.rotate(specialAngle + (i * (TWO_PI / 3)));
                p.image(sprite,(-size.x/2-offset) + displacement,-size.y/2-offset);
                p.popMatrix();
            }
        } else if (name.equals("magicMissleerElectric")) {
            int frame = this.frame;
            if (state == State.Load) frame = compressedLoadFrames.get(this.frame);

            p.image(ElectricComponent.Core.getSprite(state, frame), (-size.x/2-offset),-size.y/2-offset);
            p.pushMatrix();
            p.rotate(specialAngle);
            p.image(ElectricComponent.InnerRing.getSprite(state, frame), (-size.x/2-offset),-size.y/2-offset);
            p.popMatrix();
            p.pushMatrix();
            p.rotate(-specialAngle);
            p.image(ElectricComponent.OuterRing.getSprite(state, frame), (-size.x/2-offset),-size.y/2-offset);
            p.popMatrix();
        } else if (sprite != null) p.image(sprite,-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        p.tint(255);

        if (!isPaused) {
            float specialRotationSpeed = 0.01f;
            if (name.equals("magicMissleerElectric")) {
                specialRotationSpeed = switch (state) {
                    case Idle -> 0.03f;
                    case Fire -> 0.03f * (1 - (frame / (float) fireFrames.length));
                    case Load -> 0.03f * (frame / (float) compressedLoadFrames.size());
                };

                if (p.random(25) < 1 && state == State.Idle)
                    arcs.add(Arc.presets(
                            "electrified",
                            p, getCenter().copy(), this,
                            0, 1, (int) p.random(20, 100),
                            Priority.None
                    ));
            }
            if (specialAngle < TWO_PI) specialAngle += specialRotationSpeed;
            else specialAngle = 0;

        }
    }

    @Override
    protected void setUpgrades() {
        //price
        upgradePrices[0] = 2500;
        upgradePrices[1] = 5000;
        upgradePrices[2] = 40000;

        upgradePrices[3] = 4000;
        upgradePrices[4] = 6000;
        upgradePrices[5] = 30000;
        //titles
        upgradeTitles[0] = "More Range";
        upgradeTitles[1] = "More Magic";
        upgradeTitles[2] = "Electrifying";

        upgradeTitles[3] = "More Firing";
        upgradeTitles[4] = "More Missiles";
        upgradeTitles[5] = "Missile Swarm";
        //descriptions
        upgradeDescA[0] = "Increase";
        upgradeDescB[0] = "range";
        upgradeDescC[0] = "";

        upgradeDescA[1] = "Increase";
        upgradeDescB[1] = "damage";
        upgradeDescC[1] = "";

        upgradeDescA[2] = "Electrify";
        upgradeDescB[2] = "critters";
        upgradeDescC[2] = "";


        upgradeDescA[3] = "Increase";
        upgradeDescB[3] = "firerate";
        upgradeDescC[3] = "";

        upgradeDescA[4] = "Fire more";
        upgradeDescB[4] = "missiles";
        upgradeDescC[4] = "";

        upgradeDescA[5] = "Fire a";
        upgradeDescB[5] = "swarm of";
        upgradeDescC[5] = "missiles";
        //icons
        upgradeIcons[0] = animatedSprites.get("upgradeIC")[60];
        upgradeIcons[1] = animatedSprites.get("upgradeIC")[61];
        upgradeIcons[2] = animatedSprites.get("upgradeIC")[40];

        upgradeIcons[3] = animatedSprites.get("upgradeIC")[7];
        upgradeIcons[4] = animatedSprites.get("upgradeIC")[14];
        upgradeIcons[5] = animatedSprites.get("upgradeIC")[39];
    }

    @Override
    protected void upgradeEffect(int id) {
        if (id == 0) {
            switch (nextLevelA) {
                case 0 -> {
                    range += 50;
                    pjSpeed += 50;
                } case 1 -> damage += 1000;
                case 2 -> {
                    effectLevel = 5000;
                    effectDuration = 10;
                    damage *= 5;
                    name = "magicMissleerElectric";
                    material = Material.darkMetal;
                    placeSound = sounds.get("titaniumPlace");
                    breakSound = sounds.get("titaniumBreak");
                    damageSound = sounds.get("titaniumDamage");
                    titleLines = new String[]{"Electric Tower"};
                    effect = "electrified";

                    Color specialColor = new Color(0xFFFD83);
                    extraInfo.clear();
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            specialColor, "Homing", null));
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            specialColor, "Magic Missiles", 3 + additionalMissiles + ""));
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            specialColor, "Electrification:", null));
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            specialColor, "DPS", nfc(((int) (effectLevel / 0.5f)))));
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            specialColor, "Duration", nf(effectDuration, 1, 1) + "s"));
                    loadSprites();
                }
            }
        } if (id == 1) {
            switch (nextLevelB) {
                case 3 -> delay -= 1;
                case 4 -> additionalMissiles = 2;
                case 5 -> {
                    pjSpeed += 50;
                    range += 50;
                    name = "magicMissleerSwarm";
                    additionalMissiles = 9;
                    titleLines = new String[]{"Mythic Tower"};
                }
            }
        }
    }
}