package main.towers;

import main.particles.Debris;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;

public abstract class Tower {

    public PApplet p;

    public Tile tile;

    public String name;
    public float angle;
    public int delay;
    public float error;
    public PVector size;
    public int maxHp;
    public int hp;
    public int damage;
    public boolean hit;
    public PImage sprite;
    int barTrans;
    protected int tintColor;
    protected String debrisType;
    public int price;
    public int value;
    public boolean turret;
    public boolean visualize;
    public int priority;
    public int nextLevelZero;
    public int nextLevelOne;

    protected boolean[] upgradeSpecial;
    public int[] upgradePrices;
    protected int[] upgradeHealth;
    protected int[] upgradeDamage;
    protected int[] upgradeDelay;
    protected float[] upgradeError;
    public String[] upgradeNames;
    protected String[] upgradeDebris;
    public String[] upgradeTitles;
    public String[] upgradeDescOne;
    public String[] upgradeDescTwo;
    public String[] upgradeDescThree;
    public PImage[] upgradeIcons;
    public PImage[] upgradeSprites;

    public Tower(PApplet p, Tile tile) {
        this.p = p;
        this.tile = tile;

        name = "null";
        size = new PVector(120, 37);
        delay = 0;
        error = 0;
        this.maxHp = 1;
        hp = maxHp;
        hit = false;
        sprite = spritesH.get("nullWallTW");
        barTrans = 255;
        tintColor = 255;
        debrisType = "null";
        price = 0;
        turret = false;
        visualize = false;
        nextLevelOne = 2;
        upgradeSpecial = new boolean[4];
        upgradeDamage = new int[4];
        upgradeDelay = new int[4];
        upgradePrices = new int[4];
        upgradeHealth = new int[4];
        upgradeError = new float[4];
        upgradeNames = new String[4];
        upgradeDescOne = new String[4];
        upgradeDescTwo = new String[4];
        upgradeDescThree = new String[4];
        upgradeDebris = new String[4];
        upgradeTitles = new String[4];
        upgradeIcons = new PImage[4];
        upgradeSprites = new PImage[4];
    }

    public void main(){
        if (hp <= 0) die();
        value = (int)(((float)hp / (float)maxHp) * price);
        if (inputHandler.leftMousePressedPulse && p.mouseX < tile.position.x && p.mouseX > tile.position.x-size.x && p.mouseY < tile.position.y && p.mouseY > tile.position.y-size.y && alive){ //clicked on
            selection.swapSelected(tile.id);
        }
        display();
    }

    private void display() {
        if (hit){ //change to red if under attack
            tintColor = 0;
            hit = false;
        }
        p.tint(255,tintColor,tintColor);
        p.image(sprite,tile.position.x-size.x,tile.position.y-size.y);
        p.tint(255);
        //no inverted bars
        if (hp > 0) HpBar();
        if (tintColor < 255) tintColor += 20;
    }

    public void collideEN(int dangerLevel) { //if it touches an enemy, animate and loose health
        hp -= dangerLevel;
        hit = true;
        barTrans = 255;
        int num = (int)(p.random(1,4));
        for (int i = num; i >= 0; i--){ //spray debris
            particles.add(new Debris(p,(tile.position.x-size.x/2)+p.random((size.x/2)*-1,size.x/2), (tile.position.y-size.y/2)+p.random((size.y/2)*-1,size.y/2), p.random(0,360), debrisType));
        }
    }

    public void upgrade(int id) {
        price += upgradePrices[nextLevelOne];
        maxHp += upgradeHealth[nextLevelOne];
        hp += upgradeHealth[nextLevelOne];
        name = upgradeNames[nextLevelOne];
        debrisType = upgradeDebris[nextLevelOne];
        sprite = upgradeSprites[nextLevelOne];
        nextLevelOne++;
        if (nextLevelOne < upgradeNames.length) upgradeIconOne.sprite = upgradeIcons[nextLevelOne];
        else upgradeIconOne.sprite = spritesAnimH.get("upgradeIC")[0];
        int num = (int)(p.random(30,50)); //shower debris
        for (int j = num; j >= 0; j--){
            particles.add(new Debris(p,(tile.position.x-size.x/2)+p.random((size.x/2)*-1,size.x/2), (tile.position.y-size.y/2)+p.random((size.y/2)*-1,size.y/2), p.random(0,360), debrisType));
        }
//        path.nodeCheckObs();
    }

    public void die() {
        int num = (int)(p.random(30,50)); //shower debris
        for (int j = num; j >= 0; j--){
            particles.add(new Debris(p,(tile.position.x-size.x/2)+p.random((size.x/2)*-1,size.x/2), (tile.position.y-size.y/2)+p.random((size.y/2)*-1,size.y/2), p.random(0,360), debrisType));
        }
        if (selection.id > tile.id) selection.id--;
        if (selection.id == tile.id) selection.id = 0;
        tile.tower = null;
//        path.nodeCheckObs();
    }

    public void repair() {
        money -= ceil((float)(price) - (float)(value));
        hp = maxHp;
    }

    public void sell() {
        money += (int) (value * .8);
        die(); //creates particles (may need to change later)
    }

    protected void HpText(){ //displays the towers health
        p.text(hp, tile.position.x-size.x/2, tile.position.y + size.y/4);
    }

    protected void HpBar(){ //displays the towers health with style
        p.fill(0,255,0,barTrans);
        //after hit or if below 50%
        if (barTrans > 0 && hp > maxHp/2) barTrans--;
        p.noStroke();
        p.rect(tile.position.x-size.x, tile.position.y + size.y/4, (size.x)*(((float) hp)/((float) maxHp)), -6);
    }
}