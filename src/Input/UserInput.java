package Input;

import Containers.GamePanel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Controls all user input.
 *
 * @author Matthew Babel
 */
public class UserInput extends MouseAdapter {

    private final JComponent inputDevice;

    // contains mouseattacking, mouse listening, mouse pressed, paused, attacked
    public static boolean[] boolHolder = {false, false, false, false, false};

    /**
     * integer array to hold the following.
     * horMovement, verMovement, rotation movement, mouse x cord, mouse y cord,
     */
    private final int[] INTHOLDER = new int[5];

    /**
     * Represents the keys being used and given them an index.
     */
    public enum UsedKeys {
        MOVEUPKEY(0),
        MOVEDOWNKEY(1),
        MOVELEFTKEY(2),
        MOVERIGHTKEY(3),
        ROTATELEFTKEY(4),
        ROTATERIGHTKEY(5),
        ATTACKKEY(6),
        PAUSEKEY(7);

        private final int KEYINDEX;

        private UsedKeys(int i) {
            KEYINDEX = i;
        }

        public int getKeyIndex() {
            return KEYINDEX;
        }
    }

    //The actual keys being used, change this to change controls
    public static int[] actualKeys = {KeyEvent.VK_UP, KeyEvent.VK_DOWN,
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_Q, KeyEvent.VK_E,
        KeyEvent.VK_SPACE, KeyEvent.VK_ESCAPE};

    /**
     * Takes component and adds key input bindings to actions maps.
     *
     * @param o the component used for input
     */
    public UserInput(JComponent o) {
        inputDevice = o;

        for (int i = 0; i < 5; i++) {
            INTHOLDER[0] = 0;
        }

        mapKeyInput();
    }

    /**
     * Maps key variables to input maps and action maps.
     */
    private void mapKeyInput() {
        int mode = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMapping(mode);
        ActionMapping();
    }

