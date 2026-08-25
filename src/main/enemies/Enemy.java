package main.enemies;

import main.Main;
import main.buffs.*;
import main.buffs.glued.Glued;
import main.buffs.glued.SpikyGlued;
import main.buffs.stunned.Frozen;
import main.buffs.stunned.Stunned;
import main.gui.guiObjects.PopupText;
import main.misc.Corpse;
import main.misc.GoreVortex;
import main.misc.Tile;
import main.particles.Floaty;
import main.particles.MiscParticle;
import main.particles.Ouch;
import main.pathfinding.Node;
import main.pathfinding.PathRequest;
import main.sound.MoveSoundLoop;
import main.towers.Tower;
import main.towers.turrets.Turret;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static main.Main.*;
import static main.misc.Utilities.*;
import static main.sound.SoundUtilities.playSoundRandomSpeed;

public class Enemy {

    public enum State {
        Moving,
        Attacking,
        Special
    }

    // used in weird ways, so inspection suppressed
    @SuppressWarnings("unused")
    public enum HitParticle {
        //lower case for string reasons
        glowOuch(new Color(0, 255, 195)),
        greenOuch(new Color(100, 166, 0)),
        leafOuch(new Color(19, 183, 25)),
        lichenOuch(new Color(144, 146, 133)),
        pinkOuch(new Color(255, 0, 255)),
        redOuch(new Color(216, 0, 0)),
        iceOuch(new Color(49, 135, 223)),
        mudOuch(new Color(111, 58, 0)),
        sapOuch(new Color(0xb76e09)),
        oilOuch(new Color(0x2d1a02)),
        brownLeafOuch(new Color(0xBB5E3B)),
        bluePuff(new Color(84, 180, 246)),
        greenPuff(new Color(100, 207, 123));

        public final Color tintColor;

        HitParticle(Color color) {
            this.tintColor = color;
        }
    }

    // used in weird ways, so inspection suppressed
    @SuppressWarnings("unused")
    public enum DamageType {
        burning(new Color(60,60,60), "fire"),
        blueBurning(new Color(0, 14, 64), "blueGreenFire"),
        decay(new Color(0,0,0), "decay"),
        poisoned(new Color(120, 180, 0), "poison"),
        glued(new Color(234, 229, 203), "glue"),
        energy(new Color(60, 60, 60), "energy"),
        electricity(new Color(60, 60, 60), "electricity"),
        nuclear(new Color(60, 60, 60), "nuclear"),
        electrified(new Color(60, 60, 60), "nuclear"),
        orangeMagic(new Color(232, 99, 17), "orangeMagic"),
        greenMagic(new Color(54, 211, 104), "greenMagic"),
        dark(new Color(79, 0, 128), "dark"),
        frozen(new Color(150, 225, 255), null),
        bleeding(null, null),
        stunned(null, null);

        public final Color finalTintColor;
        public final String particle;

        DamageType(Color color, String particle) {
            finalTintColor = color;
            this.particle = particle;
        }
    }

    /** measured in pixels per second */
    public float speed;
    public float speedModifier;
    public float rotation;
    public float radius;
    public boolean showBar;
    public int hp;
    public int maxHp;
    public int attackFrame;
    public int pfSize;
    public int intersectingIceCount;
    public int[] attackDmgFrames;
    /** something to do with glue */
    public int[] tempAttackDmgFrames;
    public boolean immobilized;
    public ArrayList<TurnPoint> trail;
    public PImage[] attackFrames;
    public HitParticle hitParticle;
    public DamageType lastDamageType;
    public String name;
    public PVector position;
    public PVector size;
    public State state = State.Moving;
    public int moneyDrop;
    public ArrayList<Buff> buffs;

    protected int damage;
    protected int walkDelay;
    protected int attackDelay;
    protected int corpseDelay;
    protected int corpseLifespan;
    protected int pathRequestWaitTimer;
    protected int moveFrame;
    protected int idleTime;
    protected float targetAngle;
    protected boolean overkill;
    protected PApplet p;
    protected PVector partsDirection;
    protected PVector corpseSize;
    protected PVector partSize;
    protected PVector dmgSourcePosition;
    protected float dmgSourceRadius;
    protected PImage[] moveFrames;
    protected PImage sprite;
    protected Color currentTintColor;
    protected SoundFile overkillSound;
    protected SoundFile dieSound;
    protected SoundFile attackSound;
    protected MoveSoundLoop moveSoundLoop;

