package main.projectiles;

import main.enemies.Enemy;
import main.particles.MiscParticle;
import main.projectiles.shockwaves.NuclearShockwave;
import main.particles.ExplosionDebris;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;

public class NuclearBlast extends Projectile {

    public NuclearBlast(PApplet p, float x, float y, float angle, Turret turret, int damage, int effectRadius, int maxSpeed) {
        super(p, x, y, angle, turret);
        position = new PVector(x, y);
        size = new PVector(10, 18);
        radius = 22;
        this.maxSpeed = maxSpeed;
        speed = maxSpeed;
        this.damage = damage/2; //because applies damage from splash and shockwave
        this.angle = angle;
        sprite = staticSprites.get("nuclearPj");
        hitSound = sounds.get("hugeExplosion");
        this.effectRadius = effectRadius;
        type = Enemy.DamageType.nuclear;
        particleTrail = "nuclear";
        debrisTrail = damage > 800 ? particleTrail : null;
        trainChance = maxSpeed > 1200 ? 1 : 3;
    }

    @Override
    public void die() {
        for (int i = 0; i < p.random(10,15); i++) {
            topParticles.add(new ExplosionDebris(p, position.x, position.y, p.random(TWO_PI), "nuclear", p.random(500, 1000)));
            topParticles.add(new ExplosionDebris(p, position.x, position.y, p.random(TWO_PI), "fire", p.random(500, 1000)));
            topParticles.add(new ExplosionDebris(p, position.x, position.y, p.random(TWO_PI), "metal", p.random(500, 1000)));
        }
        shockwaves.add(new NuclearShockwave(p, position.x, position.y, effectRadius * 2, damage, turret));
        projectiles.remove(this);
    }
}


