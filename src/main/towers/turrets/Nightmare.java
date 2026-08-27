package main.towers.turrets;

import main.enemies.Enemy;
import main.particles.NightmareVortex;
import main.particles.Ouch;
import main.projectiles.Needle;
import main.misc.Tile;
import main.particles.MiscParticle;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class Nightmare extends Turret {

    private int numProjectiles;
    private boolean isWindy;

    public static String pid = "T2-200-0-3.5";
    public static String description =
            "Unleashes a flurry of cursed needles that turn critters to ash.";
    public static char shortcut = 'G';
    public static String title1 = "Nightmare";
    public static String title2 = "Blaster";
    public static int price = 20_000;

    public Nightmare(PApplet p, Tile tile) {
        super(p,tile);
        name = "nightmare";
        delay = 3.5f;
        pjSpeed = 1000;
        range = 200;
        damage = 0;
        numProjectiles = 5;
        effectLevel = 1000;
        effectDuration = 3.6f;
        fireParticle = "decay";
        barrelLength = 20;
        loadSprites();
        material = Material.darkMetal;
        basePrice = price;
        priority = Priority.Unbuffed;
        effect = "decay";
        titleLines = new String[]{"Nightmare", "Blaster"};
//        infoDisplay = (o) -> {
//            selection.setTextPurple("Decay", o);
//            selection.setTextPurple(numProjectiles + " needles", o);
//        };

        placeSound = sounds.get("titaniumPlace");
        breakSound = sounds.get("titaniumBreak");
        damageSound = sounds.get("titaniumDamage");
        fireSound = sounds.get("nightmareFire");
    }

    @Override
    public void update() {
        if (hp <= 0) {
            die(false);
            tile.tower = null;
        }
        updateBoosts();
        if (!isWindy) {
            if (!enemies.isEmpty() && !machine.dead && !isPaused) checkTarget();
        } else {
            if (!isPaused) fire(0, "decay");
        }
        if (p.mousePressed && boardMousePosition.x < tile.position.x
                && boardMousePosition.x > tile.position.x - size.x && boardMousePosition.y < tile.position.y
                && boardMousePosition.y > tile.position.y - size.y && alive && !isPaused) {
            selection.swapSelected(tile.id);
        }
    }

    @Override
    protected void fire(float barrelLength, String particleType) {
        PVector pos = new PVector(tile.position.x-size.x/2,tile.position.y-size.y/2);
        if (!isWindy) {
            float angleDelta = PApplet.radians(10);
            playSoundRandomSpeed(p, fireSound, 1);
            for (int i = 0; i < numProjectiles; i++) {
                int num = ceil(i - numProjectiles / 2f);
                PVector spa = PVector.fromAngle(angle-HALF_PI);
                spa.setMag(20);
                pos.add(spa);
                spawnProjectiles(pos, angle + num * angleDelta);
            }
        } else {
            for (Enemy enemy : enemies) {
                if (PVector.sub(pos, enemy.position).mag() < range) {
                    enemy.damageVortex(0, "decay", effectLevel, effectDuration, this,
                            Enemy.DamageType.decay, pos, pos, range, -0.5f);
                }
            }
            for (int j = 0; j < 3; j++) {
                PVector partpos = PVector.add(pos, PVector.fromAngle(p.random(TWO_PI)).setMag(p.random(range)));
                towerParticles.add(new Ouch(p, partpos.x, partpos.y, p.random(TWO_PI), "greyPuff"));
            }
            for (int i = 0; i < p.random(2, 5); i++) {
                topParticles.add(new NightmareVortex(p, pos.copy(), range));
            }
        }
    }

    @Override
    protected void spawnProjectiles(PVector position, float angle) {
        projectiles.add(new Needle(p, position.x, position.y, angle, this, getDamage(), (int) effectLevel,
                effectDuration, range));
        for (int j = 0; j < 3; j++) {
            PVector spa2 = PVector.fromAngle(angle-HALF_PI+radians(p.random(-20,20)));
            spa2.setMag(-2);
            PVector spp2 = new PVector(position.x,position.y);
            spp2.add(spa2);
            towerParticles.add(new MiscParticle(p,spp2.x,spp2.y,angle+radians(p.random(-45,45)),"decay"));
        }
    }

    @Override
    protected void setUpgrades(){
        //price
        upgradePrices[0] = 5000;
        upgradePrices[4] = 7500;
        upgradePrices[2] = 100000;

        upgradePrices[3] = 6000;
        upgradePrices[1] = 10000;
        upgradePrices[5] = 100000;
        //titles
        upgradeTitles[0] = "Firerate";
        upgradeTitles[1] = "Effect Power";
        upgradeTitles[2] = "Dark Wind";

        upgradeTitles[3] = "Range";
        upgradeTitles[4] = "More Needles";
        upgradeTitles[5] = "Prismatic";
        //descriptions
        upgradeDescA[0] = "Increase";
        upgradeDescB[0] = "firerate";
        upgradeDescC[0] = "";

        upgradeDescA[1] = "Increase";
        upgradeDescB[1] = "damage &";
        upgradeDescC[1] = "duration";

        upgradeDescA[2] = "A dark";
        upgradeDescB[2] = "wind blows";
        upgradeDescC[2] = "";


        upgradeDescA[3] = "Increase";
        upgradeDescB[3] = "range";
        upgradeDescC[3] = "";

        upgradeDescA[4] = "Fire more";
        upgradeDescB[4] = "projectiles";
        upgradeDescC[4] = "";

        upgradeDescA[5] = "A hail of";
        upgradeDescB[5] = "crystallized";
        upgradeDescC[5] = "light";
        //icons
        upgradeIcons[0] = animatedSprites.get("upgradeIC")[7];
        upgradeIcons[1] = animatedSprites.get("upgradeIC")[3];
        upgradeIcons[2] = animatedSprites.get("upgradeIC")[4];

        upgradeIcons[3] = animatedSprites.get("upgradeIC")[5];
        upgradeIcons[4] = animatedSprites.get("upgradeIC")[4];
        upgradeIcons[5] = animatedSprites.get("upgradeIC")[3];
    }

    @Override
    protected void upgradeEffect(int id) {
        if (id == 0) {
            switch (nextLevelA) {
                case 0 -> delay -= 1;
                case 1 -> {
                    effectDuration += 3;
                    effectLevel += 1000;
                }
                case 2 -> {
                    effectLevel += 1000;
                    isWindy = true;
                    range -= 50;
                }
            }
        } if (id == 1) {
            switch (nextLevelB) {
                case 3 -> range += 40;
                case 4 -> numProjectiles += 3;
                case 5 -> {
                    effectDuration += 5;
                    effectLevel += 1100;
                }
            }
        }
    }
}