    protected int attackCount;
    protected boolean attackCue;
    protected boolean targetMachine;
    protected Tower targetTower;

    protected Enemy(PApplet p, float x, float y) {
        this.p = p;

        buffs = new ArrayList<>();
        trail = new ArrayList<>();
        position = new PVector(roundTo(x, 25) + 12.5f, roundTo(y, 25) + 12.5f);
        size = new PVector(20, 20);
        rotation = radians(270);
        radius = 10;
        speed = 60;
        speedModifier = 1;
        moneyDrop = 1;
        damage = 1;
        maxHp = 20; //Hp <---------------------------
        hp = maxHp;
        hitParticle = HitParticle.redOuch;
        name = "";
        walkDelay = 0;
        attackDmgFrames = new int[]{0};
        tempAttackDmgFrames = new int[attackDmgFrames.length];
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        pfSize = 1;
        attackCount = 0;
        corpseSize = size;
        partSize = size;
        corpseDelay = down60ToFramerate(7);
        corpseLifespan = 8;
        lastDamageType = null;
    }

    protected Enemy(PApplet p, float x, float y,
            String name,
            PVector size,
            int pfSize,
            int radius,
            int speed,
            int moneyDrop,
            int damage,
            int hp,
            int[] attackDmgFrames,
            int walkDelay,
            int attackDelay,
            PVector corpseSize,
            PVector partSize,
            int corpseLifespan,
            int corpseDelay,
            HitParticle hitParticle,
            String dieSound,
            String overkillSound,
            String attackSound,
            String moveSoundLoop
    ) {
        this.p = p;

        buffs = new ArrayList<>();
        trail = new ArrayList<>();
        position = new PVector(roundTo(x, 25) + 12.5f, roundTo(y, 25) + 12.5f);
        tempAttackDmgFrames = new int[attackDmgFrames.length];
        rotation = PI + HALF_PI;
        speedModifier = 1;
        attackCount = 0;

        this.name = name;
        this.size = size;
        this.pfSize = pfSize;
        this.radius = radius;
        this.speed = speed;
        this.moneyDrop = moneyDrop;
        this.damage = damage;
        this.maxHp = hp;
        this.hp = maxHp;
        this.attackDmgFrames = attackDmgFrames;
        this.walkDelay = walkDelay;
        this.attackDelay = attackDelay;
        this.corpseSize = corpseSize;
        this.corpseLifespan = corpseLifespan;
        this.corpseDelay = corpseDelay;
        this.partSize = partSize;
        this.hitParticle = hitParticle;
        this.dieSound = sounds.get(dieSound);
        this.overkillSound = sounds.get(overkillSound);
        this.attackSound = sounds.get(attackSound);
        this.moveSoundLoop = moveSoundLoops.get(moveSoundLoop);

        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        loadStuff();
    }

    public void update(int i) {
        boolean dead = false; //if its gotten this far, it must be alive?
        swapPoints(false);

        if (!isPaused && !immobilized) {
            rotation = normalizeAngle(rotation);
            targetAngle = normalizeAngle(targetAngle);
            rotation += getAngleDifference(targetAngle, rotation) / 10;

            switch (state) {
                case Moving -> move();
                case Attacking -> attack();
            }

            //prevent wandering
            if (trail.isEmpty() && state != State.Attacking) pathRequestWaitTimer++;
            if (pathRequestWaitTimer > FRAMERATE) {
                requestPath(i);
                pathRequestWaitTimer = 0;
            }
        }
        if (!trail.isEmpty() && intersectTurnPoint()) swapPoints(true);
        //buffs
        for (int j = buffs.size() - 1; j >= 0; j--) {
            Buff buff = buffs.get(j);
            buff.update();
        }
        //if health is 0, die
        if (hp <= 0) dead = true;
        if (dead) die();
    }

