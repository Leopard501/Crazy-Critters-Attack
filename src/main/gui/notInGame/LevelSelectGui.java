package main.gui.notInGame;

import main.gui.SettingsGui;
import main.gui.guiObjects.buttons.MenuButton;
import processing.core.PApplet;
import processing.core.PVector;

import static main.Main.*;
import static main.misc.Utilities.closeSettingsMenu;

public class LevelSelectGui {

    private final PApplet p;

    private static MenuButton[] levelSelectButtons;
    private static MenuButton settingsButton;
    private static MenuButton goToTitle;

    /** Exists so LevelSelectScreen and SelectLevel aren't pressed at the same time */
    public static int delay;

    /**
     * Creates the level select screen.
     * @param p the PApplet
     */
    public LevelSelectGui(PApplet p) {
        this.p = p;
        build();
    }

    private void build() {
        levelSelectButtons = new MenuButton[levels.length];
        settingsButton = new MenuButton(p, p.width/2f, p.height-100 - 50, "Settings", () -> {
            SettingsGui.delay = 1;
            if (isSettings) closeSettingsMenu();
            else isSettings = true;
        });
        goToTitle = new MenuButton(p, p.width/2f, p.height-100, "Back to Title", () ->
                transition(Screen.Title, new PVector(-1, 0)));
        float factor = (levelSelectButtons.length/2f) - 0.5f;
        for (int i = 0; i < levelSelectButtons.length; i++) {
            levelSelectButtons[i] = new MenuButton(p, p.width/2f, p.height/2f + (i-factor)*50, "level " + (i+1));
        }
    }

    public void update() {
        checkButtonsPressed();
        for (MenuButton levelSelectButton : levelSelectButtons) levelSelectButton.update();
        settingsButton.update();
        goToTitle.update();
    }

    private void checkButtonsPressed() {
        for (int i = 0; i < levelSelectButtons.length; i++) {
            if (levelSelectButtons[i].isPressed()) {
                currentLevel = i;
                isPlaying = false;
                isPaused = false;
                alive = true;
                transition(Screen.LoadGame, new PVector(1, 0));
            }
        }
    }

    public void display() {
        delay--;
        //big text
        p.fill(255, 254);
        p.textFont(title);
        p.textAlign(p.CENTER);
        p.text("Level Select [wip]", p.width/2f, 300);
        //buttons
        p.fill(200, 254);
        p.textFont(h4);
        for (MenuButton levelSelectButton : levelSelectButtons) {
            levelSelectButton.display();
            if (delay < 0) levelSelectButton.hover();
        }
        settingsButton.display();
        goToTitle.display();
    }
}
