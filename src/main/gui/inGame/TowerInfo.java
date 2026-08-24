package main.gui.inGame;

import processing.core.PApplet;

import java.awt.*;

import static main.Main.*;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

public class TowerInfo {

    public static void displayTurretInfo(PApplet p, Class<?> turretClass) {
        p.image(staticSprites.get("towerBuyPn"), 900, 212);

        if (turretClass == null) return;

        String pid;
        String description;
        char shortcut;
        String title1;
        String title2;
        int price;
        try {
            pid = (String) turretClass.getField("pid").get(null);
            description = (String) turretClass.getField("description").get(null);
            shortcut = (char) turretClass.getField("shortcut").get(null);
            title1 = (String) turretClass.getField("title1").get(null);
            title2 = (String) turretClass.getField("title2").get(null);
            price = (int) turretClass.getField("price").get(null);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            System.err.println("Something bad happened in TurretInfo: " + ex);
            return;
        }
        if (pid == null || description == null || shortcut == '`' || title1 == null || price == 0) {
            return;
        }

        //pid
        p.textFont(monoSmall);
        p.textAlign(LEFT);
        p.fill(0);
        p.text(pid, 910, 235);

        //CCA
        p.textAlign(RIGHT);
        p.text("CCA", 1080, 235);

        //title
        p.stroke(0);
        p.strokeWeight(2);
        p.line(910, 245, 1080, 245);
        p.textAlign(CENTER);
        p.textFont(h2);
        p.text(title1, 1000, 268);
        if (title2 != null) {
            p.text(title2, 1000, 291);
            p.line(910, 298, 1080, 298);
        } else {
            p.line(910, 275, 1080, 275);
        }

        //price
        p.textAlign(LEFT);
        p.textFont(h3);
        p.text("Price", 910, 320);
        p.textAlign(RIGHT);
        p.textFont(monoMedium);
        if (price > money) p.fill(new Color(0xAB0000).getRGB());
        p.text("$" + nfc(price), 1080, 320);
        p.line(910, 325, 1080, 325);
        p.fill(0);

        //shortcut
        p.textAlign(LEFT);
        p.textFont(h3);
        p.text("Shortcut", 910, 345);
        p.textAlign(RIGHT);
        p.textFont(monoMedium);
        p.text(shortcut, 1080, 345);
        p.line(910, 350, 1080, 350);

        //description
        p.textAlign(LEFT);
        p.textFont(h3);
        p.text("Description", 910, 370);
        p.line(910, 375, 1080, 375);
        p.textFont(pg);
        p.text(description, 910, 380, 170, 500);

        p.strokeWeight(1);
    }
}
