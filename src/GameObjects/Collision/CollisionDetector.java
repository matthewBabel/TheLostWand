package GameObjects.Collision;

import java.awt.Shape;
import java.awt.geom.Area;

/**
 * Detects if there's a collision between two collidable Objects. If there is
 * then calls their collision method.
 *
 * @author Matthew Babel
 */
public class CollisionDetector {

    /**
     * Checks for a collision between two basic enemy objects.
     *
     * @param obj1 the first collidable object
     * @param obj2 the second collidable object
     */
    public void checkCollision(CollidableObject obj1, CollidableObject obj2) {
        Shape mask1 = obj1.getCollisionMask();
        Shape mask2 = obj2.getCollisionMask();

        Area a1 = new Area(mask1);
        Area a2 = new Area(mask2);

        a1.intersect(a2);
        if (!(a1.isEmpty())) {
            obj1.collision(1);
            obj2.collision(2);
        }
    }
    
    /**
     * Checks for collision between two special objects determined by type.
 
     * type 1 - enemy hit by player
     *      2 - enemy dead but not killed
     *      3 - power-up speed
     *      4 - power-up charge attack
     *      5 - player attack and glob missile
     *      6 - glob collision with basic enemy
     *      7 - glob on glob
     * 
     * @param type collision type
     * @param main a special object usually player, bullet, or glob
     * @param secondary the secondary object, usually enemy or power up
     */
    public void checkCollision(int type, CollidableObject main, CollidableObject secondary) {
        Shape specMask = main.getCollisionMask();
        Shape mask2 = secondary.getCollisionMask();
        
        Area a1 = new Area(specMask);
        Area a2 = new Area(mask2);
        
      
        a1.intersect(a2);
        if (!(a1.isEmpty())) {
            switch (type) {
                case 1:
                    main.collision(1);
                    secondary.collision(3);
                    break;
                case 2:
                    main.collision(1);
                    secondary.collision(4);
                    break;
                case 3:
                    main.collision(2);
                    secondary.collision(1);
                    break;
                case 4:
                    main.collision(3);
                    secondary.collision(1);
                    break;
                case 5: // player hit missile with basic attack
                    secondary.collision(3);
                    break;
                case 6: // glob collided with basic enemy
                    ((Enemy)secondary).startAbsorbed((Glob)main);                 
                    break;
                case 7: // glob collided with another glob
                    main.collision(1);
                    secondary.collision(2);
                default:
                    break;
            }
        }
    }  
}