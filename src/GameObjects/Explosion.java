package GameObjects;

import DataHolders.ImageDataHolder;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
/**
 * Holds the information for a enemy explosion.
 *
 * @author Matthew Babel
 */
public class Explosion {

    private boolean isShowing;
    private final BufferedImage[] IMAGES;

    /**
     * list of integer arrays.
     * list containing arrays with 4 integers - xcord, ycord, framesShown, imageType
     * image types 0 - basic enemy, 1 - missile, 2 - glob
     */
    private final List<int[]> exploTracker;

    /**
     * initialize and load image.
     */
    public Explosion() {
        isShowing = false;
        exploTracker = new ArrayList<>();
        IMAGES = new BufferedImage[]{ImageDataHolder.getEnemyExplosion(),
                                     ImageDataHolder.getMissileExplosion(),
                                     ImageDataHolder.getGlobExplosion()};
    }

    /**
     * get sprite image.
     *
     * @param imgType type of explosion
     * @return image
     */
    public BufferedImage getImage(int imgType) {
        return IMAGES[imgType];
    }

    /**
     * get list containing arrays with 4 numbers, first two numbers are
     * explosion x cord and y cord 3rd is frame count and 4th is image type.
     *
     * @return the list
     */
    public List<int[]> getExploTracker() {
        return exploTracker;
    }

    /**
     * true if explosions are being shown.
     *
     * @return true if is showing
     */
    public boolean isShowing() {
        return isShowing;
    }

    public int getImageType(int index) {
        return exploTracker.get(index)[3];
    }

    /**
     * add an explosion point and start frames at 0.
     *
     * @param explo the explosion data
     */
    public void addExplo(int[] explo) {
        exploTracker.add(explo);
        if (!isShowing) {
            isShowing = true;
        }
    }

    /**
     * Called every update of the game to update frame counts.
     */
    public void tickFrame() {
        final int FRAMES = 11;

        if (!exploTracker.isEmpty()) {
            exploUpdate(FRAMES);

        }
    }

    /**
     * Going through explosion list and updating.
     *
     * @param FRAMES the max frames of an explosion
     */
    private void exploUpdate(int FRAMES) {
        for (int i = 0; i < exploTracker.size(); i++) {
            int[] e = exploTracker.get(i);
            e[2]++;

            if (e[2] > FRAMES) { // removing 
                exploTracker.remove(e);
                if (exploTracker.isEmpty()) {
                    isShowing = false;
                }
            }
        }
    }
}
