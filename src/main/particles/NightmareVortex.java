package main.particles;

import main.misc.Animator;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.animatedSprites;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.randomizeBy;
import static processing.core.PConstants.TWO_PI;

public class NightmareVortex extends Particle {

    private final float end;
    private final float argument;
    private final float speedMult;
    private final int lifespan;
    private final PVector center;

    private int age;

    public NightmareVortex(PApplet p, PVector center, float end) {
        super(p, center.x, center.y, p.random(TWO_PI));
        size = new PVector(9, 9);

        boolean isSmall = p.random(2) < 1;

        this.end = end;
        this.center = center;
        argument = p.random(TWO_PI);
        lifespan = Math.max((int) randomizeBy(p, this.end / 2, 0.2f), 1);

        PImage[] anim;
        if (isSmall) {
            anim = getResource("decayMiscPT", animatedSprites);
            speedMult = 0.5f + p.random(-0.05f, 0.05f);
        } else {
            anim = getResource("decayExDebrisPT", animatedSprites);
            speedMult = 1f + p.random(-0.1f, 0.1f);
        }

        animation = new Animator(
                anim,
                lifespan / anim.length,
                false);

        age = 12;
    }

    @Override
    protected void move() {
        animation.update();
        if (animation.ended()) dead = true;

        position = new PVector(
                (float) Math.cos(((double) age / lifespan + argument) * TWO_PI * speedMult),
                (float) Math.sin(((double) age / lifespan + argument) * TWO_PI * speedMult)
        ).mult(((float) age / lifespan) * end)
                .add(center);

        displayAngle = ((float) age / lifespan + argument) * TWO_PI * speedMult;

        age++;
    }
}
