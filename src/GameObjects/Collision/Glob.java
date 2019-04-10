package GameObjects.Collision;

import Audio.AudioHolder;
import Audio.AudioPlayer;
import DataHolders.ColorDataHolder;
import DataHolders.GlobDataHolder;
import DataHolders.ImageDataHolder;
import static GameObjects.Collision.Enemy.getType1HorDir;
import static GameObjects.Collision.Enemy.getType1VerDir;
import static GameObjects.Collision.Enemy.getType2HorDir;
import static GameObjects.Collision.Enemy.getType2VerDir;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * An advanced enemy type.
 * Takes multiple hits.
 * Actions -
 * 1. Move and rotate in the direction of the player with an adjustment cool
 * down for movement
 * 2. Fire 4 auto locking missiles with a cool down between each shot
 * 3. If a basic enemy collides with a Glob absorb enemy for a number of frames,
 * then shoot enemy out in the direction of the player at max speed
 *
 * @author Matthew Babel
 */
public class Glob extends CollidableObject implements Spawn {

    /**
     * the speed will be activated when the distance between self and the player
     * is less than range.
     */
    private enum GlobSpeeds {
        SLOW(100),
        MEDIUOM(400),
        FAST(1000),
        SUPERFAST(5000);

        private final int r;

        private GlobSpeeds(int range) {
            r = range;
        }

        public int getRange() {
            return r;
        }
    }

    private BufferedImage curImage;

    // data holder class to hold and manipulate the rest of Glob's data
    private final GlobDataHolder DATA;

    /**
     * Create a Glob object, spawn it outside of the given parameters.
     *
     * @param frameW the frames max width
     * @param frameH the frames max height
     * @param position the position in enemy list
     */
    public Glob(int frameW, int frameH, int position) {
        final int MAXHITS = 12;
        final int INVCOOLDOWN = 25;
        final int PIXELSIZE = 310;

        int[] cords = generateCoordinates(frameW, frameH);
        DATA = new GlobDataHolder(cords[0], cords[1], MAXHITS, PIXELSIZE,
                PIXELSIZE, new int[]{0, 0, 1, 0}, 4, 40, INVCOOLDOWN, position,
                frameW, frameH);

        curImage = ImageDataHolder.getGlobImage();
    }

    /**
     * Get spawning location outside of visible area.
     *
     * @param fw frame width
     * @param fh frame height
     * @return new coordinates
     */
    private int[] generateCoordinates(int fw, int fh) {
        // Math.random() returns a number >= 0.0 and < 1
        int randNumTens = (int) (10 * Math.random());
        int randNumThous = (int) (1000 * Math.random());
        int direction = Enemy.getDirection(randNumTens);

        Random r = new Random();
        int randHeight = r.nextInt(1000) + fh + 400; // returns fh+400 + (0-999)
        int randWidth = r.nextInt(1000) + fw + 400;

        int[] cords = new int[2];
        switch (direction) {
            case 1:
                cords[0] = randNumThous * -1;
                cords[1] = randHeight;
                break;
            case 2:
                cords[0] = randWidth;
                cords[1] = randNumThous * -1;
                break;
            case 3:
                cords[0] = (int) (randNumThous * 4);
                cords[1] = randHeight;
                break;
            case 4:
                cords[0] = randWidth;
                cords[1] = (int) (randNumThous * 4);
                break;
            default:
                System.out.println("Cant generate location");
        }
        return cords;
    }

    /**
     * Accepts the coordinates of the center of the player, updates movement
     * and rotation.
     *
     * @param playerX x center of player
     * @param playerY y center of player
     */
    @Override
    public void move(float playerX, float playerY) {
        if (this.isCollision()) {
            if (DATA.isCooldownOff()) {
                this.collisionOff();
            }
        }

        if (DATA.shouldAdjust()) {
            adjust(playerX, playerY);
        }

        DATA.setRotSpeed(updateRotateSpeed(playerX, playerY, DATA.getTargetRotation()));
        DATA.setTargetXCord(changeXCord(DATA.getHorDirection(), DATA.getHorSpeed()));
        DATA.setTargetYCord(changeYCord(DATA.getVerDirection(), DATA.getVerSpeed()));

        if (!isDegreeClose(playerX, playerY, DATA.getTargetRotation(), 2)) {
            if (DATA.isFacingPlayer()) {
                DATA.setFacingPlayer(false);
            }
            DATA.setTargetRotation(checkRotation(DATA.getTargetRotation() + DATA.getRotSpeed()));
        } else if (!DATA.isFacingPlayer()) {
            DATA.setFacingPlayer(true);
        }

        if (isOnScreen(DATA.getFrameWidth(), DATA.getFrameHeight())) {
            doMissileCheck(playerX, playerY);
        }
    }

