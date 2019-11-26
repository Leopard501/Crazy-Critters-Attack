package main.projectiles;

import main.enemies.Enemy;
import main.particles.ExplosionDebris;
import main.particles.LargeExplosion;
import main.particles.MediumExplosion;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.enemies;
import static main.Main.spritesH;
import static processing.core.PApplet.abs;
import static main.Main.particles;

public class EnergyBlast extends Projectile{

    private boolean bigExplosion;

    public EnergyBlast(PApplet p, float x, float y, float angle, int damage, int effectRadius, boolean bigExplosion) {
        super(p, x, y, angle);
        position = new PVector(x, y);
        size = new PVector(10, 18);
        radius = 14;
        maxSpeed = 16;
        speed = maxSpeed;
        this.damage = damage;
        pierce = 1;
        this.angle = angle;
        sprite = spritesH.get("energyPj");
        hasTrail = true;
        this.effectRadius = effectRadius;
        trail = "energy";
        this.bigExplosion = bigExplosion;
    }

    public void collideEn(){
        if (p.frameCount > hitTime){
            for (int i = enemies.size()-1; i >= 0; i--){
                Enemy enemy = enemies.get(i);
                if (abs(enemy.position.x-position.x) <= (radius + enemy.radius) && abs(enemy.position.y-position.y) <= (radius + enemy.radius) && pierce > 0){ //if touching enemy, and has pierce
                    enemy.collidePJ(damage,buff,i);
                    if (!bigExplosion){
                        int num = (int)(p.random(10,16));
                        for (int j = num; j >= 0; j--){
                            particles.add(new ExplosionDebris(p,position.x, position.y, p.random(0,360), "energy", maxSpeed = p.random(0.5f,2.5f)));
                        }
                        particles.add(new MediumExplosion(p,position.x, position.y, p.random(0,360)));
                    } else{
                        int num = (int)(p.random(16,42));
                        for (int j = num; j >= 0; j--){
                            particles.add(new ExplosionDebris(p,position.x, position.y, p.random(0,360), "energy", maxSpeed = p.random(1.5f,4.5f)));
                        }
                        particles.add(new LargeExplosion(p,position.x, position.y, p.random(0,360)));
                    }
                    hitTime = p.frameCount + hitDelay; //little timer so no constant damage, NOT unnecessary
                    pierce--;
                    for (int j = enemies.size()-1; j >= 0; j--){
                        Enemy erEnemy = enemies.get(j);
                        if (abs(erEnemy.position.x-position.x) <= (effectRadius + erEnemy.radius) && abs(erEnemy.position.y-position.y) <= (effectRadius + erEnemy.radius)){ //if near enemy
                            erEnemy.collidePJ(damage/2,buff,i);
                        }
                    }
                }
                if (pierce == 0) {
                    dead = true;
                }
            }
        }
    }
}