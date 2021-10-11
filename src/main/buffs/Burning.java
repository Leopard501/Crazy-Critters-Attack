package main.buffs;

import main.enemies.Enemy;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.buffs;
import static main.Main.enemies;
import static main.misc.Utilities.secondsToFrames;

public class Burning extends Buff {

    public Burning(PApplet p, int enId, float damage, float duration, Turret turret) {
        super(p,enId,turret);
        particleChance = 4;
        effectDelay = secondsToFrames(0.2f);
        lifeDuration = secondsToFrames(duration);
        effectLevel = damage;
        particle = "fire";
        name = "burning";
        this.enId = enId;
    }

    @Override
    public void effect() { //small damage fast
        if (enId < 0) buffs.remove(this);
        else {
            Enemy enemy = enemies.get(enId);
            enemy.barAlpha = 255;
            enemy.damageWithoutBuff((int) effectLevel, turret, "fire", new PVector(0, 0), false);
        }
    }
}