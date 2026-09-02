package main.misc;

import main.enemies.Enemy;
import main.particles.MiscParticle;
import main.particles.Ouch;
import main.particles.Pile;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;

import static main.Main.*;
import static main.misc.Utilities.*;
import static processing.core.PApplet.radians;

public class Corpse {

    public static final int CAP = 50;

    protected final PVector velocity;
    protected final PVector position;
    protected final int maxLife;

    protected int lifespan;
    protected float angle;
    protected float angularVelocity;

    private final boolean isAnimated;
    private final int betweenFrames;
    private final PVector size;
    private final PImage[] sprites;
    private final Enemy.HitParticle bloodParticle;
    private final PApplet p;

    private int betweenTime;
    private int frame;
    private Enemy.DamageType type;
    private Color currentTintColor;
    private PImage disintegratingSprite;

    /**
     * A dead enemy.
     * @param p                 the PApplet
     * @param position          position of the corpse
     * @param size              size of the corpse
     * @param angle             rotation of the corpse
     * @param velocity          movement of the corpse
     * @param currentTintColor  initial tint color
     * @param betweenFrames     number of times to duplicate a frame
     * @param maxLife           how long it should last
     * @param effectType        what sort of visual effect to apply
     * @param name              what enemy this was
     * @param bloodParticle     what color the blood should be
     * @param frame             what frame to start its animation on
     * @param animated          should it be animated
     */
    public Corpse(PApplet p, PVector position, PVector size, float angle, PVector velocity, Color currentTintColor,
                  int betweenFrames, int maxLife, Enemy.DamageType effectType, String name,
                  Enemy.HitParticle bloodParticle, int frame, boolean animated) {
        this.p = p;

        this.position = new PVector(position.x, position.y);
        this.size = size;
        this.angle = angle;
        this.currentTintColor = currentTintColor;
        if (!animated) {
            float speed = 3.5f;
            speed *= p.random(1, 2);
            float a = p.random(radians(0), radians(360));
            if (!(velocity.x == 0 && velocity.y == 0)) a = Utilities.findAngle(velocity);
            a -= HALF_PI;
            a += p.random(radians(-40), radians(40));
            velocity = PVector.fromAngle(a);
            this.velocity = velocity.setMag(speed);
        } else this.velocity = velocity;
        sprites = animatedSprites.get(name + "EN");
        this.type = effectType;
        if (this.type == null) this.type = null;
        this.bloodParticle = bloodParticle;
        this.frame = frame;
        this.isAnimated = animated;
        float maxRotationSpeed = up60ToFramerate(200f / size.x) * 2;
        angularVelocity = p.random(radians(-maxRotationSpeed), radians(maxRotationSpeed));

        if (type != null && type.equals(Enemy.DamageType.decay)) {
            if (isAnimated) {
                disintegratingSprite = sprites[sprites.length-1].copy();
            } else {
                disintegratingSprite = sprites[frame].copy();
            }
        }

        this.betweenFrames = betweenFrames;
        betweenTime = 0;

        this.maxLife = secondsToFrames(maxLife);
        lifespan = this.maxLife;
    }

    public void update(int i) {
        if (isPaused) return;
        move();
        bloodParticles();
        buffParticles();
        if (type != null && type.equals(Enemy.DamageType.decay)) {
            disintegratingSprite = disintegration(disintegratingSprite);
        }
        lifespan--;
        if (lifespan <= 0) corpses.remove(i);
    }

    protected void move() {
        velocity.x *= (float) lifespan / maxLife;
        velocity.y *= (float) lifespan / maxLife;
        angularVelocity *= (float) lifespan / maxLife;
        angle += radians(angularVelocity);
        position.add(velocity);
    }

    public void display() {
        PImage sprite;
        if (type != null && type.equals(Enemy.DamageType.decay) && (frame == sprites.length-1 || !isAnimated)) {
            sprite = disintegratingSprite;
        } else {
            sprite = sprites[frame];
        }

        if (!isPaused) {
            if (isAnimated && frame < sprites.length - 1) {
                betweenTime++;
                if (betweenTime >= betweenFrames) {
                    frame++;
                    betweenTime = 0;
                }
            }
        }

        if (type != null && type.finalTintColor != null) {
            drawSprites(tinting(sprite, tintedColor(type.finalTintColor)));
        } else if (type == Enemy.DamageType.bleeding) {
            drawSprites(tinting(sprite, tintedColor(bloodParticle.tintColor)));
        } else {
            drawSprites(tinting(sprite, null));
        }
        currentTintColor = incrementColorTo(currentTintColor, up60ToFramerate(20), new Color(255, 255, 255));
    }

