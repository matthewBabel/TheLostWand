package Containers;

import Audio.AudioPlayer;
import DataHolders.GameDataHolder;
import DataHolders.GameDataHolder.Mode;
import DataHolders.ImageDataHolder;
import GameObjects.LevelDesign;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Frame used to hold and switch the panels of the program.
 *
 * @author Matthew Babel
 */
public class Frame {

    private final JFrame FRAME;
    public Dimension FRAMESIZE;

    /**
     * Initialize frame and any panels needed.
     */
    public Frame() {
        FRAME = new JFrame("The Lost Wand");

        setCustomCursor();
        setFrameSize();
        
        MainMenuPanel mainMenu = new MainMenuPanel(this);
        OptionsPanel opPnl = new OptionsPanel(this); // this inits the game mode to mouse
        initFrame(mainMenu);
    }

    private void setCustomCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = ImageDataHolder.getCustomCursorImage();

        Cursor c = toolkit.createCustomCursor(image, new Point(FRAME.getX() + 10,
                FRAME.getY() + 10), "img");
        FRAME.setCursor(c);
    }

    private void setBlankCursor() {
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the JFrame.
        FRAME.setCursor(blankCursor);
    }

    private void initFrame(MainMenuPanel mainMenu) {
        // setting up main frame
        FRAME.setTitle("The Lost Wand");
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(true);
        FRAME.setUndecorated(true);

        FRAME.getContentPane().add(mainMenu);
        FRAME.setVisible(true);
    }

    private void setFrameSize() throws HeadlessException {
        // making frame full screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int width = ((int) tk.getScreenSize().getWidth());
        int height = ((int) tk.getScreenSize().getHeight());
        FRAME.setSize(width, height);
        FRAME.setLocationRelativeTo(null);  // centers frame

        // public variable for other classes to refrence
        FRAMESIZE = new Dimension(width, height);
    }

    /**
     * Stops the game and shows the game over screen.
     *
     * @param score players final score
     */
    public void gameOver(int score) {
        if (GameDataHolder.currentMode == Mode.CLASSIC) {
            setCustomCursor();
        }

        AudioPlayer.stopBackgroundSong();
        LevelDesign.stop();
        GameDataHolder.level = 1;
        GameOverPanel gameOverPnl = new GameOverPanel(this, score);
        switchPanel(gameOverPnl);
    }

    /**
     * Starts the game screen.
     */
    public void startGame() {
        if (GameDataHolder.currentMode == Mode.CLASSIC) {
            setBlankCursor();
        }

        AudioPlayer.stopIntro();
        AudioPlayer.startBackgroundSong();
        GamePanel gamePnl = new GamePanel(this);
        switchPanel(gamePnl);
    }

    /**
     * Shows the main menu screen.
     */
    public void showMainMenu() {
        MainMenuPanel mainMenu = new MainMenuPanel(this);
        switchPanel(mainMenu);
    }

    /**
     * Shows the options screen.
     */
    public void options() {
        OptionsPanel optionPnl = new OptionsPanel(this);
        switchPanel(optionPnl);
    }

    /**
     * show the high score panel.
     */
    public void showHighScore() {
        HighScorePanel highscorePnl = new HighScorePanel(this);
        switchPanel(highscorePnl);
    }

    /**
     * add a score to the high score information and then show the high score
     * panel.
     *
     * @param s the new score
     * @param n name of player for high score
     */
    public void showHighScore(int s, String n) {
        HighScorePanel highscorePnl = new HighScorePanel(this);
        highscorePnl.addScore(s, n);
        switchPanel(highscorePnl);
    }

    /**
     * refresh the frame and switch to a given panel.
     *
     * @param pnl new panel
     */
    private void switchPanel(JPanel pnl) {
        FRAME.getContentPane().removeAll();
        FRAME.getContentPane().invalidate();
        FRAME.getContentPane().add(pnl);
        FRAME.getContentPane().revalidate();
    }
}
