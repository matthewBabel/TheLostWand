package Containers;

import DataHolders.ColorDataHolder;
import DataHolders.FontDataHolder;
import DataHolders.GameDataHolder;
import DataHolders.GameDataHolder.Mode;
import Input.UserInput;
import Input.UserInput.UsedKeys;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Options Menu, allows reconfiguring of controls, as well as switching between
 * modes.
 *
 * @author Matthew Babel
 */
public class OptionsPanel extends JPanel {

    /**
     * Keys bindings being used in game.
     */
    private enum KeyBindings {
        UP, DOWN, LEFT, RIGHT, ROTATELEFT, ROTATERIGHT, ATTACK, PAUSE;

        @Override
        public String toString() {
            String origName = super.toString();

            if (origName.contains("ROTATE")) {
                return origName.substring(0, 1) + origName.substring(1, 6).toLowerCase()
                        + " " + origName.substring(6, 7)
                        + origName.substring(7).toLowerCase();
            } else {
                return origName.substring(0, 1) + origName.substring(1).toLowerCase();
            }
        }
    }

    /**
     * Actual key values.
     */
    private final int[] RAWKEYS = {UserInput.actualKeys[UsedKeys.MOVEUPKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.ROTATELEFTKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.ROTATERIGHTKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.ATTACKKEY.getKeyIndex()],
        UserInput.actualKeys[UsedKeys.PAUSEKEY.getKeyIndex()]};

    // holds all the arrays in one object array
    private final Object[][] ARRAYHOLDER;

    /**
     * Constructor initializes variables and components then calls make screen.
     *
     * @param frame refrence to frame object
     */
    public OptionsPanel(Frame frame) {
        final UserInput INPUT = new UserInput(this);
        MyKeyListener KL = new MyKeyListener();

        // to detect mouse click 
        this.addMouseListener(INPUT);

        JComponent[] singleComps = new JComponent[6];
        initSingleLabels(singleComps);
        initSingleButtons(singleComps, frame);
        initClassicModeListener(singleComps);
        initMouseModeListener(singleComps, KL);

        ARRAYHOLDER = new Object[5][];
        initArrays(singleComps, INPUT, KL, ARRAYHOLDER);

        updateLabels();
        makeScreen(singleComps);
        setGameMode(GameDataHolder.currentMode, singleComps);
    }

    private void initSingleLabels(JComponent[] c) {
        c[0] = new JLabel("    OPTIONS MENU +-");
        c[0].setFont(FontDataHolder.getPtSans130());
        ((JLabel) c[0]).setHorizontalAlignment(SwingConstants.CENTER);

        c[1] = new JLabel("Press a Key now");
        c[1].setFont(FontDataHolder.getPtSansReg37());
        c[1].setVisible(false);
        c[1].setPreferredSize(new Dimension(290, 50));

        c[2] = new JLabel("Current Mode: Classic");
        c[2].setFont(FontDataHolder.getPtSansReg37());
        c[2].setPreferredSize(new Dimension(380, 50));
    }

    private void initSingleButtons(JComponent[] c, Frame fr) {
        final Font REG = FontDataHolder.getPtSansReg60();

        c[3] = new JButton("BACK");
        c[3].setFont(REG);
        c[3].setMaximumSize(new Dimension(150, 200));
        ((JButton) c[3]).addActionListener((java.awt.event.ActionEvent e) -> {
            fr.showMainMenu();
        });

        c[4] = new JButton("Classic");
        c[4].setFont(REG);
        c[4].setMaximumSize(new Dimension(150, 80));
        c[4].setEnabled(false);

        c[5] = new JButton("Mouse");
        c[5].setFont(REG);
        c[5].setMaximumSize(new Dimension(new Dimension(150, 80)));

    }

    /**
     * Switch to classic mode configuration when pressed.
     * @param c the components
     */
    private void initClassicModeListener(JComponent[] c) {
        ((JButton) c[4]).addActionListener((java.awt.event.ActionEvent e) -> {
            c[4].setEnabled(false);
            c[5].setEnabled(true);

            switchClassicKeys();
            UserInput.boolHolder[1] = false;
            UserInput.boolHolder[0] = false;

            ((JLabel) c[2]).setText("Current Mode: Classic");
            updateLabels();
            GameDataHolder.setMode(Mode.CLASSIC);
        });
    }

