package main.projectiles;

import main.particles.Ouch;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static main.Main.*;
import static main.misc.Utilities.down60ToFramerate;

public class Frag extends Projectile {

    private final int LIFESPAN;
    private int lifeTimer;

    public Frag(PApplet p, float x, float y, float angle, Turret turret, int damage) {
        super(p, x, y, angle, turret);
        position = new PVector(x, y);
        size = new PVector(5, 5);
        radius = 5;
        maxSpeed = 600 + p.random(-150, 150);
        speed = maxSpeed;
        this.damage = damage;
        this.angle = angle;
        angularVelocity = down60ToFramerate(p.random(-15,15));
        sprite = staticSprites.get("darkMetalPt");
        hitSound = sounds.get("smallImpact");
        LIFESPAN = down60ToFramerate(15);
    }

    public void main(ArrayList<Projectile> projectiles, int i) {
        if (!paused) {
            lifeTimer++;
            trail();
            move();
        }
        displayPassB();
        collideEn();
        if (lifeTimer > LIFESPAN) dead = true;
        if (position.y - size.y > BOARD_HEIGHT + 100 || position.x - size.x > BOARD_WIDTH + 100 ||
                position.y + size.y < -100 || position.x + size.x < -100 || dead) {
            die();
        }
    }

    public void die() {
        projectiles.remove(this);
        particles.add(new Ouch(p,position.x,position.y,p.random(0,360),"greyPuff"));
    }
}