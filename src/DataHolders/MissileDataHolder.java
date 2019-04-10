package DataHolders;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Holds data for a missile object.
 *
 * @author Matthew Babel
 */
public class MissileDataHolder {

    private final double[] MOVEMENTS = new double[2];
    private final Point2D[][] POINTS;
    private final BufferedImage IMAGE;

    //startCdcount, start Cd max, rotSpeed, speed max, rotate offset
    private final int[] intHolder = new int[5];
    
    //dead, killed, start cd on
    private final boolean[] boolHolder = new boolean[3];
    
    private final float[] floatHolder = new float[6];
    private final int[] CENTERCORDS = new int[2];
    private final Dimension SIZE;

    public MissileDataHolder(float xc, float yc, float dir, int MAXS, int RS, int W,
            int H, int CHANGE, int startPause, double xMove, double yMove,
            BufferedImage img) {
        floatHolder[0] = xc;
        floatHolder[1] = yc;
        floatHolder[2] = xc;
        floatHolder[3] = yc;
        floatHolder[4] = dir;
        floatHolder[5] = dir;
        
        intHolder[0] = 0;
        intHolder[1] = startPause;
        intHolder[2] = RS;
        intHolder[3] = MAXS;
        intHolder[4] = CHANGE;
        
        boolHolder[0] = false;
        boolHolder[1] = false;
        boolHolder[2] = true;
        
        SIZE = new Dimension(W, H);

        MOVEMENTS[0] = xMove;
        MOVEMENTS[1] = yMove;
        POINTS = new Point2D[2][6];
        IMAGE = img;
    }

    public float getTargetX() {
        return floatHolder[0];
    }

    public float getTargetY() {
        return floatHolder[1];
    }
    
    public float getCurrentX() {
        return floatHolder[2];
    }
    
    public float getCurrentY() {
        return floatHolder[3];
    }

    public double getXMove() {
        return MOVEMENTS[0];
    }

    public double getYMove() {
        return MOVEMENTS[1];
    }

    public int getWidth() {
        return SIZE.width;
    }

    public int getHeight() {
        return SIZE.height;
    }

    public int getCenterX() {
        return CENTERCORDS[0];
    }

    public int getCenterY() {
        return CENTERCORDS[1];
    }

    public BufferedImage getImage() {
        return IMAGE;
    }

    public boolean isDead() {
        return boolHolder[0];
    }

    public boolean isKilled() {
        return boolHolder[1];
    }

    public boolean isStartCdOn() {
        return boolHolder[2];
    }

    public int getStartCdCount() {
        return intHolder[0];
    }

    public int getSTARTCDMAX() {
        return intHolder[1];
    }

    public float getTargetRotation() {
        return floatHolder[4];
    }

    public float getCurrentRotation() {
        return floatHolder[5];
    }
    
    public int getMAXSPEED() {
        return intHolder[3];
    }

    public int getRotSpeed() {
        return intHolder[2];
    }

    public int getROTATEOFFSET() {
        return intHolder[4];
    }

    public Point2D[] getPoints(int i) {
        return POINTS[i];
    }

    public void setPoints(Point2D[] p, int index) {
        POINTS[index] = p;
    }

    public void setTargetX(float x) {
        this.floatHolder[0] = x;
    }

    public void setTargetY(float y) {
        this.floatHolder[1] = y;
    }

    public void setCurrentX(float x) {
        this.floatHolder[2] = x;
    }

    public void setCurrentY(float y) {
        this.floatHolder[3] = y;
    }

    public void setCenterXCord(int x) {
        this.CENTERCORDS[0] = x;
    }

    public void setCenterYCord(int y) {
        this.CENTERCORDS[1] = y;
    }

    public void setXMove(double x) {
        this.MOVEMENTS[0] = x;
    }

    public void setYMove(double y) {
        this.MOVEMENTS[1] = y;
    }

    public void setDead(boolean dead) {
        this.boolHolder[0] = dead;
    }

    public void setKilled(boolean killed) {
        this.boolHolder[1] = killed;
    }

    public void setStartCdOn(boolean startCdOn) {
        this.boolHolder[2] = startCdOn;
    }

    public void tickStartCdCount() {
        this.intHolder[0]++;
    }

    public void resetStartCdCount() {
        this.intHolder[0] = 0;
    }

    public void setTargetRotation(float r) {
        this.floatHolder[4] = r;
    }
    
    public void setCurrentRotation(float r) {
        this.floatHolder[5] = r;
    }

    public void setRotSpeed(int rotSpeed) {
        this.intHolder[2] = rotSpeed;
    }
}