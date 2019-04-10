package GameObjects;

import Audio.AudioHolder;
import Audio.AudioPlayer;
import GameObjects.Collision.Enemy;
import GameObjects.Collision.Glob;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to spawn basic enemies and globs. Enemies spawn one after another
 * with a delay rather than all at once. Also allows changing of spawn count
 * and delay.
 *
 * @author Matthew Babel
 */
public class EnemySpawner {

    private List<Enemy> enemies;
    private final List<Glob> GLOBS;
    private boolean spawning;

    // added to when enemys die     
    private final Explosion EXPLO;

    /**
     * array of integer variables.
     * enemy amountToSpawn, amountSpawned, spawnframeCounter, spawnDelay, kills
     * two constants - FRAMEWIDTH, FRAMEHEIGHT
     * glob amount to spawn, glob amount spawned, globframeCounter, globDelay
     */
    private final int[] VARHOLDER = new int[11];

    /**
     * Initializes spawner variables.
     *
     * @param x the frame width
     * @param y the frame height
     * @param e refrence to enemy explosion class
     */
    public EnemySpawner(int x, int y, Explosion e) {
        VARHOLDER[0] = 0;
        VARHOLDER[1] = 0;
        VARHOLDER[2] = 0;
        VARHOLDER[3] = 50; // basic enemy frame delay
        VARHOLDER[4] = 0;
        VARHOLDER[5] = x;
        VARHOLDER[6] = y;
        VARHOLDER[7] = 0;
        VARHOLDER[8] = 0;
        VARHOLDER[9] = 0;
        VARHOLDER[10] = 100;
        enemies = new ArrayList<>(LevelDesign.SPAWNAMOUNTS[1]);
        GLOBS = new ArrayList<>(2);
        spawning = false;
        EXPLO = e;
    }

    /**
     * Spawns an enemy if spawn counter has exceeded delay.
     */
    private void spawn() {
        int amountToSpawn = VARHOLDER[0];
        int amountSpawned = VARHOLDER[1];
        int spawnCounter = VARHOLDER[2];
        int spawnDelay = VARHOLDER[3];
        final int FRAMEWIDTH = VARHOLDER[5];
        final int FRAMEHEIGHT = VARHOLDER[6];

        spawnCounter++;
        if (spawnCounter > spawnDelay) {
            spawnCounter = 0;
            enemies.add(amountSpawned, new Enemy(FRAMEWIDTH, FRAMEHEIGHT, amountSpawned));

            amountSpawned++;
            if (amountSpawned >= amountToSpawn) {
                spawning = false;
            }
        }

        VARHOLDER[1] = amountSpawned;
        VARHOLDER[2] = spawnCounter;
    }

    /**
     * Do the glob spawn update, spawn glob if counter if over delay.
     */
    private void globSpawn() {
        int globSpawnCount = VARHOLDER[9];
        int globSpawnDelay = VARHOLDER[10];

        globSpawnCount++;
        if (globSpawnCount > globSpawnDelay) {
            globSpawnCount = 0;
            addGlob();
        }
        VARHOLDER[9] = globSpawnCount;
    }

    public void addToGlobLimit() {
        VARHOLDER[7]++;
    }

    private void addGlob() {
        GLOBS.add(VARHOLDER[8], new Glob(VARHOLDER[5], VARHOLDER[6], VARHOLDER[8]));
        VARHOLDER[8]++;
    }

    /**
     * Change the amount of enemies to spawn.
     *
     * @param num the new spawn amount
     * @param changeAllocation change the size of the list if true
     */
    public void changeSpawnCount(int num, boolean changeAllocation) {
        VARHOLDER[0] = num;
        spawning = true;

        if (changeAllocation) {
            changeListSize();
        }
    }

    /**
     * Manual reallocation of list size to keep memory usage down.
     */
    private void changeListSize() {
        List<Enemy> temp = new ArrayList<>(LevelDesign.SPAWNAMOUNTS[3]);

        enemies.stream().forEach((e) -> {
            temp.add(e);
        });

        enemies = temp;
    }

