package GameObjects.Collision;

import Audio.AudioHolder;
import Audio.AudioPlayer;
import DataHolders.MissileDataHolder;
import static GameObjects.Collision.Glob.checkRotation;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * A missile detached from a Glob, auto targets the player. Has a noticeably
 * quicker adjust rate and speed compared to the other AI.
 *
 * @author Matthew Babel
 */
public class GlobMissile extends CollidableObject implements Spawn {

    /**
     * the speed will be activated when the distance between self and the player
     * is less than range.
     */
    private enum MissleSpeeds {
        SLOW(200),
        FAST(500),
        SUPERFAST(5000);

        private final int r;

        private MissleSpeeds(int range) {
            r = range;
        }

        public int getRange() {
            return r;
        }
    }

    private final MissileDataHolder DATA;

    /**
     * Constructs a glob missile.
     * @param xCord spawn x cord
     * @param yCord spawn y cord
     * @param direction direction or rotation of missile
     * @param img missile picture
     */
    public GlobMissile(int xCord, int yCord, int direction, BufferedImage img) {
        final int MAXSPEED = 11;
        final int ROTSPEED = 6;
        final int WIDTH = 65;
        final int HEIGHT = 65;
        final int ROTATECHANGE = -120;
        final int STARTCD = 20;

        double xMove = Math.sin(Math.toRadians(direction));
        double yMove = Math.cos(Math.toRadians(direction)) * -1;

        DATA = new MissileDataHolder(xCord, yCord, direction, MAXSPEED, ROTSPEED,
                WIDTH, HEIGHT, ROTATECHANGE, STARTCD, xMove, yMove, img);
    }

    /**
     * Move the missile towards the player.
     * @param playerX the players x cord
     * @param playerY the players y cord
     */
    @Override
    public void move(float playerX, float playerY) {

        double distance = Math.hypot(DATA.getTargetX() - playerX, DATA.getTargetY() - playerY);
        int s = getSpeed(distance);

        // new rotation speed/direction
        DATA.setRotSpeed(updateRotateSpeed(playerX, playerY, DATA.getTargetRotation()));

        // new rotation degree
        if (DATA.isStartCdOn()) {
            DATA.tickStartCdCount();
            if (DATA.getStartCdCount() > DATA.getSTARTCDMAX()) {
                DATA.setStartCdOn(false);
                DATA.resetStartCdCount();
            }
        } else if (!isDegreeClose(playerX, playerY, DATA.getTargetRotation(), 7)) {
            DATA.setTargetRotation(checkRotation(DATA.getTargetRotation() + DATA.getRotSpeed()));
        }
        DATA.setXMove(Math.sin(Math.toRadians(DATA.getTargetRotation())));
        DATA.setYMove(Math.cos(Math.toRadians(DATA.getTargetRotation())) * -1);

        // new x and y cords
        DATA.setTargetX(DATA.getTargetX() + ((int) (s * DATA.getXMove())));
        DATA.setTargetY(DATA.getTargetY() + ((int) (s * DATA.getYMove())));
    }

    /**
     * return the new current speed at the given distance.
     *
     * @param distance current distance from player
     * @param curSpeed current speed
     * @return updated speed
     */
    private int getSpeed(double distance) {
        int MAXSPEED = DATA.getMAXSPEED();
        if (distance > MissleSpeeds.FAST.getRange()) {
            return MAXSPEED;
        } else if (distance > MissleSpeeds.SLOW.getRange()) {
            return MAXSPEED - 1;
        } else {
            return MAXSPEED - 2;
        }
    }

    /**
     * returns true if the current degree is close to degree facing player.
     *
     * @param playerX the players x cord
     * @param playerY the players y cord
     * @param curDegree the current degree
     * @return true if degrees close false if not
     */
    private boolean isDegreeClose(float playerX, float playerY, float curDegree,
            int stopRange) {
        double n = getDegreeTowardsPlayer(playerX, playerY);
        return (n + stopRange > curDegree && n - stopRange < curDegree);
    }

    /**
     * update direction of the rotation or stop it if degree is close.
     *
     * @param px the players x cord
     * @param py the players y cord
     * @param curDegree current degree
     * @return new rotation
     */
    private int updateRotateSpeed(float px, float py, float curDegree) {
        double n = getDegreeTowardsPlayer(px, py);

        int rotationSpeed = DATA.getRotSpeed();
        if (n >= 180) {
            if (curDegree > n) {
                return (Math.abs(rotationSpeed)) * -1;
            } else if (curDegree < (n - 180)) {
                return (Math.abs(rotationSpeed)) * -1;
            } else {
                return (Math.abs(rotationSpeed));
            }
        } else if (n < 180) {
            if (curDegree < n) {
                return Math.abs(rotationSpeed);
            } else if (curDegree > (n + 180)) {
                return Math.abs(rotationSpeed);
            } else {
                return (Math.abs(rotationSpeed)) * -1;
            }
        }
        return 0;
    }

