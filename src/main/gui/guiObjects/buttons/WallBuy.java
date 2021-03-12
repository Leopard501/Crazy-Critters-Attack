package main.gui.guiObjects.buttons;

import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;

public class WallBuy extends Button {

    public boolean depressed;
    public int timer;

    public WallBuy(PApplet p, float x, float y, String type, boolean active) {
        super(p,x,y,type,active);
        position = new PVector(x, y);
        size = new PVector(200, 24);
        spriteIdle = animatedSprites.get("wallBuyBT")[0];
        spritePressed = animatedSprites.get("wallBuyBT")[1];
        spriteHover = animatedSprites.get("wallBuyBT")[2];
        sprite = spriteIdle;
        depressed = false;
    }

    public void main(){
        timer++;
        if (active){
            hover();
            display();
        }
    }

    /**
     * If hovered or depressed.
     */
    public void hover() {
        if (p.mouseX < position.x+size.x/2 && p.mouseX > position.x-size.x/2 && p.mouseY < position.y+size.y/2 &&
                p.mouseY > position.y-size.y/2 && alive && active && !paused) {
            sprite = spriteHover;
            if (inputHandler.leftMousePressedPulse) {
                clickIn.stop();
                clickIn.play(1, volume);
            }
            if (p.mousePressed && p.mouseButton == LEFT) sprite = spritePressed;
            if (inputHandler.leftMouseReleasedPulse && timer > 20) {
                timer = 0;
                action();
                sprite = spritePressed;
            }
        }
        else sprite = spriteIdle;
        if (!hand.displayInfo.equals("null")) sprite = spritePressed;
    }

    public void action() {
        depressed = !depressed;
        selection.name = "null";
        //if already holding, stop
        if (hand.held.equals("wall")) hand.setHeld("null");
            //if not, do
        else hand.setHeld("wall");
    }
}