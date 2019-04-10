package GameObjects.Collision;

import DataHolders.BasicEnemyDataHolder;
import DataHolders.BasicEnemyDataHolder.CountTypes;
import DataHolders.ImageDataHolder;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * A basic enemy. It will fly towards the player and within a certain amount of
 * time adjust its flight path according to players new position.
 * This is designed to create a curve like flight pattern, as if the enemy is
 * attracted to the Player but has its own momentum as well.
 *
 * @author Matthew Babel
 */
public class Enemy extends CollidableObject implements Spawn {

    /**
     * Distance from player in which speed type is activated
     */
    private enum SpeedRanges {
        SLOW(10),
        MEDIUOM(40),
        FAST(120),
        SUPERFAST(500),
        MAX(5000);

        private final int r;

        private SpeedRanges(int range) {
            r = range;
        }

        public int getRange() {
            return r;
        }
    }

    private final BufferedImage image;
    private final BasicEnemyDataHolder DATA;

    /**
     * Initialize enemy variables, loads sprite image and generates initial
     * coordinates.
     *
     * @param frameWidth the width of the frame, needed to generate coordinates
     * @param frameHeight the height of the frame, needed to generate coordinates
     * @param pos an id number to determine position in spawner
     */
    public Enemy(int frameWidth, int frameHeight, int pos) {
        DATA = new BasicEnemyDataHolder(pos, 0, new int[]{20, 20, 30, 70},
                7, 50, 50);
        
        image = ImageDataHolder.getEnemyImage();
        generateCoordinates(frameWidth, frameHeight);
    }

    /**
     * Randomly generating x and y coordinates somewhere outside of the playable
     * area.
     */
    private void generateCoordinates(int FRAMEWIDTH, int FRAMEHEIGHT) {
        // Math.random() returns a number >= 0.0 and < 1
        int randNumTens = (int) (10 * Math.random());
        int randNumThous = (int) (1000 * Math.random());
        int direction = getDirection(randNumTens);

        Random r = new Random();
        int randHeight = r.nextInt(FRAMEHEIGHT + 1); // gets a number between 0 and FRAMEHEIGHT
        int randWidth = r.nextInt(FRAMEWIDTH + 1);
        switch (direction) {
            case 1:
                DATA.setTargetXCord((randNumThous * -1) - 100);
                DATA.setTargetYCord(randHeight);
                DATA.setCurrentXCord((randNumThous * -1) - 100);
                DATA.setCurrentYCord(randHeight);             
                break;
            case 2:
                DATA.setTargetXCord(randWidth);
                DATA.setTargetYCord((randNumThous * -1) - 100);
                DATA.setCurrentXCord(randWidth);
                DATA.setCurrentYCord((randNumThous * -1) - 100);
                break;
            case 3:
                DATA.setTargetXCord((int) (FRAMEWIDTH * 1.5) + randNumThous);
                DATA.setTargetYCord(randHeight);
                DATA.setCurrentXCord((int) (FRAMEWIDTH * 1.5) + randNumThous);
                DATA.setCurrentYCord(randHeight);
                break;
            case 4:
                DATA.setTargetXCord(randWidth);
                DATA.setTargetYCord((int) (FRAMEHEIGHT * 1.5) + randNumThous);
                DATA.setCurrentXCord(randWidth);
                DATA.setCurrentYCord((int) (FRAMEHEIGHT * 1.5) + randNumThous);
                break;
            default:
                System.out.println("Cant generate location");
        }
    }

    /**
     * Return a direction based off the number given.
     *
     * @param randNumTens a random number between 0 - 9
     * @return a number to represent a direction
     */
    public static int getDirection(int randNumTens) {
        int direction;
        switch (randNumTens) {
            case 0:
                direction = 1;
                break;
            case 1:
                direction = 1;
                break;
            case 2:
                direction = 2;
                break;
            case 3:
                direction = 2;
                break;
            case 4:
                direction = 3;
                break;
            case 5:
                direction = 3;
                break;
            case 6:
                direction = 4;
                break;
            case 7:
                direction = 4;
                break;
            case 8:
                direction = 3;
                break;
            case 9:
                direction = 4;
                break;
            default:
                direction = 0;
                break;
        }
        return direction;
    }