    /**
     * Change the key bindings to classic mode configuration.
     */
    private void switchClassicKeys() {
        UserInput.actualKeys[UsedKeys.MOVEUPKEY.getKeyIndex()] = KeyEvent.VK_UP;
        UserInput.actualKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()] = KeyEvent.VK_DOWN;
        UserInput.actualKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()] = KeyEvent.VK_LEFT;
        UserInput.actualKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()] = KeyEvent.VK_RIGHT;
        UserInput.actualKeys[UsedKeys.ROTATELEFTKEY.getKeyIndex()] = KeyEvent.VK_Q;
        UserInput.actualKeys[UsedKeys.ROTATERIGHTKEY.getKeyIndex()] = KeyEvent.VK_E;
        UserInput.actualKeys[UsedKeys.ATTACKKEY.getKeyIndex()] = KeyEvent.VK_SPACE;

        RAWKEYS[0] = UserInput.actualKeys[UsedKeys.MOVEUPKEY.getKeyIndex()];
        RAWKEYS[1] = UserInput.actualKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()];
        RAWKEYS[2] = UserInput.actualKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()];
        RAWKEYS[3] = UserInput.actualKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()];
        RAWKEYS[4] = UserInput.actualKeys[UsedKeys.ROTATELEFTKEY.getKeyIndex()];
        RAWKEYS[5] = UserInput.actualKeys[UsedKeys.ROTATERIGHTKEY.getKeyIndex()];
        RAWKEYS[6] = UserInput.actualKeys[UsedKeys.ATTACKKEY.getKeyIndex()];
    }

    /**
     * Switch to mouse mode configuration including allowing the a mouse button
     * to be bound.
     *
     * @param c components
     * @param KL key listener
     */
    private void initMouseModeListener(JComponent[] c, MyKeyListener KL) {
        ((JButton) c[5]).addActionListener((java.awt.event.ActionEvent e) -> {
            c[4].setEnabled(true);
            c[5].setEnabled(false);

            switchMouseKeys(KL);
            UserInput.boolHolder[1] = true;

            ((JLabel) c[2]).setText("Current Mode: Mouse");
            updateLabels();
            GameDataHolder.setMode(Mode.MOUSE);
        });
    }

    /**
     * Switch key bindings to the mouse mode configuration.
     *
     * @param KL key listener
     */
    private void switchMouseKeys(MyKeyListener KL) {
        changeMovementKeys();

        RAWKEYS[0] = UserInput.actualKeys[UsedKeys.MOVEUPKEY.getKeyIndex()];
        RAWKEYS[1] = UserInput.actualKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()];
        RAWKEYS[2] = UserInput.actualKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()];
        RAWKEYS[3] = UserInput.actualKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()];
        RAWKEYS[4] = -1;
        RAWKEYS[5] = -1;

        UserInput.actualKeys[UsedKeys.ROTATELEFTKEY.getKeyIndex()] = 0;
        UserInput.actualKeys[UsedKeys.ROTATERIGHTKEY.getKeyIndex()] = 0;

        // attack is 6 in Key Bindings enum
        changeToMousePrimary(6, KL);
    }

    /**
     * Changing movement keys to be mouse movement.
     */
    private void changeMovementKeys() {
        int[] myKeys = UserInput.actualKeys;
        myKeys[UsedKeys.MOVEUPKEY.getKeyIndex()] = KeyEvent.VK_W;
        myKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()] = KeyEvent.VK_S;
        myKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()] = KeyEvent.VK_A;
        myKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()] = KeyEvent.VK_D;

        UserInput.actualKeys = myKeys;
    }

    /**
     * Change a given key to be the primary mouse button.
     *
     * @param inputIndex key binding index
     * @param KL key listener
     */
    private void changeToMousePrimary(int inputIndex, MyKeyListener KL) {
        KL.setKey(KeyEvent.VK_0);
        changeKey(inputIndex, KL);
        ((JLabel) ARRAYHOLDER[0][inputIndex]).setText("Current "
                + KeyBindings.values()[inputIndex].toString() + " Key: Mouse Primary");
        removeListener(inputIndex, KL);
        UserInput.boolHolder[0] = true;
    }

    /**
     * Initialize all the array objects.
     *
     * @param c the components being used
     * @param input use input class
     * @param KL key listener
     * @param arr double dimensioned array
     */
    private void initArrays(JComponent[] c, UserInput input, MyKeyListener KL,
            Object[][] arr) {
        final int NUMOFINPUTS = KeyBindings.values().length;
        arr[0] = new JLabel[NUMOFINPUTS];
        arr[1] = new MyButton[NUMOFINPUTS];
        arr[2] = new MyTimerAction[NUMOFINPUTS];
        arr[3] = new MyButtonAction[NUMOFINPUTS];
        arr[4] = new Timer[NUMOFINPUTS];

        // intializing arrays
        for (int i = 0; i < NUMOFINPUTS; i++) {
            KeyBindings[] inputArray = KeyBindings.values();
            arr[0][i] = new JLabel("Current " + inputArray[i].toString() + " Key: "
                    + KeyEvent.getKeyText(RAWKEYS[i]));
            ((JLabel) arr[0][i]).setFont(FontDataHolder.getPtSansReg37());
            ((JLabel) arr[0][i]).setPreferredSize(new Dimension(700, 40));
            arr[2][i] = new MyTimerAction(i, c, input, KL);
            arr[3][i] = new MyButtonAction(i, c, KL);
            arr[4][i] = new Timer(10, (ActionListener) arr[2][i]);
            arr[1][i] = new MyButton(i);
            ((JButton) arr[1][i]).setText("Replace " + inputArray[i].toString() + " Key");
            ((JButton) arr[1][i]).setFont(FontDataHolder.getPtSansReg37());
            ((JButton) arr[1][i]).setMaximumSize(new Dimension(80, 60));
            ((JButton) arr[1][i]).addActionListener((ActionListener) arr[3][i]);
        }
    }

    /**
     * Update the Key Labels, check if mouse primary is being used, represented
     * by 48, -1 is preset value set to rotation keys when switched to mouse
     * mode.
     */
    private void updateLabels() {
        final JLabel[] KEYLBLS = (JLabel[]) ARRAYHOLDER[0];
        final KeyBindings[] inputArray = KeyBindings.values();

        for (int i = 0; i < KeyBindings.values().length; i++) {
            switch (RAWKEYS[i]) {
                case 48:
                    KEYLBLS[i].setText("Current " + inputArray[i] + " Key: Mouse Primary");
                    break;
                case -1:
                    KEYLBLS[i].setText("Current " + inputArray[i] + " Key: Mouse Cursor");
                    break;
                default:
                    KEYLBLS[i].setText("Current " + inputArray[i] + " Key: " + KeyEvent.getKeyText(RAWKEYS[i]));
                    break;
            }

            this.repaint();
        }
    }

    /**
     * Remove key listener and reset objects being used for it.
     *
     * @param index used to find right timer
     */
    private void removeListener(int index, MyKeyListener KL) {
        this.repaint();
        KL.pressedOff();
        ((Timer) ARRAYHOLDER[4][index]).stop();
        this.removeKeyListener(KL);
    }

    /**
     * Do the layout of the panel using a GridBagLayout to place components.
     */
    private void makeScreen(JComponent[] comp) {
        this.setLayout(new GridBagLayout());

        GridBagConstraints a = new GridBagConstraints();
        GridBagConstraints b = new GridBagConstraints();
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints d = new GridBagConstraints();
        GridBagConstraints e = new GridBagConstraints();
        GridBagConstraints f = new GridBagConstraints();

        a.gridx = 0;
        a.gridy = 0;
        a.gridwidth = 3;
        a.weightx = .9;
        a.weighty = .3;
        a.anchor = GridBagConstraints.NORTH;

        b.gridx = 0;
        b.weightx = .4;
        b.weighty = .25;
        b.anchor = GridBagConstraints.WEST;
        b.insets = new Insets(0, 10, 0, 0);
        int yCount = 0;
        for (int i = 0; i < KeyBindings.values().length; i++) {
            yCount++;
            b.gridy = yCount;
            this.add((JComponent) ARRAYHOLDER[0][i], b);

            b.gridx = 1;
            this.add((JComponent) ARRAYHOLDER[1][i], b);
            b.gridx = 0;
        }

        b.gridx = 2;
        b.gridy = 4;
        b.gridwidth = 2;
        b.weightx = .2;
        b.weighty = .3;
        b.insets = new Insets(0, 0, 0, 0);
        b.anchor = GridBagConstraints.CENTER;

        c.gridx = 2;
        c.gridy = 2;
        c.weightx = .2;
        c.weighty = .4;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 0);

        d.gridx = 3;
        d.gridy = 2;
        d.weightx = .2;
        d.weighty = .4;
        d.anchor = GridBagConstraints.WEST;
        d.insets = new Insets(0, 0, 0, 0);

        e.gridx = 2;
        e.gridy = 1;
        e.gridwidth = 2;
        e.weightx = .2;
        e.weighty = .2;

        f.gridx = 3;
        f.gridy = yCount;
        f.weightx = .1;
        f.weighty = .1;
        f.anchor = GridBagConstraints.SOUTHEAST;

        this.add(comp[0], a);
        this.add(comp[1], b);
        this.add(comp[5], c);
        this.add(comp[4], d);
        this.add(comp[2], e);
        this.add(comp[3], f);

        this.setBackground(ColorDataHolder.getBackgroundColor());
    }

    /**
     * Set game mode. true if classic mode false if mouse mode.
     *
     * @param m the mode
     */
    private void setGameMode(Mode m, JComponent[] c) {
        if (m == Mode.CLASSIC) {
            ((JButton) c[4]).doClick();
        } else {
            ((JButton) c[5]).doClick();
        }
    }

    /**
     * This is used to listen for a key press and save its integer value as
     * key.
     */
    private class MyKeyListener implements KeyListener {

        private int key;
        private boolean pressed;

        public MyKeyListener() {
            key = 0;
            pressed = false;

        }

        public int getKey() {
            return key;
        }

        public void setKey(int k) {
            key = k;
        }

        public boolean isPressed() {
            return pressed;
        }

        public void pressedOff() {
            pressed = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //empty
        }

        @Override
        public void keyPressed(KeyEvent e) {
            key = e.getKeyCode();
            pressed = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //empty
        }
    }

    /**
     * Button that keeps track of a place number.
     */
    private class MyButton extends JButton {

        int place = 0;

        public MyButton(int p) {
            place = p;
        }

        public int getPlace() {
            return place;
        }
    }

    /**
     * Action listener for buttons, keeps track of a place. Starts listening for
     * key or mouse press when pressed.
     */
    private class MyButtonAction implements ActionListener {

        final int place;
        final JLabel lbl;
        final MyKeyListener KL;

        public MyButtonAction(int p, JComponent[] c, MyKeyListener listner) {
            place = p;
            lbl = (JLabel) c[1];
            KL = listner;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addKeyListener(KL);
            lbl.setVisible(true);
            setFocusable(true);
            requestFocusInWindow();
            repaint();
            ((Timer) ARRAYHOLDER[4][place]).start();
        }
    }

    /**
     * Timer that is called every 10 milliseconds and checks if a key or mouse
     * button has been pressed. If pressed change that key with key at current
     * place in arrays
     */
    private class MyTimerAction implements ActionListener {

        final int place;
        final JLabel lbl;
        final UserInput ui;
        final MyKeyListener KL;

        public MyTimerAction(int p, JComponent[] c, UserInput input,
                MyKeyListener listner) {
            place = p;
            lbl = (JLabel) c[1];
            ui = input;
            KL = listner;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (KL.isPressed()) {
                lbl.setVisible(false);

                changeKey(place, KL);
                setNewLabelText(place);
                removeListener(place, KL);
            }
            if (ui.isMousePressed()) {
                changeToMousePrimary(place, KL);
                lbl.setVisible(false);
                removeListener(place, KL);
            }
        }
    }

    /**
     * Change the text of a label at a give index in the label array.
     *
     * @param n index
     */
    private void setNewLabelText(int n) {
        KeyBindings[] inputArray = KeyBindings.values();
        String s = "Current " + inputArray[n].toString() + " Key: "
                + KeyEvent.getKeyText(UserInput.actualKeys[n]);
        ((JLabel) ARRAYHOLDER[0][n]).setText(s);
    }

    /**
     * Change key at current index.
     *
     * @param index
     * @param KL the key listener
     */
    private void changeKey(int index, MyKeyListener KL) {
        switch (index) {
            case 0:
                UserInput.actualKeys[UsedKeys.MOVEUPKEY.getKeyIndex()] = KL.getKey();
                break;
            case 1:
                UserInput.actualKeys[UsedKeys.MOVEDOWNKEY.getKeyIndex()] = KL.getKey();
                break;
            case 2:
                UserInput.actualKeys[UsedKeys.MOVELEFTKEY.getKeyIndex()] = KL.getKey();
                break;
            case 3:
                UserInput.actualKeys[UsedKeys.MOVERIGHTKEY.getKeyIndex()] = KL.getKey();
                break;
            case 4:
                UserInput.actualKeys[UsedKeys.ROTATELEFTKEY.getKeyIndex()] = KL.getKey();
                break;
            case 5:
                UserInput.actualKeys[UsedKeys.ROTATERIGHTKEY.getKeyIndex()] = KL.getKey();
                break;
            case 6:
                RAWKEYS[6] = KL.getKey();
                UserInput.actualKeys[UsedKeys.ATTACKKEY.getKeyIndex()] = KL.getKey();
                break;
            case 7:
                RAWKEYS[7] = KL.getKey();
                UserInput.actualKeys[UsedKeys.PAUSEKEY.getKeyIndex()] = KL.getKey();
                break;
        }
    }
}
