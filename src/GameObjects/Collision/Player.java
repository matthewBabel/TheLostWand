package GameObjects.Collision;

import Audio.AudioHolder;
import Audio.AudioPlayer;
import Containers.Frame;
import Containers.GamePanel;
import DataHolders.ImageDataHolder;
import DataHolders.PlayerDataHolder;
import DataHolders.PlayerDataHolder.Speeds;
import GameObjects.*;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User played object that can move up down left and right, as well as rotate
 * left, right and attack.
 *
 * @author Matthew Babel
 */
public class Player extends CollidableObject {

    //Used for the Rotation of the collision mask
    private final Point2D[][] MASKPOINTS = new Point2D[2][4];

    private TimerTask spdReset;
    private TimerTask chargeReset;
    private final Timer t;

    private final PlayerDataHolder PLAYERDATA;

    //
    /**
     * Initializing player variables and loading sprite image.
     *
     * @param frame reference to the Frame class
     * @param gamePanel refrence to the Game Panel class
     * @param frameW the frames width
     * @param frameH the frames height
     */
    public Player(Frame frame, GamePanel gamePanel, int frameW, int frameH) {
        int lives = 3;
        int WANDLENGTH = 128;
        int WANDWIDTH = 12;
        final int IMAGEPIXELWIDTH = 250;
        final int IMAGEPIXELHEIGHT = 250;
        int XCHANGE = (IMAGEPIXELWIDTH / 2) - (WANDWIDTH / 2);
        int YCHANGE = IMAGEPIXELHEIGHT - WANDLENGTH;
        PLAYERDATA = new PlayerDataHolder(frame, gamePanel, frameW, frameH,
                lives, WANDWIDTH, WANDLENGTH, XCHANGE, YCHANGE, frameW / 2,
                frameH / 2);
        PLAYERDATA.setPlayerImage(ImageDataHolder.getPlayerImage());
        
        t = new Timer();
    }

    /**
     * Moving the player in the direction of any keys pressed at the time.
     * Update attack cool down and collision cool down.
     *
     * @param horMov the horizontal movement direction
     * @param verMov the vertical movement direction
     * @param rotMov the rotation movement direction
     * @param attack true if attacking input is on
     * @param bm the players bullet manager
     */
    public void move(int horMov, int verMov, int rotMov, boolean attack, BulletManager bm) {
        int speed = PLAYERDATA.getCurSpeed();
        int rotateSpd = PLAYERDATA.getCurRotationSpeed();
        float xCord = PLAYERDATA.getTargetXCord();
        float yCord = PLAYERDATA.getTargetYCord();
        float headDegree = PLAYERDATA.getTargetHeadDegree();
        boolean usingCharge = PLAYERDATA.isUsingCharge();
        boolean charging = PLAYERDATA.isCharging();

        if (!usingCharge) {  // basic attack
            updateBasicCooldown(attack);
        } else if (!PLAYERDATA.isOnChargeCooldown()) { // decide what to do with charge shot
            charging = updateChargeAttack(attack, charging, bm, xCord,
                    yCord, headDegree);
        } else {
            updateChargeCooldown();
        }

        updatePlayerData();
        headDegree = updateMovement(horMov, speed, verMov, rotMov, headDegree, rotateSpd);

        PLAYERDATA.setTargetHeadDegree(headDegree);
        PLAYERDATA.setCharging(charging);
    }

    /**
     * Update the players movement including rotation.
     *
     * @return players head degree
     */
    private float updateMovement(int horMov, int speed, int verMov, int rotMov, float headDegree, int rotateSpd) {
        if (horMov == 1) {
            PLAYERDATA.setTargetXCord(PLAYERDATA.getTargetXCord() - speed);
        } else if (horMov == 2) {
            PLAYERDATA.setTargetXCord(PLAYERDATA.getTargetXCord() + speed);
        }
        if (verMov == 1) {
            PLAYERDATA.setTargetYCord(PLAYERDATA.getTargetYCord() - speed);
        } else if (verMov == 2) {
            PLAYERDATA.setTargetYCord(PLAYERDATA.getTargetYCord() + speed);
        }

        if (rotMov == 1) {
            headDegree -= rotateSpd;
            if (headDegree < 0) {
                headDegree += 360;
            }
        } else if (rotMov == 2) {
            headDegree += rotateSpd;
            if (headDegree > 359) {
                headDegree -= 360;
            }
        }
        return headDegree;
    }

