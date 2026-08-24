package main.particles;

import main.misc.Animator;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.animatedSprites;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.randomizeBy;
import static processing.core.PConstants.*;

public class Vortex extends Particle {

    private final float apsis;
    private final float argument;
    private final int lifespan;
    private final PVector center;

    private int age = 0;

    public Vortex(PApplet p, PVector center, float apsis, float argument) {
        super(p, center.x, center.y, p.random(TWO_PI));
        size = new PVector(9, 9);

        this.apsis = apsis;
        this.center = center;
        this.argument = argument;
        lifespan = Math.max((int) randomizeBy(p, apsis / 2, 0.2f), 1);

        PImage[] anim = getResource("darkExDebrisPT", animatedSprites);

        animation = new Animator(
                anim,
                lifespan / anim.length,
                false);
    }

    @Override
    protected void move() {
        animation.update();
        if (animation.ended()) dead = true;

        position = new PVector(
                (float) Math.cos(((double) age / lifespan + argument) * PI),
                (float) Math.sin(((double) age / lifespan + argument) * PI)
        ).mult((1 - ((float) age / lifespan)) * apsis)
                .add(center);

        displayAngle = ((float) age / lifespan + argument) * PI;

        age++;
    }
}