    private void InputMapping(int mode) {
        //key input
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[0], 0, false), "UP");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[1], 0, false), "DOWN");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[2], 0, false), "LEFT");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[3], 0, false), "RIGHT");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[4], 0, false), "Q");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[5], 0, false), "E");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[6], 0, false), "SPACE");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[7], 0, false), "PAUSE");

        //key releases
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[0], 0, true), "UP_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[1], 0, true), "DOWN_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[2], 0, true), "LEFT_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[3], 0, true), "RIGHT_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[4], 0, true), "Q_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[5], 0, true), "E_REL");
        inputDevice.getInputMap(mode).put(KeyStroke.getKeyStroke(actualKeys[6], 0, true), "ATK_REL");
    }

    private void ActionMapping() {
        //key action mapping
        inputDevice.getActionMap().put("UP", new MoveAction(0));
        inputDevice.getActionMap().put("DOWN", new MoveAction(1));
        inputDevice.getActionMap().put("LEFT", new MoveAction(2));
        inputDevice.getActionMap().put("RIGHT", new MoveAction(3));
        inputDevice.getActionMap().put("Q", new RotationAction(1));
        inputDevice.getActionMap().put("E", new RotationAction(2));
        inputDevice.getActionMap().put("SPACE", new AttackAction());
        inputDevice.getActionMap().put("PAUSE", new PauseAction());

        //key releases
        inputDevice.getActionMap().put("UP_REL", new KeyRelease(0));
        inputDevice.getActionMap().put("DOWN_REL", new KeyRelease(1));
        inputDevice.getActionMap().put("LEFT_REL", new KeyRelease(2));
        inputDevice.getActionMap().put("RIGHT_REL", new KeyRelease(3));
        inputDevice.getActionMap().put("Q_REL", new KeyRelease(4));
        inputDevice.getActionMap().put("E_REL", new KeyRelease(5));
        inputDevice.getActionMap().put("ATK_REL", new KeyRelease(6));
    }

    public int getHorMovement() {
        return INTHOLDER[0];
    }

    public int getVerMovement() {
        return INTHOLDER[1];
    }

    public int getRotMovement() {
        return INTHOLDER[2];
    }

    public boolean hasAttacked() {
        return boolHolder[4];
    }

    public void attackOff() {
        boolHolder[4] = false;
    }

    public boolean isPaused() {
        return boolHolder[3];
    }

    public boolean isMouseListening() {
        return boolHolder[1];
    }

    public int getMouseXCord() {
        return INTHOLDER[3];
    }

    public int getMouseYCord() {
        return INTHOLDER[4];
    }

    public boolean isMousePressed() {
        return boolHolder[2];
    }

    /**
     * called when mouse is dragged, update mouse x and y cords.
     *
     * @param e mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        INTHOLDER[3] = e.getX();
        INTHOLDER[4] = e.getY();
    }

    /**
     * called wheen mouse is moved, update mouse x and y cords.
     *
     * @param e mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        INTHOLDER[3] = e.getX();
        INTHOLDER[4] = e.getY();
    }

    /**
     * called when mouse is pressed.
     *
     * @param e mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        boolHolder[2] = true;
        if (boolHolder[0]) {
            boolHolder[4] = true;
        }
    }

    /**
     * called when mouse is released, update mouse pressed.
     *
     * @param e mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        boolHolder[2] = false;

        if (boolHolder[0]) {
            boolHolder[4] = false;
        }
    }

    /**
     * Called for movements being listened to.
     */
    private class MoveAction extends AbstractAction {

        int dir;

        /**
         * 0 - up, 1 - down, 2 - left, 3 - right
         *
         * @param d the direction
         */
        public MoveAction(int d) {
            dir = d;
        }

        /**
         * Turns direction into movement
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (dir) {
                case 0:
                    INTHOLDER[1] = 1;
                    break;
                case 1:
                    INTHOLDER[1] = 2;
                    break;
                case 2:
                    INTHOLDER[0] = 1;
                    break;
                case 3:
                    INTHOLDER[0] = 2;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Manages key releases.
     */
    private class KeyRelease extends AbstractAction {

        int t;

        public KeyRelease(int type) {
            t = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkKeyRelease();
        }

        private void checkKeyRelease() {
            switch (t) {
                case 0:
                    if (!(INTHOLDER[1] == 2)) {
                        INTHOLDER[1] = 0;
                    }
                    break;
                case 1:
                    if (!(INTHOLDER[1] == 1)) {
                        INTHOLDER[1] = 0;
                    }
                    break;
                case 2:
                    if (!(INTHOLDER[0] == 2)) {
                        INTHOLDER[0] = 0;
                    }
                    break;
                case 3:
                    if (!(INTHOLDER[0] == 1)) {
                        INTHOLDER[0] = 0;
                    }
                    break;
                case 4:
                    if (!(INTHOLDER[2] == 2)) {
                        INTHOLDER[2] = 0;
                    }
                    break;
                case 5:
                    if (!(INTHOLDER[2] == 1)) {
                        INTHOLDER[2] = 0;
                    }
                    break;
                case 6:
                    if (boolHolder[4]) {
                        boolHolder[4] = false;
                    }
                default:
                    break;
            }
        }
    }

    /**
     * Called for rotation movement keys.
     */
    private class RotationAction extends AbstractAction {

        int dir;

        public RotationAction(int d) {
            dir = d;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (dir == 1) {
                INTHOLDER[2] = 1;
            } else if (dir == 2) {
                INTHOLDER[2] = 2;
            }
        }

    }

    /**
     * Called for attacking key.
     */
    private class AttackAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolHolder[4] = true;
        }
    }

    /**
     * When pause key is pressed switch the pause value.
     */
    private class PauseAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolHolder[3] = !boolHolder[3];

            if (boolHolder[3] == false) {
                ((GamePanel) inputDevice).unPause();
            }
        }
    }
}
