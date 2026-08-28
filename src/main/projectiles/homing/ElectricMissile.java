package main.projectiles.homing;

import main.enemies.Enemy;
import main.particles.ExplosionDebris;
import main.particles.Ouch;
import main.projectiles.arcs.Arc;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;

public class ElectricMissile extends MagicMissile {

    public ElectricMissile(PApplet p, float x, float y, float angle, Turret turret, int damage, Turret.Priority priority,
                           PVector spawnPos, float effectDuration, float effectLevel, int maxSpeed) {
        super(p, x, y, angle, turret, damage, priority, spawnPos, maxSpeed);
        particleTrail = "nuclear";
        type = Enemy.DamageType.nuclear;
        buff = "electrified";
        sprite = staticSprites.get("electricMissilePj");
        this.effectDuration = effectDuration;
        this.effectLevel = effectLevel;
    }

    @Override
    public void die() {
        for (int i = 0; i < 8; i++) {
            topParticles.add(new ExplosionDebris(p, position.x, position.y, p.random(TWO_PI),
                    "nuclear", p.random(100, 200)));
        }
        topParticles.add(new Ouch(p,position.x,position.y,p.random(0,360),"yellowPuff"));
        arcs.add(Arc.presets(
                "electrified",
                p, position.copy(), turret,
                0, 1, 75,
                Turret.Priority.None
        ));
        projectiles.remove(this);
    }
}
