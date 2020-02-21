package main.towers.turrets;

import main.projectiles.EnergyBlast;
import main.towers.Tile;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;
import static main.misc.MiscMethods.updateTowerArray;
import static processing.core.PConstants.HALF_PI;

public class EnergyBlaster extends Turret{

    private int effectRadius;
    private boolean bigExplosion;

    public EnergyBlaster(PApplet p, Tile tile) {
        super(p,tile);
        offset = 11;
        name = "energyBlaster";
        size = new PVector(50,50);
        maxHp = 20;
        hp = maxHp;
        hit = false;
        delay = 240; //240 frames
        delay += (round(p.random(-(delay/10f),delay/10f))); //injects 10% randomness so all don't fire at once
        delayTime = delay;
        damage = 30;
        pjSpeed = 16;
        error = 3; //3 degrees
        numFireFrames = 14; //14
        numLoadFrames = 42; //42
        fireFrames = new PImage[numFireFrames];
        loadFrames = new PImage[numLoadFrames];
        spriteType = 0;
        effectRadius = 25;
        bigExplosion = false;
        loadSprites();
        debrisType = "metal";
        price = 150;
        value = price;
        priority = 2; //strong
        nextLevelA = 0;
        nextLevelB = 2;
        setUpgrades();
        updateTowerArray();
    }

    public void fire() { //needed to change projectile fired
        float angleB = angle;
        angleB += radians(p.random(-error,error));
        delayTime = p.frameCount + delay; //waits this time before firing
        PVector spp = new PVector(tile.position.x-size.x/2,tile.position.y-size.y/2);
        PVector spa = PVector.fromAngle(angleB-HALF_PI);
        spa.setMag(40);
        spp.add(spa);
        projectiles.add(new EnergyBlast(p,spp.x,spp.y, angleB, this, damage, effectRadius, bigExplosion));
    }

    private void setUpgrades(){
        //damage
        upgradeDamage[0] = 0;
        upgradeDamage[1] = 0;
        upgradeDamage[2] = 0;
        upgradeDamage[3] = 0;
        //delay (firerate)
        upgradeDelay[0] = 0;
        upgradeDelay[1] = 0;
        upgradeDelay[2] = -25;
        upgradeDelay[3] = -35;
        //price
        upgradePrices[0] = 50;
        upgradePrices[1] = 100;
        upgradePrices[2] = 50;
        upgradePrices[3] = 100;
        //heath
        upgradeHealth[0] = 0;
        upgradeHealth[1] = 0;
        upgradeHealth[2] = 0;
        upgradeHealth[3] = 0;
        //error (accuracy)
        upgradeError[0] = -1.5f;
        upgradeError[1] = 0;
        upgradeError[2] = 0;
        upgradeError[3] = 0;
        //names
        upgradeNames[0] = name;
        upgradeNames[1] = name;
        upgradeNames[2] = name;
        upgradeNames[3] = name;
        //debris
        upgradeDebris[0] = "metal";
        upgradeDebris[1] = "metal";
        upgradeDebris[2] = "metal";
        upgradeDebris[3] = "metal";
        //titles
        upgradeTitles[0] = "More Precise";
        upgradeTitles[1] = "Splashier";
        upgradeTitles[2] = "Faster Firing";
        upgradeTitles[3] = "Yet Faster Firing";
        //desc line one
        upgradeDescA[0] = "Increase";
        upgradeDescA[1] = "increase";
        upgradeDescA[2] = "Increase";
        upgradeDescA[3] = "further";
        //desc line two
        upgradeDescB[0] = "accuracy";
        upgradeDescB[1] = "explosion";
        upgradeDescB[2] = "firerate";
        upgradeDescB[3] = "increase";
        //desc line three
        upgradeDescC[0] = "";
        upgradeDescC[1] = "radius";
        upgradeDescC[2] = "";
        upgradeDescC[3] = "firerate";
        //icons
        upgradeIcons[0] = spritesAnimH.get("upgradeIC")[5];
        upgradeIcons[1] = spritesAnimH.get("upgradeIC")[12];
        upgradeIcons[2] = spritesAnimH.get("upgradeIC")[7];
        upgradeIcons[3] = spritesAnimH.get("upgradeIC")[10];
        //sprites
        upgradeSprites[0] = spritesH.get("stoneWallTW");
        upgradeSprites[1] = spritesH.get("metalWallTW");
        upgradeSprites[2] = spritesH.get("stoneWallTW");
        upgradeSprites[3] = spritesH.get("metalWallTW");
    }

    public void upgradeSpecial() {
        if (nextLevelA == 1) {
            effectRadius += 20;
            bigExplosion = true;
        }
    }

    public void updateSprite() {}
}