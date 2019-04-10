package GameObjects;

import DataHolders.GameDataHolder;
import GameObjects.Collision.PowerUp;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controls the level structure of a play through of the game. Uses Timers
 * performing actions with Spawner and PowerUp objects.
 *
 * @author Matthew Babel
 */
public class LevelDesign {

    private final EnemySpawner SPWN;
    private final PowerUp PWRUP;
    private boolean puWasUp;

    public static final int[] SPAWNAMOUNTS = {5, 10, 20, 25};
    private final static TimerTask[] TIMERTASKS = new TimerTask[4];

    /**
     * Initialize spawner and power up variables.
     *
     * @param es - Enemy Spawner
     * @param pu - Power up
     */
    public LevelDesign(EnemySpawner es, PowerUp pu) {
        SPWN = es;
        PWRUP = pu;
        puWasUp = false;
    }

    /**
     * Start the level designer.
     */
    public void start() {
        SPWN.changeSpawnCount(SPAWNAMOUNTS[0], false);
        SPWN.start();

        final int POWERUPDUR = 10000;
        final int LVL1DUR = 30000;
        final int LVL2DUR = 30000;
        final int LVL3DUR = 80000;

        // Using this to schedule timer tasks that manipulate gameplay
        Timer t = new Timer("Timer");

        initPowerUpLoop();
        initLevel2(POWERUPDUR, LVL2DUR, t);
        initLevel3(LVL3DUR, t);
        initLevel4();

        t.schedule(TIMERTASKS[1], LVL1DUR);
    }

    /**
     * The power up timer event that either creates a power up or turns a power
     * up off.
     */
    private void initPowerUpLoop() {
        TimerTask powerUpLoop = new TimerTask() {
            @Override
            public void run() {
                if (!puWasUp) {
                    PWRUP.create();
                    puWasUp = true;
                } else {
                    puWasUp = false;
                    PWRUP.turnOff();
                }
            }

        };
        TIMERTASKS[0] = powerUpLoop;
    }

    /**
     * Level 2 increases spawn limits and adds a glob.
     *
     * @param POWERUPDUR power up duration
     * @param LVL2DUR level 3 duration
     * @param t timer
     */
    private void initLevel2(int POWERUPDUR, int LVL2DUR, Timer t) {
        TimerTask level2 = new TimerTask() {
            @Override
            public void run() {
                GameDataHolder.level++;
                SPWN.changeSpawnCount(SPAWNAMOUNTS[1], false);
                SPWN.addToGlobLimit();
                
                SPWN.start();
                t.schedule(TIMERTASKS[0], POWERUPDUR, POWERUPDUR);
                t.schedule(TIMERTASKS[2], LVL2DUR);
            }
        };

        TIMERTASKS[1] = level2;
    }

    /**
     * Level 3 increases basic enemy spawn limit and shortens their spawn delay.
     *
     * @param LVL3DUR level 4 duration
     * @param t timer
     */
    private void initLevel3(int LVL3DUR, Timer t) {
        TimerTask level3 = new TimerTask() {
            @Override
            public void run() {
                GameDataHolder.level++;
                SPWN.changeSpawnCount(SPAWNAMOUNTS[2], true);
                SPWN.changeSpawnDelay(30);
                SPWN.start();
                
                t.schedule(TIMERTASKS[3], LVL3DUR);
            }
        };

        TIMERTASKS[2] = level3;
    }

    /**
     * Level 4 increases spawn limit and adds a second glob.
     */
    private void initLevel4() {
        TimerTask level4 = new TimerTask() {
            @Override
            public void run() {
                GameDataHolder.level++;
                SPWN.changeSpawnCount(SPAWNAMOUNTS[3], false);
                SPWN.addToGlobLimit();
                SPWN.start();
            }
        };

        TIMERTASKS[3] = level4;
    }
    
    public static void stop() {
        for (TimerTask task : TIMERTASKS) {
            task.cancel();
        }
    }
}
