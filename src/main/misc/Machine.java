package main.misc;

import main.projectiles.Flame;
import main.particles.Debris;
import main.particles.LargeExplosion;
import main.particles.MediumExplosion;
import main.particles.Ouch;
import main.sound.StartStopSoundLoop;
import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.awt.*;

import static main.Main.*;
import static main.misc.ResourceLoader.getResource;
import static main.misc.Utilities.secondsToFrames;
import static main.misc.Utilities.up60ToFramerate;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class Machine {

    public PApplet p;

    public int hp;
    public int maxHp;
    public PVector position;
    public String name;
    public Tower.Material material;
    public int betweenFrames;
    private boolean hit;
    private int tintColor;
    private int currentFrame;
    private int frameTimer;
    private int deathFrame;
    public boolean dead;
    private PImage[] sprites;
    public Tile[] machTiles;
    private int damageState;
    public PVector barPosition;
    public PVector barSize;
    public boolean barHorizontal;

    private final SoundFile DAMAGE_SOUND;
    private final SoundFile BREAK_SOUND;
    private final SoundFile EXPLODE_SOUND;
    private final StartStopSoundLoop EXPLODE_LOOP;

    public Machine(PApplet p, PVector position, String name, String debris, int betweenFrames, int maxHp) {
        this.p = p;

        damageState = 0;
        hp = maxHp;
        this.maxHp = maxHp;
        this.position = position;
        this.name = name;
        this.material = Tower.Material.valueOf(debris);
        this.betweenFrames = betweenFrames;
        DAMAGE_SOUND = sounds.get(debris + "Damage");
        BREAK_SOUND = sounds.get(debris + "Break");
        EXPLODE_SOUND = sounds.get("smallExplosion");
        EXPLODE_LOOP = startStopSoundLoops.get("smallExplosion");
        sprites = getResource(name, animatedSprites);
        tintColor = 255;
        updateNodes();
    }

    public void updateNodes() {
        int l = 0;
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.machine) l++;
        }
        machTiles = new Tile[l];
        l = 0;
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.machine) {
                machTiles[l] = tile;
                l++;
            }
        }
    }

    public void update() {
        if (hp <= 0 && !dead) die();
    }

    public void display() {
        if (hit) {
            tintColor = 0;
            hit = false;
        }
        p.tint(255, tintColor, tintColor);
        p.imageMode(CENTER);
        if (p.frameCount > frameTimer && !dead && !isPaused) {
            if (currentFrame < sprites.length - 1) currentFrame++;
            else currentFrame = 0;
            frameTimer = p.frameCount + betweenFrames;
        }
        if (currentFrame >= sprites.length) currentFrame = 0;
        if (deathFrame < secondsToFrames(4))
            p.image(sprites[currentFrame], position.x, position.y);
        p.imageMode(CORNER);
        p.tint(255);
        if (dead && !isPaused) deathFrame++;
        if (!dead && !isPaused) hurtParticles();
        else if (deathFrame < secondsToFrames(5) && !isPaused) deathAnim();
        else EXPLODE_LOOP.stopLoop();
        if (deathFrame > secondsToFrames(8)) isPaused = true;
        if (tintColor < 255 && !isPaused) tintColor += 20;
    }

    public void hpBar() {
        if (hp < maxHp && hp > 0) {
            Color barColor = new Color(0, 255, 0);
            p.stroke(barColor.getRGB());
            if (barHorizontal) {
                p.fill(barColor.getRGB());
                p.rect(barPosition.x, barPosition.y, barSize.x * (hp / (float) maxHp), barSize.y);
                p.noFill();
                p.rect(barPosition.x, barPosition.y, barSize.x, barSize.y);
            }
            else {
                p.fill(barColor.getRGB());
                p.rect(barPosition.x, barPosition.y, barSize.x, barSize.y * (hp / (float) maxHp));
                p.noFill();
                p.rect(barPosition.x, barPosition.y, barSize.x, barSize.y);
            }
        }
    }

    private void hurtParticles() {
        if (damageState > 0) {
            int r = 0;
            if (damageState == 1) r = 150;
            if (damageState == 2) r = 100;
            if (damageState == 3) r = 20;
            for (Tile tile : machTiles) {
                int x = (int) tile.position.x;
                int y = (int) tile.position.y;
                if ((int) p.random(0, r) == 0)
                    topParticles.add(new Ouch(p, shuffle(x), shuffle(y), p.random(0, 360), "greyPuff"));
            }
        }
    }

    private float shuffle(int i) {
        return i + p.random(0, 50);
    }

    private void deathAnim() {
        if (deathFrame == 0) {
            playSoundRandomSpeed(p, BREAK_SOUND, 1);
        } if (deathFrame < secondsToFrames(2.6f)) {
            for (Tile tile : machTiles) {
                int x = (int) tile.position.x;
                int y = (int) tile.position.y;
                if (up60ToFramerate(p.random(0, 3)) == 0)
                    topParticles.add(new Debris(p, shuffle(x), shuffle(y), p.random(0, 360), material.name()));
                if (up60ToFramerate(p.random(0, 6)) == 0) {
                    if ((int) p.random(0, 5) == 0) {
                        playSoundRandomSpeed(p, EXPLODE_SOUND, 1);
                    } topParticles.add(new MediumExplosion(p, shuffle(x), shuffle(y), p.random(0, 360), "fire"));
                }
            }
        } else {
            EXPLODE_LOOP.startLoop(1, 1);
            for (Tile tile : machTiles) {
                int x = (int) tile.position.x;
                int y = (int) tile.position.y;
                if (up60ToFramerate(p.random(0, 4)) == 0)
                    topParticles.add(new LargeExplosion(p, shuffle(x), shuffle(y), p.random(0, 360), "fire"));
                if (up60ToFramerate((int) p.random(0, 2)) == 0)
                    topParticles.add(new MediumExplosion(p, shuffle(x), shuffle(y), p.random(0, 360), "fire"));
                for (int i = 0; i < up60ToFramerate(3); i++) {
                    topParticles.add(new Debris(p, shuffle(x), shuffle(y), p.random(0, 360), material.name()));
                } if (up60ToFramerate(p.random(0, 8)) == 0) {
                    projectiles.add(new Flame(p, shuffle(x), shuffle(y), p.random(0, 360), null, maxHp * 10, maxHp, 1000, (int) p.random(50, 200), true));
                }
            }
        }
        if (deathFrame == secondsToFrames(4))
            for (Tile tile : machTiles)
                tile.breakableLayer.set(material + "DebrisBr_Tl");
    }

    public void updateSprite() {
        int hpSegment = maxHp / 4;
        if (hp <= hpSegment * 3 && hp > hpSegment * 2) damageState = 1;
        else if (hp <= hpSegment * 2 && hp > hpSegment) damageState = 2;
        else if (hp <= hpSegment) damageState = 3;
        if (damageState > 0) {
            String key = name.substring(0, name.length() - 2) + "d" + damageState + "MA";
            sprites = getResource(key, animatedSprites);
        } else sprites = getResource(name, animatedSprites);
    }

    public void damage(int dmg) {
        hp -= dmg;
        int hpSegment = maxHp / 4;
        hit = true;
        updateSprite();
        playSoundRandomSpeed(p, DAMAGE_SOUND, 1);
        for (Tile tile : machTiles) {
            int x = (int) tile.position.x;
            int y = (int) tile.position.y;
            for (int i = 0; i < 5; i++) {
                topParticles.add(new Debris(p, shuffle(x), shuffle(y), p.random(0, 360), material.name()));
            }
            if ((int) p.random(0, 2 * ((float) hp / (float) hpSegment)) == 0) {
                playSoundRandomSpeed(p, EXPLODE_SOUND, 1);
                topParticles.add(new MediumExplosion(p, shuffle(x), shuffle(y), p.random(0, 360), "fire"));
            }
        }
    }

    public void die() {
        dead = true;
        alive = false;
    }

    public void heal(float amount) {
        if (hp < maxHp) {
            for (Tile tile : machTiles) {
                int x = (int) tile.position.x;
                int y = (int) tile.position.y;
                for (int i = 0; i < 5; i++) {
                    topParticles.add(new Ouch(p, shuffle(x), shuffle(y), p.random(0, 360), "greenPuff"));
                }
            }
        }
        hp += (int) (amount * maxHp);
        updateSprite();
        if (hp > maxHp) hp = maxHp;
    }
}
