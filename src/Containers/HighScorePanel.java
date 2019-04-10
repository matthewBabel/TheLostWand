package Containers;

import DataHolders.ColorDataHolder;
import DataHolders.FontDataHolder;
import DataHolders.GameDataHolder;
import DataHolders.GameDataHolder.Mode;
import Files.HighScore;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import Files.HighScoreFileMngr;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Shows a list of high scores using a scroll view.
 *
 * @author Matthew Babel
 */
public class HighScorePanel extends JPanel {

    private HighScoreFileMngr usedMngr;
    private final JScrollPane SP;
    private final JComponent[] COMP;

    /**
     * Initialize variables and containers.
     *
     * @param frame refrence to frame object
     */
    public HighScorePanel(Frame frame) {
        SP = new JScrollPane();

        COMP = new JComponent[6];
        initButtons(COMP, frame);
        initMode(COMP);

        COMP[3] = new JLabel("HIGH SCORES");
        initLabel(COMP);

        COMP[4] = new JPanel();
        initInnerPanel(COMP);
        COMP[5] = new JPanel();

        makeScoreLabels(usedMngr, COMP[4]);
        makeScreen();
    }

    private void initInnerPanel(JComponent[] comp) {
        comp[4] = new JPanel();
        comp[4].setBackground(ColorDataHolder.getDifferentBlue());
        comp[4].setLayout(new BoxLayout(comp[4], BoxLayout.Y_AXIS));
        comp[4].setPreferredSize(new Dimension(1200, 1500));
    }

    private void initLabel(JComponent[] comp) {
        comp[3] = new JLabel("HIGH SCORES");
        comp[3].setFont(FontDataHolder.getPtSans130());
    }

    private void initButtons(JComponent[] btns, Frame frame) {
        final Font REG = FontDataHolder.getPtSansReg60();

        btns[0] = new JButton("BACK");
        btns[0].setFont(REG);
        btns[0].setMaximumSize(new Dimension(150, 200));
        ((JButton) btns[0]).addActionListener((java.awt.event.ActionEvent e) -> {
            frame.showMainMenu();
        });

        btns[1] = new JButton("Classic");
        btns[1].setFont(REG);
        btns[1].setMaximumSize(new Dimension(150, 200));

        btns[2] = new JButton("Mouse");
        btns[2].setFont(REG);
        btns[2].setMaximumSize(new Dimension(150, 200));

        ((JButton) btns[1]).addActionListener((java.awt.event.ActionEvent e) -> {
            btns[1].setEnabled(false);
            btns[2].setEnabled(true);
            usedMngr = new HighScoreFileMngr("data/classichighscores.txt");
            makeScoreLabels(usedMngr, COMP[4]);
            makeScreen();
        });

        ((JButton) btns[2]).addActionListener((java.awt.event.ActionEvent e) -> {
            btns[1].setEnabled(true);
            btns[2].setEnabled(false);
            usedMngr = new HighScoreFileMngr("data/mousehighscores.txt");
            makeScoreLabels(usedMngr, COMP[4]);
            makeScreen();
        });
    }

    /**
     * choose which high score file to begin showing with.
     * @param btns 
     */
    private void initMode(JComponent[] btns) {
        if (GameDataHolder.currentMode == Mode.CLASSIC) {
            btns[1].setEnabled(false);
            usedMngr = new HighScoreFileMngr("data/classichighscores.txt");
        } else {
            btns[2].setEnabled(false);
            usedMngr = new HighScoreFileMngr("data/mousehighscores.txt");
        }
    }

    /**
     * Read from a high score file manager and add the data to labels, then add
     * labels to inner panel.
     *
     * @param hsMngr file manager being used
     * @param innerPnl the panel labels are added to
     */
    private void makeScoreLabels(HighScoreFileMngr hsMngr, JComponent innerPnl) {
        List<JLabel> hsLbls = new ArrayList<>();
        for (int i = 0; i < hsMngr.getSize(); i++) {
            JLabel lbl = new JLabel(hsMngr.getLine(i).replaceFirst("_", " Points : "));
            lbl.setText(getTense(i + 1).concat(lbl.getText()));
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            lbl.setFont(FontDataHolder.getDejaVu48Plain());
            hsLbls.add(lbl);
        }

        innerPnl.removeAll();
        innerPnl.setPreferredSize(new Dimension(1200, hsMngr.getSize()
                * (FontDataHolder.getDejaVu50Plain().getSize() + 7)));
        hsLbls.stream().forEach((lbl) -> {
            innerPnl.add(lbl);
        });

        setUpScrollPane(COMP);
    }