    /**
     * Updating player data. If it is hurt and if it is invincible.
     */
    private void updatePlayerData() {
        if (PLAYERDATA.isHurt()) {
            final int HURTFRAMES = 10;
            PLAYERDATA.tickHurtCount();
            if (PLAYERDATA.getHurtCount() > HURTFRAMES) {
                PLAYERDATA.setHurt(false);
                PLAYERDATA.resetHurtCount();
            }
        }

        if (this.isCollision()) {
            final int INVMAX = 30;
            PLAYERDATA.tickInvCount();
            if (PLAYERDATA.getInvCount() > INVMAX) {
                this.collisionOff();
                PLAYERDATA.resetInvCount();
            }
        }
    }

    /**
     * Update the charge attack cool down.
     */
    private void updateChargeCooldown() {
        PLAYERDATA.tickChargeCooldownCount();
        if (PLAYERDATA.getChargeCooldownCount()
                > PLAYERDATA.getChargeAttack().getAttackCoolDown()) {
            PLAYERDATA.setChargeCooldown(false);
            PLAYERDATA.resetChargeCooldownCount();
        }
    }

    /**
     * Deciding what to do with charge attack.
     *
     * @param attack true if attacking
     * @param charging true if charging
     * @param bm bullet manager
     * @param xCord player x cord
     * @param yCord player y cord
     * @param headDegree player rotation
     * @return true if charging
     */
    private boolean updateChargeAttack(boolean attack, boolean charging, BulletManager bm, float xCord, float yCord, float headDegree) {
        if (attack && charging) { // charging up
            PLAYERDATA.getChargeAttack().tick();
        } else if (!attack && charging) { // just shot
            final int FW = PLAYERDATA.getFrameWidth();
            final int FH = PLAYERDATA.getFrameHeight();
            final int XCHANGE = PLAYERDATA.getXChange();
            final int YCHANGE = PLAYERDATA.getYChange();

            makeBullet(bm, xCord, XCHANGE, yCord, YCHANGE, (int)headDegree, FW, FH);

            PLAYERDATA.getChargeAttack().reset();
            charging = false;
            PLAYERDATA.setAttacking(false);
            PLAYERDATA.setChargeCooldown(true);
        } else if (attack && !charging) { // just started charge
            charging = true;
            PLAYERDATA.setAttacking(true);
        } else {
            PLAYERDATA.setAttacking(false);
            charging = false;
        }
        return charging;
    }

