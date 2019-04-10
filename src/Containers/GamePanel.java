package Containers;

import DataHolders.BulletDataHolder;
import DataHolders.ColorDataHolder;
import DataHolders.FontDataHolder;
import DataHolders.GameDataHolder;
import DataHolders.GameObjectHolder;
import GameObjects.BulletManager;
import GameObjects.Collision.Bullet;
import GameObjects.Collision.CollidableObject;
import GameObjects.Collision.CollisionDetector;
import GameObjects.Collision.Enemy;
import GameObjects.Collision.Glob;
import GameObjects.Collision.GlobMissile;
import GameObjects.Collision.Player;
import GameObjects.Collision.PowerUp;
import GameObjects.Collision.Spawn;
import GameObjects.EnemySpawner;
import GameObjects.Explosion;
import GameObjects.MissileManager;
import Input.UserInput;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel that holds the game environment of The Lost Wand. Starts the game loop
 * which updates game objects, does collision detection, and renders the screen.
 *
 * @author Matthew Babel
 */
public class GamePanel extends JPanel {

    public int score;
    private final JLabel[] LBLS;
    private final GameObjectHolder GAMEOBJECTS;
    private final BulletDataHolder BULLETDATA;
    public final GameDataHolder GAMEDATA;

    /**
     * Creating a game panel and initializing instance variables.
     *
     * @param frame passed along for other classes to refrence
     */
    public GamePanel(Frame frame) {
        BULLETDATA = new BulletDataHolder();
        GAMEDATA = new GameDataHolder();
        GAMEOBJECTS = new GameObjectHolder(frame, (GamePanel) this);

        score = 0;
        LBLS = new JLabel[4];

        initThisPanel();
        initLabels(LBLS);
        makeScreen(LBLS, frame);
        startGame();
    }

    private void initThisPanel() {
        this.addMouseListener(GAMEOBJECTS.getInput());
        this.addMouseMotionListener(GAMEOBJECTS.getInput());
        this.setLayout(new GridBagLayout());
        this.setBackground(ColorDataHolder.getBackgroundColor());
    }

    private void initLabels(JLabel[] lbls) {
        lbls[0] = new JLabel("Score: " + score);
        lbls[1] = new JLabel("Lives: " + GAMEOBJECTS.getPlayer().getLives());
        lbls[2] = new JLabel("Game Paused");
        lbls[3] = new JLabel("Level: " + GameDataHolder.level);

        lbls[0].setFont(FontDataHolder.getPtSans35());
        lbls[0].setPreferredSize(new Dimension(220, 50));
        lbls[0].setVerticalAlignment(SwingConstants.TOP);
        lbls[0].setHorizontalAlignment(SwingConstants.CENTER);

        lbls[1].setFont(FontDataHolder.getPtSans35());
        lbls[1].setPreferredSize(new Dimension(160, 50));
        lbls[1].setVerticalAlignment(SwingConstants.TOP);
        lbls[1].setHorizontalAlignment(SwingConstants.RIGHT);

        lbls[2].setFont(FontDataHolder.getPtSans170());
        lbls[2].setPreferredSize(new Dimension(1000, 240));
        lbls[2].setVerticalAlignment(SwingConstants.CENTER);
        lbls[2].setHorizontalAlignment(SwingConstants.CENTER);
        lbls[2].setVisible(false);

        lbls[3].setFont(FontDataHolder.getPtSans35());
        lbls[3].setPreferredSize(new Dimension(160, 50));
        lbls[3].setVerticalAlignment(SwingConstants.TOP);
        lbls[3].setHorizontalAlignment(SwingConstants.RIGHT);
    }

    /**
     * Sets the grid bag layout for the labels on screen.
     */
    private void makeScreen(JLabel[] lbls, Frame FR) {
        final GridBagConstraints a = new GridBagConstraints();
        final GridBagConstraints b = new GridBagConstraints();
        final GridBagConstraints c = new GridBagConstraints();
        final GridBagConstraints d = new GridBagConstraints();

        a.gridx = 1;
        a.gridy = 0;
        a.weightx = .9;
        a.weighty = .1;
        a.insets = new Insets(0, 0, 0, (FR.FRAMESIZE.width / 2) - (FR.FRAMESIZE.width / 4));
        a.anchor = GridBagConstraints.NORTHEAST;

        b.gridx = 0;
        b.gridy = 0;
        b.weightx = .1;
        b.weighty = .1;
        b.anchor = GridBagConstraints.NORTHWEST;

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = .1;
        c.weighty = .9;
        c.insets = new Insets(0, 0, 300, 0);
        c.anchor = GridBagConstraints.CENTER;

        d.gridx = 2;
        d.gridy = 0;
        d.weightx = .1;
        d.weighty = .1;
        d.insets = new Insets(0, 0, 0, FR.FRAMESIZE.width / 10);
        d.anchor = GridBagConstraints.NORTHEAST;

        this.add(lbls[0], a); // score
        this.add(lbls[1], b); // lives
        this.add(lbls[2], c); // pause
        this.add(lbls[3], d); // level
    }