    /**
     * Adjust movement direction and speed.
     *
     * @param playerX
     * @param playerY
     */
    private void adjust(float playerX, float playerY) {
        final int CHECKRANGE = 50;
        DATA.setHorDirection(getDirection(playerX, DATA.getTargetXCord()
                + (DATA.getImageWidth() / 2), CHECKRANGE));
        DATA.setVerDirection(getDirection(playerY, DATA.getTargetYCord()
                + (DATA.getImageHeight() / 2), CHECKRANGE));

        DATA.setHorSpeed(getSpeed(Math.abs(playerX - DATA.getTargetXCord()),
                DATA.getHorSpeed()));
        DATA.setVerSpeed(getSpeed(Math.abs(playerY - DATA.getTargetYCord()),
                DATA.getVerSpeed()));
    }

    /**
     * return the new current speed at the given distance.
     *
     * @param distance current distance from player
     * @param curSpeed current speed
     * @return updated speed
     */
    private int getSpeed(float distance, int curSpeed) {
        int MAXSPEED = DATA.getMaxSpeed();
        if (distance > GlobSpeeds.FAST.getRange()) {
            return (curSpeed >= MAXSPEED) ? MAXSPEED : curSpeed + 1;
        } else if (distance > GlobSpeeds.MEDIUOM.getRange()) {
            return (curSpeed >= MAXSPEED - 1) ? MAXSPEED - 1 : curSpeed + 1;
        } else if (distance > GlobSpeeds.SLOW.getRange()) {
            return getSpecificSpeed(curSpeed, MAXSPEED - 2);
        } else {
            return getSpecificSpeed(curSpeed, MAXSPEED - 3);
        }
    }

    /**
     * Get a specific speed, could be slower or faster or the same.
     *
     * @param curSpeed the current speed
     * @param check the new speed being checked for
     * @return new speed
     */
    private int getSpecificSpeed(int curSpeed, int check) {
        if (curSpeed > check) {
            return curSpeed - 1;
        } else if (curSpeed == check) {
            return curSpeed;
        } else {
            return curSpeed + 1;
        }
    }

    /**
     * Change the x cord with a given direction and speed.
     *
     * @param direction either left or right
     * @param speed current speed
     * @return new x cord
     */
    private float changeXCord(int direction, int speed) {
        switch (direction) {
            case 1:
                return DATA.getTargetXCord() - speed;
            case 2:
                return DATA.getTargetXCord() + speed;
            default:
                return DATA.getTargetXCord();
        }
    }

    /**
     * Change the y cord with a given direction and speed.
     *
     * @param direction either up or down
     * @param speed current speed
     * @return new y cord
     */
    private float changeYCord(int direction, int speed) {
        switch (direction) {
            case 1:
                return DATA.getTargetYCord() - speed;
            case 2:
                return DATA.getTargetYCord() + speed;
            default:
                return DATA.getTargetYCord();
        }
    }

    /**
     * Gets the direction the player should be moving in.
     * 1 for left / up
     * 2 for right / down
     * 0 for no movement needed
     *
     * @param playerCord - player coordinate
     * @param globCord - glob coordinate
     * @param CHECKRANGE - the range in which the glob will stop movement
     * @return direction
     */
    private int getDirection(float playerCord, float globCord,
            final int CHECKRANGE) {
        int direction;
        // getting direction of movement and checking if within check range pixel area
        // 1 - left or up, 2 - right or down 0 - within check range
        if (playerCord < globCord - CHECKRANGE) {
            direction = 1;
        } else if (playerCord > globCord + CHECKRANGE) {
            direction = 2;
        } else {
            direction = 0;
        }
        return direction;
    }

