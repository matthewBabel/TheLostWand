package GameObjects;

import Audio.AudioPlayer;
import Containers.Frame;
import DataHolders.ImageDataHolder;

/**
 * 2d game environment in which the user controls a magic wand that fights
 * enemies. The goal is to rack up the highest score.
 *
 * @author Matthew Babel
 */
public class Main {

    /**
     * Start of the program, plays the intro and creates the frame.
     *
     * @param args
     */
    public static void main(String[] args) {
        ImageDataHolder imgData = new ImageDataHolder();
        Frame screen = new Frame();
        AudioPlayer.playIntro();
    }
}