    /**
     * Moving the enemy in a custom path curvature.
     *
     * @param targetX the x position moving towards
     * @param targetY the y position moving towards
     */
    @Override
    public void move(float targetX, float targetY) {
        absorbCooldownCheck();

        int adjustIndex = CountTypes.ADJUST.getIndex();
        incrementCount(adjustIndex);

        if (DATA.getCounts(adjustIndex) >= DATA.getCOUNTMAX(adjustIndex)) {
            SpeedRanges MAXRANGE = SpeedRanges.MAX;
            SpeedRanges SUPERFASTRANGE = SpeedRanges.SUPERFAST;
            SpeedRanges FASTRANGE = SpeedRanges.FAST;
            SpeedRanges MEDIUMRANGE = SpeedRanges.MEDIUOM;
            SpeedRanges SLOWRANGE = SpeedRanges.SLOW;

            // 0 - none, 1 - left/up, 2 - right/down    
            int horDirection;
            int verDirection;
            final int CHECKRANGE = 10;
            horDirection = getHorizontalDirection(targetX, DATA.getTargetXCord(), CHECKRANGE);
            verDirection = getVerticalDirection(targetY, DATA.getTargetYCord(), CHECKRANGE);

            //horizontal movement
            DATA.setHorSpeed(doMovement(horDirection, DATA.getHorSpeed(),
                    targetX, DATA.getTargetXCord(), MAXRANGE, 1,
                    DATA.getMAXSPEED(), SUPERFASTRANGE, FASTRANGE, MEDIUMRANGE, SLOWRANGE));
            //vertical Movement
            DATA.setVerSpeed(doMovement(verDirection, DATA.getVerSpeed(),
                    targetY, DATA.getTargetYCord(), MAXRANGE, 1, DATA.getMAXSPEED(),
                    SUPERFASTRANGE, FASTRANGE, MEDIUMRANGE, SLOWRANGE));

            DATA.setCounts(adjustIndex, 0);
        }

        DATA.setTargetXCord((int) (DATA.getTargetXCord() + DATA.getHorSpeed()));
        DATA.setTargetYCord((int) (DATA.getTargetYCord() + DATA.getVerSpeed()));
        tick();
    }

    /**
     * Increment the count at a given index of Count Type.
     *
     * @param index the count type
     */
    private void incrementCount(int index) {
        DATA.setCounts(index, DATA.getCounts(index) + 1);
    }

    /**
     * Check if absorb is on cool down, if so update the cool down.
     */
    private void absorbCooldownCheck() {
        if (DATA.isAbsorbOnCooldown()) {
            int index = CountTypes.ABSORBCOOLDOWN.getIndex();
            incrementCount(index);
            if (DATA.getCounts(index) > DATA.getCOUNTMAX(index)) {
                DATA.setAbsorbOnCooldown(false);
                DATA.setCounts(index, 0);
            }
        }
    }

    /**
     * Deciding what kind of movement to do.
     *
     * @return movement speed
     */
    private float doMovement(int dir, float speed, float findCord, float cord, SpeedRanges MAXRANGE, int SPEEDCHANGE, int MAXSPEED, SpeedRanges SUPERFASTRANGE, SpeedRanges FASTRANGE, SpeedRanges MEDIUMRANGE, SpeedRanges SLOWRANGE) {
        // horizontal movement
        switch (dir) {
            // left
            case 1:
                speed = getLeftUpMovement(findCord, cord,
                        MAXRANGE, speed, SPEEDCHANGE, MAXSPEED,
                        SUPERFASTRANGE, FASTRANGE, MEDIUMRANGE, SLOWRANGE);
                break;
            // right
            case 2:
                speed = getRightDownMovement(findCord, cord,
                        MAXRANGE, speed, SPEEDCHANGE, MAXSPEED,
                        SUPERFASTRANGE, FASTRANGE, MEDIUMRANGE, SLOWRANGE);
                break;
            // within 20 pixel window
            case 0:
                if (speed > 0) {
                    speed -= SPEEDCHANGE;
                } else if (speed < 0) {
                    speed += SPEEDCHANGE;
                }
                break;
        }
        return speed;
    }

