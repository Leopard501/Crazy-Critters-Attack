package main.enemies;

import main.particles.MiscParticle;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;
import static main.misc.Utilities.down60ToFramerate;
import static main.misc.Utilities.getRandomPointInRange;

public class EnragedGiant extends Enemy {

    public EnragedGiant(PApplet p, float x, float y) {
        super(p,x,y);
        size = new PVector(76,76);
        pfSize = 3;
        radius = 30;
        speed = 50;
        moneyDrop = 600;
        damage = 15;
        maxHp = 15000; //Hp
        hp = maxHp;
        hitParticle = "leafOuch";
        name = "enragedGiant";
        attackDmgFrames = new int[]{28};
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        betweenAttackFrames = down60ToFramerate(1);
        betweenWalkFrames = down60ToFramerate(1);
        corpseSize = new PVector(152,152);
        partSize = new PVector(68,68);
        corpseLifespan = 12;
        dieSound = sounds.get("bigLeaves");
        overkillSound = sounds.get("bigLeavesImpact");
        loadStuff();
    }

    @Override
    public void displayMain() {
        super.displayMain();

        if (p.random(5) < 1) {
            PVector pos = getParticlePosition();
            topParticles.add(new MiscParticle(p, pos.x, pos.y, p.random(360), "energy"));
        }
    }
}