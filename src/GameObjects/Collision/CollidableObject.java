package GameObjects.Collision;

import java.awt.Polygon;

/**
 * Represents any object that can have collision during the game.
 *
 * @author Mattew Babel
 */
abstract public class CollidableObject {

    private boolean collision = false;

    /**
     * Get the collision mask of the object as a polygon.
     *
     * @return polygon of collision mask
     */
    abstract Polygon getCollisionMask();

    /**
     * Called when a collision has occurred. Gives a type to decide between
     * different types of reactions
     *
     * @param type the type of reaction, gives 1 as default
     */
    abstract void collision(int type);

    /**
     * turns collision on
     */
    public void collisionOn() {
        collision = true;
    }

    /**
     * turns collision off
     */
    public void collisionOff() {
        collision = false;
    }

    /**
     * Returns true if collision is true
     *
     * @return collision
     */
    public boolean isCollision() {
        return collision;
    }
}
