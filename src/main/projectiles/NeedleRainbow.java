package main.projectiles;

import main.particles.Ouch;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;

public class NeedleRainbow extends Projectile {

    private int range;

    public NeedleRainbow(PApplet p, float x, float y, float angle, Turret turret, int damage, int range) {
        super(p, x, y, angle, turret);
        position = new PVector(x, y);
        size = new PVector(2, 17);
        radius = 10;
        maxSpeed = 1100;
        speed = maxSpeed;
        this.damage = damage;
        this.angle = angle;
        this.range = (int) (range * p.random(0.8f, 1.2f));
        sprite = staticSprites.get("needleRainbowPj");
        particleTrail = "electricity";
    }

    @Override
    public void update() {
        trail();
        display();
        move();
        checkCollision();
        if (position.y - size.y > BOARD_HEIGHT + 100 ||
                position.x - size.x > BOARD_WIDTH + 100 ||
                position.y + size.y < -100 ||
                position.x + size.x < -100) {
            dead = true;
        }
        if (range < 0) dead = true;
        if (dead) {
            die();
            boostedDieParticles();
        }
    }

    @Override
    public void move() {
        velocity.setMag(getBoostedSpeed());
        range -= (int) (speed / FRAMERATE);
        position.add(velocity);
    }

    @Override
    public void die() {
        topParticles.add(new Ouch(p,position.x,position.y,angleTwo,"rainbowPuff"));
        projectiles.remove(this);
    }
}