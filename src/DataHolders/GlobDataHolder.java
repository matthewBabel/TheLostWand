package DataHolders;

import GameObjects.Collision.GlobMissile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Holds and manipulates data of a glob object.
 *
 * @author Matthew Babel
 */
public class GlobDataHolder {

    // holds target cords, current cords, and target rotation and current 
    // rotation as arrays
    private final float[][] floatArrays = new float[3][2];

    // holds glob image dimension and frame dimension
    private final Dimension[] SIZES = new Dimension[2];

    // horizontal speed, vertical speed, and rotation speed
    private int[] speeds = new int[3];

    //holds the non-constant integer variables
    //durability, adjustCount, horDirection, verDirection, centerX, centerY, 
    //position, collideCount, missileFireCdCount, missileAutoCdCount, hurtStage,
    private final int[] intHolder = new int[11];

    //holds the constant integer variables
    //ADJSUTMAX, SPEEDMAX, collide CD, missile fire CD, missile auto fire CD
    private final int[] INTCONSTANTS = new int[5];

    //holds the boolean variables
    //misssileFireCd, missileAutoCd, missileCreated, missilesEmpty, facingPlayer
    private final boolean[] boolHolder = new boolean[5];

    private GlobMissile newMissile;

    private final Point2D[][] MASKPOINTS;

    private final BufferedImage[] missileImages;
    private final BufferedImage[] globImages;
    private Color curColor;

    //0 - top left, 1 - top right, 2 - bottom right, 3 - bottom left
    // true when these missiles have been detached
    private final boolean[] detached;

    public GlobDataHolder(float xCord, float yCord, int dur, int imgX, int imgY,
            int[] s, int MAXS, int MAXA, int COLLCD, int pos, int fw, int fh) {

        floatArrays[0] = new float[]{xCord, yCord};
        floatArrays[1] = new float[]{xCord, yCord};
        floatArrays[2] = new float[]{0, 0};

        SIZES[0] = new Dimension(imgX, imgY);
        SIZES[1] = new Dimension(fw, fh);
        speeds = s;

        intHolder[0] = dur;
        intHolder[1] = MAXA;
        intHolder[2] = 0;
        intHolder[3] = 0;
        intHolder[4] = 0;
        intHolder[5] = 0;
        intHolder[6] = pos;
        intHolder[7] = 0;
        intHolder[8] = 0;
        intHolder[9] = 0;
        intHolder[10] = 0;

        INTCONSTANTS[0] = MAXA;
        INTCONSTANTS[1] = MAXS;
        INTCONSTANTS[2] = COLLCD;
        INTCONSTANTS[3] = 90;
        INTCONSTANTS[4] = 180;

        boolHolder[0] = false;
        boolHolder[1] = true;
        boolHolder[2] = false;
        boolHolder[3] = false;
        boolHolder[4] = false;

        MASKPOINTS = new Point2D[2][14];
        curColor = ColorDataHolder.getBasicGreen();

        missileImages = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            missileImages[i] = ImageDataHolder.getMissileImage(i);
        }

        globImages = new BufferedImage[15];
        for (int i = 0; i < 15; i++) {
            globImages[i] = ImageDataHolder.getGlobGoneImage(i);
        }
        
