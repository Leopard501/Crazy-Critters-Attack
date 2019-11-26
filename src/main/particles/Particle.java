package main.particles;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

import static main.Main.*;
import static processing.core.PApplet.radians;
import static processing.core.PConstants.HALF_PI;

public abstract class Particle {

    public PApplet p;

    public PVector position;
    public PVector size;
    public PVector target;
    public float maxSpeed;
    public float speed;
    public PImage[] sprites;
    PVector velocity;
    public float angle;
    float angleTwo;
    float angularVelocity;
    boolean dead;
    int lifespan;
    int numFrames;
    int currentSprite;
    public PImage sprite;
    boolean animated;
    public int delay;
    int delayTime;

    Particle(PApplet p, float x, float y, float angle) {
        this.p = p;

        position = new PVector(x, y);
        size = new PVector(5, 5);
        speed = (float) 1;
        angleTwo = angle;
        angularVelocity = 0; //degrees mode
        lifespan = 60; //in frames
        lifespan += (PApplet.round(p.random(-(lifespan /4), lifespan /4))); //injects 10% randomness so all don't die at once
        numFrames = 1;
        currentSprite = 0;
        animated = false;
        delay = lifespan /numFrames;
        delayTime = p.frameCount + delay;
        sprite = spritesH.get("nullPt");
        velocity = PVector.fromAngle(angle-HALF_PI);
    }

    public void main(ArrayList<Particle> particles, int i){
        if (position.y - size.y > BOARD_HEIGHT || position.x - size.x > BOARD_WIDTH || position.y + size.y < 0 || position.x + size.x < 0){ //if crossed edge, delete
            dead = true;
        }
        if (dead){
            particles.remove(i);
        }
        display();
        move();
    }

    private void display(){ //move and rotate whole grid before displaying, than reset
        if (animated){
            if (p.frameCount - delayTime >= delay){
                if (currentSprite == numFrames-1){
                    dead = true;
                }
                else{
                    currentSprite++;
                    delayTime = p.frameCount + delay;
                }
            }
            angleTwo += radians(angularVelocity);
            p.pushMatrix();
            p.translate(position.x,position.y);
            p.rotate(angleTwo);
            p.image(sprites[currentSprite],-size.x/2,-size.y/2);
            p.popMatrix();
        }
        else{
            if (p.frameCount - delayTime >= delay){
                dead = true;
            }
            angleTwo += radians(angularVelocity);
            p.pushMatrix();
            p.translate(position.x,position.y);
            p.rotate(angleTwo);
            p.image(sprite,-size.x/2,-size.y/2);
            p.popMatrix();
        }
    }

    private void move(){
        velocity.setMag(speed);
        position.add(velocity);
    }
}