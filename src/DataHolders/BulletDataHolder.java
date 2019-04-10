package DataHolders;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Holds the data for the 4 types of bullets.
 * 
 * @author Matthew Babel
 */
public class BulletDataHolder {
    
    private final BufferedImage[] IMAGES;
    private final Dimension[] DIMENSIONS;
    
    public BulletDataHolder() {
        IMAGES = new BufferedImage[4];
        DIMENSIONS = new Dimension[4];
        initSizes();
    }
     
    private void initSizes() {
        int[] width = {10, 20, 30, 40};
        int[] height = {10, 20, 30, 40};
        
        for (int i = 0; i < DIMENSIONS.length; i++) {
            DIMENSIONS[i] = new Dimension(width[i], height[i]);
            IMAGES[i] = ImageDataHolder.getBulletImage(i);
        }
    }
    
    public BufferedImage getImage(int index) {
        return IMAGES[index];
    }
    
    public int getWidth(int index) {
        return DIMENSIONS[index].width;
    }
    
    public int getHeight(int index) {
        return DIMENSIONS[index].height;
    }
}
