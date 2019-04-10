package GameObjects.Collision;

import java.awt.image.BufferedImage;

/**
 * An object that is able to be spawned.
 * 
 * @author Matthew Babel
 */
public interface Spawn {
  
    /**
     * Decrement position by 1.
     */
    void decrementPosition();
    
    /**
     * Move given a player coordinates.
     * 
     * @param px player x
     * @param py player y
     */
    void move(float px, float py);
    
    boolean isDead();
    boolean isKilled();
    float getTargetXCord();
    float getTargetYCord();
    float getCurrentXCord();
    float getCurrentYCord();
    BufferedImage getImage();
    int getImageWidth();
    int getImageHeight();
}
