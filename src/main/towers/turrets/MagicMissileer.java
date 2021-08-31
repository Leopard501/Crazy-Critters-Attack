package main.towers.turrets;

import main.damagingThings.projectiles.homing.ElectricMissile;
import main.damagingThings.projectiles.homing.MagicMissile;
import main.misc.Tile;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;
import static main.misc.Utilities.down60ToFramerate;
import static main.misc.Utilities.randomizeDelay;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class MagicMissileer extends Turret {

    public boolean additionalMissile;

    private float specialAngle;

    public MagicMissileer(PApplet p, Tile tile) {
        super(p,tile);
        name = "magicMissleer";
        size = new PVector(50,50);
        hasPriority = false;
        delay = randomizeDelay(p, 3);
        damage = 600;
        pjSpeed = 300;
        range = 200;
        betweenIdleFrames = down60ToFramerate(8);
        fireSound = sounds.get("magicMissleer");
        placeSound = sounds.get("crystalPlace");
        damageSound = sounds.get("crystalDamage");
        breakSound = sounds.get("crystalBreak");
        debrisType = "crystal";
        price = MAGIC_MISSILEER_PRICE;
        value = price;
        priority = 2; //strong

        setUpgrades();
        loadSprites();
        spawnParticles();
        playSoundRandomSpeed(p, placeSound, 1);
    }

    @Override
    protected void checkTarget() {
        getTargetEnemy();
        if (state == 0 && targetEnemy != null) { //if done animating
            state = 1;
            frame = 0;
            fire();
        }
    }

    @Override
    protected void spawnProjectiles(PVector position, float angle) {
        if (name.equals("magicSwarm")) {
            for (int i = 0; i < 12; i++) {
                projectiles.add(new MagicMissile(p,p.random(tile.position.x-size.x,tile.position.x),
                  p.random(tile.position.y-size.y,tile.position.y), p.random(0,TWO_PI), this,
                  getDamage(), (int)(p.random(0,2.99f)),tile.position));
            }
        } else if (name.equals("electricMissileer")) {
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 0, tile.position, effectDuration, effectLevel));
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 1, tile.position, effectDuration, effectLevel));
            projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 2, tile.position, effectDuration, effectLevel));
            if (additionalMissile) {
                projectiles.add(new ElectricMissile(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
                  getDamage(), (int) (p.random(0, 2.99f)), tile.position, effectDuration, effectLevel));
            }
        } else {
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 0, tile.position));
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 1, tile.position));
            projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
              getDamage(), 2, tile.position));
            if (additionalMissile) {
                projectiles.add(new MagicMissile(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(0, TWO_PI), this,
                  getDamage(), (int) (p.random(0, 2.99f)), tile.position));
            }
        }
    }

    private void fire() {
        playSoundRandomSpeed(p, fireSound, 1);
        spawnProjectiles(new PVector(0,0), angle);
    }

    @Override
    public void displayMain() {
        int displacement = 20;
        //shadow
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2 + 2, tile.position.y - size.y / 2 + 2);
        p.rotate(angle);
        p.tint(0,60);
        if (name.equals("magicSwarm")) {
            for (int i = 0; i < 3; i++) {
                p.pushMatrix();
                p.rotate(specialAngle + (i * (TWO_PI / 3)));
                p.image(fireFrames[5],(-size.x/2-offset) + displacement,-size.y/2-offset);
                p.popMatrix();
            }
        } else p.image(fireFrames[5],-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        //main
        p.pushMatrix();
        p.translate(tile.position.x - size.x / 2, tile.position.y - size.y / 2);
        p.rotate(angle);
        p.tint(255, tintColor, tintColor);
        if (name.equals("magicSwarm")) {
            for (int i = 0; i < 3; i++) {
                p.pushMatrix();
                p.rotate(specialAngle + (i * (TWO_PI / 3)));
                p.image(sprite,(-size.x/2-offset) + displacement,-size.y/2-offset);
                p.popMatrix();
            }
        } else p.image(sprite,-size.x/2-offset,-size.y/2-offset);
        p.popMatrix();
        p.tint(255);
        if (!paused) {
            if (specialAngle < TWO_PI) specialAngle += 0.01f;
            else specialAngle = 0;
        }
    }

    @Override
    protected void setUpgrades() {
        //price
        upgradePrices[0] = 750;
        upgradePrices[1] = 1250;
        upgradePrices[2] = 40000;

        upgradePrices[3] = 1250;
        upgradePrices[4] = 1500;
        upgradePrices[5] = 25000;
        //titles
        upgradeTitles[0] = "More range";
        upgradeTitles[1] = "More magic";
        upgradeTitles[2] = "static?";

        upgradeTitles[3] = "Faster Firing";
        upgradeTitles[4] = "More Missiles";
        upgradeTitles[5] = "Missile Swarm";
        //descriptions
        upgradeDescA[0] = "Increase";
        upgradeDescB[0] = "range";
        upgradeDescC[0] = "";

        upgradeDescA[1] = "Increase";
        upgradeDescB[1] = "damage";
        upgradeDescC[1] = "";

        upgradeDescA[2] = "split";
        upgradeDescB[2] = "into";
        upgradeDescC[2] = "bits";


        upgradeDescA[3] = "Increase";
        upgradeDescB[3] = "firerate";
        upgradeDescC[3] = "";

        upgradeDescA[4] = "Fire an";
        upgradeDescB[4] = "additional";
        upgradeDescC[4] = "missile";

        upgradeDescA[5] = "Fire a";
        upgradeDescB[5] = "swarm of";
        upgradeDescC[5] = "missiles";
        //icons
        upgradeIcons[0] = animatedSprites.get("upgradeIC")[6];
        upgradeIcons[1] = animatedSprites.get("upgradeIC")[8];
        upgradeIcons[2] = animatedSprites.get("upgradeIC")[1];

        upgradeIcons[3] = animatedSprites.get("upgradeIC")[7];
        upgradeIcons[4] = animatedSprites.get("upgradeIC")[14];
        upgradeIcons[5] = animatedSprites.get("upgradeIC")[39];
    }

    @Override
    protected void upgradeSpecial(int id) {
        if (id == 0) {
            switch (nextLevelA) {
                case 0:
                    range += 50;
                    break;
                case 1:
                    damage += 400;
                    break;
                case 2:
                    effectLevel = 5000;
                    effectDuration = 10;
                    damage *= 1.5f;
                    name = "electricMissileer";
                    break;
            }
        } if (id == 1) {
            switch (nextLevelB) {
                case 3:
                    delay -= 1;
                    break;
                case 4:
                    additionalMissile = true;
                    break;
                case 5:
                    range += 50;
                    delay -= 0.5f;
                    name = "magicSwarm";
                    break;
            }
        }
    }
}