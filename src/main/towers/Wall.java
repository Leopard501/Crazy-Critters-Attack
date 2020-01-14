package main.towers;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static main.Main.*;
import static main.util.MiscMethods.updateTowerArray;

public class Wall extends Tower {

    private CornerSpriteDS wood;
    private CornerSpriteDS stone;
    private CornerSpriteDS metal;
    private CornerSpriteDS crystal;
    private CornerSpriteDS ultimate;

    private PImage tlCSprite;
    private PImage trCSprite;
    private PImage blCSprite;
    private PImage brCSprite;
    private PImage tSSprite;
    private PImage bSSprite;
    private PImage lSSprite;
    private PImage rSSprite;

    public Wall(PApplet p, Tile tile) {
        super(p,tile);

        name = "woodWall";
        size = new PVector(50,50);
        maxHp = 50;
        hp = maxHp;
        hit = false;
        sprite = spritesH.get("woodWallTW");
        debrisType = "wood";
        price = 25;
        value = price;
        nextLevelB = 0;

        upgradePrices = new int[4];
        upgradeHealth = new int[4];
        upgradeNames = new String[4];
        upgradeDebris = new String[4];
        upgradeTitles = new String[4];
        upgradeIcons = new PImage[4];
        upgradeSprites = new PImage[4];
        setUpgrades();
        updateTowerArray();

        wood = new CornerSpriteDS();
        stone = new CornerSpriteDS();
        metal = new CornerSpriteDS();
        crystal = new CornerSpriteDS();
        ultimate = new CornerSpriteDS();
        loadSprites();
    }

    public void main(){
        if (hp <= 0) die();
        value = (int)(((float)hp / (float)maxHp) * price);
        display();
    }

    private void display() {
        if (hit) { //change to red if under attack
            tintColor = 0;
            hit = false;
        }
        float x = tile.position.x-size.x;
        float y = tile.position.y-size.y;

        p.tint(255,tintColor,tintColor);
        p.image(sprite,x,y);
        //sides
        if (tSSprite != null) p.image(tSSprite,x,y);
        if (rSSprite != null) p.image(rSSprite,x,y);
        if (bSSprite != null) p.image(bSSprite,x,y);
        if (lSSprite != null) p.image(lSSprite,x,y);
        //corners
        if (tlCSprite != null) p.image(tlCSprite,x,y);
        if (trCSprite != null) p.image(trCSprite,x,y);
        if (blCSprite != null) p.image(blCSprite,x,y);
        if (brCSprite != null) p.image(brCSprite,x,y);
        p.tint(255);

        if (tintColor < 255) tintColor += 20;
    }

    private void setUpgrades(){
        //price
        upgradePrices[0] = 50;
        upgradePrices[1] = 100;
        upgradePrices[2] = 225;
        upgradePrices[3] = 500;
        //heath
        upgradeHealth[0] = 75;
        upgradeHealth[1] = 125;
        upgradeHealth[2] = 250;
        upgradeHealth[3] = 500;
        //names
        upgradeNames[0] = "stoneWall";
        upgradeNames[1] = "metalWall";
        upgradeNames[2] = "crystalWall";
        upgradeNames[3] = "ultimateWall";
        //debris
        upgradeDebris[0] = "stone";
        upgradeDebris[1] = "metal";
        upgradeDebris[2] = "crystal";
        upgradeDebris[3] = "ultimate";
        //titles
        upgradeTitles[0] = "Stone";
        upgradeTitles[1] = "Metal";
        upgradeTitles[2] = "Crystal";
        upgradeTitles[3] = "Titanium";
        //sprites
        upgradeSprites[0] = spritesH.get("stoneWallTW");
        upgradeSprites[1] = spritesH.get("metalWallTW");
        upgradeSprites[2] = spritesH.get("crystalWallTW");
        upgradeSprites[3] = spritesH.get("ultimateWallTW");
    }

    public void updateSprite() {
        Tile searchTile;
        boolean tl;
        boolean t;
        boolean tr;
        boolean l;
        boolean r;
        boolean bl;
        boolean b;
        boolean br;

        searchTile = tiles.get(((int)tile.position.x/50)-1,((int)tile.position.y/50)-1);
        tl = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50),((int)tile.position.y/50)-1);
        t = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50)+1,((int)tile.position.y/50)-1);
        tr = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50)-1,((int)tile.position.y/50));
        l = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50)+1,((int)tile.position.y/50));
        r = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50)-1,((int)tile.position.y/50)+1);
        bl = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50),((int)tile.position.y/50)+1);
        b = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));
        searchTile = tiles.get(((int)tile.position.x/50)+1,((int)tile.position.y/50)+1);
        br = (searchTile != null && searchTile.tower != null && searchTile.tower.name.equals(name));

        //is corner
        boolean tlC = false;
        boolean trC = false;
        boolean blC = false;
        boolean brC = false;
        //is corner concave or convex
        boolean tlCcv = false;
        boolean trCcv = false;
        boolean blCcv = false;
        boolean brCcv = false;
        //is side
        boolean tS = false;
        boolean bS = false;
        boolean lS = false;
        boolean rS = false;

        tS = !t;
        bS = !b;
        lS = !l;
        rS = !r;