    /**
     * returns true if the current degree is close to degree facing player.
     *
     * @param playerX the players x cord
     * @param playerY the players y cord
     * @param curDegree the current degree
     * @param stopRange the range in which the degree is close to facing player
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

        if (n >= 180) {
            if (curDegree > n) {
                return (Math.abs(DATA.getRotSpeed())) * -1;
            } else if (curDegree < (n - 180)) {
                return (Math.abs(DATA.getRotSpeed())) * -1;
            } else {
                return (Math.abs(DATA.getRotSpeed()));
            }
        } else if (n < 180) {
            if (curDegree < n) {
                return Math.abs(DATA.getRotSpeed());
            } else if (curDegree > (n + 180)) {
                return Math.abs(DATA.getRotSpeed());
            } else {
                return (Math.abs(DATA.getRotSpeed())) * -1;
            }
        }
        return 0;
    }

    /**
     * Return the degree from its coordinates to the player coordinates
     * uses trig tan function to find this angle.
     *
     * @param px player x cord
     * @param py player y cord
     * @return degree facing player
     */
    private double getDegreeTowardsPlayer(float px, float py) {
        updateCenterCords();
        int globCenterX = DATA.getCenterX();
        int globCenterY = DATA.getCenterY();

        double d1 = Math.abs(globCenterY - py);
        double d2 = Math.abs(globCenterX - px);
        double n = Math.atan2(d1, d2);
        n = Math.toDegrees(n);

        n = 90 - n;
        if (globCenterX <= px && globCenterY <= py) { // down right
            n = Math.abs(n);
            n = 90 - n;
            n += 90;
        } else if (globCenterX <= px && globCenterY >= py) { // up right
            // empty
        } else if (globCenterX >= px && globCenterY <= py) { // down left
            n += 180;
        } else if (globCenterX >= px && globCenterY >= py) { // up left
            n = 90 - n;
            n += 270;
        }
        return n;
    }

    /**
     * return true if Glob's cords are within the given parameters.
     *
     * @param xMax x parameter
     * @param yMax y parameter
     * @return true if with parameters
     */
    private boolean isOnScreen(int xMax, int yMax) {
        float xCord = DATA.getTargetXCord();
        float yCord = DATA.getTargetYCord();
        int width = DATA.getImageWidth();
        int height = DATA.getImageHeight();

        return ((xCord < (xMax - width))
                && (xCord > (0 + width))
                && (yCord < (yMax - height))
                && (yCord > (0 + height)));
    }

    /**
     * Check if glob should release a missile, either from player being in the
     * line of site of a missile or from a missile's auto release.
     * Both of these decisions are controlled by a cool down in the glob data
     * holder class.
     *
     * bottom right cords x : 215 y : 235 rotation 150
     * top right cords x : 220 y : 190 rotation 115
     * bottom left cords x : 90 y : 235 rotation 210
     * top left cords x : 85 y : 190 rotation 245
     *
     * @param playerX the player center x cord
     * @param playerY the player center y cord
     */
    private void doMissileCheck(float playerX, float playerY) {

        if (!DATA.isMissilesEmpty()) {
            if (DATA.isMissileCooldown()) {
                DATA.missileCooldownTick();
            } else if ((!DATA.isDetached(0)) && isDegreeClose(playerX, playerY, //top left
                    (int) checkRotation(DATA.getTargetRotation() + 245), 25)) {
                deployMissile(85, 190, 245, 0, true);
            } else if ((!DATA.isDetached(1)) && isDegreeClose(playerX, playerY, // top right
                    (int) checkRotation(DATA.getTargetRotation() + 115), 25)) {
                deployMissile(220, 190, 115, 1, true);
            } else if ((!DATA.isDetached(2)) && isDegreeClose(playerX, playerY, // bottom right
                    (int) checkRotation(DATA.getTargetRotation() + 150), 25)) {
                deployMissile(215, 235, 150, 2, true);
            } else if ((!DATA.isDetached(3)) && isDegreeClose(playerX, playerY, // bottom left
                    (int) checkRotation(DATA.getTargetRotation() + 210), 25)) {
                deployMissile(90, 235, 210, 3, true);
            } else if (DATA.isMissileAutoCooldown()) {
                DATA.missileAutoTick();
            } else {
                AutoRelease();
            }
        }
    }

