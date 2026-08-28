package main.towers.turrets;

import main.projectiles.arcs.Arc;
import main.gui.inGame.Selection;
import main.gui.guiObjects.PopupText;
import main.misc.IntVector;
import main.misc.Tile;
import main.particles.Floaty;
import main.particles.MiscParticle;
import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;

import static main.Main.*;
import static main.misc.ResourceLoader.getResource;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class Booster extends Turret {

    private static final int MONEY_GAIN = 5000;
    private static final Color SPECIAL_COLOR = Selection.BOOSTED_INFO_COLOR;

    public static String pid = "C3-1-0-0";
    public static String description =
            "Boosts the base stats of adjacent towers and walls. " +
                    "Boosts tower durability, and can upgrade to boost range, damage, and firerate. " +
                    "Effect does not stack.";
    public static char shortcut = 'V';
    public static String title1 = "Booster";
    public static String title2 = null;
    public static int price = 7000;

    public int moneyTotal;
    public Boost boost;

    public Booster(PApplet p, Tile tile) {
        super(p, tile);
        maxHp = 40;
        hp = maxHp;
        name = "booster";
        delay = -1;
        range = 1;
        pjSpeed = -1;
        material = Material.darkMetal;
        damageSound = sounds.get("metalDamage");
        breakSound = sounds.get("metalBreak");
        placeSound = sounds.get("metalPlace");
        betweenIdleFrames = 4;
        basePrice = price;
        hasPriority = false;
        titleLines = new String[]{"Booster"};

        extraInfo.add((arg) -> selection.displayInfoLine(arg,
                SPECIAL_COLOR, "Boosts Towers:", null));
        extraInfo.add((arg) -> selection.displayInfoLine(arg,
                SPECIAL_COLOR, "Health", "+" + (int) (boost.health * 100) + "%"));

        statsDisplay = () -> {
            if (name.equals("boosterMoney")) {
                selection.displayInfoLine(-2, Selection.STAT_TEXT_COLOR, "Earned", "$" + nfc(moneyTotal));
            }
            int age = levels[currentLevel].currentWave - birthday;
            selection.displayInfoLine(-1, Selection.STAT_TEXT_COLOR, "Survived", age + "");
        };

        boost = new Boost();
        boost.health = 0.5f;
    }

    @Override
    protected void loadSprites() {
        sBase = getResource(name + "BaseTr", staticSprites);
        idleFrames = getResource(name + "IdleTR", animatedSprites);
        idleSprite = idleFrames[0];
        sprite = idleSprite;
    }

    @Override
    public void update() {
        if (hp <= 0) {
            die(false);
            tile.tower = null;
        }
        if (!isPaused) {
            giveBoost();
            spawnProjectiles(new PVector(tile.position.x - 25, tile.position.y - 25), p.random(360));
        }
        if (p.mousePressed && boardMousePosition.x < tile.position.x && boardMousePosition.x > tile.position.x - size.x && boardMousePosition.y < tile.position.y
          && boardMousePosition.y > tile.position.y - size.y && alive && !isPaused) {
            selection.swapSelected(tile.id);
        }
    }

    @Override
    public void die(boolean isSold) {
        super.die(isSold);
        clearBoost();
    }

    /** Now spawns particles */
    @Override
    protected void spawnProjectiles(PVector position, float angle) {
        if (p.random(15) < 1) {
            float speed = p.random(25, 35);
            if (range > 1) speed = p.random(35, 50);
            if (name.equals("boosterMoney")) {
                towerParticles.add(new Floaty(p, position.x, position.y, speed, "coin"));
                topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(360), "yellowMagic"));
            } else if (name.equals("boosterExplosive")) {
                towerParticles.add(new Floaty(p, position.x, position.y, speed, "smokeCloud"));
                topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(360), "orangeMagic"));
                topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(360), "fire"));
            } else {
                towerParticles.add(new Floaty(p, position.x, position.y, speed, "orangeBubble"));
                topParticles.add(new MiscParticle(p, p.random(tile.position.x - size.x, tile.position.x),
                  p.random(tile.position.y - size.y, tile.position.y), p.random(360), "orangeMagic"));
            }
        }
        if (p.random(30) < 1 && name.equals("boosterExplosive")) {
            String color = "boosterOrange";
            if (p.random(10) < 1) color = "boosterRed";
            arcs.add(Arc.presets(
                    color,
                    p, new PVector(tile.position.x - (size.x / 2), tile.position.y - (size.y / 2)), this,
                    0, 1, (int) p.random(20, 100),
                    Priority.None
            ));
        }
    }

    public void giveMoney() {
        money += MONEY_GAIN;
        moneyTotal += MONEY_GAIN;
        popupTexts.add(new PopupText(p, new PVector(tile.position.x - 25, tile.position.y - 25), MONEY_GAIN));
        playSoundRandomSpeed(p, sounds.get("cash"), 1);
    }

    @Override
    public void upgrade(int id, boolean quiet) {
        upgradeEffect(id);
        int price = 0;
        if (id == 0) {
            price = upgradePrices[nextLevelA];
            if (price > money) return;
            if (nextLevelA > 2) return;
            if (nextLevelB == 6 && nextLevelA == 2) return;
            nextLevelA++;
        } else if (id == 1) {
            price = upgradePrices[nextLevelB];
            if (price > money) return;
            if (nextLevelB > 5) return;
            if (nextLevelB == 5 && nextLevelA == 3) return;
            nextLevelB++;
        }
        money -= price;
        basePrice += price;
        //icons
        if (nextLevelA < upgradeTitles.length / 2) inGameGui.upgradeIconA.sprite = upgradeIcons[nextLevelA];
        else inGameGui.upgradeIconA.sprite = animatedSprites.get("upgradeIC")[0];
        if (nextLevelB < upgradeTitles.length) inGameGui.upgradeIconB.sprite = upgradeIcons[nextLevelB];
        else inGameGui.upgradeIconB.sprite = animatedSprites.get("upgradeIC")[0];

        if (!quiet) {
            playSoundRandomSpeed(p, placeSound, 1);
            spawnParticles();
        }
    }

    @Override
    protected void setUpgrades() {
        //price
        upgradePrices[0] = 3000;
        upgradePrices[1] = 6000;
        upgradePrices[2] = 30000;

        upgradePrices[3] = 4000;
        upgradePrices[4] = 5000;
        upgradePrices[5] = 25000;
        //titles
        upgradeTitles[0] = "Boost Range";
        upgradeTitles[1] = "Increase Area";
        upgradeTitles[2] = "Money Boost";

        upgradeTitles[3] = "Boost Damage";
        upgradeTitles[4] = "Boost Firerate";
        upgradeTitles[5] = "Unstable";
        //descriptions
        upgradeDescA[0] = "Boost";
        upgradeDescB[0] = "tower";
        upgradeDescC[0] = "range";

        upgradeDescA[1] = "Affect";
        upgradeDescB[1] = "more";
        upgradeDescC[1] = "towers";

        upgradeDescA[2] = "Increase";
        upgradeDescB[2] = "income";
        upgradeDescC[2] = "";


        upgradeDescA[3] = "Boost";
        upgradeDescB[3] = "tower";
        upgradeDescC[3] = "damage";

        upgradeDescA[4] = "Boost";
        upgradeDescB[4] = "tower";
        upgradeDescC[4] = "firerate";

        upgradeDescA[5] = "Destroyed";
        upgradeDescB[5] = "towers";
        upgradeDescC[5] = "explode";
        //icons
        upgradeIcons[0] = animatedSprites.get("upgradeIC")[62];
        upgradeIcons[1] = animatedSprites.get("upgradeIC")[65];
        upgradeIcons[2] = animatedSprites.get("upgradeIC")[38];

        upgradeIcons[3] = animatedSprites.get("upgradeIC")[64];
        upgradeIcons[4] = animatedSprites.get("upgradeIC")[63];
        upgradeIcons[5] = animatedSprites.get("upgradeIC")[37];
    }

    @Override
    protected void upgradeEffect(int id) {
        if (id == 0) {
            switch (nextLevelA) {
                case 0 -> {
                    boost.range = 0.2f;
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            SPECIAL_COLOR, "Range", "+" + (int) (boost.range * 100) + "%"));
                } case 1 -> range++;
                case 2 -> {
                    boost.range = 0.4f;
                    boost.firerate += 0.15f;
                    placeSound = sounds.get("crystalPlace");
                    damageSound = sounds.get("crystalDamage");
                    breakSound = sounds.get("crystalBreak");
                    material = Material.gold;
                    name = "boosterMoney";
                    betweenIdleFrames = 2;
                    titleLines = new String[]{"Wealth Booster"};
                    extraInfo.add(0, (arg) -> selection.displayInfoLine(arg,
                            SPECIAL_COLOR, "Income", "+$" + MONEY_GAIN));
                    loadSprites();
                }
            }
        } if (id == 1) {
            switch (nextLevelB) {
                case 3 -> {
                    boost.damage = 0.3f;
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            SPECIAL_COLOR, "Damage", "+" + (int) (boost.damage * 100) + "%"));
                } case 4 -> {
                    boost.firerate += 0.2f;
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            SPECIAL_COLOR, "Firerate", "+" + (int) (boost.firerate * 100) + "%"));
                } case 5 -> {
                    boost.deathEffect = true;
                    boost.health = 1;
                    maxHp = 60;
                    hp = maxHp;
                    boost.damage = 0.5f;
                    placeSound = sounds.get("titaniumPlace");
                    breakSound = sounds.get("titaniumBreak");
                    damageSound = sounds.get("titaniumDamage");
                    name = "boosterExplosive";
                    titleLines = new String[]{"Unstable", "Booster"};
                    extraInfo.add((arg) -> selection.displayInfoLine(arg,
                            SPECIAL_COLOR, "Explosive", null));
                    loadSprites();
                }
            }
        }
    }

    private IntVector check(int i) {
        int checkX = -1;
        int checkY = -1;

        switch (i) {
            case 0 -> {
                if (range < 2) return null;
            }
            case 1 -> checkX = 0;
            case 2 -> {
                if (range < 2) return null;
                checkX = 1;
            }
            case 3 -> checkY = 0;
            case 4 -> {
                checkX = 1;
                checkY = 0;
            }
            case 5 -> {
                if (range < 2) return null;
                checkY = 1;
            }
            case 6 -> {
                checkX = 0;
                checkY = 1;
            }
            case 7 -> {
                if (range < 2) return null;
                checkX = 1;
                checkY = 1;
            }
        }

        return new IntVector(checkX, checkY);
    }

    private void clearBoost() {
        for (int i = 0; i < 8; i++) {
            IntVector check = check(i);
            if (check == null) continue;

            IntVector pos = tile.getGridPosition();
            int x = pos.x + check.x;
            int y = pos.y + check.y;
            Tower tower = tiles.get(x, y).tower;
            if (tower == null || tower instanceof Booster) continue;
            tower.removeBoost(boost);
        }
    }

    private void giveBoost() {
        for (int i = 0; i < 8; i++) {
            IntVector check = check(i);
            if (check == null) continue;

            IntVector pos = tile.getGridPosition();
            int x = pos.x + check.x;
            int y = pos.y + check.y;
            Tower tower = tiles.get(x, y).tower;
            if (tower == null || tower instanceof Booster) continue;
            tower.addBoost(boost);
        }
    }

    public static class Boost {

        public float health;
        public float damage;
        public float range;
        public float firerate;
        public boolean deathEffect;

    }
}
