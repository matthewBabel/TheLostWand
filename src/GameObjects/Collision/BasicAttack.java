package GameObjects.Collision;

import DataHolders.ImageDataHolder;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * A basic attack used by the player.
 *
 * @author Matthew Babel
 */
public class BasicAttack extends CollidableObject {

    //Hold the constants for attack length, attacking frames and attack cooldown
    private final int[] CONSTANTS = new int[3];

    private BufferedImage image = null;
    Point2D[][] maskPoints = new Point2D[2][4];

    /**
     * Initializing attack variables and loading sprite image.
     *
     * @param wandLength the players length
     */
    public BasicAttack(int wandLength) {
        int imageSize = 250;
        int ATTACKLENGTH = imageSize - wandLength;
        int ATTACKINGFRAMES = 30;
        int COOLDOWN = 20;

        CONSTANTS[0] = ATTACKLENGTH;
        CONSTANTS[1] = ATTACKINGFRAMES;
        CONSTANTS[2] = COOLDOWN;

        image = ImageDataHolder.getWandAttackingImage();
    }

    /**
     * getting the coordinate points surrounding the basic attack.
     *
     * @return point array
     */
    public Point2D[] getMask() {
        return maskPoints[1];
    }

    /**
     * Creating an array of points to surround the basic attack, then rotating
     * it if necessary.
     *
     * @param xCord player xCord
     * @param yCord player yCord
     * @param xChange change between image xcord and player xcord
     * @param yChange change between image ycord and player ycord
     * @param curX x point to be rotated around
     * @param curY y point to be rotated around
     * @param headDegree degree of player rotation
     */
    public void makeMask(int xCord, int yCord, int xChange, int yChange,
            int curX, int curY, float headDegree) {
        Point2D[] points1 = maskPoints[0];
        Point2D[] points2 = maskPoints[1];

        final int ATTACKWIDTH = 12;
        final int L = 1; //move points slightly to the left

        points1[0] = new Point(xCord + xChange - L, yCord);
        points1[1] = new Point(xCord + xChange + ATTACKWIDTH - L, yCord);
        points1[2] = new Point(xCord + xChange + ATTACKWIDTH - L, yCord + CONSTANTS[0]);
        points1[3] = new Point(xCord + xChange - L, yCord + CONSTANTS[0]);

        // rotating points if necessary, rotated points are saved to points2 
        if (headDegree != 0) {
            AffineTransform.getRotateInstance(Math.toRadians(headDegree), curX, curY)
                    .transform(points1, 0, points2, 0, 4);
        } else {
            points2 = points1.clone();
        }

        maskPoints[0] = points1;
        maskPoints[1] = points2;
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Get the frames that the attack is shown for.
     *
     * @return the frame length of attack
     */
    public int getAttackFrames() {
        return CONSTANTS[1];
    }

    /**
     * Get the frame cool down of the attack.
     *
     * @return the frame cool down of attack
     */
    public int getCooldown() {
        return CONSTANTS[2];
    }

    @Override
    Polygon getCollisionMask() {
        int[] xMask = {(int) maskPoints[1][0].getX(), (int) maskPoints[1][1].getX(), (int) maskPoints[1][2].getX(), (int) maskPoints[1][3].getX()};
        int[] yMask = {(int) maskPoints[1][0].getY(), (int) maskPoints[1][1].getY(), (int) maskPoints[1][2].getY(), (int) maskPoints[1][3].getY()};
        return new Polygon(xMask, yMask, 4);
    }

    @Override
    void collision(int type) {
        // do nothing
    }
}
