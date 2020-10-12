package main.particles;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.spritesAnimH;
import static processing.core.PApplet.radians;

public class Pile extends Particle {
    public Pile(PApplet p, float x, float y, float angle, String type) {
        super(p,x,y,angle);
        size = new PVector(10,10);
        maxSpeed = 0;
        speed = 0;
        angleTwo = 0;
        lifespan = 500;
        sprites = spritesAnimH.get(type + "PilePT");
        currentSprite = (int) p.random(0,3.99f);
        numFrames = 4;
        velocity = new PVector(0,0);
        angularVelocity = 0;
    }

    void display() {
        if (lifespan <= 0) dead = true;
        p.tint(255,255*((float)lifespan / 500));
        lifespan--;
        angleTwo += radians(angularVelocity);
        p.pushMatrix();
        p.translate(position.x,position.y);
        p.rotate(angleTwo);
        p.image(sprites[currentSprite],-size.x/2,-size.y/2);
        p.tint(255);
        p.popMatrix();
    }
}