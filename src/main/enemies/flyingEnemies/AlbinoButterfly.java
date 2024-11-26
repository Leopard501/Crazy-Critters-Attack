package main.enemies.flyingEnemies;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.moveSoundLoops;
import static main.Main.sounds;
import static main.misc.Utilities.down60ToFramerate;

public class AlbinoButterfly extends FlyingEnemy {

    public AlbinoButterfly(PApplet p, float x, float y) {
        super(p,x,y);
        size = new PVector(25,25);
        pfSize = 1;
        radius = 12.5f;
        speed = 55;
        moneyDrop = 30;
        damage = 2;
        maxHp = 100; //Hp
        hp = maxHp;
        hitParticle = HitParticle.glowOuch;
        name = "albinoButterfly";
        attackDmgFrames = new int[]{3};
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        betweenAttackFrames = down60ToFramerate(5);
        corpseSize = new PVector(25,25);
        partSize = new PVector(18, 18);
        betweenCorpseFrames = down60ToFramerate(4);
        overkillSound = sounds.get("squish");
        dieSound = sounds.get("crunch");
        attackSound = sounds.get("flap");
        moveSoundLoop = moveSoundLoops.get("smallWingbeats");
        loadStuff();
    }
}