    /**
     * Make the screen using a grid bag layout.
     */
    private void makeScreen() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints a = new GridBagConstraints();
        GridBagConstraints b = new GridBagConstraints();
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints d = new GridBagConstraints();
        GridBagConstraints e = new GridBagConstraints();

        a.gridx = 0;
        a.gridy = 0;
        a.gridwidth = 2;
        a.weightx = .1;
        a.weighty = .1;
        a.anchor = GridBagConstraints.NORTH;

        b.gridx = 1;
        b.gridy = 3;
        b.weightx = .1;
        b.weighty = .1;
        b.anchor = GridBagConstraints.SOUTHEAST;

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = .4;
        c.weighty = .4;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 400, 0, 0);

        d.gridx = 0;
        d.gridy = 0;
        d.weightx = .2;
        d.weighty = .2;
        d.anchor = GridBagConstraints.SOUTHWEST;

        e.gridx = 0;
        e.gridy = 1;
        e.weightx = .2;
        e.weighty = .2;
        e.anchor = GridBagConstraints.NORTHWEST;

        this.add(COMP[3], a);
        this.add(COMP[0], b);
        this.add(COMP[5], c);
        this.add(COMP[2], d);
        this.add(COMP[1], e);

        this.revalidate();
        this.setBackground(ColorDataHolder.getBackgroundColor());
        this.setVisible(true);
    }

    /**
     * Set up a scroll pane for the inner panel then create viewport panel 
     * and add scroll pane to it.
     * comp[4] - inner panel,
     * comp[5] - viewport panel
     * 
     * @param comp array of all components
     */
    private void setUpScrollPane(JComponent[] comp) {
        SP.getViewport().add(comp[4]);
        SP.getViewport().setBackground(ColorDataHolder.getDifferentBlue());
        SP.setPreferredSize(new Dimension(1200, 1500));

        comp[5] = new JPanel();
        comp[5].setPreferredSize(new Dimension(1200, 1500));
        comp[5].setLayout(new BorderLayout());
        comp[5].add(SP, BorderLayout.CENTER);
    }

    /**
     * Add a new high score.
     *
     * @param s the high score
     * @param n the name for the high score
     */
    public void addScore(int s, String n) {
        HighScore hs = new HighScore(s, n);
        if (GameDataHolder.currentMode == Mode.CLASSIC) {
            COMP[1].setEnabled(false);
            COMP[2].setEnabled(true);
            updateData(true, hs);
        } else {
            COMP[1].setEnabled(true);
            COMP[2].setEnabled(false);
            updateData(false, hs);
        }
        makeScoreLabels(usedMngr, COMP[4]);
        makeScreen();
    }

    /**
     * update the high score data file.
     *
     * @param mode true if using classic mode
     */
    private void updateData(boolean mode, HighScore hs) {
        HighScoreFileMngr data;
        
        if (mode) {
            data = new HighScoreFileMngr("data/classichighscores.txt");
        } else {
            data = new HighScoreFileMngr("data/mousehighscores.txt");
        }
        data.addLine(hs.getLine());
        data.save();
        usedMngr = data;
    }

    /**
     * Returns the tense for an numbers place (1st, 2nd, 3rd, 4th).
     *
     * @param n the number
     * @return number with tense
     */
    private String getTense(int n) {
        String nStr = String.valueOf(n);
        int lastDigit = Integer.valueOf(nStr.substring(nStr.length() - 1, nStr.length()));
        int lastTwoDigits = 0;
        if (n > 9) {
            lastTwoDigits = Integer.valueOf(nStr.substring(nStr.length() - 2, nStr.length()));
        }
        if (lastDigit == 1 && lastTwoDigits != 11) {
            return nStr + "st Place - ";
        } else if (lastDigit == 2 && lastTwoDigits != 12) {
            return nStr + "nd Place - ";
        } else if (lastDigit == 3 && lastTwoDigits != 13) {
            return nStr + "rd Place - ";
        } else {
            return nStr + "th Place - ";
        }
    }
}
