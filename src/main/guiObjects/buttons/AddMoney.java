package main.guiObjects.buttons;

import main.guiObjects.GuiObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static main.Main.spritesAnimH;
import static main.Main.money;

public class AddMoney extends Button {

    public AddMoney(PApplet p, float x, float y, String type, boolean active){
        super(p,x,y,type,active);
        position = new PVector(x, y);
        size = new PVector(25, 25);
        spriteOne = spritesAnimH.get("moneyAddBT")[0];
        spriteTwo = spritesAnimH.get("moneyAddBT")[1];
        sprite = spriteOne;
        holdable = true;
    }

    public void main(ArrayList<GuiObject> guiObjects, int i){
        if (active){
            hover();
            display();
        }
    }

    public void action(){ //give money
        money += 1000;
    }
}