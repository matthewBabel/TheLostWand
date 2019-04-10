package DataHolders;

/**
 * Holds and keeps track of the game data.
 *
 * @author Matthew Babel
 */
public class GameDataHolder {

    // Frame Rate
    private boolean running;
    private volatile boolean paused;
    private final int MAXFPS;
    private int frameCount;
    
    // two modes classic all keyboard input, mouse uses mouse input for aiming
    public enum Mode{CLASSIC, MOUSE}
    public static Mode currentMode = Mode.MOUSE;

    public static int level = 1;
    
    public GameDataHolder() {
        running = true;
        paused = false;
        MAXFPS = 60;
        frameCount = 0;
    }

    public void setRunning(boolean bool) {
        running = bool;
    }

    public void setPaused(boolean bool) {
        paused = bool;
    }

    public void setFrameCount(int n) {
        frameCount = n;
    }
    
    public static void setMode(Mode m) {
        currentMode = m;
    }

    public void tickFrameCount() {
        frameCount++;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getFps() {
        return MAXFPS;
    }

    public int getFrameCount() {
        return frameCount;
    }
    
    public Mode getMode() {
        return currentMode;
    }
}
