package main.enemies.burrowingEnemies;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.moveSoundLoops;
import static main.Main.sounds;
import static main.misc.Utilities.down60ToFramerate;

public class Shark extends BurrowingEnemy {

    public Shark(PApplet p, float x, float y) {
        super(p,x,y);
        size = new PVector(25,25);
        pfSize = 1;
        radius = 12.5f;
        speed = 42;
        moneyDrop = 75;
        damage = 8;
        maxHp = 175; //Hp
        hp = maxHp;
        hitParticle = HitParticle.redOuch;
        name = "shark";
        attackDmgFrames = new int[]{4};
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        betweenAttackFrames = down60ToFramerate(10);
        partSize = new PVector(20, 20);
        corpseSize = new PVector(25,25);
        overkillSound = sounds.get("squish");
        dieSound = sounds.get("crunch");
        attackSound = sounds.get("bite");
        moveSoundLoop = moveSoundLoops.get("littleDig");
        loadStuff();
    }
}