//        System.out.println(tile.position);
//        System.out.print("l: " + l + " t: " + t + " tl: " + tl);
//        System.out.print(" tlC & tlCcv: " + ((l && t) && !tl));
//        System.out.print(" tlC & !tlCcv: " + !(t || l));
//        System.out.println();
        if ((l && t) && !tl) {
            tlC = true;
            tlCcv = true;
        } else tlC = !(t || l);
        if ((r && t) && !tr) {
            trC = true;
            trCcv = true;
        } else trC = !(t || r);
        if ((l && b) && !bl) {
            blC = true;
            blCcv = true;
        } else blC = !(b || l);
        if ((r && b) && !br) {
            brC = true;
            brCcv = true;
        } else brC = !(b || r);

        CornerSpriteDS spriteDS = wood;
        if (name.equals("wood") || name.equals("woodWall")) spriteDS = wood;
        if (name.equals("stone") || name.equals("stoneWall")) spriteDS = stone;
        if (name.equals("metal") || name.equals("metalWall")) spriteDS = metal;
        if (name.equals("crystal") || name.equals("crystalWall")) spriteDS = crystal;
        if (name.equals("ultimate") || name.equals("ultimateWall")) spriteDS = ultimate;
        if (tS) tSSprite = spriteDS.t;
        else tSSprite = null;
        if (bS) bSSprite = spriteDS.b;
        else bSSprite = null;
        if (lS) lSSprite = spriteDS.l;
        else lSSprite = null;
        if (rS) rSSprite = spriteDS.r;
        else rSSprite = null;

        //oops, invert c/v
        if (tlC) tlCSprite = spriteDS.get(true,true,!tlCcv);
        else tlCSprite = null;
        if (trC) trCSprite = spriteDS.get(true,false,!trCcv);
        else trCSprite = null;
        if (blC) blCSprite = spriteDS.get(false,true,!blCcv);
        else blCSprite = null;
        if (brC) brCSprite = spriteDS.get(false,false,!brCcv);
        else brCSprite = null;
    }

    private void loadSprites() {
        for (int i = 0; i < 5; i++) {
            CornerSpriteDS spriteDS = null;
            String name = "null";
            switch (i) {
                case 0:
                    spriteDS = wood;
                    name = "Wood";
                    break;
                case 1:
                    spriteDS = stone;
                    name = "Stone";
                    break;
                case 2:
                    spriteDS = metal;
                    name = "Metal";
                    break;
                case 3:
                    spriteDS = crystal;
                    name = "Crystal";
                    break;
                case 4:
                    spriteDS = ultimate;
                    name = "Ultimate";
                    break;
            }
            String idA = "null";
            String idB = "null";
            String idC = "null";
            for (int a = 0; a < 2; a++) {
                for (int b = 0; b < 2; b++) {
                    for (int c = 0; c < 2; c++) {
                        if (a == 0) idA = "T";
                        if (a == 1) idA = "B";
                        if (b == 0) idB = "l";
                        if (b == 1) idB = "r";
                        if (c == 0) idC = "c";
                        if (c == 1) idC = "v";
                        String id = idA+idB+idC;
                        spriteDS.add(spritesH.get(name + id + "WallTW"),idA,idB,idC);
                    }
                }
            }
            spriteDS.t = spritesH.get(name + "TWallTW");
            spriteDS.b = spritesH.get(name + "BWallTW");
            spriteDS.l = spritesH.get(name + "LWallTW");
            spriteDS.r = spritesH.get(name + "RWallTW");
        }
    }

    private static class CornerSpriteDS {

        CornerSpriteDSItem[] items;
        PImage t;
        PImage b;
        PImage l;
        PImage r;

        CornerSpriteDS() {
            items = new CornerSpriteDSItem[0];
        }

        PImage get(boolean tb, boolean lr, boolean cv) {
            PImage r = null;
            for (CornerSpriteDSItem item : items) {
                if (item.tb == tb && item.lr == lr && item.cv == cv) r = item.sprite;
            }
            return r;
        }

        void add(PImage sprite, String tbS, String lrS, String cvS) {
            boolean tb;
            boolean lr;
            boolean cv;
            tb = tbS.equals("T");
            lr = lrS.equals("l");
            cv = cvS.equals("c");
            CornerSpriteDSItem[] newItems = new CornerSpriteDSItem[items.length+1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = new CornerSpriteDSItem(sprite, tb, lr, cv);
            items = newItems;
        }

        private static class CornerSpriteDSItem {

            PImage sprite;
            boolean tb;
            boolean lr;
            boolean cv;

            CornerSpriteDSItem(PImage sprite, boolean tb, boolean lr, boolean cv) {
                this.sprite = sprite;
                this.tb = tb;
                this.lr = lr;
                this.cv = cv;
            }
        }
    }
}