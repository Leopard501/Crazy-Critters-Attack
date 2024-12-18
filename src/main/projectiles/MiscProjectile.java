package main.projectiles;

import main.particles.Ouch;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;
import static main.misc.Utilities.down60ToFramerate;

public class MiscProjectile extends Projectile {

    public PImage[] sprites; //alternate sprites, passed in

    public MiscProjectile(PApplet p, float x, float y, float angle, Turret turret, int spriteType, int damage, int maxSpeed, int maxRotation) {
        super(p, x, y, angle, turret);
        position = new PVector(x, y);
        size = new PVector(10, 10);
        radius = 10;
        this.maxSpeed = maxSpeed;
        speed = maxSpeed;
        this.damage = damage;
        this.angle = angle;
        angleTwo = angle;
        angularVelocity = down60ToFramerate(p.random(-maxRotation, maxRotation)); //degrees mode
        sprites = animatedSprites.get("miscPJ");
        sprite = sprites[spriteType];
        hitSound = sounds.get("smallImpact");
    }

    @Override
    public void die() {
        topParticles.add(new Ouch(p,position.x,position.y,p.random(0,360),"greyPuff"));
        projectiles.remove(this);
    }
}