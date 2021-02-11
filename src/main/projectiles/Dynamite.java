package main.projectiles;

import main.enemies.Enemy;
import main.misc.MiscMethods;
import main.particles.ExplosionDebris;
import main.particles.LargeExplosion;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;
import static processing.core.PApplet.abs;

public class Dynamite extends Projectile {

    public Dynamite(PApplet p, float x, float y, float angle, Turret turret, int damage, int effectRadius) {
        super(p, x, y, angle, turret);
        position = new PVector(x, y);
        size = new PVector(9, 15);
        angularVelocity = p.random(-15, 15); //degrees mode
        radius = 10;
        maxSpeed = 10;
        speed = maxSpeed;
        this.damage = damage;
        this.angle = angle;
        sprite = spritesH.get("dynamitePj");
        hitSound = soundsH.get("smallExplosion");
        this.effectRadius = effectRadius;
    }

    public void collideEn() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            boolean hitAlready = false;
            for (Enemy hitEnemy : hitEnemies)
                if (hitEnemy == enemy) {
                    hitAlready = true;
                    break;
                }
            if (hitAlready) continue;
            if (abs(enemy.position.x - position.x) <= (radius + enemy.radius) && abs(enemy.position.y - position.y) <= (radius + enemy.radius) && pierce > 0) { //if touching enemy, and has pierce
                hitSound.stop();
                hitSound.play(p.random(0.8f, 1.2f), volume);
                enemy.damagePj(damage, buff, effectLevel, effectDuration, turret, splashEn, "burning", velocity, i);
                int num = (int) (p.random(16, 42));
                for (int j = num; j >= 0; j--) {
                    particles.add(new ExplosionDebris(p, position.x, position.y, p.random(0, 360), "fire", maxSpeed = p.random(1.5f, 4.5f)));
                }
                particles.add(new LargeExplosion(p, position.x, position.y, p.random(0, 360), "fire"));

                pierce--;
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy erEnemy = enemies.get(j);
                    if (abs(erEnemy.position.x - position.x) <= (effectRadius + erEnemy.radius) && abs(erEnemy.position.y - position.y) <= (effectRadius + erEnemy.radius)) { //if near enemy
                        hitAlready = false;
                        for (Enemy hitEnemy : hitEnemies)
                            if (hitEnemy == enemy) {
                                hitAlready = true;
                                break;
                            }
                        if (hitAlready) continue;
                        erEnemy.damagePj(3 * (damage / 4), buff, effectLevel, effectDuration, turret, splashEn, erEnemy.lastDamageType, PVector.fromAngle(MiscMethods.findAngle(erEnemy.position, position) + HALF_PI), j);
                    }
                }
            }
            if (pierce == 0) {
                dead = true;
            }
        }
    }
}
