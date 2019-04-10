package Audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Initializes and returns the audio clips.
 *
 * @author Matthew Babel
 */
public class AudioHolder {

    /**
     * Inits and returns the sound effect clips and music.
     */
    public enum SoundEffects {
        INTRO("introMusic.wav"),
        BACKGROUNDSONG("backgroundSong.wav"),
        BADCLAPPING("badClapping.wav"),
        MEDIUMCLAPPING("mediumClapping.wav"),
        LOUDCLAPPING("loudClapping.wav"),          
        BASICATTACK("basicAttack.wav"),
        SHOT("shot.wav"),
        SMALLEXPLOSION("smallExplosion.wav"),
        BIGEXPLOSION("bigExplosion.wav"),
        MISSILEEXPLOSION("missileExplosion.wav"),
        MISSILEDEPLOY("missileDeploy.wav"),
        GRUNT("grunt.wav"),
        POWERUP("powerUp.wav");

        private Clip clip;

        SoundEffects(String soundFile) {
            try {
                URL file = this.getClass().getResource("/audiofiles/" + soundFile);
                //URL file = ClassLoader.getSystemResource("audiofiles/" + soundFile);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);

                clip = AudioSystem.getClip();
                clip.open(audioIn);
            } catch (UnsupportedAudioFileException ex) {
                System.err.println("Error creating audio: " + soundFile);
            } catch (IOException ex) {
                System.err.println("io exception: " + soundFile);
            } catch (LineUnavailableException ex) {
                System.err.println("Error creating clip: " + soundFile);
            }
        }

        public Clip getClip() {
            return clip;
        }

        public static void initSounds() {
            values();
        }
    }

    public static Clip getBasicAttack() {
        return SoundEffects.BASICATTACK.getClip();
    }

    public static Clip getShot() {
        return SoundEffects.SHOT.getClip();
    }

    public static Clip getSmallExplosion() {
        return SoundEffects.SMALLEXPLOSION.getClip();
    }

    public static Clip getBigExplosion() {
        return SoundEffects.BIGEXPLOSION.getClip();
    }

    public static Clip getGrunt() {
        return SoundEffects.GRUNT.getClip();
    }

    public static Clip getPowerUp() {
        return SoundEffects.POWERUP.getClip();
    }

    public static Clip getMissileDeploy() {
        return SoundEffects.MISSILEDEPLOY.getClip();
    }

    public static Clip getMissileExplosion() {
        return SoundEffects.MISSILEEXPLOSION.getClip();
    }

    public static Clip getBooing() {
        return SoundEffects.LOUDCLAPPING.getClip();
    }

    public static Clip getBadClapping() {
        return SoundEffects.BADCLAPPING.getClip();
    }

    public static Clip getMediumClapping() {
        return SoundEffects.MEDIUMCLAPPING.getClip();
    }

    public static Clip getLoudClapping() {
        return SoundEffects.LOUDCLAPPING.getClip();
    }
}