    /**
     * Create a player bullet can have 4 types.
     */
    private void makeBullet(BulletManager bm, float xCord, final int XCHANGE, float yCord, final int YCHANGE, int headDegree, final int FW, final int FH) {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.SHOT);
        switch (PLAYERDATA.getChargeAttack().getStage()) {
            case 1:
                bm.addBullet(new Bullet(xCord + XCHANGE, yCord + YCHANGE, 10, 10, 4, 1, 1, 0, headDegree, FW, FH));
                break;
            case 2:
                bm.addBullet(new Bullet(xCord + XCHANGE, yCord + YCHANGE, 20, 20, 5, 1, 2, 1, headDegree, FW, FH));
                break;
            case 3:
                bm.addBullet(new Bullet(xCord + XCHANGE, yCord + YCHANGE, 30, 30, 6, 1, 3, 2, headDegree, FW, FH));
                break;
            case 4:
                bm.addBullet(new Bullet(xCord + XCHANGE, yCord + YCHANGE, 40, 40, 8, 1, 6, 3, headDegree, FW, FH));
                break;
            default:
                break;
        }
    }

    /**
     * Update the basic player attack cool down.
     *
     * @param attack true if attacking
     */
    private void updateBasicCooldown(boolean attack) {
        if (PLAYERDATA.isOnCooldown()) {
            PLAYERDATA.tickAttackCooldownCount();
            if (PLAYERDATA.getAttackCooldownCount()
                    >= PLAYERDATA.getBasicAttack().getCooldown()) {
                PLAYERDATA.setAttackCooldown(false);
                PLAYERDATA.resetAttackCooldownCount();
            }
        }
        if (attack) {
            if (!PLAYERDATA.isAttacking() && !PLAYERDATA.isOnCooldown()) {
                PLAYERDATA.setAttacking(true);
            }
        }
    }

    public float getTargetX() {
        return PLAYERDATA.getTargetXCord();

    }

    public float getTargetY() {
        return PLAYERDATA.getTargetYCord();
    }
    
    public float getCurrentX() {
        return PLAYERDATA.getCurrentXCord();
    }
    
    public float getCurrentY() {
        return PLAYERDATA.getCurrentYCord();
    }
    
    public void setCurrentX(float x) {
        PLAYERDATA.setCurrentXCord(x);
    }
    
    public void setCurrentY(float y) {
        PLAYERDATA.setCurrentYCord(y);
    }

    public float getTargetHeadDegree() {
        return PLAYERDATA.getTargetHeadDegree();
    }

    public float getCurrentHeadDegree() {
        return PLAYERDATA.getCurrentHeadDegree();
    }
    
    public int getLength() {
        return PLAYERDATA.getWandLength();
    }

    public int getWidth() {
        return PLAYERDATA.getWandWidth();
    }

    public int getXChange() {
        return PLAYERDATA.getXChange();
    }

    public int getYChange() {
        return PLAYERDATA.getYChange();
    }

    public int getCenterX() {
        return PLAYERDATA.getCurrentCenterX();
    }

    public int getCenterY() {
        return PLAYERDATA.getCurrentCenterY();
    }

    public BufferedImage getImage() {
        return PLAYERDATA.getPlayerImage();
    }

    public int getLives() {
        return PLAYERDATA.getLives();
    }

    public void setTargetHeadDegree(double degree) {
        PLAYERDATA.setTargetHeadDegree((float) degree);
    }
    
    public void setCurrentHeadDegree(double degree) {
        PLAYERDATA.setCurrentHeadDegree((float) degree);
    }

    /**
     * Get the attacking sprite image as long as attacking is not on cool down.
     * if it is on cool down then return player sprite image
     *
     * @return sprite image
     */
    public BufferedImage getAttackImage() {
        if (!PLAYERDATA.isUsingCharge()) { // for basic attack
            PLAYERDATA.tickAttackingCount();

            final BasicAttack BASICATK = PLAYERDATA.getBasicAttack();
            if (PLAYERDATA.getAttackingCount() < BASICATK.getAttackFrames()){
                if (PLAYERDATA.getAttackingCount() == 2) {
                    AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.BASICATTACK);
                }
                return BASICATK.getImage();
            } else {
                PLAYERDATA.setAttacking(false);
                PLAYERDATA.resetAttackingCount();
                PLAYERDATA.setAttackCooldown(true);
                return getImage();
            }
        } else { // for charge attack
            return PLAYERDATA.getChargeAttack().getCurImg();
        }
    }

    public boolean isAttacking() {
        return PLAYERDATA.isAttacking();
    }

    public boolean isHurt() {
        return PLAYERDATA.isHurt();
    }

    public boolean isUsingCharge() {
        return PLAYERDATA.isUsingCharge();
    }

    public CollidableObject getLightning() {
        return PLAYERDATA.getBasicAttack();
    }

    /**
     * Creating the current collision mask of player then rotating it to head
     * degree.
     */
    public void makeMask() {
        int xCord = (int)PLAYERDATA.getCurrentXCord();
        int yCord = (int)PLAYERDATA.getCurrentYCord();
        final int XCHANGE = PLAYERDATA.getXChange();
        final int YCHANGE = PLAYERDATA.getYChange();
        final int WANDWIDTH = PLAYERDATA.getWandWidth();
        final int WANDLENGTH = PLAYERDATA.getWandLength();
        float headDegree = PLAYERDATA.getTargetHeadDegree();
        final int L = 1; //move mask slightly to the left

        Point2D[] MASKPOINTS1 = MASKPOINTS[0];
        Point2D[] MASKPOINTS2 = MASKPOINTS[1];

        MASKPOINTS1[0] = new Point(xCord + XCHANGE - L, yCord + YCHANGE);
        MASKPOINTS1[1] = new Point(xCord + XCHANGE + WANDWIDTH - L, yCord + YCHANGE);
        MASKPOINTS1[2] = new Point(xCord + XCHANGE + WANDWIDTH - L, yCord + YCHANGE + WANDLENGTH);
        MASKPOINTS1[3] = new Point(xCord + XCHANGE - L, yCord + YCHANGE + WANDLENGTH);

        MASKPOINTS2 = doMaskRotation(xCord, XCHANGE, WANDWIDTH, yCord, YCHANGE,
                headDegree, MASKPOINTS1, MASKPOINTS2);

        MASKPOINTS[0] = MASKPOINTS1;
        MASKPOINTS[1] = MASKPOINTS2;
    }

    private Point2D[] doMaskRotation(int xCord, final int XCHANGE, final int WANDWIDTH,
            int yCord, final int YCHANGE, float headDegree, Point2D[] MASKPOINTS1, Point2D[] MASKPOINTS2) {
        PLAYERDATA.setCurrentCenterX(xCord + XCHANGE + (WANDWIDTH / 2));
        PLAYERDATA.setCurrentCenterY(yCord + YCHANGE + 3);// adding 3 makes more accurate mask
        if (headDegree != 0) {
            AffineTransform.getRotateInstance(Math.toRadians(headDegree),
                    PLAYERDATA.getCurrentCenterX(), PLAYERDATA.getCurrentCenterY())
                    .transform(MASKPOINTS1, 0, MASKPOINTS2, 0, 4);
        } else {
            MASKPOINTS2 = MASKPOINTS1.clone();
        }
        return MASKPOINTS2;
    }

    /**
     * Returns a polygon of the player collision mask.
     *
     * @return polygon of player collision mask
     */
    @Override
    Polygon getCollisionMask() {
        int[] xMask = {(int) MASKPOINTS[1][0].getX(), (int) MASKPOINTS[1][1].getX(), (int) MASKPOINTS[1][2].getX(), (int) MASKPOINTS[1][3].getX()};
        int[] yMask = {(int) MASKPOINTS[1][0].getY(), (int) MASKPOINTS[1][1].getY(), (int) MASKPOINTS[1][2].getY(), (int) MASKPOINTS[1][3].getY()};
        return new Polygon(xMask, yMask, 4);
    }

    /**
     * Make the attack mask by calling the basic attack make mask method.
     */
    public void makeAttackMask() {
        PLAYERDATA.getBasicAttack().makeMask((int)PLAYERDATA.getCurrentXCord(), (int)PLAYERDATA.getCurrentYCord(),
                PLAYERDATA.getXChange(), PLAYERDATA.getYChange(),
                PLAYERDATA.getCurrentCenterX(), PLAYERDATA.getCurrentCenterY(),
                PLAYERDATA.getCurrentHeadDegree());
    }

    /**
     * Create and return a mask surround the lightning attack.
     *
     * @return polygon surround lightning attack
     */
    public Polygon getAttackMask() {
        Point2D[] p = PLAYERDATA.getBasicAttack().getMask();
        int[] xMask = {(int) p[0].getX(), (int) p[1].getX(), (int) p[2].getX(), (int) p[3].getX()};
        int[] yMask = {(int) p[0].getY(), (int) p[1].getY(), (int) p[2].getY(), (int) p[3].getY()};
        return new Polygon(xMask, yMask, 4);
    }

    /**
     * Called whenever the player collides with another game object.
     *
     * @param type the type of collision, used to react in different ways
     */
    @Override
    void collision(int type) {
        if (!this.isCollision()) {
            switch (type) {
                case 1:
                    playerHit();
                    this.collisionOn();
                    break;
                case 2:
                    giveSpeedPoweup();
                    break;
                case 3:
                    giveChargePowerup();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Start a charge power up. Change attack to being the charge attack for 30
     * seconds.
     */
    private void giveChargePowerup() {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.POWERUP);

        // charge attack
        if (PLAYERDATA.isUsingCharge() == true) {
            PLAYERDATA.setChargeUp(true);
        } else {
            PLAYERDATA.setUsingCharge(true);
        }

        chargeReset = new TimerTask() {
            @Override
            public void run() {
                if (PLAYERDATA.isChargeUp()) {
                    PLAYERDATA.setChargeUp(false);
                } else {
                    PLAYERDATA.setUsingCharge(false);
                    PLAYERDATA.getChargeAttack().reset();
                }
                PLAYERDATA.setChargeCooldown(false);
                PLAYERDATA.resetChargeCooldownCount();
                PLAYERDATA.setCharging(false);
            }
        };

        final int cDuration = 30000;
        t.schedule(chargeReset, cDuration);
    }

    /**
     * Start a speed power up. Give player a faster speed and faster rotation
     * if in Classic mode.
     */
    private void giveSpeedPoweup() {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.POWERUP);

        // speed boost
        if (PLAYERDATA.getCurSpeed() == Speeds.FASTSPEED.getSpeed()) {
            PLAYERDATA.setSpeedUp(true);
        } else {
            PLAYERDATA.setCurrentSpeed(Speeds.FASTSPEED);
            PLAYERDATA.setCurrentRotationSpeed(Speeds.FASTROTATION);
        }

        // power up reset
        spdReset = new TimerTask() {
            @Override
            public void run() {
                if (PLAYERDATA.isSpeedUp()) {
                    PLAYERDATA.setSpeedUp(false);
                } else {
                    PLAYERDATA.setCurrentSpeed(Speeds.REGULARSPEED);
                    PLAYERDATA.setCurrentRotationSpeed(Speeds.REGULARROTATION);
                }
            }
        };  // 1000 = 1 second
        final int sDuration = 30000;
        t.schedule(spdReset, sDuration);
    }

    /**
     * Called when player is hit. Checks to stop game if lives is 0.
     */
    private void playerHit() {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.GRUNT);

        // enemy hit
        PLAYERDATA.decrementLives();
        PLAYERDATA.setHurt(true);

        if (PLAYERDATA.getLives() <= 0) {
            GamePanel gp = PLAYERDATA.getGamePanel();
            
            gp.GAMEDATA.setRunning(false);
            PLAYERDATA.getFrame().gameOver(gp.score);
        }
    }
}