    /**
     * Adds money with a popup.
     * Plays death sound.
     * If overkill, fling bits everywhere, else create a corpse.
     * Clear buffs.
     * Remove from array.
     */
    protected void die() {
        Main.money += moneyDrop;
        popupTexts.add(new PopupText(p, new PVector(position.x, position.y), moneyDrop));

        DamageType type = lastDamageType;
        if (!buffs.isEmpty()) {
            type = DamageType.valueOf(buffs.get((int) p.random(buffs.size() - 1)).name);
        }
        if (overkill) playSoundRandomSpeed(p, overkillSound, 1);
        else playSoundRandomSpeed(p, dieSound, 1);

        if (settings.isHasGore()) goreyDeathEffect(type);
        else cleanDeathEffect();

        for (Buff buff : buffs) {
            buff.dieEffect();
        }

        enemies.remove(this);
    }

    protected void goreyDeathEffect(DamageType type) {
        if (overkill) {
            for (int j = 0; j < animatedSprites.get(name + "PartsEN").length; j++) {
                if (dmgSourcePosition != null) {
                    corpses.add(new GoreVortex(p, position, partSize, rotation,
                            adjustPartVelocityToFramerate(partsDirection),
                            currentTintColor,
                            corpseLifespan, j, type, name + "Parts", hitParticle,
                            dmgSourcePosition, dmgSourceRadius));
                } else {
                    corpses.add(new Corpse(p, position, partSize, rotation,
                            adjustPartVelocityToFramerate(partsDirection),
                            currentTintColor,
                            0, corpseLifespan, type, name + "Parts", hitParticle, j, false));
                }
            }
            for (int k = 0; k < sq(pfSize); k++) {
                Corpse.bloodSplatter(p, position, size, hitParticle);
            }
        } else
            corpses.add(new Corpse(p, position, corpseSize,
              rotation + p.random(radians(-5), radians(5)), new PVector(0, 0),
              currentTintColor, corpseDelay, corpseLifespan, type, name + "Die",
              hitParticle, 0, true));
    }

    protected void cleanDeathEffect() {
        int num = (int) p.random(pfSize, pfSize * pfSize);
        if (!overkill) {
            for (int i = 0; i < num * 5; i++) {
                PVector partPos = getParticlePosition();
                topParticles.add(new Floaty(p, partPos.x, partPos.y, p.random(20, 30), "smokeCloud"));
            }
        } else {
            for (int i = 0; i < num * 10; i++) {
                PVector partPos = getParticlePosition();
                topParticles.add(new Floaty(p, partPos.x, partPos.y, p.random(50 * pfSize, 100 * pfSize), "smokeCloud"));
            }
        }
        for (int j = num; j >= 0; j--) { //sprays puff
            PVector partPos = getParticlePosition();
            topParticles.add(new Ouch(p, partPos.x, partPos.y, p.random(0, 360), "greyPuff"));
        }
    }

    protected PVector getParticlePosition() {
        return getRandomPointInRange(p, position, size.mag() * 0.4f);
    }

    protected PVector adjustPartVelocityToFramerate(PVector partVelocity) {
        return partVelocity.setMag(partVelocity.mag() * up60ToFramerate(1));
    }

    protected void move() {
        if (moveSoundLoop != null) moveSoundLoop.increment();
        PVector m = PVector.fromAngle(rotation);
        float pixelsMoved = getActualSpeed() / FRAMERATE;
        m.setMag(pixelsMoved);
        //don't move if no path
        if (!trail.isEmpty()) position.add(m);
    }

    public float getActualSpeed() {
        float actualSpeed = speed * speedModifier;
        if (actualSpeed > 20 && intersectCombatPoint()) actualSpeed = 20;
        return actualSpeed;
    }

    /** handle animation states */
    protected void animate() {
        if (!immobilized) {
            switch (state) {
                case Attacking -> {
                    if (attackFrame >= attackFrames.length) attackFrame = 0;
                    sprite = attackFrames[attackFrame];
                    idleTime++;
                    if (attackFrame < attackFrames.length - 1) {
                        if (idleTime >= attackDelay) {
                            attackFrame += 1;
                            idleTime = 0;
                        }
                    } else attackFrame = 0;
                } case Moving -> {
                    idleTime++;
                    if (moveFrame < moveFrames.length - 1) {
                        if (idleTime >= walkDelay) {
                            moveFrame++;
                            idleTime = 0;
                        }
                    } else moveFrame = 0;
                    sprite = moveFrames[moveFrame];
                }
            }
        }
        //shift back to normal
        currentTintColor = incrementColorTo(currentTintColor, up60ToFramerate(20), new Color(255, 255, 255));
    }

