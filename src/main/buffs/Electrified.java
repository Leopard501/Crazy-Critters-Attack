package main.buffs;

import main.projectiles.arcs.Arc;
import main.enemies.Enemy;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.sound.SoundFile;

import static main.Main.*;
import static main.misc.Utilities.secondsToFrames;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class Electrified extends Buff {

    private final SoundFile sound;

    public Electrified(PApplet p, Enemy target, int damage, float duration, Turret turret) {
        super(p, target, turret);
        particleChance = 4;
        effectDelay = secondsToFrames(0.5f);
        effectTimer = 0;
        effectLevel = damage;
        sound = sounds.get("teslaFire");
        lifeDuration = secondsToFrames(duration);
        particle = "nuclear";
        name = "electrified";
    }

    @Override
    public void effect() {
        arcs.add(Arc.presets(
                "electrified",
                p, target.position.copy(), turret,
                (int) effectLevel, 4, 150,
                Turret.Priority.values()[(int) p.random(3)]
        ));
        playSoundRandomSpeed(p, sound, 1);
    }
}
