package GameObjects;

import GameObjects.Collision.Bullet;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of bullets.
 *
 * @author Matthew Babel
 */
public class BulletManager {
    List<Bullet> mngr;
    
    /**
     * Initialize bullet manager.
     */
    public BulletManager() {
        mngr = new ArrayList<>();
    }
    
    /**
     * Add a bullet to the list.
     * @param b the bullet
     */
    public void addBullet(Bullet b) {
        mngr.add(b);
    }
    
    /**
     * Move each bullet in the list and check if it should be removed from list.
     */
    public void update() {
        mngr.stream().forEach((b) -> {
            b.move(); 
        });
        
        List<Bullet> toRemove = new ArrayList<>();
        for (int i = 0; i < mngr.size(); i++) {
            Bullet b = mngr.get(i);
            b.move();
            if (b.isOffScreen() || !b.isAlive()) {
                toRemove.add(b);
            }
        }
        
        mngr.removeAll(toRemove);
    }
    
    /**
     * Get the entire list of bullets.
     * @return list of bullets
     */
    public List getAll() {
        return mngr;
    }
    
    public boolean isEmpty() {
        return mngr.isEmpty();
    }
}
