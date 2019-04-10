package GameObjects;

import DataHolders.ImageDataHolder;
import java.awt.image.BufferedImage;

/**
 * A charge attack with 4 stages, each stage shoots out a more powerful shot.
 * stage 1 shot - lasts one hit and is slow and small
 * stage 2 shot - lasts 2 hits and is medium speed and size
 * stage 3 shot - lasts 3 hits and is fast and medium size
 * stage 4 shot - lasts very long, is very fast and big
 *
 * @author Matthew Babel
 */
public class ChargeAttack {

    private final BufferedImage[] IMAGES;
    private BufferedImage curImg;

    //int array holding 4 variables, frameCount, stage, STAGESWITCH, ATKCOOLDOWN
    // frameCount and stage change, the other two are constants
    // stage counts from 1 to 4
    private final int[] VARHOLDER = {0, 1, 10, 6};

    public ChargeAttack() {
        IMAGES = new BufferedImage[]{ImageDataHolder.getChargeImage(0),
                                   ImageDataHolder.getChargeImage(1),
                                   ImageDataHolder.getChargeImage(2),
                                   ImageDataHolder.getChargeImage(3)};
        curImg = IMAGES[0];
    }

    /**
     * Should only be called when charge attack is in use, updates the frame
     * count.
     */
    public void tick() {
        int frameCount = VARHOLDER[0];
        int stage = VARHOLDER[1];
        final int FCHANGE = VARHOLDER[2];

        frameCount++;
        if (frameCount > FCHANGE) {
            switch (stage) {
                case 1:
                    changeImg(1);
                    break;
                case 2:
                    changeImg(2);
                    break;
                case 3:
                    changeImg(3);
                    break;
                default:
                    break;
            }
            frameCount = 0;
        }
        
        VARHOLDER[0] = frameCount;
    }

    private void changeImg(int n) {
        curImg = IMAGES[n];
        VARHOLDER[1] = n + 1;
    }

    /**
     * Charge shot is reset back to stage 1.
     */
    public void reset() {
        curImg = IMAGES[0];
        VARHOLDER[0] = 0;
        VARHOLDER[1] = 1;
    }

    public BufferedImage getCurImg() {
        return curImg;
    }

    public int getAttackCoolDown() {
        return VARHOLDER[3];
    }

    public int getStage() {
        return VARHOLDER[1];
    }
}