    /**
     * Auto release a missile.
     */
    private void AutoRelease() {
        if (!DATA.isDetached(0)) {
            deployMissile(85, 190, 245, 0, false);
        } else if (!DATA.isDetached(1)) {
            deployMissile(220, 190, 115, 1, false);
        } else if (!DATA.isDetached(2)) {
            deployMissile(215, 235, 150, 2, false);
        } else {
            deployMissile(90, 235, 210, 3, false);
        }
    }

    /**
     * create a missile and change the Glob's image to look like its lost a
     * missile.
     *
     * @param x x change for x cord
     * @param y y change for y cord
     * @param rotation missile rotation
     * @param n missile index number
     * @param fired true if fired false if auto deployed
     */
    private void deployMissile(int x, int y, int rotation, int n, boolean fired) {
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.MISSILEDEPLOY);
        initMissile(x, y, rotation, fired);
        DATA.setDetached(n);
        curImage = DATA.getGlobImage(DATA.getImageIndex());
        imageProcessing(ColorDataHolder.getBasicGreen(), ColorDataHolder.getDarkGreen(),
                DATA.getCurColor());
    }

    /**
     * create a missile. rotate its points to match the rotation of the Glob.
     *
     * @param xChange x change to make more accurate spawn
     * @param yChange y change to make more accurate spawn
     * @param rotation rotation of Glob
     * @param fired true if fired false if auto released
     */
    private void initMissile(int xChange, int yChange, int rotation, boolean fired) {
        Point offset = getOffset();

        Point2D[] p1 = {new Point((int) DATA.getTargetXCord() + xChange, (int) DATA.getTargetYCord() + yChange)};
        Point2D[] p2 = new Point2D[1];

        AffineTransform.getRotateInstance(Math.toRadians(DATA.getTargetRotation()),
                DATA.getCenterX(), DATA.getCenterY())
                .transform(p1, 0, p2, 0, 1);

        BufferedImage img = DATA.getMissileImages(DATA.getHurtStage());

        DATA.addMissile(new GlobMissile((int) p2[0].getX() + offset.x,
                (int) p2[0].getY() + offset.y, (int) checkRotation(DATA.getTargetRotation() + rotation), img), fired);
    }

    /**
     * Create the offset used to further increase spawn accuracy of missile.
     *
     * @return offset x and offset y wrapped in a Point object
     */
    private Point getOffset() {
        Point p = new Point(50, 80);
        float rot = DATA.getTargetRotation();
        if (rot <= 180) {
            rot = rot - 180;
            p.x *= (rot / 180);
            p.y *= (rot / 180);
            p.y -= 40;
        } else {
            rot = 360 - rot;
            p.x *= (rot / 180);
            p.y *= (rot / 180);
        }
        return p;
    }

    /**
     * Makes sure the given number is between 0 and 359.
     *
     * @param r the given rotation
     * @return new rotation between 0 and 359
     */
    public static float checkRotation(float r) {
        if (r < 0) {
            return r += 360;
        } else if (r > 359) {
            return r -= 360;
        } else {
            return r;
        }
    }

    /**
     * update the location of the center coordinates.
     */
    private void updateCenterCords() {
        DATA.setCenterX((int) DATA.getTargetXCord() + (DATA.getImageWidth() / 2));
        DATA.setCenterY((int) DATA.getTargetYCord() + (DATA.getImageHeight() / 2));
    }

    @Override
    public BufferedImage getImage() {
        return curImage;
    }

    @Override
    public float getTargetXCord() {
        return DATA.getTargetXCord();
    }

    @Override
    public float getTargetYCord() {
        return DATA.getTargetYCord();
    }

    @Override
    public float getCurrentXCord() {
        return DATA.getCurrentXCord();
    }

    @Override
    public float getCurrentYCord() {
        return DATA.getCurrentYCord();
    }

    public int getCenterX() {
        return DATA.getCenterX();
    }

    public int getCenterY() {
        return DATA.getCenterY();
    }

    public float getTargetRotation() {
        return DATA.getTargetRotation();
    }

    public float getCurrentRotation() {
        return DATA.getCurrentRotation();
    }

    public void setCurrentRotation(float r) {
        DATA.setCurrentRotation(r);
    }

    @Override
    public int getImageWidth() {
        return DATA.getImageWidth();
    }

    @Override
    public int getImageHeight() {
        return DATA.getImageHeight();
    }

    public int getPosition() {
        return DATA.getPosition();
    }

    @Override
    public void decrementPosition() {
        DATA.decrementPosition();
    }

    @Override
    public boolean isDead() {
        return (DATA.getDurability() <= 0);
    }

    @Override
    public boolean isKilled() {
        return true;
    }

    public boolean isMissileCreated() {
        return DATA.isMissileCreated();
    }

    public GlobMissile getMissile() {
        return DATA.getNewMissile();
    }

    public boolean isFacingPlayer() {
        return DATA.isFacingPlayer();
    }

    public void setCurrentX(float x) {
        DATA.setCurrentXCord(x);
    }

    public void setCurrentY(float y) {
        DATA.setCurrentYCord(y);
    }

    /**
     * Rotate the mask according to given rotation.
     *
     * @param degree the rotation
     * @param MASKPOINTS1 array of points before rotation
     * @param newPoints will hold points after rotation
     * @return rotated mask
     */
    private Point2D[] doMaskRotation(float degree, Point2D[] MASKPOINTS1, Point2D[] newPoints) {

        if (degree != 0) {
            AffineTransform.getRotateInstance(Math.toRadians(degree),
                    DATA.getCenterX(), DATA.getCenterY())
                    .transform(MASKPOINTS1, 0, newPoints, 0, 14);
        } else {
            newPoints = MASKPOINTS1.clone();
        }

        return newPoints;
    }

    /**
     * make the mask before being rotated.
     */
    private void makeMask() {
        int xCord = (int) DATA.getTargetXCord();
        int yCord = (int) DATA.getTargetYCord();

        Point2D[] MASKPOINTS1 = DATA.getMaskPoints1();
        MASKPOINTS1[0] = new Point(xCord + 55, yCord + 40);
        MASKPOINTS1[1] = new Point(xCord + 160, yCord + 20);
        MASKPOINTS1[2] = new Point(xCord + 255, yCord + 40);
        MASKPOINTS1[3] = new Point(xCord + 255, yCord + 67);
        MASKPOINTS1[4] = new Point(xCord + 218, yCord + 80);
        MASKPOINTS1[5] = new Point(xCord + 245, yCord + 186);
        MASKPOINTS1[6] = new Point(xCord + 175, yCord + 252);
        MASKPOINTS1[7] = new Point(xCord + 184, yCord + 268);
        MASKPOINTS1[8] = new Point(xCord + 125, yCord + 268);
        MASKPOINTS1[9] = new Point(xCord + 135, yCord + 252);
        MASKPOINTS1[10] = new Point(xCord + 53, yCord + 186);
        MASKPOINTS1[11] = new Point(xCord + 86, yCord + 80);
        MASKPOINTS1[12] = new Point(xCord + 45, yCord + 63);
        MASKPOINTS1[13] = new Point(xCord + 55, yCord + 40);

        DATA.setMaskPoints1(MASKPOINTS1);
        Point2D[] points2 = DATA.getMaskPoints2();
        DATA.setMaskPoints2(doMaskRotation(DATA.getTargetRotation(),
                DATA.getMaskPoints1(), points2));
    }

    /**
     * Make the collision mask then return it as a polygon.
     *
     * @return polygon collision mask
     */
    @Override
    public Polygon getCollisionMask() {
        makeMask();

        Point2D[] points = DATA.getMaskPoints2();
        final int SIZE = 14;
        int[] xMask = new int[SIZE];
        int[] yMask = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            xMask[i] = (int) points[i].getX();
            yMask[i] = (int) points[i].getY();
        }
        return new Polygon(xMask, yMask, SIZE);
    }

    /**
     * Called when collided with.
     * 1 is collided with glob
     * 2 is collided with glob
     * 3 is a hit by player
     *
     * @param type the type of collision
     */
    @Override
    void collision(int type) {
        if (!this.isCollision()) {
            Random r = new Random();
            int direction = r.nextInt(4); // gets a number between 0 and 4

            switch (type) {
                case 1:
                    DATA.setHorSpeed(getType1HorDir(direction, DATA.getMaxSpeed() - 2));
                    DATA.setVerSpeed(getType1VerDir(direction, DATA.getMaxSpeed() - 2));
                    break;
                case 2:
                    DATA.setHorSpeed(getType2HorDir(direction, DATA.getMaxSpeed() - 2));
                    DATA.setVerSpeed(getType2VerDir(direction, DATA.getMaxSpeed() - 2));
                    break;
                case 3:
                    hit();
                    break;
            }
            this.collisionOn();
        }
    }

    /**
     * called when hit by player. decrement durability and change image color
     * if needed.
     */
    private void hit() {
        DATA.decrementDur();

        int dur = DATA.getDurability();

        switch (dur) {
            case 10:
                imageProcessing(DATA.getCurColor(),
                        ColorDataHolder.getDarkGreen(),
                        ColorDataHolder.getGlobHurt1());
                DATA.setCurColor(ColorDataHolder.getGlobHurt1());
                break;
            case 7:
                imageProcessing(DATA.getCurColor(),
                        ColorDataHolder.getGlobHurt2());
                DATA.setCurColor(ColorDataHolder.getGlobHurt2());
                break;
            case 4:
                imageProcessing(DATA.getCurColor(),
                        ColorDataHolder.getGlobHurt3());
                DATA.setCurColor(ColorDataHolder.getGlobHurt3());
                break;
            case 2:

                imageProcessing(ColorDataHolder.getGlobHurt3(),
                        ColorDataHolder.getGlobHurt4());
                DATA.setCurColor(ColorDataHolder.getGlobHurt4());
                break;
        }
    }

    /**
     * Test the images pixels for the current color and if found
     * switch pixel to the new color.
     *
     * @param curColor color being checked for
     * @param newColor color being changed to
     */
    private void imageProcessing(Color curColor, Color newColor) {
        final int WIDTH = DATA.getImageWidth();
        final int HEIGHT = DATA.getImageHeight();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                processPixel(i, j, curColor, newColor, curImage);
            }
        }
    }

    /**
     * Test the images pixels for the current color as well as a second color
     * and if found switch pixel to the new color.
     *
     * @param curColor color being checked for
     * @param secondColor a second color being checked for
     * @param newColor color being changed to
     */
    private void imageProcessing(Color curColor, Color secondColor, Color newColor) {
        final int WIDTH = DATA.getImageWidth();
        final int HEIGHT = DATA.getImageHeight();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                processPixel(i, j, curColor, secondColor, newColor, curImage);
            }
        }
    }

    /**
     * tests if the pixel at the given x, y will be switched to the next color.
     *
     * @param x row of pixel
     * @param y column of pixel
     * @param curColor tested color
     * @param nextColor color being switched to
     */
    private void processPixel(int x, int y, Color curColor, Color nextColor,
            BufferedImage img) {
        int p = img.getRGB(x, y);
        // get alpha
        int a = (p >> 24) & 0xff;
        // get red
        int r = (p >> 16) & 0xff;
        // get green
        int g = (p >> 8) & 0xff;
        // get blue
        int b = p & 0xff;

        if (new Color(r, g, b, a).equals(curColor)) {
            a = nextColor.getAlpha();
            r = nextColor.getRed();
            g = nextColor.getGreen();
            b = nextColor.getBlue();
            p = (a << 24) | (r << 16) | (g << 8) | b;
            img.setRGB(x, y, p);
        }
    }

    /**
     * tests if the pixel at the given x, y holds either the current color or
     * the second color. If it does switch to the next color.
     *
     * @param x row of pixel
     * @param y column of pixel
     * @param curColor current color
     * @param secondColor second color being tested for
     * @param nextColor color being changed to
     */
    private void processPixel(int x, int y, Color curColor, Color secondColor, Color nextColor,
            BufferedImage img) {
        int p = img.getRGB(x, y);
        // get alpha
        int a = (p >> 24) & 0xff;
        // get red
        int r = (p >> 16) & 0xff;
        // get green
        int g = (p >> 8) & 0xff;
        // get blue
        int b = p & 0xff;

        if (new Color(r, g, b, a).equals(curColor)
                || new Color(r, g, b, a).equals(secondColor)) {
            a = nextColor.getAlpha();
            r = nextColor.getRed();
            g = nextColor.getGreen();
            b = nextColor.getBlue();
            p = (a << 24) | (r << 16) | (g << 8) | b;
            img.setRGB(x, y, p);
        }
    }
}