        detached = new boolean[4];
        Arrays.fill(detached, false);
    }

    /**
     * Is true if the glob should adjust it's movement.
     *
     * @return true if glob should adjust
     */
    public boolean shouldAdjust() {
        intHolder[1]++;
        if (intHolder[1] >= INTCONSTANTS[0]) {
            intHolder[1] = 0;
            return true;
        }
        return false;
    }

    /**
     * Get the image index of the glob gone image currently needed.
     *
     * detached 0 - top left, 1 - top right, 2 - bottom right, 3 - bottom left
     * glob images - top left, top right, bottom right, bottom left, top, bottom, right, left,
     * top left bottom right, top right bottom left, left bottom right, left top right
     * right top left, right bottom left, all
     *
     * @return correct glob gone image index
     */
    public int getImageIndex() {
        boolean tl = detached[0];
        boolean tr = detached[1];
        boolean br = detached[2];
        boolean bl = detached[3];

        if (tl && !tr && !br && !bl) {
            return 0;
        } else if (!tl && tr && !br && !bl) {
            return 1;
        } else if (!tl && !tr && br && !bl) {
            return 2;
        } else if (!tl && !tr && !br && bl) {
            return 3;
        } else if (tl && tr && !br && !bl) {
            return 4;
        } else if (!tl && !tr && br && bl) {
            return 5;
        } else if (!tl && tr && br && !bl) {
            return 6;
        } else if (tl && !tr && !br && bl) {
            return 7;
        } else if (tl && !tr && br && !bl) {
            return 8;
        } else if (!tl && tr && !br && bl) {
            return 9;
        } else if (tl && !tr && br && bl) {
            return 10;
        } else if (tl && tr && !br && bl) {
            return 11;
        } else if (tl && tr && br && !bl) {
            return 12;
        } else if (!tl && tr && br && bl) {
            return 13;
        } else if (tl && tr && br && bl) {
            return 14;
        }

        return 0;
    }

    /**
     * Add a new missile to be spawned.
     *
     * @param m the missile
     * @param fired true if missile was fired from aim rather than auto fire
     */
    public void addMissile(GlobMissile m, boolean fired) {
        newMissile = m;
        boolHolder[2] = true;

        if (fired) {
            boolHolder[0] = true;
        }
        boolHolder[1] = true;
        intHolder[8] = 0;
        intHolder[9] = 0;
    }

    public void missileCooldownTick() {
        intHolder[8]++;

        if (intHolder[8] > INTCONSTANTS[3]) {
            boolHolder[0] = false;
            intHolder[8] = 0;
        }
    }

    public void missileAutoTick() {
        intHolder[9]++;

        if (intHolder[9] > INTCONSTANTS[4]) {
            boolHolder[1] = false;
            intHolder[9] = 0;
        }
    }

    /**
     * true if collide cool down is off.
     *
     * @return true if collide cool down is off
     */
    public boolean isCooldownOff() {
        intHolder[7]++;

        if (intHolder[7] > INTCONSTANTS[2]) {
            intHolder[7] = 0;
            return true;
        }
        return false;
    }

    public void setTargetXCord(float x) {
        floatArrays[0][0] = x;
    }

    public void setTargetYCord(float y) {
        floatArrays[0][1] = y;
    }

    public float getTargetXCord() {
        return floatArrays[0][0];
    }

    public float getTargetYCord() {
        return floatArrays[0][1];
    }

    public void setCurrentXCord(float x) {
        floatArrays[1][0] = x;
    }

    public void setCurrentYCord(float y) {
        floatArrays[1][1] = y;
    }

    public float getCurrentXCord() {
        return floatArrays[1][0];
    }

    public float getCurrentYCord() {
        return floatArrays[1][1];
    }

    public void decrementDur() {
        intHolder[0]--;
    }

    public int getDurability() {
        return intHolder[0];
    }

    public int getImageWidth() {
        return SIZES[0].width;
    }

    public int getImageHeight() {
        return SIZES[0].height;
    }

    public void setHorSpeed(int s) {
        speeds[0] = s;
    }

    public void setVerSpeed(int s) {
        speeds[1] = s;
    }

    public void setRotSpeed(int s) {
        speeds[2] = s;
    }

    public int getHorSpeed() {
        return speeds[0];
    }

    public int getVerSpeed() {
        return speeds[1];
    }

    public int getRotSpeed() {
        return speeds[2];
    }

    public void setTargetRotation(float r) {
        floatArrays[2][0] = r;
    }

    public void setCurrentRotation(float r) {
        floatArrays[2][1] = r;
    }

    public float getTargetRotation() {
        return floatArrays[2][0];
    }

    public float getCurrentRotation() {
        return floatArrays[2][1];
    }

    public int getMaxSpeed() {
        return INTCONSTANTS[1];
    }

    public void setHorDirection(int d) {
        intHolder[2] = d;
    }

    public void setVerDirection(int d) {
        intHolder[3] = d;
    }

    public int getHorDirection() {
        return intHolder[2];
    }

    public int getVerDirection() {
        return intHolder[3];
    }

    public void setMaskPoints1(Point2D[] p) {
        MASKPOINTS[0] = p.clone();
    }

    public void setMaskPoints2(Point2D[] p) {
        MASKPOINTS[1] = p.clone();
    }

    public Point2D[] getMaskPoints1() {
        return MASKPOINTS[0];
    }

    public Point2D[] getMaskPoints2() {
        return MASKPOINTS[1];
    }

    public void setCenterX(int x) {
        intHolder[4] = x;
    }

    public void setCenterY(int y) {
        intHolder[5] = y;
    }

    public int getCenterX() {
        return intHolder[4];
    }

    public int getCenterY() {
        return intHolder[5];
    }

    public void decrementPosition() {
        intHolder[6]--;
    }

    public int getPosition() {
        return intHolder[6];
    }

    public Color getCurColor() {
        return curColor;
    }

    public void setCurColor(Color c) {
        curColor = c;
        intHolder[10]++;
    }

    public int getHurtStage() {
        return intHolder[10];
    }

    public BufferedImage getMissileImages(int i) {
        return missileImages[i];
    }

    public BufferedImage getGlobImage(int i) {
        return globImages[i];
    }

    public GlobMissile getNewMissile() {
        boolHolder[2] = false;
        return newMissile;
    }

    public boolean isMissileCreated() {
        return boolHolder[2];
    }

    public boolean isMissileCooldown() {
        return boolHolder[0];
    }

    public boolean isMissileAutoCooldown() {
        return boolHolder[1];
    }

    public boolean isDetached(int i) {
        return detached[i];
    }

    public void setDetached(int i) {
        detached[i] = true;

        if (Arrays.equals(detached, new boolean[]{true, true, true, true})) {
            boolHolder[3] = true;
        }
    }

    public int getFrameWidth() {
        return SIZES[1].width;
    }

    public int getFrameHeight() {
        return SIZES[1].height;
    }

    public boolean isMissilesEmpty() {
        return boolHolder[3];
    }

    public boolean isFacingPlayer() {
        return boolHolder[4];
    }

    public void setFacingPlayer(boolean b) {
        boolHolder[4] = b;
    }
}
