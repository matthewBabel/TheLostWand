package Audio;

import Audio.AudioHolder.SoundEffects;
import javax.sound.sampled.Clip;

/**
 * Controls the playing and stopping of the audio clips.
 *
 * @author Matthew Babel
 */
public class AudioPlayer {

    private static final Clip[] CLIPS = {AudioHolder.getBasicAttack(), AudioHolder.getSmallExplosion(),
        AudioHolder.getBigExplosion(), AudioHolder.getGrunt(), AudioHolder.getShot(),
        AudioHolder.getPowerUp(), AudioHolder.getMissileDeploy(), AudioHolder.getMissileExplosion(),
        AudioHolder.getBadClapping(), AudioHolder.getMediumClapping(), AudioHolder.getLoudClapping()};

    /**
     * Plays a sound effect that is saved in the audio player class instead of
     * loading it on use.
     *
     * @param type the sound effect
     */
    public static void playPreloadedSound(SoundEffects type) {
        switch (type) {
            case BASICATTACK:
                CLIPS[0].setFramePosition(0);
                CLIPS[0].start();
                break;
            case SMALLEXPLOSION:
                CLIPS[1].setFramePosition(0);
                CLIPS[1].start();
                break;
            case BIGEXPLOSION:
                CLIPS[2].setFramePosition(0);
                CLIPS[2].start();
                break;
            case GRUNT:
                CLIPS[3].setFramePosition(0);
                CLIPS[3].start();
                break;
            case SHOT:
                CLIPS[4].setFramePosition(0);
                CLIPS[4].start();
                break;
            case POWERUP:
                CLIPS[5].setFramePosition(0);
                CLIPS[5].start();
                break;
            case MISSILEDEPLOY:
                CLIPS[6].setFramePosition(0);
                CLIPS[6].start();
                break;
            case MISSILEEXPLOSION:
                CLIPS[7].setFramePosition(0);
                CLIPS[7].start();
                break;
            case BADCLAPPING:
                CLIPS[8].setFramePosition(0);
                CLIPS[8].start();
                break;
            case MEDIUMCLAPPING:
                CLIPS[9].setFramePosition(0);
                CLIPS[9].start();
                break;
            case LOUDCLAPPING:
                CLIPS[10].setFramePosition(0);
                CLIPS[10].start();
                break;            
            default:
                System.err.println("Sound not pre loaded : " + type.toString());
                break;
        }
    }

    public static void playIntro() {
        Clip clip = SoundEffects.INTRO.getClip();
        clip.start();
    }

    public static void stopIntro() {
        Clip clip = SoundEffects.INTRO.getClip();
        clip.stop();
    }
    
    public static void startBackgroundSong() {
        Clip clip = SoundEffects.BACKGROUNDSONG.getClip();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public static void stopBackgroundSong() {
        Clip clip = SoundEffects.BACKGROUNDSONG.getClip();
        clip.stop();
        clip.setFramePosition(0);
    }
}
