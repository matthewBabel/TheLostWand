package GameObjects.Collision;

import DataHolders.ImageDataHolder;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * When triggered, create a power up item randomly on the visible area. 2 Power
 * ups, Speed and Charge Shot.
 * Speed - red
 * Charge - blue
 * 
 * @author Matthew Babel
 */
public class PowerUp extends CollidableObject {
    
    // holds speed image, charge image, and current image
    private final BufferedImage[] IMAGES = new BufferedImage[3];
    
    // holds pixel dimensions, frame dimensions, and xcord and ycord as a 
    // dimension
    private final Dimension[] SIZES = new Dimension[3]; 
    
    private boolean up;
    
    //3 - speed, 4 - charge
    private int type;

    public PowerUp(int fw, int fh) {
        SIZES[0] = new Dimension(50,50);
        SIZES[1] = new Dimension(fw, fh);

        up = false;
        
        IMAGES[0] = ImageDataHolder.getSpeedPowerUpImage();
        IMAGES[1] = ImageDataHolder.getChargePowerUpImage();
    }

    /**
     * Create a power up item randomly on the visible screen.
     */
    public void create() {
        final int BUFFER = 300;
        final int RANDOMNESS = 3;  
        int xCord = (int) ((SIZES[1].width-BUFFER) * Math.random());
        int yCord = (int) ((SIZES[1].height-BUFFER) * Math.random());

        if (xCord < BUFFER) {
            xCord = BUFFER;
        }

        if (yCord < BUFFER) {
            yCord = BUFFER;
        }

        SIZES[2] = new Dimension(xCord, yCord);
        
        if ((Math.random() * 10) > RANDOMNESS) {
            IMAGES[2] = IMAGES[1];
            type = 4;
        } else {
            IMAGES[2] = IMAGES[0];
            type = 3;
        }
        up = true;
    }

    public BufferedImage getImage() {
        return IMAGES[2];
    }

    public int getXCord() {
        return SIZES[2].width;
    }
    
    public int getYCord() {
        return SIZES[2].height;
    }

    /**
     * if a power up is up get what type is it.
     * 3 - speed
     * 4 - charge
     *
     * @return the type of power up
     */
    public int getType() {
        return type;
    }

    /**
     * true if power up is showing.
     *
     * @return up true if power up is up
     */
    public boolean isUp() {
        return up;
    }

    /**
     * Turn off the power up.
     */
    public void turnOff() {
        up = false;
    }

    @Override
    public Polygon getCollisionMask() {
        int PIXELWIDTH = SIZES[0].width;
        int PIXELHEIGHT = SIZES[0].height;
        int xCord = SIZES[2].width;
        int yCord = SIZES[2].height;                
        
        int[] xcords = {xCord, xCord + PIXELWIDTH, xCord + PIXELWIDTH, xCord};
        int[] ycords = {yCord, yCord, yCord + PIXELHEIGHT, yCord + PIXELHEIGHT};
        return new Polygon(xcords, ycords, 4);
    }

    @Override
    void collision(int type) {
        up = false;
    }
}
