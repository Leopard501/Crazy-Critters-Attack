package main.gui.inGame;

import main.gui.guiObjects.buttons.UpgradeTower;
import main.towers.turrets.*;
import processing.core.PApplet;
import processing.sound.SoundFile;

import java.awt.*;
import java.util.function.Consumer;

import static main.Main.*;
import static main.sound.SoundUtilities.playSound;

public class Selection {

    private final PApplet p;

    public String name;
    public Turret turret;
    public boolean towerJustPlaced;
    private final SoundFile clickIn;
    private final SoundFile clickOut;

    public static final Color STAT_TEXT_COLOR = new Color(255, 0, 0);
    public static final Color BOOSTED_INFO_COLOR = new Color(255, 132, 0);
    public static final Color NORMAL_INFO_COLOR = Color.WHITE;

    /** what tower is selected */
    public Selection(PApplet p) {
        this.p = p;
        name = "null";
        clickIn = sounds.get("littleButtonIn");
        clickOut = sounds.get("littleButtonOut");
    }

    public void update() {
        clickOff();
    }

    /**
     * Switches what is selected.
     * @param id tower id
     */
    public void swapSelected(int id) {
        if (isPaused) return;
        Turret turret = (Turret) tiles.get(id).tower;
        if (this.turret != turret || name.equals("null")) {
            if (!towerJustPlaced) {
                playSound(clickIn, 1, 1);
            } else towerJustPlaced = false;
        }
        swapSelected(turret);
    }

    /**
     * Switches what is selected.
     * @param turret turret to select to
     */
    public void swapSelected(Turret turret) {
        if (isPaused) return;
        hand.held = "null";
        if (turret == null) return;
        if (this.turret != null) this.turret.visualize = false;
        this.turret = turret;
        name = turret.name;
        turret.visualize = true;
        inGameGui.sellButton.active = true;
        inGameGui.upgradeButtonB.active = true;
        inGameGui.upgradeIconB.active = true;
        inGameGui.priorityButton.active = true;
        inGameGui.upgradeButtonA.active = true;
        inGameGui.upgradeButtonB.position.y = 780;
        inGameGui.upgradeButtonA.position.y = 630;
        inGameGui.upgradeIconA.active = true;
        inGameGui.upgradeIconA.position.y = 610;
        inGameGui.upgradeIconB.position.y = 760;
        inGameGui.priorityButton.noTarget = !turret.hasPriority;
        updateUpgradeIcons();
    }

    /** deselect, hide stuff */
    private void clickOff() {
        if (turret == null) return;
        if (inputHandler.leftMousePressedPulse &&
                mouseOnBoard() && alive && !isPaused && !hand.held.equals("wall")) {
            if (!name.equals("null") && !towerJustPlaced) {
                playSound(clickOut, 1, 1);
            }
            name = "null";
            inGameGui.sellButton.active = false;
            inGameGui.priorityButton.active = false;
            inGameGui.upgradeButtonA.active = false;
            inGameGui.upgradeButtonB.active = false;
            inGameGui.upgradeIconA.active = false;
            inGameGui.upgradeIconB.active = false;
            turret.visualize = false;
            turret = null;
        }
    }

    private boolean mouseOnBoard() {
        boolean inCenter = boardMousePosition.x < BOARD_WIDTH && boardMousePosition.x > 0;
        boolean onTurret = (boardMousePosition.x > turret.tile.position.x ||
                        boardMousePosition.x < turret.tile.position.x - turret.size.x ||
                        boardMousePosition.y > turret.tile.position.y ||
                        boardMousePosition.y < turret.tile.position.y - turret.size.y);
        return inCenter && onTurret;
    }

    /**
     * Displays big circle around selected tower
     */
    public void turretOverlay() {
        if (name.equals("null") || turret == null) {
            return;
        }
        //display range and square
        float x = turret.tile.position.x - turret.size.x;
        float y = turret.tile.position.y - turret.size.y;
        if (turret instanceof Booster) {
            if (turret.range == 1) {
                p.rect(x, y - 50, 50, 150);
                p.rect(x - 50, y, 150, 50);
            } else {
                p.rect(x, y, turret.size.y, turret.size.y);
                p.rect(x - 50, y - 50, 150, 150);
            }
            p.noStroke();
            return;
        }
        if (turret instanceof SeismicTower) {
            float width = radians(((SeismicTower) turret).shockwaveWidth / 2);
            if (width < 6) {
                float startX = x + turret.size.x / 2;
                float startY = y + turret.size.y / 2;
                float angleA = turret.angle - HALF_PI + width;
                float angleB = turret.angle - HALF_PI - width;
                p.arc(startX, startY, turret.range * 2, turret.range * 2, angleB, angleA, PIE);
            }
        }
        p.rect(x, y, turret.size.y, turret.size.y);
        if (turret.getRange() > 1000) { //prevents lag
            p.noStroke();
            p.rect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
            return;
        }
        if (turret.boostedRange() > 0) p.stroke(InGameGui.BOOSTED_TEXT_COLOR.getRGB());
        p.circle(turret.tile.position.x - (turret.size.x / 2), turret.tile.position.y - (turret.size.y / 2), turret.getRange() * 2);
        p.noStroke();
    }