    /**
     * Change the spawn delay.
     *
     * @param frames the number of frames the new delay will take
     */
    public void changeSpawnDelay(int frames) {
        VARHOLDER[3] = frames;
    }

    /**
     * Start the spawner.
     */
    public void start() {
        spawning = true;
        spawn();
    }

    /**
     * Called every game update, check if enemies should spawn.
     */
    public void tick() {
        if (spawning) {
            spawn();
        }

        if (VARHOLDER[8] < VARHOLDER[7]) {
            globSpawn();
        }
    }

    public boolean isSpawning() {
        return spawning;
    }

    public Enemy getEnemy(int i) {
        return enemies.get(i);
    }

    public Glob getGlob(int i) {
        return GLOBS.get(i);
    }

    /**
     * Get the size of the enemy spawner list.
     *
     * @return the size of enemy spawner
     */
    public int getEnemySize() {
        return enemies.size();
    }

    /**
     * Get the size of the glob spawner list.
     *
     * @return the size of glob spawner
     */
    public int getGlobSize() {
        return GLOBS.size();
    }

    /**
     * checks for dead enemies and removes them from list.
     */
    public void checkDead() {
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                removeEnemy(i);
            }
        }

        for (int i = 0; i < GLOBS.size(); i++) {
            if (GLOBS.get(i).isDead()) {
                removeGlob(i);
            }
        }
    }

    /**
     * Remove an enemy from the list and decrement the position of all enemies
     * in list afterwards, then add an explosion.
     *
     * @param n index of enemy being removed
     */
    private void removeEnemy(int n) {
        int amountToSpawn = VARHOLDER[0];
        int amountSpawned = VARHOLDER[1];

        Enemy e = enemies.remove(n);
        for (int j = n; j < enemies.size(); j++) {
            enemies.get(j).decrementPosition();
        }

        final int OFFSET = 20;
        EXPLO.addExplo(new int[]{(int)e.getCurrentXCord() - OFFSET, (int)e.getCurrentYCord() - OFFSET, 0, 0});
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.SMALLEXPLOSION);

        if (amountSpawned > 0) {
            amountSpawned--;
        }

        if (amountSpawned < amountToSpawn) {
            spawning = true;
        }

        if (e.isKilled()) {
            VARHOLDER[4]++;
        }
        VARHOLDER[1] = amountSpawned;
    }

    /**
     * Remove a glob from the list and decrement position of all other globs
     * in list afterwards. Also check to see if any enemy is being absorbed by
     * this glob.
     *
     * @param n index of glob
     */
    private void removeGlob(int n) {
        Glob g = GLOBS.remove(n);
        for (int i = n; i < GLOBS.size(); i++) {
            GLOBS.get(i).decrementPosition();
        }

        updateAbsorbedEnemies(n);

        final int OFFSET = 20;
        EXPLO.addExplo(new int[]{(int)g.getTargetXCord() + OFFSET,(int)g.getTargetYCord() + OFFSET, 0, 2});
        AudioPlayer.playPreloadedSound(AudioHolder.SoundEffects.BIGEXPLOSION);
        
        if (g.isKilled()) {
            VARHOLDER[4] += 5; // score is equal to 5x a basic enemy
        }
        VARHOLDER[8]--;
    }

    /**
     * checks if an absorbed enemy needs to decrement their position to equal
     * their Glob's position.
     *
     * @param n index of removed glob
     */
    private void updateAbsorbedEnemies(int n) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.isBeingAbsored()) {
                if (e.getGlobPosition() == n) {
                    e.setBeingAbsorbed(false);
                } else if (e.getGlobPosition() > n) {
                    e.decrementGlobPosition();
                }
            }
        }
    }

    /**
     * Returns number of dead enemies and resets kills to 0.
     *
     * @return number of dead enemies
     */
    public int getKills() {
        int temp = VARHOLDER[4];
        VARHOLDER[4] = 0;
        return temp;
    }
}
