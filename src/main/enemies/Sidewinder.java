package main.enemies;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.moveSoundLoops;
import static main.Main.sounds;
import static main.misc.Utilities.down60ToFramerate;

public class Sidewinder extends Enemy {

    public Sidewinder(PApplet p, float x, float y) {
        super(p,x,y);
        size = new PVector(25,25);
        pfSize = 1;
        radius = 12.5f;
        speed = 42;
        moneyDrop = 20;
        damage = 5;
        maxHp = 60; //Hp
        hp = maxHp;
        hitParticle = HitParticle.redOuch;
        name = "sidewinder";
        attackDmgFrames = new int[]{4};
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        betweenAttackFrames = down60ToFramerate(5);
        betweenWalkFrames = down60ToFramerate(2);
        corpseSize = size;
        partSize = new PVector(9,9);
        overkillSound = sounds.get("hissSquish");
        dieSound = sounds.get("hiss");
        attackSound = sounds.get("snakeStrike");
        moveSoundLoop = moveSoundLoops.get("rattle");
        loadStuff();
    }
}