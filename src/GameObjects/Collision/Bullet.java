package GameObjects.Collision;

import java.awt.Polygon;

/**
 * A bullet traveling in a direction with a speed, size, durability, damage
 * and image type.
 *
 * @author Matthew Babel
 */
public class Bullet extends CollidableObject {

    private boolean alive;

    // this is to move the bullet in the correct direction
    private final double xMove;
    private final double yMove;

    /**
     * integer variable holder array to keep track of 10 variables.
     * durability, then constants, WIDTH, HEIGHT, SPEED, DAMAGE,
     * FRAMEWIDTH, FRAMEHEIGHT, and IMAGETYPE
     */
    private final int[] varHolder = new int[10];
    
    // target x, targe y, current x, current y
    private final float[] cordHolder = new float[4];

    public Bullet(float xc, float yc, int W, int H, int S, int dmg, int durability, int imgType, int dir, int FW, int FH) {
        cordHolder[0] = xc;
        cordHolder[1] = yc;
        cordHolder[2] = xc;
        cordHolder[3] = yc;        
        
        varHolder[0] = durability;
        varHolder[1] = W;
        varHolder[2] = H;
        varHolder[3] = S;
        varHolder[4] = dmg;
        varHolder[5] = FW;
        varHolder[6] = FH;
        varHolder[7] = imgType;
        xMove = Math.sin(Math.toRadians(dir));
        yMove = Math.cos(Math.toRadians(dir)) * -1; // *-1 to y to get right direction
        alive = true;
    }

    /**
     * Move the bullet in the direction it is traveling.
     */
    public void move() {
        cordHolder[0] +=  (varHolder[3] * xMove);
        cordHolder[1] +=  (varHolder[3] * yMove);
    }

    /**
     * return true if bullet x and y coordinates are off the visible screen.
     *
     * @return true if bullet is off screen
     */
    public boolean isOffScreen() {
        final int OFFSET = 50;
        return (cordHolder[2] > varHolder[5] + OFFSET || cordHolder[2] < -OFFSET
                || cordHolder[3] > varHolder[6] + OFFSET || cordHolder[3] < -OFFSET);
    }

    /**
     * Called when the bullet has hit something subtracts one from durability.
     */
    private void hit() {
        varHolder[0]--;
        if (varHolder[0] <= 0) {
            alive = false;
        }
    }

    public float getTargetXCord() {
        return cordHolder[0];
    }

    public float getTargetYCord() {
        return cordHolder[1];
    }

    public float getCurrentXCord() {
        return cordHolder[2];
    }

    public float getCurrentYCord() {
        return cordHolder[3];
    }
    
    public void setCurrentX(float x) {
        cordHolder[2] = x;
    }
    
    public void setCurrentY(float y) {
        cordHolder[3] = y;
    }

    public int getWidth() {
        return varHolder[1];
    }

    public int getHeight() {
        return varHolder[2];
    }

    public int getDmg() {
        return varHolder[3];
    }

    public int getImageType() {
        return varHolder[7];
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    Polygon getCollisionMask() {
        int xCord = (int)cordHolder[2];
        int yCord = (int)cordHolder[3];
        int WIDTH = varHolder[1];
        int HEIGHT = varHolder[2];

        int[] xCords = {xCord, xCord + WIDTH, xCord + WIDTH, xCord};
        int[] yCords = {yCord, yCord, yCord + HEIGHT, yCord + HEIGHT};
        return new Polygon(xCords, yCords, 4);
    }

    @Override
    void collision(int type) {
        hit();
    }
}
