package Containers;

import Audio.AudioHolder;
import Audio.AudioPlayer;
import DataHolders.ColorDataHolder;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import DataHolders.FontDataHolder;
import DataHolders.GameDataHolder;
import DataHolders.GameDataHolder.Mode;
import Files.HighScoreFileMngr;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * A JPanel containing game over information including a text file to get the
 * players name.
 *
 * @author Matthew Babel
 */

public class GameOverPanel extends JPanel {

    /**
     * Creates a game over panel that accepts a high score name. Will go to the
     * main menu if backspace is pressed instead.
     *
     * @param frame a refrence to a frame object
     * @param s the players final score
     */
    public GameOverPanel(Frame frame, int s) {

        //choosing classic mode high scores or mouse mode high scores
        HighScoreFileMngr mngr;
        if (GameDataHolder.currentMode == Mode.CLASSIC) {
            mngr = new HighScoreFileMngr("data/classichighscores.txt");
        } else {
            mngr = new HighScoreFileMngr("data/mousehighscores.txt");
        }

        JLabel[] lbls = new JLabel[5];
        initLabels(lbls, s);

        int place = mngr.getPlace(s);    
        playSound(place);
        setMessage(lbls[3], lbls[4], place);

        JTextField textField = new JTextField();
        initTxtField(textField, s, frame);

        makeScreen(lbls, textField, frame);
    }

    private void initLabels(JLabel[] lbls, int score) {
        final Font REG = FontDataHolder.getLato60Plain();

        lbls[0] = new JLabel("GAME OVER");
        lbls[0].setFont(FontDataHolder.getPtSans200());
        lbls[0].setHorizontalAlignment(SwingConstants.CENTER);

        lbls[1] = new JLabel("Press BACKSPACE for Main Menu");
        lbls[1].setFont(REG);
        lbls[1].setHorizontalAlignment(SwingConstants.RIGHT);

        lbls[2] = new JLabel("Final Score : " + score);
        lbls[2].setFont(REG);

        // message labels 1 and 2
        lbls[3] = new JLabel();
        lbls[3].setFont(REG);
        lbls[3].setSize(800, 40);

        lbls[4] = new JLabel();
        lbls[4].setFont(REG);
        lbls[4].setSize(800, 40);
    }

    private void initTxtField(JTextField tf, int score, Frame fr) {
        final int MAXNAME = 20;

        tf.setColumns(10);
        tf.setFont(FontDataHolder.getLato60Plain());
        tf.setPreferredSize(new Dimension(300, 70));
        tf.addActionListener((java.awt.event.ActionEvent e) -> {
            String str = tf.getText();
            if (str.length() > MAXNAME) {
                str = str.substring(0, MAXNAME);
            }
            fr.showHighScore(score, str);
        });
    }
    
    /**
     * Play one of three sounds reacting to the high score placement.
     */
    private void playSound(int place) {
        if (place <= 10) {
            AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.LOUDCLAPPING);
        } else if (place <= 50) {
            AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.MEDIUMCLAPPING);
        } else {
            AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.BADCLAPPING);
        }
    }
    
    /**
     * Set the message according to where the player placed in the high scores.
     */
    private void setMessage(JLabel msgLbl, JLabel msgLbl2, int place) {
        String msg1;
        String msg2;
        if (place == 1) {
            msg1 = "Woah! That's a new Record!! 1st Place!";
            msg2 = "Who is the Rightful Champion?";
        } else if (place > 1 && place <= 4) {
            msg1 = "Well now, it seems we have ourselves a natural.";
            msg2 = "What is the naturals name?";
        } else if (place > 4 && place <= 10) {
            msg1 = "I'd be lying if I said you were not not not good.";
            msg2 = "So there's that. For real though what's your name?";
        } else if (place > 10 && place <= 15) {
            msg1 = "Bad news is your not in the top 10, good news is your close.";
            msg2 = "Keep on hustling, Lemme get your name?";
        } else if (place > 15 && place <= 25) {
            msg1 = "You gotta get a little better to play with the top dogs";
            msg2 = "Just keeping it real, And your name?";
        } else if (place > 25 && place <= 35) {
            msg1 = "You need a little more practice, not even in the top 25...";
            msg2 = "Maybe try to eat a healthier breakfast, Whats your name?";
        } else if (place > 35 && place <= 50) {
            msg1 = "What is up with that score though.";
            msg2 = "Maybe try harder next time? wait.. What's your name?";
        } else if (place > 50 && place <= 60) {
            msg1 = "50 players have placed higher than you";
            msg2 = "Just think about that, Your name please?";
        } else if (place > 60 && place <= 75) {
            msg1 = "You're almost so bad that you're good.. almost.";
            msg2 = "Lets just get this over with and enter your name";
        } else if (place > 75 && place <= 100) {
            msg1 = "Alright at this point you don't even deserve a message";
            msg2 = "Name -";
        } else {
            msg1 = "Bottom of the barrel, end of the road, just disgraceful";
            msg2 = "Please, from Matt the Creator himself, get good. Your Name?";
        }
        msgLbl.setText(msg1);
        msgLbl2.setText(msg2);
    }

    /**
     * Make the screen using a grid bag layout.
     */
    private void makeScreen(JLabel[] lbls, JTextField tf, Frame fr) {
        this.setLayout(new GridBagLayout());

        GridBagConstraints a = new GridBagConstraints();
        GridBagConstraints b = new GridBagConstraints();
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints d = new GridBagConstraints();
        GridBagConstraints e = new GridBagConstraints();
        GridBagConstraints f = new GridBagConstraints();

        a.gridx = 0;
        a.gridy = 0;
        a.gridwidth = 2;
        a.weightx = 1;
        a.weighty = .3;

        b.gridx = 0;
        b.gridwidth = 2;
        b.gridy = 4;
        b.weightx = .5;
        b.weighty = .2;

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = .5;
        c.weighty = .1;

        d.gridx = 0;
        d.gridy = 1;
        d.weightx = .5;
        d.weighty = .2;

        e.gridx = 0;
        e.gridwidth = 2;
        e.gridy = 2;
        e.weightx = .5;
        e.weighty = .2;

        f.gridx = 0;
        f.gridwidth = 2;
        f.gridy = 3;
        f.weightx = .5;
        f.weighty = .2;

        this.setBackground(ColorDataHolder.getBackgroundColor());
        this.add(lbls[0], a);
        this.add(tf, b);
        this.add(lbls[1], c);
        this.add(lbls[2], d);
        this.add(lbls[3], e);
        this.add(lbls[4], f);
        addKeyBinding(fr);
    }

    /**
     * Add a key binding for backspace that returns to the main menu.
     */
    private void addKeyBinding(Frame fr) {
        int mode = JComponent.WHEN_IN_FOCUSED_WINDOW;

        this.getInputMap(mode).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), "BACKSPACE");
        this.getActionMap().put("BACKSPACE", new BackSpaceAction(fr));

    }

    /**
     * Show main menu if backspace is pressed.
     */
    private class BackSpaceAction extends AbstractAction {
        final Frame FR;
        
        public BackSpaceAction(Frame frame) {
            FR = frame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            FR.showMainMenu();
        }
    }
}
