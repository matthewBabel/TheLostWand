package DataHolders;

import Containers.Frame;
import Containers.GamePanel;
import GameObjects.ChargeAttack;
import GameObjects.Collision.BasicAttack;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Holds data for the player object.
 *
 * @author Matthew Babel
 */
public class PlayerDataHolder {

    private final Frame FRAME;
    private final GamePanel GAMEPANEL;
    private BufferedImage playerImage;
    private final Dimension FRAMESIZE;

    //array to hold all the boolean variables holds the following
    //attacking, attackOnCooldown, hurt, speed powerup is up, charge power up
    //is up, usingCharge, charging, chargeOnCooldown
    private final boolean[] BOOLHOLDER = new boolean[8];

    //array to hold all the ints holds the following
    //current speed, current rotation, invisibiilty count, hurt count, attacking
    //count, attacking cooldown count, charge cooldown count, lives, 
    //current center x, current center y,
    //WANDWIDTH, WANDLENGTH, XCHANGE, YCHANGE
    private final int[] INTHOLDER = new int[14];
    
    // holds target x cord, target y cord, current x cord, current y cord, target x degree, current head degree
    private final float[] FLOATHOLDER = new float[6];

    private final BasicAttack BASICATTACK;
    private final ChargeAttack CHARGEATTACK;

    public enum Speeds {
        REGULARSPEED(8),
        FASTSPEED(12),
        REGULARROTATION(7),
        FASTROTATION(10);

        int speed;

        private Speeds(int spd) {
            speed = spd;
        }

        public int getSpeed() {
            return speed;
        }
    }

    public PlayerDataHolder(Frame fr, GamePanel gp, int fw, int fh, int myLives,
            int wandWidth, int wandLength, int xChange,
            int yChange, float startX, float startY) {
        FRAME = fr;
        GAMEPANEL = gp;
        FRAMESIZE = new Dimension(fw, fh);

        INTHOLDER[0] = Speeds.REGULARSPEED.getSpeed();
        INTHOLDER[1] = Speeds.REGULARROTATION.getSpeed();

        // intializing the counts
        for (int i = 2; i < 7; i++) {
            INTHOLDER[i] = 0;
        }

        INTHOLDER[7] = myLives;
        INTHOLDER[10] = wandWidth;
        INTHOLDER[11] = wandLength;
        INTHOLDER[12] = xChange;
        INTHOLDER[13] = yChange;
        
        FLOATHOLDER[0] = startX;
        FLOATHOLDER[1] = startY;
        FLOATHOLDER[2] = startX;
        FLOATHOLDER[3] = startY;
        FLOATHOLDER[4] = 0;
        FLOATHOLDER[5] = 0;
        
        

        for (int i = 0; i < 8; i++) {
            BOOLHOLDER[i] = false;
        }

        BASICATTACK = new BasicAttack(wandLength);
        CHARGEATTACK = new ChargeAttack();
    }

    public void setPlayerImage(BufferedImage img) {
        playerImage = img;
    }

    public BufferedImage getPlayerImage() {
        return playerImage;
    }

    public Frame getFrame() {
        return FRAME;
    }

    public GamePanel getGamePanel() {
        return GAMEPANEL;
    }

    public int getFrameWidth() {
        return FRAMESIZE.width;
    }

    public int getFrameHeight() {
        return FRAMESIZE.height;
    }

    public boolean isAttacking() {
        return BOOLHOLDER[0];
    }

    public boolean isOnCooldown() {
        return BOOLHOLDER[1];
    }

    public boolean isHurt() {
        return BOOLHOLDER[2];
    }

    public boolean isSpeedUp() {
        return BOOLHOLDER[3];
    }

    public boolean isChargeUp() {
        return BOOLHOLDER[4];
    }

    public boolean isUsingCharge() {
        return BOOLHOLDER[5];
    }

    public boolean isCharging() {
        return BOOLHOLDER[6];
    }

    public boolean isOnChargeCooldown() {
        return BOOLHOLDER[7];
    }

    public void setAttacking(boolean isAttacking) {
        BOOLHOLDER[0] = isAttacking;
    }

    public void setAttackCooldown(boolean cooldownOn) {
        BOOLHOLDER[1] = cooldownOn;
    }

