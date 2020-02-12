package main.enemies;

import processing.core.PApplet;
import processing.core.PVector;

public class MidBug extends Enemy{
    public MidBug(PApplet p, float x, float y){
        super(p,x,y);
        size = new PVector(25,25);
        pfSize = 1; //1
        radius = 13;
        maxSpeed = .5f;
        speed = maxSpeed;
        moneyDrop = 10;
        twDamage = 5;
        maxHp = 40; //Hp <---------------------------
        hp = maxHp;
        hitParticle = "greenOuch";
        name = "midBug";
        numAttackFrames = 42;
        numMoveFrames = 8;
        betweenWalkFrames = 4;
        startFrame = 34; //attack start
        attackFrame = startFrame;
        loadSprites();
    }
}