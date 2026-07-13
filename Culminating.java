package TileScroller;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static BufferedImage img;

    static final int NO_TIMER_DOOR = 0;
    static final int FRAMES_PER_SECOND = 30;

    static int maxCoinDrop = 4;
    static int minCoinDrop = 1;

    static boolean debugging = false;

    static int maxGhostAmount = 15;

    static int state = 0;

    static final int HERO = 0;
    static final int MENU = 1;
    static final int PLAYING = 2;
    static final int WIN = 3;

    static final int WIDTH = 1280; 
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final long SECONDS_TO_NANO = 1000000000L;

    static int currentRows;
    static int currentCols;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 80;

    static final int STARTING_XOFFSET = 0;
    static final int STARTING_YOFFSET = 0;
    static final int STARTING_PLAYERXOFFSET = TILE_SIZE * 7;
    static final int STARTING_PLAYERYOFFSET = TILE_SIZE * 1;

    static final int CAMERA_SPEED = 5;

    static final int REWIND_SPEED = 10;

    static final int SHOP_WIDTH = 450;
    static final int SHOP_HEIGHT = 425;

    static int coins = 0;

    static boolean clicked = false;

    static long secondTime = 20L;

    static long resetTime = secondTime * SECONDS_TO_NANO;

    static Tile[][] map = new Tile[currentRows][currentCols];

    static Player player = new Player(PLAYER_SIZE, CAMERA_SPEED);

    static int xOffset = 0;
    static int yOffset = 0;

    static int playerWorldX = WIDTH / 2 - PLAYER_SIZE / 2 - xOffset - player.playerXOffset;
    static int playerWorldY = HEIGHT / 2 - PLAYER_SIZE / 2 - yOffset - player.playerYOffset;

    static CollisionChecker CollisionChecker;

    static boolean goingUp = false;
    static boolean goingDown = false;
    static boolean goingLeft = false;
    static boolean goingRight = false;

    static int mouseX;
    static int mouseY;

    static int ghostStart = 0;

    static long startTime;
    static long currentTime;
    static double elapsedTime;
    static int rewindIndex;
    static int rewindCount = 0;
    static int finalRewinds = 0;

    static boolean playerDying = false;

    static volatile Clip rewindClip;
    static boolean rewindSoundPlaying = false;

    static volatile Clip backgroundAudioClip;

    static volatile Clip deathClip;

    static boolean shopOpen = false;

    static ArrayList<Ghost> ghosts = new ArrayList<>();

    static ArrayList<ArrayList<Movement>> movementHistory = new ArrayList<>();

    static ArrayList<Items> items = new ArrayList<>();

    static ArrayList<Door> doors = new ArrayList<>();

    static ArrayList<Enemy> enemies = new ArrayList<>();

    static Shop shop = new Shop();

    static boolean increasedTime = false;
    static int plusOneBrightness = 0;
    
    static boolean increasedGhost = false;
    static int plusGhostBrightness = 0;

    static boolean rewinding = false;

    static {
        player.playerXOffset = STARTING_PLAYERXOFFSET;
        player.playerYOffset = STARTING_PLAYERYOFFSET;
        movementHistory.add(new java.util.ArrayList<>());
    }

    static Level[] levels;

    static int currentLevelIndex = 0;

    static int unlockedLevels = 1;

    static boolean interactHeld = false;
    public static void main(String[] args) {
        JFrame frame = new JFrame("👾");
        Culminating game = new Culminating();
        Culminating.CollisionChecker = new CollisionChecker();
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        game.addKeyListener(game);
        game.addMouseListener(game);
        game.addMouseMotionListener(game);
        game.requestFocus();

        game.createBufferStrategy(3);
        BufferStrategy bs = game.getBufferStrategy();

        new Thread(() -> {
            player.loadSounds();
            loadAudio();
        }, "audio-loader").start();

        loadAllLevels();
        loadCurrentLevel();

        while (true) {
            // 1. Logic (Thinking)
            update();


            // 2. Rendering (Showing)
            Graphics g = bs.getDrawGraphics();
            Graphics2D g2d = (Graphics2D) g;

            // Turn on Anti-Aliasing for smooth edges
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            draw(g2d);

            g.dispose();
            bs.show();

            // 3. Timing
            try {
                Thread.sleep(FRAME_DELAY);
            }
            catch (Exception e) {
            }
        }
    }

    public static void update() {
        switch (state)
        {
            case PLAYING:
            {
                if (rewinding)
                {
                    rewinding();
                }
                else
                {
                    playing();
                }
                break;  
            }
            case HERO:
            {
                break;
            }
        }
    }

    public static void draw(Graphics2D g2d) {

        switch (state)
        {
            case HERO:
            {
                drawHero(g2d);
                break;
            }
            case MENU:
            {
                drawMenu(g2d);
                break;
            }
            case PLAYING:
            {
                drawPlaying(g2d);
                break;
            }
            case WIN:
            {
                drawWin(g2d);
                break;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        clicked = true;

    }

    public void mouseReleased(MouseEvent e) {
        clicked = false;
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (!rewinding && !interactHeld && !playerDying && !player.attacking && state == PLAYING)
        {
            if (e.getKeyCode() == KeyEvent.VK_Q) shopOpen = !shopOpen;

            if (!shopOpen)
            {
                if (e.getKeyCode() == KeyEvent.VK_S) goingDown = true;
                if (e.getKeyCode() == KeyEvent.VK_W) goingUp = true;
                if (e.getKeyCode() == KeyEvent.VK_A) goingLeft = true;
                if (e.getKeyCode() == KeyEvent.VK_D) goingRight = true;

                if (e.getKeyCode() == KeyEvent.VK_DOWN) goingDown = true;
                if (e.getKeyCode() == KeyEvent.VK_UP) goingUp = true;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) goingLeft = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) goingRight = true;

                if (e.getKeyCode() == KeyEvent.VK_E)
                {
                    interactHeld = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE && !player.attacking) 
                {
                    player.attacking = true;

                    goingUp = false;
                    goingDown = false;
                    goingLeft = false;
                    goingRight = false;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_0) debugging = !debugging;
        }

        if (state == HERO && e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            state = MENU;
        }

        if (state == MENU && e.getKeyCode() == KeyEvent.VK_R)
        {
            startTime = System.nanoTime();
            state = PLAYING;            
        }

        
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) goingDown = false;
        if (e.getKeyCode() == KeyEvent.VK_W) goingUp = false;
        if (e.getKeyCode() == KeyEvent.VK_A) goingLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_D) goingRight = false;
        
        if (e.getKeyCode() == KeyEvent.VK_DOWN) goingDown = false;
        if (e.getKeyCode() == KeyEvent.VK_UP) goingUp = false;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) goingLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) goingRight = false;

        if (e.getKeyCode() == KeyEvent.VK_E)
        { 
            interactHeld = false; 
        }
    }

    static void loadAllLevels()
    {
        levels = new Level[2];
        levels[0] = createLevel(1);
        levels[1] = createLevel(2);

    }

    static Level createLevel(int levelId)
    {
        int width = 0;
        int height = 0;
        if (levelId == 1)
        {
            width = 100;
            height = 100;
        }
        else if (levelId == 2)
        {
            width = 15;
            height = 26;
        }
        Level level = new Level("Jungle Escape", 20, width, height);
        loadMap(level.map, levelId);
        spawnEnemies(level.enemies, levelId);
        spawnItems(level.items, levelId);
        spawnDoors(level.doors, levelId);

        return level;
    }

    static void loadCurrentLevel()
    {
        Level level = levels[currentLevelIndex];

        map = level.map;
        enemies = level.enemies;
        items = level.items;
        doors = level.doors;

        currentRows = level.map.length;
        currentCols = level.map[0].length;
    }

    public static void loadMap(Tile[][] levelMap, int levelId)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Culminating.class.getResourceAsStream("/TileScroller/levels/levelGrid" + levelId + ".txt")));
            String line = br.readLine();
            int j = 0;

            while (line != null)
            {
                int i = 0;
                String character[] = line.split(",");
                while (i < character.length)
                {
                    levelMap[j][i] = new Tile(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, character[i]);
                    i++;
                }
                line = br.readLine();
                j++;
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawnEnemies(ArrayList<Enemy> enemies, int levelId)
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Culminating.class.getResourceAsStream("/TileScroller/levels/enemies" + levelId + ".txt"))))
        {
            br.readLine();
            
            String line = br.readLine();

            while (line != null)
            {
                String[] enemyInfo = line.split(",");

                enemies.add(new Enemy(Integer.valueOf(enemyInfo[0].trim()), Integer.valueOf(enemyInfo[1].trim()), Integer.valueOf(enemyInfo[2].trim()), Integer.valueOf(enemyInfo[3].trim()), Integer.valueOf(enemyInfo[4].trim()), enemyInfo[5].trim()));

                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void spawnItems(ArrayList<Items> items, int levelId)
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Culminating.class.getResourceAsStream("/TileScroller/levels/items" + levelId + ".txt"))))
        {
            br.readLine();

            String line = br.readLine();

            while (line != null)
            {
                String[] itemInfo = line.split(",");

                Color color = (Color) Color.class.getField(itemInfo[0].trim()).get(null);

                items.add(new Items(color, Integer.valueOf(itemInfo[1].trim()) * TILE_SIZE, Integer.valueOf(itemInfo[2].trim()) * TILE_SIZE, itemInfo[3].trim()));

                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    public static void spawnDoors(ArrayList<Door> doors, int levelId)
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Culminating.class.getResourceAsStream("/TileScroller/levels/doors" + levelId + ".txt"))))
        {
            br.readLine();

            String line = br.readLine();

            while (line != null)
            {
                String[] doorInfo = line.split(",");

                if (doorInfo[6].trim().equals("NO_TIMER_DOOR"))
                {
                    doorInfo[6] = "0";
                }

                doors.add(new Door(Integer.valueOf(doorInfo[0].trim()) * TILE_SIZE, Integer.valueOf(doorInfo[1].trim()) * TILE_SIZE, Integer.valueOf(doorInfo[2].trim()) * TILE_SIZE, Integer.valueOf(doorInfo[3].trim()) * TILE_SIZE, doorInfo[4].trim(), Boolean.valueOf(doorInfo[5].trim()), Integer.valueOf(doorInfo[6].trim())));
            
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAudio()
    {
        try 
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource("TileScroller/assets/rewind.wav"));
            rewindClip = AudioSystem.getClip();
            rewindClip.open(audioInputStream);
        } 
        catch (Exception e)
        {
            System.out.println("REWIND SOUND WRONG");
        }

        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource("TileScroller/assets/backgroundSound.wav"));
            backgroundAudioClip = AudioSystem.getClip();
            backgroundAudioClip.open(audioInputStream);
            backgroundAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundAudioClip.start();
        }
        catch (Exception e)
        { 
            System.out.println("BG SOUND FAIL!!"); 
        }
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource("TileScroller/assets/enemyDeath.wav"));
            deathClip = AudioSystem.getClip();
            deathClip.open(audioInputStream);
        }
        catch (Exception e)
        { 
            System.out.println("DEATH SOUND FAIL!!"); 
        }
    }

    public static void rewinding()
    {
        shopOpen = false;
        if (playerDying) return;

        if (!rewindSoundPlaying && rewindClip != null)
        {
            rewindClip.setFramePosition(0);
            rewindClip.loop(Clip.LOOP_CONTINUOUSLY);
            rewindSoundPlaying = true;
        }

        goingLeft = false;
        goingRight = false;
        goingUp = false;
        goingDown = false;
        
        for (int i = 0; i < REWIND_SPEED; i++)
        {
            if (rewindIndex > 0)
            {
                rewindIndex--;
                Movement movement = movementHistory.get(rewindCount).get(rewindIndex);
                player.playerXOffset += movement.playerX;
                player.playerYOffset += movement.playerY;
                xOffset += movement.cameraX;
                yOffset += movement.cameraY;
                player.lastDirection = movement.facing;
                player.attacking = movement.attacking;
                if (movement.interacted)
                {
                    for (Items item : items)
                    {
                        if (item.isTouchingPlayer(player))
                        {
                            //Culminating.player.playInteractSound();
                            item.activated = true;
                        }
                    }
                }
            }
            else
            {
                rewindReset();
                break;
            }
        }
    }

    public static void rewindReset()
    {
        startTime = System.nanoTime();
        ghosts.add(new Ghost(rewindCount, PLAYER_SIZE)); 
        rewindCount++;
        movementHistory.add(new java.util.ArrayList<>()); 
                            
        for (Ghost ghost : ghosts)
        {
            ghost.i = 0;
            ghost.ghostX = STARTING_PLAYERXOFFSET;
            ghost.ghostY = STARTING_PLAYERYOFFSET;
            ghost.ghostCameraX = 0;
            ghost.ghostCameraY = 0;
            ghost.finished = false;
            ghost.isDead = false;
        }

        player.directionX = Player.STANDARD_ATTACK_OFFSET;
        player.directionY = player.directionDown;

        for (Items item : items)
        {
            item.activated = false;
        }
        for (Enemy enemy : enemies)
        {
            enemy.reset();
        }
        for (Door door : doors)
        {
            door.startTime = door.currentTime-FRAMES_PER_SECOND/2;
            door.isOpen = (door.startPosition.equals("Closed") ? false : true);
                    
            door.startTime = 0;  
            door.currentTime = 0;     
            door.elaspedTime = 0; 
        }
                            
        player.reset();

        if (Shop.addGhost)
        {
            maxGhostAmount += Shop.addGhostAmount;
            Shop.addGhostAmount = 0;
            Shop.addGhost = false;
        }

        rewinding = false;

        if (backgroundAudioClip != null)
        {
            backgroundAudioClip.stop();
            backgroundAudioClip.setFramePosition(0);
            backgroundAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundAudioClip.start();
        }
    }

    public static void playing()
    {
        if (rewindSoundPlaying && rewindClip != null)
        {
            rewindClip.stop();
            rewindSoundPlaying = false;
        }

        Movement frameMovement = new Movement(0, 0, 0, 0, false, false, null);
        playerWorldX = WIDTH / 2 - PLAYER_SIZE / 2 - xOffset - player.playerXOffset;
        playerWorldY = HEIGHT / 2 - PLAYER_SIZE / 2 - yOffset - player.playerYOffset;

        for (Items item : items)
        {
            item.activated = false;
        }

        updateGhosts();

        for (Enemy enemy : enemies)
        {
            enemy.update();
        }

        updateInteracting(frameMovement);

        for (Door door : doors)
        {
            door.update();
        }
                    
        clampCamera();
        updatePlayerMovement(frameMovement);

        for (Door door : doors)
        {
            if (!door.isOpen && player.getBounds(WIDTH, HEIGHT).intersects(new Rectangle(door.x + xOffset, door.y + yOffset, door.width, door.height)))
            {
                pushOut(door, frameMovement);
            }
        }

        playerBounds();
                    
        if (player.attacking)
        {
            frameMovement.attacking = true;
        }

        if (goingUp) frameMovement.facing = "Up";
        if (goingDown) frameMovement.facing = "Down";
        if (goingLeft) frameMovement.facing = "Left";
        if (goingRight) frameMovement.facing = "Right";
                    
        movementHistory.get(rewindCount).add(frameMovement);

        playerDeathCheck();
        ghostDeathCheck();
        timerUpdate();
    }

    public static void updateGhosts()
    {
        ghostStart = 0;

        if (ghosts.size() - maxGhostAmount > 0)  ghostStart = ghosts.size() - maxGhostAmount;

        for (int i = ghostStart; i < ghosts.size(); i++)
        {
            ghosts.get(i).update();
        }
    }

    public static void updateInteracting(Movement frameMovement)
    {
        if (interactHeld)
        {
            for (Items item : items)
            {
                if (item.isTouchingPlayer(player))
                {
                    item.activated = true;
                    frameMovement.interacted = true;
                    frameMovement.interactedItemId = item.id;

                    player.playInteractSound();
                }
            }
        }
    }

    public static void clampCamera()
    {
        if (xOffset > 0) xOffset = 0;
        if (yOffset > 0) yOffset = 0;
        if (xOffset < -(currentRows*TILE_SIZE - WIDTH)) xOffset = -(currentRows*TILE_SIZE - WIDTH);
        if (yOffset < -((currentCols)*TILE_SIZE - HEIGHT)) yOffset = -((currentCols)*TILE_SIZE - HEIGHT);
    }

    public static void updatePlayerMovement(Movement frameMovement)
    {
        if (xOffset == 0 && goingLeft && CollisionChecker.canMove(player, -CAMERA_SPEED, 0)) 
        {
            player.playerXOffset += CAMERA_SPEED;
            frameMovement.playerX -= CAMERA_SPEED;
        }
        if (yOffset == 0 && goingUp && CollisionChecker.canMove(player, 0, -CAMERA_SPEED))
        {
            player.playerYOffset += CAMERA_SPEED;
            frameMovement.playerY -= CAMERA_SPEED;
        }
        if (xOffset == -(currentRows * TILE_SIZE - WIDTH) && goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
        {
            player.playerXOffset -= CAMERA_SPEED;
            frameMovement.playerX += CAMERA_SPEED;
        }
        if (yOffset == -(currentCols * TILE_SIZE - HEIGHT) && goingDown && CollisionChecker.canMove(player, 0, CAMERA_SPEED))
        {
            player.playerYOffset -= CAMERA_SPEED;
            frameMovement.playerY += CAMERA_SPEED;
        }
        if (goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
        {
            if (player.playerXOffset > 0) 
            {
                player.playerXOffset -= CAMERA_SPEED; 
                frameMovement.playerX += CAMERA_SPEED;
            }
            else if (player.playerXOffset == 0)
            {
                xOffset -= CAMERA_SPEED; 
                frameMovement.cameraX += CAMERA_SPEED; 
            }
        }
        if (goingDown && CollisionChecker.canMove(player, 0, CAMERA_SPEED))
        {
            if (player.playerYOffset > 0)
            { 
                player.playerYOffset -= CAMERA_SPEED; 
                frameMovement.playerY += CAMERA_SPEED; 
            }
            else if (player.playerYOffset == 0)
            { 
                yOffset -= CAMERA_SPEED; 
                frameMovement.cameraY += CAMERA_SPEED; 
            }
        }
        if (goingLeft && CollisionChecker.canMove(player, -CAMERA_SPEED, 0))
        {
            if (player.playerXOffset < 0)
            { 
                player.playerXOffset += CAMERA_SPEED; 
                frameMovement.playerX -= CAMERA_SPEED; 
            }
            else if (player.playerXOffset == 0)
            { 
                xOffset += CAMERA_SPEED; 
                frameMovement.cameraX -= CAMERA_SPEED; 
            }
        }
        if (goingUp && CollisionChecker.canMove(player, 0, -CAMERA_SPEED))
        {
            if (player.playerYOffset < 0)
            { 
                player.playerYOffset += CAMERA_SPEED; 
                frameMovement.playerY -= CAMERA_SPEED; 
            }
            else if (player.playerYOffset == 0) 
            { 
                yOffset += CAMERA_SPEED; 
                frameMovement.cameraY -= CAMERA_SPEED; 
            }
        }
    }

    public static void playerBounds()
    {
        if (player.playerXOffset > (WIDTH-PLAYER_SIZE)/2) player.playerXOffset = (WIDTH-PLAYER_SIZE)/2;
        if (player.playerYOffset > (HEIGHT-PLAYER_SIZE)/2) player.playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
        if (player.playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) player.playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
        if (player.playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) player.playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);
    }

    public static void playerDeathCheck()
    {
        Rectangle playerBounds = player.getBounds(WIDTH, HEIGHT);
        for (int i = 0; i < currentRows; i++)
        {
            for (int j = 0; j < currentCols; j++)
            {
                Tile tile = map[i][j];

                if (tile != null && tile.isLava())
                {
                    Rectangle tileBounds = tile.getBounds(xOffset, yOffset);
                    if (playerBounds.intersects(tileBounds))
                    {    
                        if (!playerDying) 
                        {
                            playerDying = true;
                            player.deathFrame = 0;
                            player.lastDeathFrame = System.nanoTime();
                            goingUp = false;
                            goingDown = false;
                            goingLeft = false;
                            goingRight = false;
                        }
                        rewinding = true;
                        rewindIndex = movementHistory.get(rewindCount).size();
                    }
                }
                if (tile != null && tile.isEndZone())
                {
                    Rectangle tileBounds = tile.getBounds(xOffset, yOffset);

                    if (playerBounds.intersects(tileBounds))
                    {
                        state = WIN;
                        currentLevelIndex++;
                        unlockedLevels = Math.max(unlockedLevels, currentLevelIndex + 1);

                        stopWalkSound();

                        finalRewinds = rewindCount;
                        goingUp = false;
                        goingDown = false;
                        goingLeft = false;
                        goingRight = false;
                        return;
                    }
                }
            }
        }

        for (Enemy enemy : enemies)
        {
            if (enemy.getBounds().intersects(playerBounds) && !enemy.dead)
            {
                if (!playerDying)
                {
                    playerDying = true;
                    player.deathFrame = 0;
                    player.lastDeathFrame = System.nanoTime();
                    goingUp = goingDown = goingLeft = goingRight = false;
                }
                rewinding = true;
                rewindIndex = movementHistory.get(rewindCount).size();
            }
        }
    }

    public static void stopWalkSound()
    {
        if (player.walkClip != null)
        {
            player.walkClip.stop();
            player.walkClip.setFramePosition(0);
            player.walkSoundPlaying = false;
        }
    }

    public static void ghostDeathCheck()
    {
        for (Ghost ghost : ghosts)
        {
            for (int i = 0; i < currentRows; i++)
            {
                for (int j = 0; j < currentCols; j++)
                {
                    Tile tile = map[i][j];

                    if (tile != null && tile.isLava())
                    {
                        if (ghost.getBounds(WIDTH, HEIGHT, xOffset, yOffset).intersects(tile.getBounds(xOffset, yOffset)))
                        {
                            ghost.isDead = true;
                        }
                    }
                }
            }

            for (Enemy enemy : enemies)
            {
                if (enemy.getBounds().intersects(ghost.getBounds(WIDTH, HEIGHT, xOffset, yOffset)) && !enemy.dead)
                {
                    ghost.isDead = true;
                }
            }
        }
    }

    public static void timerUpdate()
    {
        currentTime = System.nanoTime();
        elapsedTime = (resetTime - (currentTime - startTime))/ (double) SECONDS_TO_NANO;

        if (!rewinding && (currentTime - startTime) >= resetTime)
        {
            if (!playerDying)
            {
                playerDying = true;
                player.deathFrame = 0;
                player.lastDeathFrame = System.nanoTime();
                goingUp = false;
                goingDown = false;
                goingLeft = false;
                goingRight = false;
            }
            rewinding = true;
            rewindIndex = movementHistory.get(rewindCount).size();
        }
    }
    
    public static void pushOut(Door door, Movement frameMovement)
    {
        Rectangle playerBounds = player.getBounds(WIDTH, HEIGHT);

        Rectangle doorBounds = new Rectangle(door.x + xOffset, door.y + yOffset, door.width, door.height);

        if (!playerBounds.intersects(doorBounds)) return;


        Rectangle intersection = playerBounds.intersection(doorBounds);

        if (door.type.equals("vertical"))
        {
            if (playerBounds.x < doorBounds.x)
            {
                if (player.playerXOffset > 0 || xOffset == 0)
                {
                    player.playerXOffset += intersection.width;
                    frameMovement.playerX -= intersection.width;
                }
                else
                {
                    xOffset += intersection.width;
                    frameMovement.cameraX -= intersection.width;
                }
            }
            else
            {
                if (player.playerXOffset < 0 || xOffset == -(currentRows * TILE_SIZE - WIDTH))
                {
                    player.playerXOffset -= intersection.width;
                    frameMovement.playerX += intersection.width;
                }
                else
                {
                    xOffset -= intersection.width;
                    frameMovement.cameraX += intersection.width;
                }
            }
        }
        else if (door.type.equals("horizontal"))
        {
            if (playerBounds.y < doorBounds.y)
            {
                if (player.playerYOffset > 0 || yOffset == 0)
                {
                    player.playerYOffset += intersection.height;
                    frameMovement.playerY += intersection.height;
                }
                else
                {
                    yOffset += intersection.height;
                    frameMovement.cameraY -= intersection.height;
                }
            }
            else
            {
                if (player.playerYOffset < 0 || yOffset == -(currentCols * TILE_SIZE - HEIGHT))
                {
                    player.playerYOffset -= intersection.height;
                    frameMovement.playerY -= intersection.height;
                }
                else
                {
                    yOffset -= intersection.height;
                    frameMovement.cameraY += intersection.height;
                }
            }
        }
    }

    public static void drawHero(Graphics2D g2d)
    {
        g2d.setColor(new Color(28, 20, 12));        
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        drawHeroTitle(g2d);
        drawHeroButton(g2d);
    }

    public static void drawHeroTitle(Graphics2D g2d)
    {
        g2d.setFont(new Font("Serif", Font.ITALIC, 72));
        g2d.setColor(new Color(160, 146, 74));
        FontMetrics titleFont = g2d.getFontMetrics();
        String title = "~ It's About Time ~";
        g2d.drawString(title, WIDTH / 2 - titleFont.stringWidth(title) / 2, HEIGHT / 2 - 80);

        g2d.setFont(new Font("Serif", Font.ITALIC, 20));
        g2d.setColor(new Color(140, 120, 70));
        FontMetrics subFont = g2d.getFontMetrics();
        String subtitle = "Join Us Eternally";
        g2d.drawString(subtitle, WIDTH / 2 - subFont.stringWidth(subtitle) / 2, HEIGHT / 2 - 40);
    }

    public static void drawHeroButton(Graphics2D g2d)
    {
        int buttonWidth = 240;
        int buttonHeight = 50;
        int buttonX = WIDTH / 2 - buttonWidth / 2;
        int buttonY = HEIGHT / 2 + 10;
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

        boolean isHovered = button.contains(mouseX, mouseY);

        buttonX = isHovered ? buttonX - 3 : buttonX;
        buttonY = isHovered ? buttonY - 1 : buttonY;
        buttonWidth = isHovered ? buttonWidth + 6 : buttonWidth;
        buttonHeight = isHovered ? buttonHeight + 2 : buttonHeight;

        g2d.setColor(isHovered ? new Color(95, 72, 28) : new Color(46, 37, 25));
        g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);

        g2d.setColor(isHovered ? new Color(160, 130, 50) : new Color(107, 90, 62));
        g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 6, 6);
        g2d.drawRoundRect(buttonX + 2, buttonY + 2, buttonWidth - 4, buttonHeight - 4, 6, 6);

        g2d.setFont(new Font("Bahnschrift", Font.BOLD, 18));
        g2d.setColor(new Color(200, 176, 104));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Enter the Jungle";
        g2d.drawString(text, buttonX + buttonWidth / 2 - fm.stringWidth(text) / 2, buttonY + 32);

        if (clicked && button.contains(mouseX, mouseY))
        {
            startTime = System.nanoTime();
            state = MENU;
            clicked = false;
        }
    }

    public static void drawMenu(Graphics2D g2d)
    {
        g2d.setColor(new Color(28, 20, 12));        
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        drawMenuTitle(g2d);
        drawLevelPath(g2d);
    }

    public static void drawMenuTitle(Graphics2D g2d)
    {
        g2d.setFont(new Font("Serif", Font.ITALIC, 72));
        g2d.setColor(new Color(160, 146, 74));
        FontMetrics titleFont = g2d.getFontMetrics();
        String title = "~ It's About Time ~";
        g2d.drawString(title, WIDTH / 2 - titleFont.stringWidth(title) / 2, HEIGHT / 2 - 220);
    }

    public static void drawLevelPath(Graphics2D g2d)
    {
        int levelCount = levels.length;

        

        int boxSize = 100;

        int margin = 150;

        int boxX = (WIDTH-(boxSize + (levelCount - 1) * (boxSize + margin)))/2;
        int boxY = HEIGHT/2 + 60;


        for (int i = 0; i < levelCount; i++)
        {
            boolean unlocked = i < unlockedLevels;


            Rectangle button = new Rectangle(boxX, boxY + ((int) (Math.sin(i * 0.7) * 100)), boxSize, boxSize);

            g2d.setColor(!unlocked ? new Color(30, 26, 20) : (button.contains(mouseX, mouseY) ? new Color(95, 72, 28) : new Color(46, 37, 25)));

            g2d.fillRoundRect(boxX, boxY + ((int) (Math.sin(i * 0.7) * 100)), boxSize, boxSize, 10, 10);

            g2d.setColor(new Color(46, 37, 25));
            if (i != levelCount - 1)
            {
                g2d.drawLine(boxX + boxSize, boxY + ((int) (Math.sin(i * 0.7) * 100)) + boxSize/2, boxX + boxSize + margin,  boxY + ((int) (Math.sin((i + 1) * 0.7) * 100)) + boxSize/2);
            }

            if (clicked && unlocked && button.contains(mouseX, mouseY))
            {
                resetGame();
                currentLevelIndex = i;
                loadCurrentLevel();
                startTime = System.nanoTime();
                state = PLAYING;
                clicked = false;
            }

            boxX += boxSize + margin;
        }


    }
    
    public static void drawPlaying(Graphics2D g2d)
    {
        drawTiles(g2d);

        for (Items item : items)
        {
            item.draw(g2d, xOffset, yOffset);
        }

        for (Door door : doors)
        {
            door.draw(g2d, xOffset, yOffset);
        }

        for (Enemy enemy : enemies)
        {
            enemy.draw(g2d, xOffset, yOffset);
        }
        drawGhosts(g2d);
        player.draw(g2d, WIDTH, HEIGHT);

        drawText(g2d);

        if (shopOpen)
        {
            shop.update(g2d, SHOP_WIDTH, SHOP_HEIGHT);
        }
    }

    public static void drawTiles(Graphics2D g2d)
    {
        for (int i = 0; i < currentRows; i++)
        {
            for (int j = 0; j < currentCols; j++)
            {
                Tile tile = map[i][j];

                if (tile != null) 
                {
                    int x = tile.x + xOffset;
                    int y = tile.y + yOffset;

                    if (x + TILE_SIZE > 0 && x < WIDTH && y + TILE_SIZE > 0 && y < HEIGHT)
                    {
                        tile.draw(g2d, xOffset, yOffset);
                    }
                }
            }
        }
    }

    public static void drawGhosts(Graphics2D g2d)
    {
        g2d.setColor(Color.CYAN);
        int ghostDrawStart = 0;

        if (ghosts.size() - maxGhostAmount < 0) ghostDrawStart = 0;
        else ghostDrawStart = ghosts.size() - maxGhostAmount;
                
        for (int i = ghostDrawStart; i < ghosts.size(); i++)   
        {
            ghosts.get(i).draw(g2d, WIDTH, HEIGHT, xOffset, yOffset);
        }
    }

    public static void drawText(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Bahnschrift", Font.BOLD, 32));

        FontMetrics fontMetric = g2d.getFontMetrics();
        int timeWidth = fontMetric.stringWidth("Time: " + String.format("%.0f", Math.abs(elapsedTime)));

        int ghostWidth = fontMetric.stringWidth("Max Ghosts: " + (maxGhostAmount + Shop.addGhostAmount));

        g2d.drawString("Time: " + String.format("%.1f", Math.abs(elapsedTime)), 30, 50);

        g2d.drawString("Max Ghosts: " + (maxGhostAmount + Shop.addGhostAmount), 30, 150);
        if (increasedTime)
        {
            plusOneBrightness -= 5;
            if (plusOneBrightness <= 0)
            {
                plusOneBrightness = 0;
                increasedTime = false;
            }

            g2d.setColor(new Color(255,255,255, plusOneBrightness));
            g2d.drawString("+3", 65 + timeWidth, 50);
        }

        if (increasedGhost)
        {
            plusGhostBrightness -= 5;
            if (plusGhostBrightness <= 0)
            {
                plusGhostBrightness = 0;
                increasedGhost = false;
            }

            g2d.setColor(new Color(255,255,255, plusGhostBrightness));
            g2d.drawString("+1", 40 + ghostWidth, 150);
        }
        
        g2d.setColor(new Color(255,255,255));

        g2d.drawString("Coins: " + String.valueOf(coins), 30, 100);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Bahnschrift", Font.BOLD, 32));
        g2d.drawString("Rewinds: " + rewindCount, 30, 200);
    }

    public static void drawWin (Graphics2D g2d)
    {
        g2d.setColor(new Color(28, 20, 12));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setFont(new Font("Serif", Font.ITALIC, 72));
        g2d.setColor(new Color(160, 146, 74));
        String text = "~ You Escaped ~";
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int winWidth = fontMetrics.stringWidth(text);
        g2d.drawString(text, WIDTH / 2 - winWidth / 2, HEIGHT / 2 - 60);

        g2d.setFont(new Font("Serif", Font.ITALIC, 24));
        g2d.setColor(new Color(140, 120, 70));
        String stats = "Rewinds used: " + finalRewinds;
        int rewindWidth = g2d.getFontMetrics().stringWidth(stats);
        g2d.drawString(stats, WIDTH / 2 - rewindWidth / 2, HEIGHT / 2);

        drawWinButton(g2d);
        drawBackToMenuButton(g2d);
    }
    
    public static void drawWinButton(Graphics2D g2d)
    {
        Rectangle playAgainButton = new Rectangle(WIDTH/2 - 120, HEIGHT/2 + 40, 240, 60);

        if (playAgainButton.contains(mouseX, mouseY))
        {
            g2d.setColor(Color.GRAY);

            if (clicked)
            {
                resetGame();
            }
        }
        else
        {
            g2d.setColor(Color.DARK_GRAY);
        }
        
        g2d.fill(playAgainButton);

        g2d.setColor(Color.WHITE);
        g2d.draw(playAgainButton);

        String button = "PLAY AGAIN";
        int buttonWidth = g2d.getFontMetrics().stringWidth(button);

        g2d.drawString(button, playAgainButton.x + playAgainButton.width/2 - buttonWidth/2, playAgainButton.y + 40);
    }

    public static void drawBackToMenuButton(Graphics2D g2d)
    {
        Rectangle backToMenu = new Rectangle(WIDTH/2 - 120, HEIGHT/2 + 120, 240, 60);

        if (backToMenu.contains(mouseX, mouseY))
        {
            g2d.setColor(Color.GRAY);

            if (clicked)
            {
                state = MENU;
            }
        }
        else
        {
            g2d.setColor(Color.DARK_GRAY);
        }
        
        g2d.fill(backToMenu);

        g2d.setColor(Color.WHITE);
        g2d.draw(backToMenu);

        String button = "RETURN TO MENU";
        int buttonWidth = g2d.getFontMetrics().stringWidth(button);

        g2d.drawString(button, backToMenu.x + backToMenu.width/2 - buttonWidth/2, backToMenu.y + 40);
    }

    public static void resetGame()
    {
        state = PLAYING;

        rewindCount = 0;
        finalRewinds = 0;
        rewinding = false;
        playerDying = false;

        coins = 0;
        secondTime = 20L;
        resetTime = secondTime * SECONDS_TO_NANO;
            
        maxGhostAmount = 15;
        Shop.addGhostAmount = 0;

        maxCoinDrop = 4;
        minCoinDrop = 1;

        Shop.shopPrices = new int[] {3, 6, 15, 35};

        xOffset = 0;
        yOffset = 0;

        player.playerXOffset = STARTING_PLAYERXOFFSET;
        player.playerYOffset = STARTING_PLAYERYOFFSET;

        player.lastDirection = "Down";

        ghosts.clear();
        movementHistory.clear();
        movementHistory.add(new ArrayList<>());

        for (Items item : items) 
        {
            item.activated = false;
        }

        for (Enemy enemy : enemies) 
        {
            enemy.reset();
        }

        startTime = System.nanoTime();
    }

    public static java.net.URL resource(String path)
    {
        return Culminating.class.getResource("/" + path);
    }

    public static void playSound(String path)
    {
        try
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.start();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } 
    
    public static void playDeathSound()
    {
        if (deathClip == null) return;
        deathClip.stop();
        deathClip.setFramePosition(0);
        deathClip.start();
    }
}