    /**
     * Doing movement for right or down direction.
     *
     * @return movement speed.
     */
    private float getRightDownMovement(float playerX, float xCord, SpeedRanges MAXRANGE, float speedHorizontal, int SPEEDCHANGE, int MAXSPEED, SpeedRanges SUPERFASTRANGE, SpeedRanges FASTRANGE, SpeedRanges MEDIUMRANGE, SpeedRanges SLOWRANGE) {
        if (playerX >= xCord + MAXRANGE.getRange()) {
            if (!(speedHorizontal + SPEEDCHANGE > MAXSPEED)) {
                speedHorizontal += SPEEDCHANGE;
            }
        } else if (playerX >= xCord + SUPERFASTRANGE.getRange()) {
            if (!(speedHorizontal + SPEEDCHANGE > (MAXSPEED - 1))) {
                speedHorizontal += SPEEDCHANGE;
            }
        } else if (playerX >= xCord + FASTRANGE.getRange()) {
            if (!(speedHorizontal + SPEEDCHANGE > (MAXSPEED - 2))) {
                speedHorizontal += SPEEDCHANGE;
            }
        } else if (playerX >= xCord + MEDIUMRANGE.getRange()) {
            if (!(speedHorizontal + SPEEDCHANGE > (MAXSPEED - 3))) {
                speedHorizontal += SPEEDCHANGE;
            }
        } else if (playerX >= xCord + SLOWRANGE.getRange()) {
            if (!(speedHorizontal + SPEEDCHANGE > (MAXSPEED - 4))) {
                speedHorizontal += SPEEDCHANGE;
            }
        }
        return speedHorizontal;
    }

    /**
     * Doing movement either left or up direction.
     *
     * @return movement speed
     */
    private float getLeftUpMovement(float playerCord, float cord, SpeedRanges MAXRANGE, float curSpeed, int SPEEDCHANGE, int MAXSPEED, SpeedRanges SUPERFASTRANGE, SpeedRanges FASTRANGE, SpeedRanges MEDIUMRANGE, SpeedRanges SLOWRANGE) {
        if (playerCord <= cord - MAXRANGE.getRange()) {
            if (!(curSpeed - SPEEDCHANGE < (-1 * MAXSPEED))) {
                curSpeed -= SPEEDCHANGE;
            }
        } else if (playerCord <= cord - SUPERFASTRANGE.getRange()) {
            if (!(curSpeed - SPEEDCHANGE < (-1 * MAXSPEED) - 1)) {
                curSpeed -= SPEEDCHANGE;
            }
        } else if (playerCord <= cord - FASTRANGE.getRange()) {
            if (!(curSpeed - SPEEDCHANGE < (-1 * MAXSPEED) - 2)) {
                curSpeed -= SPEEDCHANGE;
            }
        } else if (playerCord <= cord - MEDIUMRANGE.getRange()) {
            if (!(curSpeed - SPEEDCHANGE < (-1 * MAXSPEED) - 3)) {
                curSpeed -= SPEEDCHANGE;
            }
        } else if (playerCord <= cord - SLOWRANGE.getRange()) {
            if (!(curSpeed - SPEEDCHANGE < (-1 * MAXSPEED) - 4)) {
                curSpeed -= SPEEDCHANGE;
            }
        }
        return curSpeed;
    }

