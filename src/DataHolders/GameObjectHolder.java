package DataHolders;

import Containers.Frame;
import Containers.GamePanel;
import GameObjects.*;
import GameObjects.Collision.*;
import Input.UserInput;

/**
 * Holds the refrence of all the game objects being used by the Game Panel.
 *
 * @author Matthew Babel
 */
public class GameObjectHolder {

    private final UserInput INPUT;
    private final Player PLAYER;
    private final Explosion EXPLO;
    private final EnemySpawner SPAWNER;
    private final CollisionDetector COLLDETECT;
    private final PowerUp POWERUP;
    private final LevelDesign DESIGN;
    private final BulletManager BULLETMNGR;
    private final MissileManager MISSILEMNGR;

    public GameObjectHolder(Frame fr, GamePanel gp) {   
        int frWidth = fr.FRAMESIZE.width;
        int frHeight = fr.FRAMESIZE.height;
        
        INPUT = new UserInput(gp);
        PLAYER = new Player(fr, gp, frWidth, frHeight);
        EXPLO = new Explosion();
        SPAWNER = new EnemySpawner(frWidth, frHeight, EXPLO);
        COLLDETECT = new CollisionDetector();
        POWERUP = new PowerUp(frWidth, frHeight);
        DESIGN = new LevelDesign(SPAWNER, POWERUP);
        BULLETMNGR = new BulletManager();        
        MISSILEMNGR = new MissileManager(EXPLO);
    }
    
    public UserInput getInput() {
        return INPUT;
    }
    
    public Player getPlayer() {
        return PLAYER;
    }
    
    public Explosion getExplosion() {
        return EXPLO;
    }
    
    public EnemySpawner getSpawner() {
        return SPAWNER;
    }
    
    public CollisionDetector getDetecter() {
        return COLLDETECT;
    }
    
    public PowerUp getPowerUp() {
        return POWERUP;
    }
    
    public LevelDesign getLevelDesign() {
        return DESIGN;
    }
    
    public BulletManager getBulletManager() {
        return BULLETMNGR;
    }
    
    public MissileManager getMissileManager() {
        return MISSILEMNGR;
    }
}
