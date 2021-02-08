package main.gui.guiObjects.buttons;

import main.towers.Tower;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;

public class UpgradeTower extends Button {

    private final PImage SPRITE_RED;
    private final PImage SPRITE_GREY;

    public int id;
    public boolean greyed;

    public UpgradeTower(PApplet p, float x, float y, String type, boolean active, int id) {
        super(p,x,y,type,active);
        position = new PVector(x, y);
        size = new PVector(200, 150);
        spriteIdle = spritesAnimH.get("upgradeBT")[0]; //green out
        spritePressed = spritesAnimH.get("upgradeBT")[1]; //green in
        spriteHover = spritesAnimH.get("upgradeBT")[2]; //hover
        SPRITE_RED = spritesAnimH.get("upgradeBT")[3]; //red
        SPRITE_GREY = spritesAnimH.get("upgradeBT")[4]; //grey
        sprite = spriteIdle;
        this.id = id;
    }

    public void main() {
        if (active) {
            if (towers.size() > 0) {
                Tower tower = tiles.get(selection.id).tower;
                int thisNextLevel;
                int thisMax;
                int otherNextLevel;
                int otherMax;
                if (id == 0) {
                    thisNextLevel = tower.nextLevelA;
                    thisMax = tower.upgradeTitles.length / 2;
                    otherNextLevel = tower.nextLevelB;
                    otherMax = tower.upgradeTitles.length;
                } else {
                    thisNextLevel = tower.nextLevelB;
                    thisMax = tower.upgradeTitles.length;
                    otherNextLevel = tower.nextLevelA;
                    otherMax = tower.upgradeTitles.length / 2;
                }
                greyed = false;
                if (thisNextLevel == thisMax || (otherNextLevel == otherMax && thisNextLevel == thisMax-1)) {
                    greyed = true;
                    sprite = SPRITE_GREY;
                }
                else if (tower.upgradePrices[thisNextLevel] > money) sprite = SPRITE_RED;
                else hover();
                display();
            } else active = false;
        }
    }

    public void action() {
        Tower tower = tiles.get(selection.id).tower;
        int nextLevel;
        if (id == 0) nextLevel = tower.nextLevelA;
        else nextLevel = tower.nextLevelB;
        money -= tower.upgradePrices[nextLevel];
        tower.upgrade(id);
        inGameGui.flashA = 255;
    }
}