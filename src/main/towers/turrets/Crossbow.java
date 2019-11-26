package main.towers.turrets;

import main.particles.Debris;
import main.projectiles.Bolt;
import main.towers.Tile;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;

public class Crossbow extends Turret{

    private int pierce;

    public Crossbow(PApplet p, Tile tile) {
        super(p,tile);
        name = "crossbow";
        size = new PVector(50,50);
        maxHp = 20;
        hp = maxHp;
        hit = false;
        delay = 210; //default: 210 frames
        delay += (round(p.random(-(delay/10),delay/10))); //injects 10% randomness so all don't fire at once
        delayTime = delay;
        pjSpeed = 24;
        error = 2; //set to 360 for a fun time. default: 2 degrees
        numFireFrames = 13;
        numLoadFrames = 81;
        fireFrames = new PImage[numFireFrames];
        loadFrames = new PImage[numLoadFrames];
        spriteType = 0;
        frame = 0;
        loadDelay = 0;
        loadDelayTime = 0;
        damage = 20;
        pierce = 2;
        loadSprites();
        debrisType = "wood";
        price = 100;
        value = price;
        priority = 1; //last
        nextLevelZero = 0;
        nextLevelOne = 2;
        setUpgrades();
    }

    public void fire(){ //needed to change projectile fired
        angle += radians(p.random(-error,error));
        delayTime = p.frameCount + delay; //waits this time before firing
        projectiles.add(new Bolt(p,tile.position.x-size.x/2,tile.position.y-size.y/2, angle, damage, pierce));
    }

    private void setUpgrades(){
        //special
        upgradeSpecial[0] = false;
        upgradeSpecial[1] = true;
        upgradeSpecial[2] = false;
        upgradeSpecial[3] = false;
        //damage
        upgradeDamage[0] = 10;
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
        upgradeError[0] = 0;
        upgradeError[1] = 0;
        upgradeError[2] = 0;
        upgradeError[3] = 0;
        //names
        upgradeNames[0] = name;
        upgradeNames[1] = name;
        upgradeNames[2] = name;
        upgradeNames[3] = name;
        //debris
        upgradeDebris[0] = "wood";
        upgradeDebris[1] = "wood";
        upgradeDebris[2] = "wood";
        upgradeDebris[3] = "wood";
        //titles
        upgradeTitles[0] = "+Sharpness";
        upgradeTitles[1] = "+Piercing";
        upgradeTitles[2] = "Faster Firing";
        upgradeTitles[3] = "Yet Faster Firing";
        //desc line one
        upgradeDescOne[0] = "+10";
        upgradeDescOne[1] = "Increase";
        upgradeDescOne[2] = "Increase";
        upgradeDescOne[3] = "Further";
        //desc line two
        upgradeDescTwo[0] = "Damage";
        upgradeDescTwo[1] = "Piercing";
        upgradeDescTwo[2] = "firerate";
        upgradeDescTwo[3] = "Increase";
        //desc line three
        upgradeDescThree[0] = "";
        upgradeDescThree[1] = "";
        upgradeDescThree[2] = "";
        upgradeDescThree[3] = "firerate";
        //icons
        upgradeIcons[0] = spritesAnimH.get("upgradeIC")[8];
        upgradeIcons[1] = spritesAnimH.get("upgradeIC")[9];
        upgradeIcons[2] = spritesAnimH.get("upgradeIC")[7];
        upgradeIcons[3] = spritesAnimH.get("upgradeIC")[10];
        //sprites
        upgradeSprites[0] = spritesH.get("stoneWallTW");
        upgradeSprites[1] = spritesH.get("metalWallTW");
        upgradeSprites[2] = spritesH.get("stoneWallTW");
        upgradeSprites[3] = spritesH.get("metalWallTW");
    }

    public void upgrade(int id){
        int nextLevel;
        if (id == 0){
            nextLevel = nextLevelZero;
        } else{
            nextLevel = nextLevelOne;
        }
        damage += upgradeDamage[nextLevel];
        delay += upgradeDelay[nextLevel];
        price += upgradePrices[nextLevel];
        value += upgradePrices[nextLevel];
        maxHp += upgradeHealth[nextLevel];
        hp += upgradeHealth[nextLevel];
        error += upgradeError[nextLevel];
        name = upgradeNames[nextLevel];
        debrisType = upgradeDebris[nextLevel];
        sprite = upgradeSprites[nextLevel];
        if (upgradeSpecial[nextLevel]){
            pierce += 2;
        }
        if (id == 0){
            nextLevelZero++;
        } else if (id == 1){
            nextLevelOne++;
        }
        if (id == 0){
            if (nextLevelZero < upgradeNames.length/2){
                upgradeIconZero.sprite = upgradeIcons[nextLevelZero];
            } else{
                upgradeIconZero.sprite = spritesAnimH.get("upgradeIC")[0];
            }
        }
        if (id == 1){
            if (nextLevelOne < upgradeNames.length){
                upgradeIconOne.sprite = upgradeIcons[nextLevelOne];
            } else{
                upgradeIconOne.sprite = spritesAnimH.get("upgradeIC")[0];
            }
        }
        int num = floor(p.random(30,50)); //shower debris
        for (int j = num; j >= 0; j--){
            particles.add(new Debris(p,(tile.position.x-size.x/2)+p.random((size.x/2)*-1,size.x/2), (tile.position.y-size.y/2)+p.random((size.y/2)*-1,size.y/2), p.random(0,360), debrisType));
        }
    }

    public void display(){
        p.tint(255,tintColor,tintColor);
        p.image(sBase,tile.position.x-size.x,tile.position.y-size.y);
        p.pushMatrix();
        p.translate(tile.position.x-size.x/2,tile.position.y-size.y/2);
        p.rotate(angle);
        p.image(sprite,-size.x/2-2,-size.y/2-2);
        p.popMatrix();
        p.tint(255,255,255);
    }
}