    /**
     * Start the level designer and run the game loop.
     */
    private void startGame() {
        GAMEOBJECTS.getLevelDesign().start();
        this.setFocusable(true);
        runGameLoop();
    }

    /**
     * Starts a new thread and runs the game loop in it.
     */
    public void runGameLoop() {
        Thread loop = new Thread() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        loop.start();
    }

    /**
     * Main game loop only run this in another Thread.
     */
    @SuppressWarnings({"SleepWhileInLoop", "CallToThreadYield"})
    private void gameLoop() {
        final double GAME_HERTZ = 30.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        @SuppressWarnings("UnusedAssignment")
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = GAMEDATA.getFps();
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (GAMEDATA.isRunning()) {
            double now = System.nanoTime();
            int updateCount = 0;

            if (!GAMEDATA.isPaused()) {

                //Do as many game updates as we need to, potentially playing catchup.
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                    updateGameLogic();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                updateScreen(interpolation);
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime) {
                    int frameCount = GAMEDATA.getFrameCount();
                    int fps = GAMEDATA.getFps();
                    if (frameCount > fps) {
                        GAMEDATA.setFrameCount(fps);
                        frameCount = fps;
                    }
                    if (frameCount < fps) {
                        //System.out.println("fps: " + frameCount);
                    }
                    GAMEDATA.setFrameCount(0);
                    lastSecondTime = thisSecond;
                }
                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }

                    now = System.nanoTime();
                }
            }
        }
    }

    /**
     * Called every frame, does interpolation, repaints, then does 
     * collision detection.
     */
    private void updateScreen(float interpolation) {
        doInterpolation(interpolation);
        repaint();
        collisionDetection();
    }

    /**
     * Do the interpolation of all objects that move.
     * 
     * @param interp interpolation value
     */
    private void doInterpolation(float interp) {
        EnemySpawner SPAWNER = GAMEOBJECTS.getSpawner();

        // basic enemy
        for (int i = 0; i < SPAWNER.getEnemySize(); i++) {
            Enemy e = SPAWNER.getEnemy(i);
            e.setCurrentX(lerp(e.getCurrentXCord(), e.getTargetXCord(), interp));
            e.setCurrentY(lerp(e.getCurrentYCord(), e.getTargetYCord(), interp));
        }

        // glob
        for (int i = 0; i < SPAWNER.getGlobSize(); i++) {
            Glob g = SPAWNER.getGlob(i);
            g.setCurrentX(lerp(g.getCurrentXCord(), g.getTargetXCord(), interp));
            g.setCurrentY(lerp(g.getCurrentYCord(), g.getTargetYCord(), interp));
            g.setCurrentRotation(rotationLerp(g.getCurrentRotation(), g.getTargetRotation(), interp));
        }

        // player
        Player p = GAMEOBJECTS.getPlayer();
        p.setCurrentX(lerp(p.getCurrentX(), p.getTargetX(), interp));
        p.setCurrentY(lerp(p.getCurrentY(), p.getTargetY(), interp));
        p.setCurrentHeadDegree(rotationLerp(p.getCurrentHeadDegree(), p.getTargetHeadDegree(), interp));

        // player bullet
        BulletManager bm = GAMEOBJECTS.getBulletManager();
        if (!bm.isEmpty()) {
            List<Bullet> bullets = bm.getAll();
            bullets.stream().map((b) -> {
                b.setCurrentX(lerp(b.getCurrentXCord(), b.getTargetXCord(), interp));
                return b;
            }).forEach((b) -> {
                b.setCurrentY(lerp(b.getCurrentYCord(), b.getTargetYCord(), interp));
            });
        }
        
        //missiles
        MissileManager mm = GAMEOBJECTS.getMissileManager();
        if (!mm.isEmpty()) {
            List<GlobMissile> missiles = mm.getAll();
            missiles.stream().map((m) -> {
                m.setCurrentX(lerp(m.getCurrentXCord(), m.getTargetXCord(), interp));
                return m;
            }).map((m) -> {
                m.setCurrentY(lerp(m.getCurrentYCord(), m.getTargetYCord(), interp));
                return m;
            }).forEach((m) -> {
                m.setCurrentRotation(rotationLerp(m.getCurrentRotation(), m.getTargetRotation(), interp));
            });
        }
    }

    /**
     * Linear interpolation for rotation data. Checks for data close to 360-0 
     * and skips over it.
     * @param current
     * @param target
     * @param interp
     * @return 
     */
    private float rotationLerp(float current, float target, float interp) {
        if (!(Math.abs(target - current) > 300)) {
            return lerp(current, target, interp);
        } else {
            return target;
        }
    }

    /**
     * Linear interpolation formula.
     * 
     * @param current current data
     * @param target target data
     * @param interp interpolation value
     * @return new current data
     */
    private float lerp(float current, float target, float interp) {
        return current + (interp * (target - current));
    }

    /**
     * Do the collision detection.
     */
    private void collisionDetection() {
        final Player PLAYER = GAMEOBJECTS.getPlayer();
        final PowerUp POWERUP = GAMEOBJECTS.getPowerUp();
        final CollisionDetector COLLDETECT = GAMEOBJECTS.getDetecter();
        final EnemySpawner SPAWNER = GAMEOBJECTS.getSpawner();

        boolean attackMask = PLAYER.isAttacking();

        // don't make attack mask if using charge attack
        if (PLAYER.isUsingCharge()) {
            attackMask = false;
        }

        PLAYER.makeMask();
        if (POWERUP.isUp()) {
            COLLDETECT.checkCollision(POWERUP.getType(), PLAYER, POWERUP);
        }

        if (attackMask) {
            PLAYER.makeAttackMask();
        }

        collisionLoop(attackMask, SPAWNER, COLLDETECT, PLAYER);
        SPAWNER.checkDead();
        updateLabels(SPAWNER.getKills() + GAMEOBJECTS.getMissileManager().getKills());
    }

    /**
     * Algorithm that does all the collision checking.
     *
     * @param basicAttack true if player is using basic attack
     */
    private void collisionLoop(boolean basicAttack, EnemySpawner SPAWNER,
            final CollisionDetector COLLDETECT, Player PLAYER) {

        final BulletManager bulletMngr = GAMEOBJECTS.getBulletManager();
        final MissileManager missileMngr = GAMEOBJECTS.getMissileManager();

        for (int i = 0; i < SPAWNER.getGlobSize(); i++) {
            globDetection(COLLDETECT, PLAYER, SPAWNER, i, basicAttack);
        }

        int length = SPAWNER.getEnemySize();
        for (int i = 0; i < length; i++) {
            basicEnemyDetection(COLLDETECT, PLAYER, SPAWNER, i, basicAttack, length);
        }

        missilePlayerDetetction(missileMngr, COLLDETECT, PLAYER, basicAttack);
        bulletDetection(bulletMngr, SPAWNER, COLLDETECT, missileMngr);
    }

    /**
     * Collision detection involving the basic enemy.
     *
     * @param COLLDETECT collision detector
     * @param PLAYER the player
     * @param SPAWNER the spawner
     * @param i index
     * @param basicAttack is player using basic attack
     * @param bulletMngr bullet manager
     * @param missileMngr missile manager
     * @param length length of enemy list
     */
    private void basicEnemyDetection(final CollisionDetector COLLDETECT,
            Player PLAYER, EnemySpawner SPAWNER, int i, boolean basicAttack,
            int length) {

        //collision between player's body and enemy
        COLLDETECT.checkCollision(2, PLAYER, (CollidableObject) SPAWNER.getEnemy(i));

        //collision between player's attack and enemy
        if (basicAttack) {
            COLLDETECT.checkCollision(1, PLAYER.getLightning(), (CollidableObject) SPAWNER.getEnemy(i));
        }

        enemyOnEnemyCollision(i, length, COLLDETECT, SPAWNER);
    }

    /**
     * checking for collision between enemies.
     *
     * @param i index
     * @param length length of enemy list
     * @param COLLDETECT collision detector
     * @param SPAWNER the spawner
     */
    private void enemyOnEnemyCollision(int i, int length, final CollisionDetector COLLDETECT, EnemySpawner SPAWNER) {
        //collision between enemies
        for (int j = i + 1; j < length; j++) {
            COLLDETECT.checkCollision(SPAWNER.getEnemy(i),
                    SPAWNER.getEnemy(j));
        }

        // between glob and basic enemy, starts absorb attack here
        for (int j = 0; j < SPAWNER.getGlobSize(); j++) {
            if (!SPAWNER.getEnemy(i).isAbsorbCdOn()
                    && !SPAWNER.getEnemy(i).isBeingAbsored()) {
                COLLDETECT.checkCollision(6, SPAWNER.getGlob(j),
                        SPAWNER.getEnemy(i));
            }
        }

        //between two globs
        if (SPAWNER.getGlobSize() > 1) {
            for (int j = 0; j < SPAWNER.getGlobSize() - 1; j++) {
                COLLDETECT.checkCollision(7, SPAWNER.getGlob(j), SPAWNER.getGlob(j + 1));
            }
        }
    }

    /**
     * Doing collision for the glob objects.
     *
     * @param COLLDETECT the collision detector
     * @param PLAYER the player
     * @param SPAWNER the spawner
     * @param i the index
     * @param basicAttack true if player is using basic attack
     * @param bulletMngr the bullet manager
     * @param missileMngr the missile manager
     */
    private void globDetection(final CollisionDetector COLLDETECT, Player PLAYER, EnemySpawner SPAWNER, int i, boolean basicAttack) {
        //collision between player's body and glob
        COLLDETECT.checkCollision(2, PLAYER, (CollidableObject) SPAWNER.getGlob(i));

        //collision between player's attack and enemy
        if (basicAttack) {
            COLLDETECT.checkCollision(1, PLAYER.getLightning(), (CollidableObject) SPAWNER.getGlob(i));
        }
    }

    /**
     * doing collision detection between missiles and the player.
     *
     * @param missileMngrthe missile manager
     * @param COLLDETECT the collision detector
     * @param PLAYER the player
     * @param basicAttack true if player is using basic attack
     */
    private void missilePlayerDetetction(final MissileManager missileMngr, final CollisionDetector COLLDETECT, Player PLAYER, boolean basicAttack) {
        if (!missileMngr.isEmpty()) {
            List<GlobMissile> missiles = missileMngr.getAll();
            missiles.stream().map((m) -> {
                COLLDETECT.checkCollision(2, PLAYER, m);
                return m;
            }).filter((m) -> (basicAttack)).forEach((m) -> {
                COLLDETECT.checkCollision(5, PLAYER.getLightning(), m);
            });
        }
    }

    /**
     * doing all the bullet detection.
     *
     * @param bulletMngr the bullet manager
     * @param SPAWNER the spawner
     * @param COLLDETECT the collision detector
     * @param missileMngr the missile manager
     */
    private void bulletDetection(final BulletManager bulletMngr, EnemySpawner SPAWNER, final CollisionDetector COLLDETECT, final MissileManager missileMngr) {
        if (!bulletMngr.isEmpty()) {
            List<Bullet> bullets = bulletMngr.getAll();

            bullets.stream().map((b) -> { // glob
                for (int i = 0; i < SPAWNER.getGlobSize(); i++) {
                    COLLDETECT.checkCollision(1, b, (CollidableObject) SPAWNER.getGlob(i));
                }
                return b;
            }).map((b) -> {
                for (int i = 0; i < SPAWNER.getEnemySize(); i++) { // basic enemy
                    COLLDETECT.checkCollision(1, b, (CollidableObject) SPAWNER.getEnemy(i));
                }
                return b;
            }).filter((b) -> (!missileMngr.isEmpty())).forEach((b) -> { //missiles
                List<GlobMissile> missiles = missileMngr.getAll();
                missiles.stream().forEach((m) -> {
                    COLLDETECT.checkCollision(1, b, m);
                });
            });
        }
    }

    /**
     * Update the game labels with score. Score is kills times a hundred.
     *
     * @param k the amount of enemies killed since the last update
     */
    private void updateLabels(int k) {
        score += k * 100;
        LBLS[0].setText("Score: " + score);
        LBLS[1].setText("Lives: " + GAMEOBJECTS.getPlayer().getLives());
        LBLS[3].setText("Level: " + GameDataHolder.level);
    }

    /**
     * Checking if game is paused, updating mouse position if needed, ticking
     * all game objects and updating player attack in the input object.
     */
    public void updateGameLogic() {
        final UserInput INPUT = GAMEOBJECTS.getInput();
        if (INPUT.isPaused()) {
            GAMEDATA.setPaused(true);
            LBLS[2].setVisible(true); // pause label
        }

        if (INPUT.isMouseListening()) {
            doMouseInput();
        }

        tickObjects();

        if (!GAMEOBJECTS.getPlayer().isAttacking()) {
            INPUT.attackOff();
        }
    }

    /**
     * Using trig to find the angle pointing towards mouse cursor then setting
     * the players degree to it.
     */
    private void doMouseInput() {
        final Player PLAYER = GAMEOBJECTS.getPlayer();
        final UserInput INPUT = GAMEOBJECTS.getInput();

        float px = PLAYER.getTargetX() + PLAYER.getXChange();
        float py = PLAYER.getTargetY() + PLAYER.getYChange();
        int mx = INPUT.getMouseXCord();
        int my = INPUT.getMouseYCord();
        double d1 = Math.abs(py - my);
        double d2 = Math.abs(px - mx);
        double n = Math.atan2(d1, d2);

        n = Math.toDegrees(n);
        n = 90 - n;

        if (px <= mx && py <= my) {
            n += 90;
            n = 90 - n;
            n += 180;
        } else if (px >= mx && py <= my) {
            n += 180;
        } else if (px >= mx && py >= my) {
            n = 90 - n;
            n += 270;
        }

        PLAYER.setTargetHeadDegree(n);
    }

    /**
     * Ticking all game objects.
     */
    private void tickObjects() {
        final Player PLAYER = GAMEOBJECTS.getPlayer();
        final UserInput INPUT = GAMEOBJECTS.getInput();
        final BulletManager bulletMngr = GAMEOBJECTS.getBulletManager();
        final MissileManager missileMngr = GAMEOBJECTS.getMissileManager();
        final EnemySpawner SPAWNER = GAMEOBJECTS.getSpawner();
        final Explosion EXPLO = GAMEOBJECTS.getExplosion();

        if (!bulletMngr.isEmpty()) {
            bulletMngr.update();
        }

        PLAYER.move(INPUT.getHorMovement(), INPUT.getVerMovement(),
                INPUT.getRotMovement(), INPUT.hasAttacked(), bulletMngr);

        float px = PLAYER.getTargetX() + PLAYER.getXChange();
        float py = PLAYER.getTargetY() + PLAYER.getYChange();

        enemyMovement(SPAWNER, px, py, missileMngr);

        if (SPAWNER.isSpawning()) {
            SPAWNER.tick();
        }

        if (EXPLO.isShowing()) {
            EXPLO.tickFrame();
        }

        if (!missileMngr.isEmpty()) {
            missileMngr.update(px, py);
        }
    }

    /**
     * Moving all the enemies that have been spawned.
     *
     * @param SPAWNER the enemy spawner
     * @param px player x
     * @param py player y
     * @param missileMngr missile manager
     */
    private void enemyMovement(final EnemySpawner SPAWNER, float px, float py, final MissileManager missileMngr) {
        for (int i = 0; i < SPAWNER.getEnemySize(); i++) {

            Enemy e = SPAWNER.getEnemy(i);
            if (!e.isBeingAbsored()) {
                SPAWNER.getEnemy(i).move(px, py);
            } // absorbed enemy movement
            else {
                Glob g = (Glob) SPAWNER.getGlob(e.getGlobPosition());

                e.move(g.getCenterX(), g.getCenterY());
                e.tickAbsorbCount();

                if (e.isAbsorbCount() && g.isFacingPlayer()) {
                    e.endAbsorb(g.getTargetRotation());
                }
            }
        }

        for (int i = 0; i < SPAWNER.getGlobSize(); i++) {
            Glob g = SPAWNER.getGlob(i);
            g.move(px, py);
            if (g.isMissileCreated()) {
                missileMngr.addBullet(g.getMissile());
            }

        }
    }

    /**
     * Un pause the game.
     */
    public void unPause() {
        GAMEDATA.setPaused(false);
        LBLS[2].setVisible(false); //pause label
    }

    /**
     * Drawing game objects.
     *
     * @param g Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        final Player PLAYER = GAMEOBJECTS.getPlayer();
        final PowerUp POWERUP = GAMEOBJECTS.getPowerUp();
        final BulletManager bulletMngr = GAMEOBJECTS.getBulletManager();
        final MissileManager missileMngr = GAMEOBJECTS.getMissileManager();
        final EnemySpawner SPAWNER = GAMEOBJECTS.getSpawner();
        final Explosion EXPLO = GAMEOBJECTS.getExplosion();

        super.paintComponent(g);

        // Drawing Power up
        if (POWERUP.isUp()) {
            g.drawImage(POWERUP.getImage(), POWERUP.getXCord(), POWERUP.getYCord(), this);
        }

        // Drawing the player 
        AffineTransformOp wandop = getTransformedPlayer();
        if (!PLAYER.isAttacking()) {
            g.drawImage(wandop.filter(PLAYER.getImage(), null), (int) PLAYER.getCurrentX(), (int) PLAYER.getCurrentY(), null);
        } else {
            g.drawImage(wandop.filter(PLAYER.getAttackImage(), null), (int) PLAYER.getCurrentX(), (int) PLAYER.getCurrentY(), null);
        }

        // Drawing missiles
        Object[] m1 = missileMngr.getAll().toArray();

        for (Object m : m1) {
            rotatedSpawnDraw(g, (GlobMissile) m, ((GlobMissile) m).getCurrentRotation());
        }

        // drawing basic enemies
        for (int i = 0;
                i < SPAWNER.getEnemySize();
                i++) {
            Spawn e = SPAWNER.getEnemy(i);
            g.drawImage(e.getImage(), (int) e.getCurrentXCord(), (int) e.getCurrentYCord(), this);

        }

        // Drawing globs
        for (int i = 0;
                i < SPAWNER.getGlobSize();
                i++) {
            Spawn e = SPAWNER.getGlob(i);
            rotatedSpawnDraw(g, e, ((Glob) e).getCurrentRotation());

        }

        // Drawing explosions 
        List<int[]> et = EXPLO.getExploTracker();
        for (int i = 0;
                i < et.size();
                i++) {
            g.drawImage(EXPLO.getImage(et.get(i)[3]), et.get(i)[0], et.get(i)[1], this);
        }

        // Drawing bullets
        Object[] bl = bulletMngr.getAll().toArray();
        BufferedImage img;
        for (Object b : bl) {
            img = BULLETDATA.getImage(((Bullet) b).getImageType());
            g.drawImage(img, (int) ((Bullet) b).getCurrentXCord(), (int) ((Bullet) b).getCurrentYCord(), this);
        }

        updateBackground();
        GAMEDATA.tickFrameCount();
    }

    /**
     * draw a spawn object rotated at a given degree.
     *
     * @param g graphics
     * @param spawn a spawn object
     * @param rotation a rotation in degrees
     */
    private void rotatedSpawnDraw(Graphics g, Spawn spawn, double rotation) {
        double degree = Math.toRadians(rotation);
        int x = spawn.getImageWidth() / 2;
        int y = spawn.getImageHeight() / 2;

        AffineTransform globtx = AffineTransform.getRotateInstance(degree, x, y);
        AffineTransformOp globop = new AffineTransformOp(globtx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(globop.filter(spawn.getImage(), null), (int) spawn.getCurrentXCord(), (int) spawn.getCurrentYCord(), this);
    }

    /**
     * Get the transformed player image according to players head degree.
     *
     * @return affine transform op rotated to players head degree
     */
    private AffineTransformOp getTransformedPlayer() {
        final Player PLAYER = GAMEOBJECTS.getPlayer();
        final int PLAYERIMGWIDTH = PLAYER.getImage().getHeight();
        final int PLAYERIMGHEIGHT = PLAYER.getImage().getWidth();
        final int OFFSET = 0;

        double pr = Math.toRadians(PLAYER.getCurrentHeadDegree());
        double px = PLAYERIMGWIDTH / 2;
        double py = (PLAYERIMGHEIGHT / 2) + OFFSET;

        AffineTransform wandtx = AffineTransform.getRotateInstance(pr, px, py);
        return new AffineTransformOp(wandtx, AffineTransformOp.TYPE_BILINEAR);
    }

    /**
     * Change the background color if necessary.
     */
    private void updateBackground() {
        if (GAMEOBJECTS.getPlayer().isHurt()) {
            if (!this.getBackground().equals(ColorDataHolder.getHurtColor())) {
                this.setBackground(ColorDataHolder.getHurtColor());
            }
        } else if (!this.getBackground().equals(ColorDataHolder.getBackgroundColor())) {
            this.setBackground(ColorDataHolder.getBackgroundColor());
        }
    }
}
