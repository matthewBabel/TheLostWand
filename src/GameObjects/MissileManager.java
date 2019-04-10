package GameObjects;

import GameObjects.Collision.GlobMissile;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of all glob missiles.
 *
 * @author Matthew Babel
 */
public class MissileManager {

    List<GlobMissile> mngr;
    private int kills;
    private final Explosion EXPLO;
    
    public MissileManager(Explosion e) {
        mngr = new ArrayList<>();
        kills = 0;
        EXPLO = e;
    }

    /**
     * Add a missile to the list.
     *
     * @param m the missile
     */
    public void addBullet(GlobMissile m) {
        mngr.add(m);
    }

    /**
     * Move each missile in the list and check if it should be removed from list.
     *
     * @param playerX the player x cord
     * @param playerY the player y cord
     */
    public void update(float playerX, float playerY) {
        mngr.stream().forEach((m) -> {
            m.move(playerX, playerY);
        });

        List<GlobMissile> toRemove = new ArrayList<>();
        for (int i = 0; i < mngr.size(); i++) {
            GlobMissile m = mngr.get(i);

            if (!m.isAlive()) {
                toRemove.add(m);

                if (m.isKilled()) {
                    kills++;
                }
                final int OFFSET = 5;
                EXPLO.addExplo(new int[] {(int)m.getCurrentXCord()-OFFSET, 
                    (int)m.getCurrentYCord()-OFFSET, 0, 1});
            }
        }

        mngr.removeAll(toRemove);
    }

    public List<GlobMissile> getAll() {
        return mngr;
    }

    public boolean isEmpty() {
        return mngr.isEmpty();
    }

    public int getKills() {
        int temp = kills;
        kills = 0;
        return temp;
    }
}
