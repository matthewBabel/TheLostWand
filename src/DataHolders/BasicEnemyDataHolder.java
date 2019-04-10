package DataHolders;

import java.awt.Dimension;

/**
 * Holds and manipulates data for the basic enemy.
 * 
 * @author Matthew Babel
 */
public class BasicEnemyDataHolder {
    
    /**
     * Used to tell apart the different counters the enemy is using.
     */
    public enum CountTypes{
        ADJUST(0),
        COLLISION(1),
        ABSORB(2),
        ABSORBCOOLDOWN(3);
        
        private final int INDEX;
        
        private CountTypes(int i) {
            INDEX = i;
        }
        
        public int getIndex() {
            return INDEX;
        }
    }
    
    private final float[] TARGETCORDS;
    private final float[] CURCORDS;
    
    private final float[] MOVESPEEDS;
    
    // adjustcount, collision cooldown count, absorb count, absorb cooldown count
    private final int[] COUNTS;
    private final int[] COUNTMAX;
    
    private int position;
    private int globPosition;
    private final int MAXSPEED;
    private final Dimension SIZE;
    
    private boolean dead;
    private boolean killed;
    private boolean beingAbsorbed;
    private boolean absorbOnCooldown;
    
    public BasicEnemyDataHolder(int pos, int globPos,
            int[] countLimits, int maxSpeed, int width, int height) {
        TARGETCORDS = new float[2];
        CURCORDS = new float[2];
        
        MOVESPEEDS = new float[] {0, 0};
        
        COUNTMAX = countLimits;
        COUNTS = new int[countLimits.length];
        
        COUNTS[0] = COUNTMAX[0];
        for (int i = 1; i < COUNTS.length; i++) {
            COUNTS[i] = 0;
        }
        
        MAXSPEED = maxSpeed;
        position = pos;
        globPosition = globPos;
        SIZE = new Dimension(width,height);
        
        dead = false;
        killed = false;
        beingAbsorbed = false;
        absorbOnCooldown = false;
    }

    public float getTargetXCord() {
        return TARGETCORDS[0];
    }

    public float getTargetYCord() {
        return TARGETCORDS[1];
    }
    
    public float getCurrentXCord() {
        return CURCORDS[0];
    }

    public float getCurrentYCord() {
        return CURCORDS[1];
    }
    
    public float getHorSpeed() {
        return MOVESPEEDS[0];
    }
    
    public float getVerSpeed() {
        return MOVESPEEDS[1];
    }

    public int getCounts(int index) {
        return COUNTS[index];
    }

    public int getCOUNTMAX(int index) {
        return COUNTMAX[index];
    }

    public int getPosition() {
        return position;
    }

    public int getGlobPosition() {
        return globPosition;
    }

    public int getMAXSPEED() {
        return MAXSPEED;
    }

    public int getWidth() {
        return SIZE.width;
    }

    public int getHeight() {
        return SIZE.height;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isKilled() {
        return killed;
    }

    public boolean isBeingAbsorbed() {
        return beingAbsorbed;
    }

    public boolean isAbsorbOnCooldown() {
        return absorbOnCooldown;
    }
  
    public void setTargetXCord(float xc) {
        this.TARGETCORDS[0] = xc;
    }

    public void setTargetYCord(float yc) {
        this.TARGETCORDS[1] = yc;
    }
  
    public void setCurrentXCord(float xc) {
        this.CURCORDS[0] = xc;
    }

    public void setCurrentYCord(float yc) {
        this.CURCORDS[1] = yc;
    }

    public void setHorSpeed(float s) {
        this.MOVESPEEDS[0] = s;
    }
    
    public void setVerSpeed(float s) {
        this.MOVESPEEDS[1] = s;
    }

    public void setCounts(int index, int count) {
        this.COUNTS[index] = count;
    }
    
    public void setAdjustSpeed(int newSpeed) {
        this.COUNTMAX[CountTypes.ADJUST.getIndex()] = newSpeed;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public void setGlobPosition(int globPos) {
        this.globPosition = globPos;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public void setBeingAbsorbed(boolean beingAbsorbed) {
        this.beingAbsorbed = beingAbsorbed;
    }

    public void setAbsorbOnCooldown(boolean absorbOnCooldown) {
        this.absorbOnCooldown = absorbOnCooldown;
    }   
}