    /**
     * Displays but tinted black and semi-transparent.
     * Calls to animate sprite.
     */
    public void displayShadow() {
        if (!isPaused) animate();
        p.pushMatrix();
        p.tint(0, 60);
        int x = 1;
        if (pfSize > 1) x++;
        p.translate(position.x + x, position.y + x);
        p.rotate(rotation);
        if (sprite != null) p.image(sprite, -size.x / 2, -size.y / 2);
        p.tint(255);
        p.popMatrix();
    }

    /** Display main sprite */
    public void display() {
        if (sprite == null) return;
        if (isDebug) for (int i = trail.size() - 1; i > 0; i--) {
            trail.get(i).display();
        }
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.rotate(rotation);
        p.tint(currentTintColor.getRGB());
        p.image(sprite, -size.x / 2, -size.y / 2);
        p.tint(255);
        p.popMatrix();
        if (isDebug) {
            PVector pfPosition = new PVector(position.x - ((pfSize - 1) * 12.5f), position.y - ((pfSize - 1) * 12.5f));
            p.stroke(0, 0, 255);
            p.line(pfPosition.x - 10, pfPosition.y, pfPosition.x + 10, pfPosition.y);
            p.stroke(255, 0, 0);
            p.line(pfPosition.x, pfPosition.y - 10, pfPosition.x, pfPosition.y + 10);
            p.noFill();
            p.stroke(255, 0, 255);
            p.rect(pfPosition.x - 12.5f, pfPosition.y - 12.5f, pfSize * 25, pfSize * 25);
        }
        for (Buff buff : buffs) buff.display();
    }

    public void damageVortex(int damage, String buffName, float effectLevel, float effectDuration, Turret turret,
                             DamageType damageType, PVector direction, PVector sourcePos, float sourceRadius) {
        dmgSourcePosition = sourcePos;
        dmgSourceRadius = sourceRadius;

        damageWithBuff(damage, buffName, effectLevel, effectDuration, turret, true, damageType, direction);
    }

