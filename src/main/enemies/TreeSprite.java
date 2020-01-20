package main.enemies;

import processing.core.PApplet;
import processing.core.PVector;

public class TreeSprite extends Enemy{

    public TreeSprite(PApplet p, float x, float y){
        super(p,x,y);
        size = new PVector(25,25);
        pfSize = 1; //1
        radius = 15;
        maxSpeed = .4f;
        speed = maxSpeed;
        dangerLevel = 1;
        twDamage = 8; //2
        maxHp = 20; //Hp <---------------------------
        hp = maxHp;
        hitParticle = "redOuch";
        name = "treeSprite";
        numAttackFrames = 50;
        numMoveFrames = 58;
        startFrame = 28;
        attackFrame = startFrame;
        loadSprites();
    }
}