    /**
     * Return the degree from missile's coordinates to the player coordinates.
     * Uses trig tan function to find this angle.
     *
     * @param px player x cord
     * @param py player y cord
     * @return degree facing player
     */
    private double getDegreeTowardsPlayer(float px, float py) {
        updateCenterCords();
        int centerX = DATA.getCenterX();
        int centerY = DATA.getCenterY();

        double d1 = Math.abs(centerY - py);
        double d2 = Math.abs(centerX - px);
        double n = Math.atan2(d1, d2);
        n = Math.toDegrees(n);

        n = 90 - n;
        if (centerX <= px && centerY <= py) { // down right
            n = Math.abs(n);
            n = 90 - n;
            n += 90;
        } else if (centerX <= px && centerY >= py) { // up right
            // empty
        } else if (centerX >= px && centerY <= py) { // down left
            n += 180;
        } else if (centerX >= px && centerY >= py) { // up left
            n = 90 - n;
            n += 270;
        }
        return n;
    }

    /**
     * Update the missile's center coordinates.
     */
    private void updateCenterCords() {
        DATA.setCenterXCord((int)(DATA.getTargetX() + (DATA.getWidth() / 2)));
        DATA.setCenterYCord((int)(DATA.getTargetY() + (DATA.getHeight() / 2)));
    }

    public float getTargetRotation() {
        return checkRotation(DATA.getTargetRotation() + DATA.getROTATEOFFSET());
    }
    
    public float getCurrentRotation() {
        return checkRotation(DATA.getCurrentRotation());
    }

    @Override
    public BufferedImage getImage() {
        return DATA.getImage();
    }

    @Override
    public float getTargetXCord() {
        return DATA.getTargetX();
    }

    @Override
    public float getTargetYCord() {
        return DATA.getTargetY();
    }

    @Override
    public float getCurrentXCord() {
        return DATA.getCurrentX();
    }

    @Override
    public float getCurrentYCord() {
        return DATA.getCurrentY();
    }

    @Override
    public void decrementPosition() {
        // empty
    }

    @Override
    public boolean isDead() {
        return DATA.isDead();
    }

    public boolean isAlive() {
        return !DATA.isDead();
    }

    @Override
    public boolean isKilled() {
        return DATA.isKilled();
    }

    @Override
    public int getImageWidth() {
        return DATA.getWidth();
    }

    @Override
    public int getImageHeight() {
        return DATA.getHeight();
    }
    
    public void setCurrentX(float x) {
        DATA.setCurrentX(x);
    }
    
    public void setCurrentY(float y) {
        DATA.setCurrentY(y);
    }
    
    public void setCurrentRotation(float r) {
        DATA.setCurrentRotation(r);
    }

    /**
     * Make the mask before being rotated.
     */
    private void makeMask() {
        int xCord = (int)DATA.getCurrentX();
        int yCord = (int)DATA.getCurrentY();

        Point2D[] MASKPOINTS1 = DATA.getPoints(0);
        MASKPOINTS1[0] = new Point(xCord + 25, yCord + 9);
        MASKPOINTS1[1] = new Point(xCord + 48, yCord + 29);
        MASKPOINTS1[2] = new Point(xCord + 57, yCord + 53);
        MASKPOINTS1[3] = new Point(xCord + 29, yCord + 46);
        MASKPOINTS1[4] = new Point(xCord + 10, yCord + 20);
        MASKPOINTS1[5] = new Point(xCord + 25, yCord + 9);

        DATA.setPoints(MASKPOINTS1.clone(), 0);
        Point2D[] points2 = DATA.getPoints(1);
        DATA.setPoints(doMaskRotation(DATA.getTargetRotation(), DATA.getPoints(0), points2), 1);
    }

    /**
     * Rotate the mask according to degree.
     *
     * @param degree the rotation
     * @param MASKPOINTS1 array of points before rotation
     * @param newPoints will hold points after rotation
     * @return rotated mask
     */
    private Point2D[] doMaskRotation(float degree, Point2D[] MASKPOINTS1, Point2D[] newPoints) {
        updateCenterCords();
        degree = degree + DATA.getROTATEOFFSET();
        if (degree != 0) {
            AffineTransform.getRotateInstance(Math.toRadians(degree),
                    DATA.getCenterX(), DATA.getCenterY())
                    .transform(MASKPOINTS1, 0, newPoints, 0, 6);
        } else {
            newPoints = MASKPOINTS1.clone();
        }

        return newPoints;
    }

    /**
     * Creates and returns the collision mask.
     * @return collision mask as a polygon
     */
    @Override
    public Polygon getCollisionMask() {
        makeMask();

        Point2D[] p = DATA.getPoints(1);
        final int SIZE = 6;
        int[] xMask = new int[SIZE];
        int[] yMask = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            xMask[i] = (int) p[i].getX();
            yMask[i] = (int) p[i].getY();
        }
        return new Polygon(xMask, yMask, SIZE);
    }

    /**
     * Called when missile is collided with. 
     * type 4 - dead but not killed
     * type 3 - killed 
     * @param type the type of collision
     */
    @Override
    void collision(int type) {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.MISSILEEXPLOSION);
        switch (type) {
            case 4: // dead not killed
                DATA.setDead(true);
                break;
            case 3: // killed
                DATA.setDead(true);
                DATA.setKilled(true);
                break;
        }
    }
}