    /**
     * Applies damage to the enemy, can also apply a buff
     * @param damage the amount of damage to be taken
     * @param buffName the name of the buff to be applied, nullable
     * @param effectLevel level of the buff to be applied
     * @param effectDuration duration of the buff to be applied
     * @param turret turret that caused the damage, nullable
     * @param displayParticles if it should create particles
     * @param damageType determines what effect to apply to corpse
     * @param direction determines where parts will be flung, (0, 0) for everywhere
     */
    public void damageWithBuff(int damage, String buffName, float effectLevel, float effectDuration, Turret turret,
                               boolean displayParticles, DamageType damageType, PVector direction) {
        lastDamageType = damageType;
        overkill = damage >= maxHp;
        partsDirection = direction;
        hp -= damage;
        if (turret != null) {
            int statDamage = damage;
            if (hp <= 0) {
                turret.killsTotal++;
                statDamage = damage + hp;
            }
            if (statDamage > 0) turret.damageTotal += statDamage;
        }
        int effectTimer = p.frameCount + 10;
        //prevent duplicates
        if (!buffs.isEmpty()) {
            for (int j = 0; j < buffs.size(); j++) {
                Buff buff = buffs.get(j);
                if (buff.matches(this) && buff.name.equals(buffName)) {
                    effectTimer = buff.effectTimer;
                    buffs.remove(j);
                    break;
                }
            }
        }
        if (buffName != null) {
            Buff buff;
            switch (buffName) {
                case "burning" -> buff = new Burning(p, this, effectLevel, effectDuration, turret);
                case "blueBurning" -> buff = new BlueBurning(p, this, effectLevel, effectDuration, turret);
                case "bleeding" -> buff = new Bleeding(p, this, effectLevel, effectDuration, turret);
                case "poisoned" -> buff = new Poisoned(p, this, turret);
                case "decay" -> {
                    if (turret != null) buff = new Decay(p, this, effectLevel, effectDuration, turret);
                    else buff = new Decay(p, this, 1, 120, null);
                }
                case "glued" -> buff = new Glued(p, this, effectLevel, effectDuration, turret);
                case "spikeyGlued" -> buff = new SpikyGlued(p, this, effectLevel, effectDuration, turret);
                case "stunned" -> buff = new Stunned(p, this, turret);
                case "frozen" -> buff = new Frozen(p, this, turret);
                case "electrified" -> buff = new Electrified(p, this, (int) effectLevel, effectDuration, turret);
                default -> buff = null;
            }
            if (buff != null) {
                boolean duplicate = false;
                for (Buff other : buffs) {
                    if (buff.name.equals(other.name)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    //in order to prevent resetting timer after buff is reapplied
                    buff.effectTimer = effectTimer;
                    buffs.add(buff);
                }
            }
        }
        damageEffect(displayParticles);
    }

    /**
     * Applies damage to the enemy
     * @param damage amount of damage to be applied
     * @param turret the turret that caused the damage, nullable
     * @param damageType determines what effect to apply to corpse
     * @param direction where parts will be flung, (0, 0) for everywhere
     * @param displayParticles whether it should spawn particles
     */
    public void damageWithoutBuff(int damage, Turret turret, DamageType damageType, PVector direction, boolean displayParticles) {
        lastDamageType = damageType;
        overkill = damage >= maxHp;
        partsDirection = direction;
        hp -= damage;
        if (turret != null) {
            if (hp <= 0) {
                turret.killsTotal++;
                turret.damageTotal += damage + hp;
            } else turret.damageTotal += damage;
        }
        damageEffect(displayParticles);
    }

    /**
     * Display hp bar.
     * Tint.
     * @param particles whether to display hurt particles
     */
    protected void damageEffect(boolean particles) {
        if (hp == maxHp) return;
        showBar = true;
        if (particles) {
            int num = pfSize;
            int chance = 5;
            if (notRecentlyHit()) {
                num = (int) p.random(pfSize, pfSize * pfSize);
                chance = 0;
            }
            if (p.random(6) > chance) {
                for (int j = num; j >= 0; j--) { //sprays ouch
                    PVector partPos = getParticlePosition();
                    if (settings.isHasGore()) topParticles.add(
                            new Ouch(p, partPos.x, partPos.y, p.random(0, 360), hitParticle.name()));
                    else topParticles.add(
                            new MiscParticle(p, partPos.x, partPos.y, p.random(0, 360), "stun"));
                }
            }
            currentTintColor = new Color(hitParticle.tintColor.getRGB());
        }
    }

    private boolean notRecentlyHit() {
        int totalTintColor = currentTintColor.getRed() + currentTintColor.getGreen() + currentTintColor.getBlue();
        return totalTintColor >= 700;
    }

    public void hpBar() {
        if (!showBar) return;
        Color barColor = new Color(255, 0, 0);
        float barWidth = size.x * (hp / (float) maxHp);
        p.stroke(barColor.getRGB());
        p.noFill();
        p.rect(position.x - size.x / 2, position.y + size.y / 2 + 6, size.x, 6);
        p.fill(barColor.getRGB());
        p.rect(position.x - size.x / 2, position.y + size.y / 2 + 6, barWidth, 6);
    }

    protected void loadStuff() {
        hp = maxHp;
        System.arraycopy(attackDmgFrames, 0, tempAttackDmgFrames, 0, tempAttackDmgFrames.length);
        attackFrames = animatedSprites.get(name + "AttackEN");
        moveFrames = animatedSprites.get(name + "MoveEN");
        sprite = moveFrames[0];
        currentTintColor = new Color(255, 255, 255);
    }

    /**
     * Angles towards target.
     * Damages target turret or machine.
     * Messes with state a bit.
     * Prevents attacking multiple times at once.
     */
    protected void attack() {
        boolean dmg = false;
        for (int frame : tempAttackDmgFrames) {
            if (attackFrame == frame) {
                if (attackDelay > 1) attackCount++;
                dmg = true;
                break;
            }
        }
        //all attackCount stuff prevents attacking multiple times
        if (!dmg) attackCount = 0;
        if (attackCount > 1) dmg = false;
        if (targetTower != null && targetTower.alive) {
            if (pfSize > 2) { //angle towards tower correctly
                PVector t = new PVector(targetTower.tile.position.x - 25, targetTower.tile.position.y - 25);
                targetAngle = findAngleBetween(t, position);
            }
            moveFrame = 0;
            //actually do damage to towers
            if (dmg) {
                targetTower.damage(damage);
                //ignored if no assigned attack sound
                playSoundRandomSpeed(p, attackSound, 1);
            }
        } else if (!targetMachine && attackFrame == 0) state = State.Moving;
        else if (targetMachine) {
            moveFrame = 0;
            //actually do damage to machines
            if (dmg) {
                machine.damage(damage);
                //ignored if no assigned attack sound
                playSoundRandomSpeed(p, attackSound, 1);
            }
        }
        if (!attackCue && attackFrame == 0) state = State.Moving;
    }

    public boolean onScreen() {
        return position.x > 0 && position.x < 900 && position.y > 0 && position.y < 900;
    }

    public static Enemy get(PApplet p, String name, PVector pos) {
        return EnemyFactory.get(p, name, pos);
    }

    //pathfinding ------------------------------------------------------------------------------------------------------

    protected boolean intersectTurnPoint() {
        TurnPoint point = trail.get(trail.size() - 1);
        PVector p = point.position;
        float tpSize;
        if (point.combat) tpSize = 3;
        else tpSize = 15;
        PVector pfPosition = new PVector(position.x - ((pfSize - 1) * 12.5f), position.y - ((pfSize - 1) * 12.5f));
        return (pfPosition.x > p.x - tpSize + (NODE_SIZE / 2f)
                    && pfPosition.x < p.x + tpSize + (NODE_SIZE / 2f))
                && (pfPosition.y > p.y - tpSize + (NODE_SIZE / 2f)
                    && pfPosition.y < p.y + tpSize + (NODE_SIZE / 2f));
    }

    protected boolean intersectCombatPoint() {
        if (trail.size() == 0f) return false;
        TurnPoint point = trail.get(trail.size() - 1);
        PVector p = point.position;
        float tpSize;
        if (point.combat) tpSize = 15;
        else return false; //this is the only bit thats different
        PVector pfPosition = new PVector(position.x - ((pfSize - 1) * 12.5f), position.y - ((pfSize - 1) * 12.5f));
        return (pfPosition.x > p.x - tpSize + (NODE_SIZE / 2f)
                    && pfPosition.x < p.x + tpSize + (NODE_SIZE / 2f))
                && (pfPosition.y > p.y - tpSize + (NODE_SIZE / 2f)
                    && pfPosition.y < p.y + tpSize + (NODE_SIZE / 2f));
    }

    public void requestPath(int i) {
        pathFinder.requestQueue.add(new PathRequest(i, this));
        trail = new ArrayList<>();
    }

    public void swapPoints(boolean remove) {
        if (!trail.isEmpty()) {
            TurnPoint intersectingPoint = trail.get(trail.size() - 1);
            if (remove) {
                if (intersectingPoint.combat) {
                    state = State.Attacking;
                    attackCue = true;
                    targetTower = intersectingPoint.tower;
                    targetMachine = intersectingPoint.machine;
                } else {
                    attackCue = false;
                    trail.remove(intersectingPoint);
                }
            }
            if (!trail.isEmpty()) {
                PVector pointPosition = trail.get(trail.size() - 1).position;
                pointPosition = new PVector(pointPosition.x + 12.5f, pointPosition.y + 12.5f);
                pointPosition = new PVector(pointPosition.x + ((pfSize - 1) * 12.5f), pointPosition.y + ((pfSize - 1) * 12.5f));
                targetAngle = findAngleBetween(pointPosition, position);
            }
        }
    }

    /** Reset all combat points to turn points, then recalculate. */
    public void setCombatPoints() {
        //set not combat
        for (TurnPoint point : trail) {
            point.combat = false;
            point.towers = null;
            point.machine = false;
        }
        //set combat
        for (TurnPoint point : trail) {
            point.towers = clearanceTowers(point);
            point.machine = clearanceMachine(point);
        }
        //iterate backwards based on enemy size
        TurnPoint backPoint = backPoint();
        if (backPoint != null) {
            backPoint.combat = true;
            if (backPoint.towers != null && !backPoint.towers.isEmpty()) { //what the hell is this for??
                backPoint.tower = backPoint.towers.get(floor(backPoint.towers.size() / 2f));
            } else backPoint.tower = null;
        }
    }

    /** returns all towers in point kernel */
    protected ArrayList<Tower> clearanceTowers(TurnPoint point) {
        ArrayList<Tower> towersInKernel = new ArrayList<>();
        boolean clear = true;
        int kernelSize = 1;
        int x = (int) (point.position.x + 100) / 25;
        int y = (int) (point.position.y + 100) / 25;
        while (true) {
            for (int xn = 0; xn < kernelSize; xn++) {
                for (int yn = 0; yn < kernelSize; yn++) {
                    if (!(x + xn >= nodeGrid.length || y + yn >= nodeGrid[x].length)) {
                        Node node = nodeGrid[x + xn][y + yn];
                        if (node.tower != null) towersInKernel.add(node.tower);
                    } else {
                        clear = false;
                        break;
                    }
                }
                if (!clear) break;
            }
            if (clear && kernelSize < pfSize) kernelSize++;
            else break;
        }
        //deletes duplicates
        CopyOnWriteArrayList<Tower> cow = new CopyOnWriteArrayList<>(towersInKernel);
        for (int i = 0; i < cow.size() - 1; i++) {
            if (cow.get(i) == cow.get(i++)) cow.remove(i);
        }
        towersInKernel = new ArrayList<>(cow);
        return towersInKernel;
    }

    private boolean clearanceMachine(TurnPoint point) {
        boolean clear = true;
        int kSize = 1;
        int x = (int) (point.position.x + 100) / 25;
        int y = (int) (point.position.y + 100) / 25;
        while (true) {
            for (int xn = 0; xn < kSize; xn++) {
                for (int yn = 0; yn < kSize; yn++) {
                    if (!(x + xn >= nodeGrid.length || y + yn >= nodeGrid[x].length)) {
                        Node nodeB = nodeGrid[x + xn][y + yn];
                        Tile tileB;
                        if (nodeB != null) {
                            tileB = nodeB.getTile();
                            if (tileB != null && tileB.machine) return true;
                        }
                    } else {
                        clear = false;
                        break;
                    }
                }
                if (!clear) break;
            }
            if (clear && kSize < pfSize) kSize++;
            else break;
        }
        return false;
    }

    private TurnPoint backPoint() {
        TurnPoint bp = null;
        for (int i = trail.size() - 1; i >= 0; i--) {
            if (trail.get(i).towers != null && !trail.get(i).towers.isEmpty() || trail.get(i).machine) {
                trail.get(i).combat = true;
                if (i < trail.size() - 1) bp = trail.get(i + 1);
                else bp = trail.get(i);
                bp.towers = trail.get(i).towers;
                bp.machine = trail.get(i).machine;
                bp.combat = true;
                bp.back = true;
                break;
            }
        }
        return bp;
    }

    public static class TurnPoint {

        private final PApplet P;
        public Tower tower;
        public ArrayList<Tower> towers;
        public boolean machine;
        public PVector position;
        boolean combat;
        boolean back;

        public TurnPoint(PApplet p, PVector position, Tower tower) {
            this.P = p;
            this.position = new PVector(position.x, position.y);
            this.tower = tower;

//            machine = tiles.get((int)(position.x/50)+2,(int)(position.y/50)+2).machine;
            combat = false;
            back = false;
        }

        public void display() {
            P.noStroke();
            if (back) P.stroke(0, 255, 0);
            else P.noStroke();
            if (combat) P.fill(255, 0, 0);
            else P.fill(255);
            P.ellipse(position.x + NODE_SIZE / 2f, position.y + NODE_SIZE / 2f, NODE_SIZE, NODE_SIZE);
            hover();
        }

        private void hover() {
            boolean intersecting;
            float tpSize = 10;
            PVector pfPosition = new PVector(P.mouseX, P.mouseY);
            intersecting = (pfPosition.x > position.x - tpSize + (NODE_SIZE / 2f) &&
                    pfPosition.x < position.x + tpSize + (NODE_SIZE / 2f)) &&
                    (pfPosition.y > position.y - tpSize + (NODE_SIZE / 2f) &&
                            pfPosition.y < position.y + tpSize + (NODE_SIZE / 2f));
            if (intersecting && tower != null) {
                P.stroke(255, 255, 0);
                P.noFill();
                P.rect(tower.tile.position.x - 50, tower.tile.position.y - 50, 50, 50);
            }
        }
    }
}
