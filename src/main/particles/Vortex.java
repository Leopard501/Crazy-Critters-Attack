package main.particles;

import main.misc.Animator;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.FRAMERATE;
import static main.Main.animatedSprites;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.findAngle;
import static main.misc.Utilities.randomizeBy;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

public class Vortex extends Particle {

    public Vortex(PApplet p, PVector center, PVector displacement, float radius) {
        super(p, center.x, center.y, p.random(TWO_PI));
        position = PVector.add(center, displacement);
        angle = findAngle(center, position) + HALF_PI;
        size = new PVector(9, 9);
        maxSpeed = 150;
        speed = maxSpeed;
        displayAngle = angle;
        animation = new Animator(
                getResource("darkExDebrisPT", animatedSprites),
                (int) randomizeBy(p, radius / 5, 0.5f),
                false);
        angularVelocity = (speed / FRAMERATE) / radius;
        velocity = PVector.fromAngle(angle-HALF_PI);
    }

    @Override
    protected void move() {
        animation.update();
        if (animation.ended()) dead = true;

        angle += angularVelocity;
        displayAngle = angle;
        velocity = PVector.fromAngle(angle-HALF_PI);
        velocity.setMag(speed/FRAMERATE);
        position.add(velocity);
    }
}
