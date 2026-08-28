package main.towers;

import main.projectiles.arcs.Arc;
import main.projectiles.Flame;
import main.projectiles.shockwaves.FireShockwave;
import main.projectiles.shockwaves.NuclearShockwave;
import main.gui.inGame.InGameGui;
import main.gui.guiObjects.PopupText;
import main.misc.Tile;
import main.particles.Debris;
import main.particles.MiscParticle;
import main.particles.Ouch;
import main.towers.turrets.Booster;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.awt.*;
import java.util.ArrayList;

import static main.Main.*;
import static main.misc.Utilities.incrementByTo;
import static main.misc.Tile.updateTowerArray;
import static main.pathfinding.PathfindingUtilities.updateCombatPoints;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public abstract class Tower {

    public enum Material {
        //lower case for string reasons
        wood,
        darkMetal,
        gold,
        stone,
        metal,
        crystal,
        titanium,
        ice
    }

    public int maxHp;
    public int hp;
    public int nextLevelA;
    public int nextLevelB;
    public int basePrice;
    public int[] upgradePrices;
    public boolean hit;
    public boolean visualize;
    public boolean alive;
    public PVector size;
    public PApplet p;
    public Tile tile;
    public PImage sprite;
    public String name;
    public String[] upgradeTitles;
    public PImage[] upgradeIcons;

    protected int tintColor;
    protected int barAlpha;
    protected Material material;
    protected SoundFile damageSound;
    protected SoundFile breakSound;
    protected SoundFile placeSound;
    protected ArrayList<Booster.Boost> boosts;

    protected Tower(PApplet p, Tile tile) {
        this.p = p;
        this.tile = tile;
        Tile otherTile = tiles.get((int) (tile.position.x / 50) - 1,(int) (tile.position.y / 50) - 1);
        if (otherTile != null) otherTile.breakableLayer.set(null);

        boosts = new ArrayList<>();
        alive = true;
        name = "null";
        size = new PVector(120, 37);
        this.maxHp = 1;
        hp = maxHp;
        hit = false;
        tintColor = 255;
        material = Material.wood;
        visualize = false;
        upgradeTitles = new String[4];
        upgradeIcons = new PImage[4];
        updateCombatPoints();
        updateTowerArray();
    }

    /** Place particles & update stuff */
    public abstract void place(boolean quiet);

    public abstract void update();

    public abstract void displayBase();

    public abstract void controlAnimation();

    /** @return money gained when sold or broken */
    public abstract int getValue();

    public PVector getCenter() {
        return new PVector(tile.position.x - (size.x / 2), tile.position.y - (size.y / 2));
    }

    /** Displays the HP bar and makes it fade over time */
    public void displayHpBar() {
        Color barColor = new Color(0, 255, 0);
        if (boostedMaxHp() > 0) barColor = InGameGui.BOOSTED_TEXT_COLOR;
        p.stroke(barColor.getRGB(), barAlpha / 2f);
        float barWidth = size.x * Math.min(hp / (float) getMaxHp(), 1);
        p.noFill();
        p.rect(tile.position.x - size.x, tile.position.y, size.y, -5);
        p.fill(barColor.getRGB(), barAlpha);
        if (hp > 0) p.rect(tile.position.x - size.x, tile.position.y, barWidth, -5);
        if (hp >= getMaxHp()) barAlpha = (int) incrementByTo(barAlpha, 3, 0);
        else refreshHpBar();
    }

    /** Sets the bars opacity to max */
    public void refreshHpBar() {
        barAlpha = 255;
    }

    /**
     * If it touches an enemy, animate and loose health.
     * @param damage amount of damage taken
     */
    public void damage(int damage) {
        refreshHpBar();
        hp -= damage;
        hit = true;
        playSoundRandomSpeed(p, damageSound, 1);
        int num = (int)(p.random(4,10));
        for (int i = num; i >= 0; i--){ //spray debris
            topParticles.add(new Debris(p,
                    (tile.position.x-size.x/2)+p.random((size.x/2)*-1,size.x/2),
                    (tile.position.y-size.y/2)+p.random((size.y/2)*-1,size.y/2),
                    p.random(0,360), material.name()));
        }
    }

    public abstract void upgrade(int id, boolean quiet);

    /**
     * Plays sound, spawns particles, updates stuff and gives money
     * @param isSold Give 80% of value if true, 40% otherwise
     */
    public void die(boolean isSold) {
        playSoundRandomSpeed(p, breakSound, 1);
        spawnParticles();
        alive = false;
        tile.tower = null;
        if (selection.turret == this) {
            selection.name = "null";
        }
        else if (!selection.name.equals("null")) selection.swapSelected(selection.turret);
        int moneyGain;
        if (!isSold) {
            moneyGain = (int) (getValue() * 0.4);
            tiles.get(
                    ((int)tile.position.x/50) - 1,
                    ((int)tile.position.y/50) - 1).breakableLayer.set(material + "DebrisBr_Tl");
        } else moneyGain = (int) (getValue() * 0.8);
        if (moneyGain > 0) {
            popupTexts.add(new PopupText(p, new PVector(tile.position.x - 25, tile.position.y - 25), moneyGain));
            money += moneyGain;
        }
        if (hasBoostedDeathEffect()) boostedDeathEffect();
        Tile.updateFlooring();
        updateTowerArray();
        updateCombatPoints();
    }

    /** Special death effect when boosted by Booster with Unstable upgrade */
    protected void boostedDeathEffect() {
        int radius = getValue() / 10;
        if (radius < 40) radius = 40;
        float x = tile.position.x - (TILE_SIZE / 2f);
        float y = tile.position.y - (TILE_SIZE / 2f);
        shockwaves.add(new FireShockwave(p, x, y, 0, radius * 2, getValue(), null, 15,
          30));
        shockwaves.add(new NuclearShockwave(p, x, y, radius, getValue(), null));
        for (int i = 0; i < radius / 5; i++) {
            projectiles.add(new Flame(p, x, y, p.random(360), null, getValue() / 8, getValue() / 8f,
              getValue() / 8f, (int) p.random(radius, radius * 2), true));
        }
        int arcCount = 3;
        if (radius > 300) arcCount = 8;
        if (radius > 600) arcCount = 16;
        for (int i = 0; i < arcCount; i++) {
            arcs.add(Arc.presets(
                    "boosterOrange",
                    p, new PVector(x, y), null,
                    getValue() / 8, radius / 10, radius * 2,
                    Turret.Priority.values()[(int) p.random(3)]
            ));
        }
        for (int i = 0; i < arcCount / 3; i++) {
            arcs.add(Arc.presets(
                    "boosterRed",
                    p, new PVector(x, y), null,
                    getValue() / 8, radius / 10, radius * 2,
                    Turret.Priority.values()[(int) p.random(3)]
            ));
        }
        if (radius > 200) playSoundRandomSpeed(p, sounds.get("hugeExplosion"), 1);
        else playSoundRandomSpeed(p, sounds.get("smallExplosion"), 1);
    }

    /**
     * Heals the tower by a proportion & spawns particles
     * @param proportionHealed amount to heal tower by
     */
    public void heal(float proportionHealed) {
        if (hp < getMaxHp()) {
            refreshHpBar();
            for (int i = 0; i < 5; i++) {
                topParticles.add(new Ouch(p,
                        p.random(tile.position.x - size.x, tile.position.x),
                        p.random(tile.position.y - size.y, tile.position.y),
                        p.random(360),
                        "greenPuff"));
            }
        }
        hp += (int) (proportionHealed * getMaxHp());
        if (hp > getMaxHp()) hp = getMaxHp();
    }

    public void updateSprite() {}

    protected void spawnParticles() {
        PVector center = new PVector(tile.position.x-size.x/2, tile.position.y-size.y/2);
        int num = (int) p.random(30,50); //shower debris
        for (int j = num; j >= 0; j--) {
            PVector deviation = new PVector(p.random(-size.x/2,size.x/2), p.random(-size.y/2,size.y/2));
            PVector spawnPos = PVector.add(center, deviation);
            topParticles.add(new Debris(p,spawnPos.x, spawnPos.y, p.random(360), material.name()));
        }
        num = (int) p.random(6, 12);
        for (int k = num; k >= 0; k--) {
            PVector deviation = new PVector(p.random(-size.x/2,size.x/2), p.random(-size.y/2,size.y/2));
            PVector spawnPos = PVector.add(center, deviation);
            topParticles.add(new Ouch(p, spawnPos.x, spawnPos.y, p.random(360), "greyPuff"));
        }
    }

    //boosts --------------------------------------------------

    public void addBoost(Booster.Boost boost) {
        for (Booster.Boost b : boosts) if (b == boost) return;
        boosts.add(boost);
    }

    public void removeBoost(Booster.Boost boost) {
        for (int i = boosts.size() - 1; i >= 0; i--) {
            Booster.Boost b = boosts.get(i);
            if (b == boost) {
                boosts.remove(i);
                return;
            }
        }
    }

    protected void updateBoosts() {
        if (isPaused) return;
        displayBoost();
    }

    protected void displayBoost() {
        if (this instanceof IceWall) return;
        if (!(!boosts.isEmpty() && p.random(30) < 1)) return;
        topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
          p.random(tile.position.y - size.y, tile.position.y), p.random(360), "orangeMagic"));
        if (hasBoostedDeathEffect()) {
            topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
              p.random(tile.position.y - size.y, tile.position.y), p.random(360), "fire"));
        }
    }

    public int boostedMaxHp() {
        int h = 0;
        for (Booster.Boost boost : boosts) {
            float amount = boost.health;
            if (this instanceof Turret) amount *= 2;
            int h2 = (int) (maxHp * amount);
            if (h2 > h) h = h2;
        }
        return h;
    }

    public int getMaxHp() {
        return maxHp + boostedMaxHp();
    }

    public boolean hasBoostedDeathEffect() {
        if (name.equals("boosterExplosive")) return true;
        for (Booster.Boost boost : boosts) {
            if (boost.deathEffect) return true;
        }
        return false;
    }
}