    public void display() {
        if (name.equals("null") || turret == null) {
            return;
        }

        background();
        displayTitle(p, turret.titleLines);
        displayInfo();
        displayStats();
        updateUpgradeIcons();
        upgradeButton(0, turret.nextLevelA, inGameGui.upgradeButtonA);
        upgradeButton(150, turret.nextLevelB, inGameGui.upgradeButtonB);
        priorityButton();
        sellButton();
    }

    private void background() {
        if (turret.titleLines.length == 1) {
            p.image(staticSprites.get("selectionSinglePn"), 900, 212);
        } else {
            p.image(staticSprites.get("selectionDoublePn"), 900, 212);
        }
    }

    public void displayTitle(PApplet p, String[] titleLines) {
        p.textAlign(CENTER);
        p.fill(InGameGui.MAIN_TEXT_COLOR.getRGB(), 254);
        p.textFont(h2);
        if (titleLines.length == 1) {
            p.text(titleLines[0], 1000, 235 + 12);
        } else {
            p.text(titleLines[0], 1000, 235);
            p.text(titleLines[1], 1000, 235 + 25);
        }
    }

    private void displayInfo() {
        //health
        p.textFont(h4);
        p.textAlign(LEFT);
        displayInfoLine(0, turret.boostedMaxHp() > 0 ?
                        BOOSTED_INFO_COLOR :
                        NORMAL_INFO_COLOR,
                "Health",
                turret.hp + "/" + turret.getMaxHp());

        int nextLine = 1;

        //booster
        if (!(turret instanceof Booster)) {
            //damage
            displayInfoLine(1, turret.boostedDamage() > 0 ?
                            BOOSTED_INFO_COLOR :
                            NORMAL_INFO_COLOR,
                    "Damage",
                    nfc(turret.getDamage())
            );

            //firerate (delay)
            displayInfoLine(2, turret.boostedFirerate() > 0 ?
                            BOOSTED_INFO_COLOR :
                            NORMAL_INFO_COLOR,
                    "Reload",
                    turret.getDelay() <= 0 ?
                            "0" :
                            nf(turret.getDelay(), 1, 1) + "s"
            );

            nextLine = 3;
        }

        for (Consumer<Integer> extraStat : turret.extraInfo) {
            extraStat.accept(nextLine);
            nextLine++;
        }
    }

    public void displayInfoLine(int line, Color color, String left, String right) {
        int y = 286 + (20 * line);
        if (line < 0) {
            y = 525 + (20 * line);
        }

        //line
        p.stroke(255, 75);
        if (line >= 0) {
            p.line(905, y + 3, 1095, y + 3);
        } else {
            p.line(905, y - 17, 1095, y - 17);
        }

        //text
        p.textFont(monoLarge);
        p.fill(color.getRGB(), 254);
        p.textAlign(LEFT);
        p.text(left, 905, y);
        if (right != null) {
            p.textAlign(RIGHT);
            p.text(right, 1095, y);
        }
    }

    private void displayStats() {
        turret.statsDisplay.run();
    }

    private void updateUpgradeIcons() {
        inGameGui.upgradeButtonA.updateGrey();
        inGameGui.upgradeButtonB.updateGrey();
        if (!inGameGui.upgradeButtonA.greyed) {
            inGameGui.upgradeIconA.sprite = turret.upgradeIcons[turret.nextLevelA];
        } else inGameGui.upgradeIconA.sprite = null;
        if (!inGameGui.upgradeButtonB.greyed && turret.nextLevelB < turret.upgradeIcons.length) {
            inGameGui.upgradeIconB.sprite = turret.upgradeIcons[turret.nextLevelB];
        } else inGameGui.upgradeIconB.sprite = null;
    }

