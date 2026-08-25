package main.projectiles;

import main.enemies.Enemy;
import main.misc.Utilities;
import main.particles.ExplosionDebris;
import main.particles.MiscParticle;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.util.ArrayList;

import static main.Main.*;
import static main.sound.SoundUtilities.playSoundRandomSpeed;
import static processing.core.PConstants.HALF_PI;

public abstract class Projectile {

    public int damage;
    public float radius;
    public float maxSpeed;
    public float speed;
    public float angle;
    public PImage sprite;
    public PApplet p;
    public PVector position;
    public PVector size;

    protected int pierce;
    protected int effectRadius;
    protected int trainChance;
    protected float effectDuration;
    protected float angleTwo;
    protected float angularVelocity;
    protected float effectLevel;
    protected boolean dead;
    protected boolean causeEnemyParticles;
    protected PVector velocity;
    protected String particleTrail;
    protected String debrisTrail;
    protected String buff;
    protected Enemy.DamageType type;
    protected ArrayList<Enemy> hitEnemies;
    protected Turret turret;
    protected SoundFile hitSound;

    protected Projectile(PApplet p, float x, float y, float angle, Turret turret) {
        this.p = p;

        causeEnemyParticles = true;
        this.turret = turret;
        hitEnemies = new ArrayList<>();
        position = new PVector(x, y);
        size = new PVector(10, 10);
        radius = 10;
        speed = 100;
        damage = 1;
        pierce = 0;
        angleTwo = angle;
        angularVelocity = 0; //degrees mode
        sprite = staticSprites.get("boltPj");
        velocity = PVector.fromAngle(angle - HALF_PI);
        trainChance = 3;
        buff = "null";
        effectRadius = 0;
        effectLevel = 0;
        effectDuration = 0;
    }

    public void update() {
        if (!isPaused) {
            trail();
            move();
        }
        checkCollision();
        if (position.y - size.y > BOARD_HEIGHT + 100 || position.x - size.x > BOARD_WIDTH + 100 ||
                position.y + size.y < -100 || position.x + size.x < -100) {
            projectiles.remove(this);
        }
        if (dead) {
            die();
            boostedDieParticles();
        }
    }

    protected float getBoostedSpeed() {
        if (turret == null) return speed / FRAMERATE;
        else return (speed * (turret.boostedRange() > 0 ? 1.2f : 1f)) / FRAMERATE;
    }

    protected void boostedDieParticles() {
        if (turret == null || turret.boostedDamage() <= 0) return;
        for (int i = 0; i < 8; i++) {
            topParticles.add(new ExplosionDebris(p, position.x, position.y, p.random(TWO_PI),
                    "orangeMagic", p.random(100, 200)));
        }
    }

    protected void boostedTrailParticles() {
        if (turret == null || turret.boostedDamage() <= 0 || !(p.random(trainChance) > 1)) return;
        topParticles.add(new MiscParticle(p, position.x, position.y,
                p.random(TWO_PI), "orangeMagic"));
    }

    public abstract void die();

    protected void trail() { //leaves a trail of particles
        boostedTrailParticles();
        if (particleTrail != null && p.random(trainChance) > 1) {
            topParticles.add(new MiscParticle(p, position.x, position.y,
                    p.random(TWO_PI), particleTrail));
        }
        if (debrisTrail != null && p.random(trainChance) > 1) {
            topParticles.add(new ExplosionDebris(p, position.x, position.y,
                    p.random(angle - 0.4f, angle + 0.4f), particleTrail,
                    p.random( maxSpeed / 2f, maxSpeed)));
        }
    }

    public void displayShadow() {
        if (!isPaused) angleTwo += radians(angularVelocity);
        p.pushMatrix();
        p.tint(0,60);
        p.translate(position.x + 2, position.y + 2);
        p.rotate(angleTwo);
        p.image(sprite, -size.x / 2, -size.y / 2);
        p.tint(255);
        p.popMatrix();
    }

    public void display() {
        if (!isPaused) angleTwo += radians(angularVelocity);
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.rotate(angleTwo);
        p.image(sprite, -size.x / 2, -size.y / 2);
        p.popMatrix();
    }

    public void move() {
        velocity.setMag(getBoostedSpeed());
        position.add(velocity);
    }

    public void checkCollision() {
        for (int enemyId = 0; enemyId < enemies.size(); enemyId++) {
            Enemy enemy = enemies.get(enemyId);
            if (enemyAlreadyHit(enemy)) continue;
            if (intersectingEnemy(enemy) && pierce > -1) {
                playSoundRandomSpeed(p, hitSound, 1);
                PVector applyVelocity = velocity;
                if (effectRadius > 0) applyVelocity = new PVector(0, 0);
                damageEnemy(enemy, applyVelocity);
                hitEnemies.add(enemy);
                pierce--;
                //splash
                for (int splashEnemyId = enemies.size() - 1; splashEnemyId >= 0; splashEnemyId--) {
                    Enemy splashEnemy = enemies.get(splashEnemyId);
                    if (enemyAlreadyHit(splashEnemy)) continue;
                    if (nearEnemy(splashEnemy)) {
                        applyVelocity = fromExplosionCenter(splashEnemy);
                        if (intersectingEnemy(splashEnemy)) applyVelocity = new PVector(0, 0);
                        splashDamageEnemy(splashEnemy, applyVelocity);
                    }
                }
            }
            if (pierce < 0) dead = true;
        }
    }

    protected void damageEnemy(Enemy enemy, PVector applyVelocity) {
        enemy.damageWithBuff(damage,
                buff, effectLevel, effectDuration, turret, causeEnemyParticles, type, applyVelocity);
    }

    protected void splashDamageEnemy(Enemy enemy, PVector applyVelocity) {
        enemy.damageWithBuff(3 * (damage / 4),
                buff, effectLevel, effectDuration, turret, causeEnemyParticles, type, applyVelocity);
    }

    protected PVector fromExplosionCenter(Enemy enemy) {
        return PVector.fromAngle(Utilities.findAngle(enemy.position, position) + HALF_PI);
    }

    protected boolean enemyAlreadyHit(Enemy enemy) {
        for (Enemy hitEnemy : hitEnemies) {
            if (hitEnemy == enemy) {
                return true;
            }
        }
        return false;
    }

    protected boolean intersectingEnemy(Enemy enemy) {
        boolean touchingX = abs(enemy.position.x - position.x) < radius + enemy.radius;
        boolean touchingY = abs(enemy.position.y - position.y) < radius + enemy.radius;
        return touchingX && touchingY;
    }

    protected boolean nearEnemy(Enemy enemy) {
        boolean nearX = abs(enemy.position.x - position.x) <= effectRadius + enemy.radius;
        boolean nearY = abs(enemy.position.y - position.y) <= effectRadius + enemy.radius;
        return nearX && nearY;
    }
}