    private int getVerticalDirection(float playerY, float yCord, final int CHECKRANGE) {
        int wandVertical;
        if (playerY < yCord - CHECKRANGE) {
            wandVertical = 1;
        } else if (playerY > yCord + CHECKRANGE) {
            wandVertical = 2;
        } else {
            wandVertical = 0;
        }
        return wandVertical;
    }

    private int getHorizontalDirection(float playerX, float xCord, final int CHECKRANGE) {
        int wandHorizontal;
        if (playerX < xCord - CHECKRANGE) {
            wandHorizontal = 1;
        } else if (playerX > xCord + CHECKRANGE) {
            wandHorizontal = 2;
        } else {
            wandHorizontal = 0;
        }
        return wandHorizontal;
    }

    /**
     * Called from move counter to do additional enemy updates. Turns off
     * collision if it is off cool down.
     */
    private void tick() {
        if (this.isCollision()) {
            int index = CountTypes.COLLISION.getIndex();
            incrementCount(index);

            // making it collidable
            if (DATA.getCounts(index) > DATA.getCOUNTMAX(index)) {
                DATA.setCounts(index, 0);
                this.collisionOff();
            }
        }
    }

    @Override
    public BufferedImage getImage() {
        return image;
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

    @Override
    public boolean isDead() {
        return DATA.isDead();
    }

    @Override
    public boolean isKilled() {
        return DATA.isKilled();
    }

    public boolean isBeingAbsored() {
        return DATA.isBeingAbsorbed();
    }

    public boolean isAbsorbCdOn() {
        return DATA.isAbsorbOnCooldown();
    }

    public int getPosition() {
        return DATA.getPosition();
    }

    @Override
    public void decrementPosition() {
        DATA.setPosition(DATA.getPosition() - 1);
    }

    @Override
    public int getImageWidth() {
        return DATA.getWidth();
    }

    @Override
    public int getImageHeight() {
        return DATA.getHeight();
    }

    private void setGlobPosition(int p) {
        DATA.setGlobPosition(p);
    }
    
    public void setCurrentX(float x) {
        DATA.setCurrentXCord(x);
    }
    
    public void setCurrentY(float y) {
        DATA.setCurrentYCord(y);
    }  

    public int getGlobPosition() {
        return DATA.getGlobPosition();
    }

    public void decrementGlobPosition() {
        DATA.setGlobPosition(DATA.getGlobPosition() - 1);
    }

    public void setBeingAbsorbed(boolean b) {
        DATA.setBeingAbsorbed(b);
    }

    public void tickAbsorbCount() {
        incrementCount(CountTypes.ABSORB.getIndex());
    }

    public boolean isAbsorbCount() {
        int index = CountTypes.ABSORB.getIndex();
        return (DATA.getCounts(index) > DATA.getCOUNTMAX(index));
    }

    public boolean isAbsorbcdCount() {
        int index = CountTypes.ABSORBCOOLDOWN.
                getIndex();
        return (DATA.getCounts(index) > DATA.getCOUNTMAX(index));
    }

    /**
     * return a polygon of the enemy collision mask.
     *
     * @return polygon of collision mask
     */
    @Override
    Polygon getCollisionMask() {
        int xCord = (int) DATA.getCurrentXCord();
        int yCord = (int) DATA.getCurrentYCord();
        int PIXELWIDTH = DATA.getWidth();
        int PIXELHEIGHT = DATA.getHeight();
        final int L = 5; // move in by 5

        int[] xcords = {xCord + L, xCord + PIXELWIDTH - L, xCord + PIXELWIDTH - L, xCord + L};
        int[] ycords = {yCord + L, yCord + L, yCord + PIXELHEIGHT - L, yCord + PIXELHEIGHT - L};
        return new Polygon(xcords, ycords, 4);
    }

    /**
     * Called when collided with. Is given a type of collision and will shoot
     * off into a random direction either to the left or the right if hit by
     * another enemy.
     *
     * @param type the type of collision (left or right) 3 & 4 is player collision
     */
    @Override
    void collision(int type) {
        // type 3 won't get ignored

        if (!DATA.isBeingAbsorbed()) {
            if (type == 3) {
                DATA.setDead(true);
                DATA.setKilled(true);
            } else if (type == 4) {
                DATA.setDead(true);
            }
        }

        // check if ignoring collision between enemies
        if (!this.isCollision() && !DATA.isAbsorbOnCooldown()) {
            Random r = new Random();
            int direction = r.nextInt(4); // gets a number between 0 and 4

            switch (type) {
                case 1:
                    DATA.setHorSpeed(getType1HorDir(direction, DATA.getMAXSPEED()));
                    DATA.setVerSpeed(getType1VerDir(direction, DATA.getMAXSPEED()));
                    break;
                case 2:
                    DATA.setHorSpeed(getType2HorDir(direction, DATA.getMAXSPEED()));
                    DATA.setVerSpeed(getType2VerDir(direction, DATA.getMAXSPEED()));
                    break;
                default:
                    break;
            }
        }

        this.collisionOn();
    }

    public static int getType1VerDir(int direction, int MAXSPEED) {
        switch (direction) {
            //down left
            case 0:
                return MAXSPEED - 1;
            //left
            case 1:
                return 0;
            //up left
            case 2:
                return (-1 * MAXSPEED) - 1;
            //up
            case 3:;
                return (-1 * MAXSPEED) - 1;
            default:
                return 0;
        }
    }

    public static int getType2VerDir(int direction, int MAXSPEED) {
        switch (direction) {
            //down left
            case 0:
                return (-1 * MAXSPEED) - 1;
            //left
            case 1:
                return 0;
            //up left
            case 2:
                return MAXSPEED - 1;
            //up
            case 3:;
                return MAXSPEED - 1;
            default:
                return 0;
        }
    }

    public static int getType1HorDir(int d, int MAXSPEED) {
        switch (d) {
            case 0:
                return (-1 * MAXSPEED) - 1;
            case 1:
                return (-1 * MAXSPEED) - 1;
            case 2:
                return (-1 * MAXSPEED) - 1;
            case 3:
                return 0;
            default:
                return 0;
        }
    }

    public static int getType2HorDir(int d, int MAXSPEED) {
        switch (d) {
            case 0:
                return MAXSPEED - 1;
            case 1:
                return MAXSPEED - 1;
            case 2:
                return MAXSPEED - 1;
            case 3:
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Start being absorbed by a glob. Will follow the glob until glob
     * fires it or is killed
     *
     * @param glob the glob being absorbed by.
     */
    public void startAbsorbed(Glob glob) {
        setGlobPosition(glob.getPosition());
        DATA.setBeingAbsorbed(true);
        DATA.setAdjustSpeed(0);
    }

    /**
     * Stop being absorbed. fire off into direction of player at max speed.
     *
     * @param rotationToPlayer rotation facing the player.
     */
    public void endAbsorb(float rotationToPlayer) {
        DATA.setBeingAbsorbed(false);

        DATA.setAdjustSpeed(10);
        DATA.setCounts(CountTypes.ABSORB.getIndex(), 0);
        DATA.setCounts(CountTypes.ADJUST.getIndex(), -10);
        DATA.setAbsorbOnCooldown(true);

        setToMaxSpeed(rotationToPlayer);
    }

    /**
     * set max speed aimed at the direction of the player.
     *
     * @param rot rotation towards player
     */
    private void setToMaxSpeed(float rot) {
        float xMultiplier = (float) Math.sin(Math.toRadians(rot));
        float yMultiplier = ((float) Math.cos(Math.toRadians(rot))) * -1;

        DATA.setHorSpeed((DATA.getMAXSPEED() * 2) * xMultiplier);
        DATA.setVerSpeed((DATA.getMAXSPEED() * 2) * yMultiplier);

    }
}
