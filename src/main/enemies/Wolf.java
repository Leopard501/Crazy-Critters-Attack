package main.enemies;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.sounds;
import static main.misc.Utilities.down60ToFramerate;

public class Wolf extends Enemy {

    public Wolf(PApplet p, float x, float y) {
        super(p,x,y);
        size = new PVector(50,50);
        pfSize = 2;
        radius = 25;
        maxSpeed = 50;
        speed = maxSpeed;
        moneyDrop = 20;
        damage = 3;
        maxHp = 60; //Hp
        hp = maxHp;
        hitParticle = "redOuch";
        name = "wolf";
        attackStartFrame = 0;
        attackDmgFrames = new int[]{8};
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        betweenAttackFrames = down60ToFramerate(5);
        betweenWalkFrames = down60ToFramerate(10);
        attackFrame = attackStartFrame;
        corpseSize = size;
        partSize = new PVector(9,9);
        overkillSound = sounds.get("hissSquish");
        dieSound = sounds.get("hiss");
        loadStuff();
    }
}