    private void upgradeButton(int offset, int nextLevel, UpgradeTower upgradeButton) {
        Color fillColor = new Color(20, 20, 50, 254);
        p.textAlign(CENTER);
        if (!upgradeButton.greyed) {
            boolean canAfford = money >= turret.upgradePrices[nextLevel];
            if (!canAfford) fillColor = new Color(100, 100, 100, 254);
            p.fill(fillColor.getRGB());
            p.textFont(h2);
            p.text(turret.upgradeTitles[nextLevel], 1000, 584 + offset);
            p.textFont(h4);
            p.textAlign(LEFT);
            p.text(turret.upgradeDescA[nextLevel], 910, 615 + offset);
            p.text(turret.upgradeDescB[nextLevel], 910, 635 + offset);
            p.text(turret.upgradeDescC[nextLevel], 910, 655 + offset);
            p.textAlign(CENTER);
            p.fill(new Color(20, 20, 50, 254).getRGB());
            p.textFont(monoMedium);
            if (turret.upgradePrices[nextLevel] >= 100_000) {
                p.text("$" + nfc(turret.upgradePrices[nextLevel] / 1000) + "k", BOARD_WIDTH + 150, 690 + offset);
            } else {
                p.text("$" + nfc(turret.upgradePrices[nextLevel]), BOARD_WIDTH + 150, 690 + offset);
            }
        } else {
            fillColor = new Color(100, 100, 100, 254);
            p.fill(fillColor.getRGB());
            p.textFont(h2);
            p.text("N/A", 1000, 584 + offset);
            p.textFont(h4);
            p.textAlign(LEFT);
            p.text("No more", 910, 615 + offset);
            p.text("upgrades", 910, 635 + offset);
        }
        // squares
        int x = 910;
        int y = 680;
        int size = 10;
        if (turret.nextLevelB > 5 || turret.nextLevelA > 2) {
            //little x
            p.stroke(fillColor.getRGB());
            p.line(x + 4 * size, y + offset, x + 5 * size, y + offset + size);
            p.line(x + 5 * size, y + offset, x + 4 * size, y + offset + size);
        }
        if (upgradeButton == inGameGui.upgradeButtonA) { //A
            for (int i = 0; i < 3; i++) {
                if (nextLevel <= i) {
                    p.noFill();
                    p.stroke(fillColor.getRGB());
                } else {
                    p.fill(fillColor.getRGB());
                    p.noStroke();
                }
                p.rect(x + (size * 2 * i), y + offset, size, size);
            }
        } else { //B
            for (int i = 3; i < 6; i++) {
                if (nextLevel <= i) {
                    p.noFill();
                    p.stroke(fillColor.getRGB());
                } else {
                    p.fill(fillColor.getRGB());
                    p.noStroke();
                }
                p.rect(x + (size * 2 * (i-3)), y + offset, size, size);
            }
        }
    }

    private void priorityButton() {
        p.textFont(monoMedium);
        p.textAlign(CENTER);
        if (turret.hasPriority) {
            p.fill(NORMAL_INFO_COLOR.getRGB(), 254);
            p.text(turret.priority.text, 1000, inGameGui.priorityButton.position.y + 6);
        }
    }

    private void sellButton() {
        p.textFont(h2);
        p.textAlign(CENTER);
        p.fill(InGameGui.MAIN_TEXT_COLOR.getRGB(), 254);
        p.text("Sell", 957, 885);
        p.textFont(monoMedium);
        p.text("$" + nfc(floor(turret.getValue() * .8f)),
                900 + 150, inGameGui.sellButton.position.y + 5);
    }

    public void changePriority() {
        if (name.equals("null") || turret == null) return;
        if (turret.priority == Turret.Priority.Weak) {
            if (turret.effect != null) {
                turret.priority = Turret.Priority.Unbuffed;
            } else {
                turret.priority = Turret.Priority.Close;
            }
        } else if (turret.priority == Turret.Priority.Unbuffed) {
            turret.priority = Turret.Priority.Close;
        } else turret.priority = Turret.Priority.values()[turret.priority.ordinal() + 1];
    }

    public void upgradeBottom() {
        if (name.equals("null") || turret == null) return;
        turret.upgrade(1, false);
    }

    public void upgradeTop() {
        if (name.equals("null") || turret == null) return;
        turret.upgrade(0, false);
    }

    public void sell() {
        if (name.equals("null") || turret == null) return;
        selection.turret.die(true);
        inGameGui.priorityButton.active = false;
        inGameGui.upgradeButtonA.active = false;
        inGameGui.upgradeButtonB.active = false;
        inGameGui.upgradeIconA.active = false;
        inGameGui.upgradeIconB.active = false;
        selection.name = "null";
    }
}