package main.misc;

import main.enemies.Enemy;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.awt.*;

public class GoreVortex extends Corpse {

    private final PVector vortexCenter;
    private final float vortexRadius;
    private final float attractiveMag;

    /**
     * A gore fragment that orbits around a vortex
     * @param p                the PApplet
     * @param position         position of the corpse
     * @param size             size of the corpse
     * @param velocity         initial velocity
     * @param angle            rotation of the corpse
     * @param currentTintColor initial tint color
     * @param maxLife          how long it should last
     * @param name             what enemy this was
     * @param bloodParticle    what color the blood should be
     */
    public GoreVortex(PApplet p, PVector position, PVector size, float angle, PVector velocity, Color currentTintColor,
                      int maxLife, int frame, Enemy.DamageType type, String name,
                      Enemy.HitParticle bloodParticle, PVector vortexCenter, float vortexRadius, float attractiveMag) {
        super(p, position, size, angle, velocity, currentTintColor, 0, maxLife,
                type, name, bloodParticle, frame, false);

        this.vortexCenter = vortexCenter;
        this.vortexRadius = vortexRadius;
        this.attractiveMag = attractiveMag;
    }

    @Override
    public void move() {
        velocity.mult((float) lifespan / maxLife);
        angularVelocity *= (float) lifespan / maxLife;

        // not sure why this is in radians
        angle += angularVelocity;

        float dist = (float) Math.sqrt(
                Math.pow(position.x - vortexCenter.x, 2) +
                        Math.pow(position.y - vortexCenter.y, 2));
        float mag = (float) (Math.max(1 - dist / vortexRadius, 0) * 10 * Math.pow((float) lifespan / maxLife, 5));
        PVector attractiveVelocity = PVector.sub(vortexCenter, position).setMag(mag * attractiveMag);
        PVector orbitVelocity = PVector.fromAngle(Utilities.findAngle(attractiveVelocity) + PConstants.QUARTER_PI)
                .setMag(mag);

        if (dist > 5) {
            position.add(velocity);
            position.add(attractiveVelocity);
            position.add(orbitVelocity);
        }
    }
}
