package DataHolders;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Holds and returns the images used in the application.
 *
 * @author Matthew Babel
 */
public class ImageDataHolder {

    /**
     * Holds images for the following:
     * player, enemy, enemy explosion, missile explosion, glob explosion,
     * player basic attack, power up speed, power up charge, glob, custom cursor
     */
    private final static BufferedImage[] BASICIMAGES = new BufferedImage[10];
    private final static BufferedImage[] BULLETIMAGES = new BufferedImage[4];
    private final static BufferedImage[] CHARGEIMAGES = new BufferedImage[4];
    private final static BufferedImage[] MISSILEIMAGES = new BufferedImage[5];
    private final static BufferedImage[] GLOBGONEIMAGES = new BufferedImage[15];

    public ImageDataHolder() {
        BASICIMAGES[0] = loadImage("images/MagicWand.png", new Dimension(250, 250));
        BASICIMAGES[1] = loadImage("images/Meteor.png", new Dimension(50, 50));
        BASICIMAGES[2] = loadImage("images/EnemyExplosion.png", new Dimension(100, 100));
        BASICIMAGES[3] = loadImage("images/MissileExplosion.png", new Dimension(90, 90));
        BASICIMAGES[4] = loadImage("images/GlobExplosion.png", new Dimension(250, 250));
        BASICIMAGES[5] = loadImage("images/MagicWandAttacking.png", new Dimension(250, 250));
        BASICIMAGES[6] = loadImage("images/PowerUpSpeed.png", new Dimension(50, 50));
        BASICIMAGES[7] = loadImage("images/PowerUpCharge.png", new Dimension(50, 50));
        BASICIMAGES[8] = loadImage("images/Glob.png", new Dimension(310, 310));
        BASICIMAGES[9] = loadImage("images/CustomCursor.png", new Dimension(4, 4));

        BULLETIMAGES[0] = loadImage("images/ChargingBullet1.png", new Dimension(10, 10));
        BULLETIMAGES[1] = loadImage("images/ChargingBullet2.png", new Dimension(20, 20));
        BULLETIMAGES[2] = loadImage("images/ChargingBullet3.png", new Dimension(30, 30));
        BULLETIMAGES[3] = loadImage("images/ChargingBullet4.png", new Dimension(40, 40));

        CHARGEIMAGES[0] = loadImage("images/MagicWandCharging1.png", new Dimension(250, 250));
        CHARGEIMAGES[1] = loadImage("images/MagicWandCharging2.png", new Dimension(250, 250));
        CHARGEIMAGES[2] = loadImage("images/MagicWandCharging3.png", new Dimension(250, 250));
        CHARGEIMAGES[3] = loadImage("images/MagicWandCharging4.png", new Dimension(250, 250));

        MISSILEIMAGES[0] = loadImage("images/GlobMissile0.png", new Dimension(65, 65));
        MISSILEIMAGES[1] = loadImage("images/GlobMissile1.png", new Dimension(65, 65));
        MISSILEIMAGES[2] = loadImage("images/GlobMissile2.png", new Dimension(65, 65));
        MISSILEIMAGES[3] = loadImage("images/GlobMissile3.png", new Dimension(65, 65));
        MISSILEIMAGES[4] = loadImage("images/GlobMissile4.png", new Dimension(65, 65));

        String[] strs = {"TopLeft", "TopRight", "BottomRight", "BottomLeft",
            "Top", "Bottom", "Right", "Left", "BottomRightTopLeft", "BottomLeftTopRight",
            "LeftBottomRight", "LeftTopRight", "RightTopLeft", "RightBottomLeft", "All"};

        for (int i = 0; i < 15; i++) {
            GLOBGONEIMAGES[i] = loadImage("images/GlobGone" + strs[i] + ".png", new Dimension(310, 310));
        }
    }

    /**
     * Loads and returns a buffered image.
     *
     * @param imgPath the image path
     * @param SIZE the dimensions of the image
     * @return the loaded image
     */
    private BufferedImage loadImage(String imgPath, Dimension SIZE) {
        BufferedImage image = null;
        try {        
            InputStream imageFile = this.getClass().getResourceAsStream('/'+imgPath);
            //InputStream imageFile = ClassLoader.getSystemResourceAsStream("resources/" +imgPath);
            image = new BufferedImage((int) SIZE.getWidth(), (int) SIZE.getHeight(), BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("in image data holder");
            System.out.println("Error: " + e);
        }

        return image;
    }

    public static BufferedImage getPlayerImage() {
        return BASICIMAGES[0];
    }

    public static BufferedImage getEnemyImage() {
        return BASICIMAGES[1];
    }

    public static BufferedImage getEnemyExplosion() {
        return BASICIMAGES[2];
    }

    public static BufferedImage getMissileExplosion() {
        return BASICIMAGES[3];
    }

    public static BufferedImage getGlobExplosion() {
        return BASICIMAGES[4];
    }

    public static BufferedImage getWandAttackingImage() {
        return BASICIMAGES[5];
    }

    public static BufferedImage getSpeedPowerUpImage() {
        return BASICIMAGES[6];
    }

    public static BufferedImage getChargePowerUpImage() {
        return BASICIMAGES[7];
    }

    public static BufferedImage getGlobImage() {
        return deepCopy(BASICIMAGES[8]);
    }
        
    public static BufferedImage getCustomCursorImage() {
        return BASICIMAGES[9];
    }
    
    public static BufferedImage getBulletImage(int index) {
        return BULLETIMAGES[index];
    }

    public static BufferedImage getChargeImage(int index) {
        return CHARGEIMAGES[index];
    }

    public static BufferedImage getMissileImage(int index) {
        return MISSILEIMAGES[index];
    }

    public static BufferedImage getGlobGoneImage(int index) {
        return deepCopy(GLOBGONEIMAGES[index]);
    }

    /**
     * Makes a copy of a buffered image and returns it.
     * @param given buffered image
     * @return copy of give image
     */
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