    private Color tintedColor(Color tint) {
        return new Color (
                getTintChannel(tint.getRed(), lifespan, maxLife),
                getTintChannel(tint.getGreen(), lifespan, maxLife),
                getTintChannel(tint.getBlue(), lifespan, maxLife)
        );
    }

    private void buffParticles() {
        if (isPaused || type == null || (type.particle == null && type != Enemy.DamageType.bleeding)) return;
        float chance = 0;
        // prevent divide by 0
        if (lifespan > 0) chance = sq(2 * ((float) maxLife / (float) lifespan));
        if (!isAnimated) chance += 16;
        if (type == Enemy.DamageType.bleeding) chance *= 3;
        int num = (int) (p.random(0, chance));
        if (num == 0) {
            if (type == Enemy.DamageType.bleeding) {
                bloodSplatter(p, position, size, bloodParticle);
            } else {
                towerParticles.add(new MiscParticle(p, (float) (position.x + 2.5 + p.random((size.x / 2) * -1,
                        (size.x / 2))), (float) (position.y + 2.5 + p.random((size.x / 2) * -1, (size.x / 2))),
                        p.random(360), type.particle));
            }
        }
    }

    public static void bloodSplatter(PApplet p, PVector position, PVector size, Enemy.HitParticle bloodParticle) {
        bottomParticles.add(new Pile(p, (float) (position.x + 2.5 + p.random((size.x / 2) * -1,
                (size.x / 2))), (float) (position.y + 2.5 + p.random((size.x / 2) * -1, (size.x / 2))),
                0, bloodParticle.name()));
    }

    private PImage tinting(PImage sprite, Color tintColor) {
        //for memory reasons
        PImage st = p.createImage(sprite.width, sprite.height, ARGB);
        sprite.loadPixels();
        arrayCopy(sprite.pixels, st.pixels);

        //tinting
        float transparency = ((float) lifespan) / ((float) maxLife);
        if (tintColor != null) {
            p.tint(currentTintColor.getRGB());
            superTint(st,
                    new Color(tintColor.getRed(), tintColor.getGreen(), tintColor.getBlue(), 0),
                    transparency);
        } else p.tint(currentTintColor.getRGB(), transparency * 255);
        return st;
    }

    private PImage disintegration(PImage sprite) {
        //for memory reasons
        PImage st = p.createImage(sprite.width, sprite.height, ARGB);
        sprite.loadPixels();
        arrayCopy(sprite.pixels, st.pixels);

        for (int i = 0; i < size.x / 20; i++) {
            st.pixels[(int) p.random(st.pixels.length)] = 0;
        }

        return st;
    }

    private void bloodParticles() {
        if (isPaused || bloodParticle == null) return;
        for (int i = (int) ((size.x / 25) * (size.y / 25)) / 25; i >= 0; i--) {
            float speed = sqrt(sq(velocity.x) + sq(velocity.y));
            float chance = sq(1 / (speed + 0.01f));
            chance += 16;
            if (p.random(chance) < 1) {
                PVector pos = getRandomPointInRange(p, position, size.mag() * 0.4f);
                towerParticles.add(new Ouch(p, pos.x, pos.y, p.random(360), bloodParticle.name()));
            }
            chance += 10;
            if (p.random(chance) < 1) {
                PVector pos = getRandomPointInRange(p, position, size.mag() * 0.2f);
                bottomParticles.add(new Pile(p, pos.x, pos.y, 0, bloodParticle.name()));
            }
        }
    }

    private void drawSprites(PImage sprite) {
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.rotate(angle);
        p.image(sprite, -size.x / 2, -size.y / 2);
        p.popMatrix();
        p.tint(255);
    }

    private int getTintChannel(float channel, float lifespan, float maxLife) {
        return (int) ((pow(lifespan / maxLife, 3) * (255 - channel)) + channel);
    }
}
