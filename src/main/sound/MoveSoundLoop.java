package main.sound;

import processing.core.PApplet;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class MoveSoundLoop {

    private final FadeSoundLoop loop;
    private final float maxEnemyNum;

    private int count;

    public MoveSoundLoop(PApplet p, String name, float maxEnemyNum) {
        loop = new FadeSoundLoop(p, name);
        this.maxEnemyNum = maxEnemyNum;
    }

    public void update() {
        loop.setTargetVolume((float) min(1, sqrt(count / maxEnemyNum)));
        loop.update();
        count = 0;
    }

    public void increment() {
        count++;
    }
}