    public void setHurt(boolean isHurt) {
        BOOLHOLDER[2] = isHurt;
    }

    public void setSpeedUp(boolean speedUp) {
        BOOLHOLDER[3] = speedUp;
    }

    public void setChargeUp(boolean chargeUp) {
        BOOLHOLDER[4] = chargeUp;
    }

    public void setUsingCharge(boolean isUsing) {
        BOOLHOLDER[5] = isUsing;
    }

    public void setCharging(boolean isCharging) {
        BOOLHOLDER[6] = isCharging;
    }

    public void setChargeCooldown(boolean cooldownOn) {
        BOOLHOLDER[7] = cooldownOn;
    }

    public int getCurSpeed() {
        return INTHOLDER[0];
    }

    public void setCurrentSpeed(Speeds spd) {
        INTHOLDER[0] = spd.getSpeed();
    }

    public int getCurRotationSpeed() {
        return INTHOLDER[1];
    }

    public void setCurrentRotationSpeed(Speeds spd) {
        INTHOLDER[1] = spd.getSpeed();
    }

    public void tickInvCount() {
        INTHOLDER[2]++;
    }

    public void tickHurtCount() {
        INTHOLDER[3]++;
    }

    public void tickAttackingCount() {
        INTHOLDER[4]++;
    }

    public void tickAttackCooldownCount() {
        INTHOLDER[5]++;
    }

    public void tickChargeCooldownCount() {
        INTHOLDER[6]++;
    }

    public void resetInvCount() {
        INTHOLDER[2] = 0;
    }

    public void resetHurtCount() {
        INTHOLDER[3] = 0;
    }

    public void resetAttackingCount() {
        INTHOLDER[4] = 0;
    }

    public void resetAttackCooldownCount() {
        INTHOLDER[5] = 0;
    }

    public void resetChargeCooldownCount() {
        INTHOLDER[6] = 0;
    }

    public int getInvCount() {
        return INTHOLDER[2];
    }

    public int getHurtCount() {
        return INTHOLDER[3];
    }

    public int getAttackingCount() {
        return INTHOLDER[4];
    }

    public int getAttackCooldownCount() {
        return INTHOLDER[5];
    }

    public int getChargeCooldownCount() {
        return INTHOLDER[6];
    }

    public void decrementLives() {
        INTHOLDER[7]--;
    }

    public int getLives() {
        return INTHOLDER[7];
    }

    public void setCurrentCenterX(int x) {
        INTHOLDER[8] = x;
    }

    public void setCurrentCenterY(int y) {
        INTHOLDER[9] = y;
    }

    public int getCurrentCenterX() {
        return INTHOLDER[8];
    }

    public int getCurrentCenterY() {
        return INTHOLDER[9];
    }

    public void setTargetXCord(float x) {
        FLOATHOLDER[0] = x;
    }

    public void setTargetYCord(float y) {
        FLOATHOLDER[1] = y;
    }
    
    public void setCurrentXCord(float x) {
        FLOATHOLDER[2] = x;
    }
    
    public void setCurrentYCord(float y) {
        FLOATHOLDER[3] = y;
    }

    public float getTargetXCord() {
        return FLOATHOLDER[0];
    }

    public float getTargetYCord() {
        return FLOATHOLDER[1];
    }
    
    public float getCurrentXCord() {
        return FLOATHOLDER[2];
    }
    
    public float getCurrentYCord() {
        return FLOATHOLDER[3];
    }

    public void setTargetHeadDegree(float n) {
        FLOATHOLDER[4] = n;
    }

    public float getTargetHeadDegree() {
        return FLOATHOLDER[4];
    }

    public void setCurrentHeadDegree(float n) {
        FLOATHOLDER[5] = n;
    }

    public float getCurrentHeadDegree() {
        return FLOATHOLDER[5];
    }

    public int getWandWidth() {
        return INTHOLDER[10];
    }

    public int getWandLength() {
        return INTHOLDER[11];
    }

    public int getXChange() {
        return INTHOLDER[12];
    }

    public int getYChange() {
        return INTHOLDER[13];
    }

    public BasicAttack getBasicAttack() {
        return BASICATTACK;
    }

    public ChargeAttack getChargeAttack() {
        return CHARGEATTACK;
    }
}