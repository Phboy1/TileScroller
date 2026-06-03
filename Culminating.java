package TileScroller;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static BufferedImage img;

    static final int NO_TIMER_DOOR = 0;
    static final int FRAMES_PER_SECOND = 30;

    static int state = 0;

    static final int MENU = 0;
    static final int PLAYING = 1;

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final long SECONDS_TO_NANO = 1000000000L;

    static final int rows = 100;
    static final int cols = 100;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 80;

    static final int STARTING_XOFFSET = 0;
    static final int STARTING_YOFFSET = 0;
    static final int STARTING_PLAYERXOFFSET = TILE_SIZE * 7;
    static final int STARTING_PLAYERYOFFSET = TILE_SIZE * 1;

    static int coins = 0;

    static final int CAMERA_SPEED = 5;

    static final long RESET_TIME = 30 * SECONDS_TO_NANO;

    static Tile[][] map = new Tile[rows][cols];

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

    static long startTime;
    static long currentTime;
    static double elapsedTime;
    static int rewindIndex;
    static int rewindCount = 0;

    static int maxHealth = 3;
    static int health = maxHealth;
    static boolean playerDying = false;

    static ArrayList<Ghost> ghosts = new ArrayList<>();

    static ArrayList<ArrayList<Movement>> movementHistory = new ArrayList<>();

    static ArrayList<Items> items = new ArrayList<>();

    static ArrayList<Door> doors = new ArrayList<>();

    static ArrayList<Enemy> enemies = new ArrayList<>();

    static Ghost possessedGhost = null;

    static boolean rewinding = false;
    static final int REWIND_SPEED = 4;

    static {
        player.playerXOffset = STARTING_PLAYERXOFFSET;
        player.playerYOffset = STARTING_PLAYERYOFFSET;
        movementHistory.add(new java.util.ArrayList<>());
    }

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

        try {
            BufferedReader br = new BufferedReader(new FileReader("TileScroller/TileGrid.txt"));
            String line = br.readLine();
            int j = 0;

            while (line != null)
            {
                int i = 0;
                String character[] = line.split(",");
                while (i < character.length)
                {
                    map[j][i] = new Tile(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, character[i]);
                    i++;
                }
                line = br.readLine();
                j++;
            }

            br.close();

        } catch (Exception e) {
            System.out.println("SOMETHING WENT WRONG WITH THE FILE!!!!!!");
        }

        

        items.add(new Items(Color.YELLOW, 8 * TILE_SIZE, 7 * TILE_SIZE, "A")); // Room A -> unlocks A->B
        items.add(new Items(Color.YELLOW, 25 * TILE_SIZE, 6 * TILE_SIZE, "B")); // Room B -> unlocks B->C
        items.add(new Items(Color.YELLOW, 22 * TILE_SIZE, 8 * TILE_SIZE, "D")); // Room B -> unlocks B->E
        items.add(new Items(Color.YELLOW, 43 * TILE_SIZE, 8 * TILE_SIZE, "C")); // Room C -> unlocks A->D
        items.add(new Items(Color.YELLOW, 40 * TILE_SIZE, 5 * TILE_SIZE, "E")); // Room C -> unlocks C->E
        items.add(new Items(Color.YELLOW, 47 * TILE_SIZE, 5 * TILE_SIZE, "F")); // Room C -> unlocks C->F
        items.add(new Items(Color.YELLOW, 8 * TILE_SIZE, 24 * TILE_SIZE, "G")); // Room D -> unlocks D->E
        items.add(new Items(Color.YELLOW, 30 * TILE_SIZE, 20 * TILE_SIZE, "H")); // Room E -> unlocks E->F
        items.add(new Items(Color.YELLOW, 35 * TILE_SIZE, 30 * TILE_SIZE, "I")); // Room E -> unlocks E->H
        items.add(new Items(Color.YELLOW, 62 * TILE_SIZE, 12 * TILE_SIZE, "J")); // Room F -> unlocks F->J
        items.add(new Items(Color.YELLOW, 62 * TILE_SIZE, 25 * TILE_SIZE, "K")); // Room F -> unlocks F->I
        items.add(new Items(Color.YELLOW, 10 * TILE_SIZE, 46 * TILE_SIZE, "L")); // Room G -> unlocks G->H
        items.add(new Items(Color.YELLOW, 40 * TILE_SIZE, 45 * TILE_SIZE, "M")); // Room H -> unlocks H->I
        items.add(new Items(Color.YELLOW, 70 * TILE_SIZE, 55 * TILE_SIZE, "N")); // Room I -> unlocks I->K
        items.add(new Items(Color.YELLOW, 3 * TILE_SIZE, 3 * TILE_SIZE, "t")); // Room I -> unlocks I->K

        doors.add(new Door(16 * TILE_SIZE, 5 * TILE_SIZE, TILE_SIZE, 5 * TILE_SIZE, "A", false, 4)); // A->B
        doors.add(new Door(34 * TILE_SIZE, 4 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "B", false, NO_TIMER_DOOR)); // B->C
        doors.add(new Door(7 * TILE_SIZE, 14 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "C", false, NO_TIMER_DOOR)); // A->D
        doors.add(new Door(24 * TILE_SIZE, 12 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "D", false, NO_TIMER_DOOR)); // B->E
        doors.add(new Door(42 * TILE_SIZE, 14 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "E", false, NO_TIMER_DOOR)); // C->E
        doors.add(new Door(52 * TILE_SIZE, 7 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "F", false, NO_TIMER_DOOR)); // C->F
        doors.add(new Door(16 * TILE_SIZE, 22 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "G", false, NO_TIMER_DOOR)); // D->E
        doors.add(new Door(52 * TILE_SIZE, 19 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "H", false, NO_TIMER_DOOR)); // E->F
        doors.add(new Door(39 * TILE_SIZE, 37 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "I", false, NO_TIMER_DOOR)); // E->H
        doors.add(new Door(70 * TILE_SIZE, 3 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "J", false, NO_TIMER_DOOR)); // F->J
        doors.add(new Door(64 * TILE_SIZE, 32 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "K", false, NO_TIMER_DOOR)); // F->I
        doors.add(new Door(22 * TILE_SIZE, 49 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "L", false, NO_TIMER_DOOR)); // G->H
        doors.add(new Door(60 * TILE_SIZE, 49 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "M", false, NO_TIMER_DOOR)); // H->I
        doors.add(new Door(74 * TILE_SIZE, 67 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "N", false, NO_TIMER_DOOR)); // I->K
        doors.add(new Door(5 * TILE_SIZE, 5 * TILE_SIZE, TILE_SIZE, 5 * TILE_SIZE, "t", false, NO_TIMER_DOOR)); // I->K

        enemies.add(new Enemy(10, 5, 20, 5, 2, "patrolling"));
        enemies.add(new Enemy(10, 6, 20, 6, 4, "patrolling"));
        enemies.add(new Enemy(5, 5, 0, 0, 1, "following"));
        enemies.add(new Enemy(20, 5, 0, 0, 2, "following"));

        System.out.println("EHLLO WORLD");
        

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
                    if (playerDying) return;
                    goingLeft = goingRight = goingUp = goingDown = false;
                    for (int i = 0; i < REWIND_SPEED; i++)
                    {
                        //Rewind Index: Frame that the rewind is on. REWIND_SPEED: Amount of frames that it rewinds by every time.
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
                                        item.activated = true;
                                    }
                                }
                            }
                        }
                        else
                        {
                            //Reset time, add new ghost, amount of ghosts gets increased, add a new list for the new ghost.
                            startTime = System.nanoTime();
                            ghosts.add(new Ghost(rewindCount, PLAYER_SIZE)); 
                            rewindCount++;
                            movementHistory.add(new java.util.ArrayList<>()); 
                            
                            //Resetting all ghosts
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

                            //Reset Player
                            player.directionX = Player.STANDARD_ATTACK_OFFSET;
                            player.directionY = player.directionDown;

                            //Reset items
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

                            
                            rewinding = false;

                            break;
                        }
                    }
                }
                else
                {
                    //Moving the player at the edges
                    Movement frameMovement = new Movement(0, 0, 0, 0, false, false, null);
                    playerWorldX = WIDTH / 2 - PLAYER_SIZE / 2 - xOffset - player.playerXOffset;
                    playerWorldY = HEIGHT / 2 - PLAYER_SIZE / 2 - yOffset - player.playerYOffset;


                    // Reset all items first
                    for (Items item : items)
                    {
                        item.activated = false;
                    }

                    //Update Ghosts only when NOT rewinding
                    for (Ghost ghost : ghosts)
                    {
                        ghost.update();
                    }
                    for (Enemy enemy : enemies)
                    {
                        enemy.update();
                    }

                    // Player votes true
                    if (interactHeld)
                    {
                        for (Items item : items)
                        {
                            if (item.isTouchingPlayer(player))
                            {
                                item.activated = true;
                                frameMovement.interacted = true;
                                frameMovement.interactedItemId = item.id;
                            }
                        }
                    }

                    for (Door door : doors)
                    {
                        door.update();
                    }
                    
                    //Moving the Background
                    if (xOffset > 0) xOffset = 0;
                    if (yOffset > 0) yOffset = 0;
                    if (xOffset < -(rows*TILE_SIZE - WIDTH)) xOffset = -(rows*TILE_SIZE - WIDTH);
                    if (yOffset < -((cols)*TILE_SIZE - HEIGHT)) yOffset = -((cols)*TILE_SIZE - HEIGHT);

                    

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
                    if (xOffset == -(rows * TILE_SIZE - WIDTH) && goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
                    {
                        player.playerXOffset -= CAMERA_SPEED;
                        frameMovement.playerX += CAMERA_SPEED;
                    }
                    if (yOffset == -(cols * TILE_SIZE - HEIGHT) && goingDown && CollisionChecker.canMove(player, 0, CAMERA_SPEED))
                    {
                        player.playerYOffset -= CAMERA_SPEED;
                        frameMovement.playerY += CAMERA_SPEED;
                    }
                    if (goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
                    {
                        if (player.playerXOffset > 0) { player.playerXOffset -= CAMERA_SPEED; frameMovement.playerX += CAMERA_SPEED; }
                        else if (player.playerXOffset == 0) { xOffset -= CAMERA_SPEED; frameMovement.cameraX += CAMERA_SPEED; }
                    }
                    if (goingDown && CollisionChecker.canMove(player, 0, CAMERA_SPEED))
                    {
                        if (player.playerYOffset > 0) { player.playerYOffset -= CAMERA_SPEED; frameMovement.playerY += CAMERA_SPEED; }
                        else if (player.playerYOffset == 0) { yOffset -= CAMERA_SPEED; frameMovement.cameraY += CAMERA_SPEED; }
                    }
                    if (goingLeft && CollisionChecker.canMove(player, -CAMERA_SPEED, 0))
                    {
                        if (player.playerXOffset < 0) { player.playerXOffset += CAMERA_SPEED; frameMovement.playerX -= CAMERA_SPEED; }
                        else if (player.playerXOffset == 0) { xOffset += CAMERA_SPEED; frameMovement.cameraX -= CAMERA_SPEED; }
                    }
                    if (goingUp && CollisionChecker.canMove(player, 0, -CAMERA_SPEED))
                    {
                        if (player.playerYOffset < 0) { player.playerYOffset += CAMERA_SPEED; frameMovement.playerY -= CAMERA_SPEED; }
                        else if (player.playerYOffset == 0) { yOffset += CAMERA_SPEED; frameMovement.cameraY -= CAMERA_SPEED; }
                    }

                    for (Door door : doors)
                    {
                        if (!door.isOpen && player.getBounds(WIDTH, HEIGHT).intersects(new Rectangle(door.x + xOffset, door.y + yOffset, door.width, door.height)))
                        {
                            pushOut(door, frameMovement);
                        }
                    }

                    

                    //Boundaries
                    if (player.playerXOffset > (WIDTH-PLAYER_SIZE)/2) player.playerXOffset = (WIDTH-PLAYER_SIZE)/2;
                    if (player.playerYOffset > (HEIGHT-PLAYER_SIZE)/2) player.playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
                    if (player.playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) player.playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
                    if (player.playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) player.playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);

                    
                    if (player.attacking)
                    {
                        frameMovement.attacking = true;
                    }

                    if (goingUp) frameMovement.facing = "Up";
                    if (goingDown) frameMovement.facing = "Down";
                    if (goingLeft) frameMovement.facing = "Left";
                    if (goingRight) frameMovement.facing = "Right";
                    
                    
                    // Add the frame
                    movementHistory.get(rewindCount).add(frameMovement);

                    Rectangle playerBounds = player.getBounds(WIDTH, HEIGHT);
                    for (int i = 0; i < rows; i++)
                    {
                        for (int j = 0; j < cols; j++)
                        {
                            Tile tile = map[i][j];

                            if (tile != null && tile.isLava())
                            {
                                Rectangle tileBounds = tile.getBounds(xOffset, yOffset);
                                if (playerBounds.intersects(tileBounds))
                                {    
                                    if (!playerDying) 
                                    {
                                        System.out.println("PLAYER DYING");
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
                    }

                    for (Enemy enemy : enemies)
                    {
                        if (enemy.getBounds().intersects(playerBounds) && !enemy.dead)
                        {
                            if (!playerDying)
                            {
                                System.out.println("PLAYER DYING");
                                playerDying = true;
                                player.deathFrame = 0;
                                player.lastDeathFrame = System.nanoTime();
                                goingUp = goingDown = goingLeft = goingRight = false;
                            }
                            rewinding = true;
                            rewindIndex = movementHistory.get(rewindCount).size();
                        }
                    }

                    for (Ghost ghost : ghosts) {
                        // Lava check
                        for (int i = 0; i < rows; i++)
                        {
                            for (int j = 0; j < cols; j++)
                            {
                                Tile tile = map[i][j];

                                if (tile != null && tile.isLava())
                                {
                                    if (ghost.getBounds(WIDTH, HEIGHT, xOffset, yOffset).intersects(tile.getBounds(xOffset, yOffset))) {
                                        ghost.isDead = true;
                                        //ghost.finished = true;
                                    }
                                }
                            }
                        }

                        // Enemy check
                        for (Enemy enemy : enemies)
                        {
                            if (enemy.getBounds().intersects(ghost.getBounds(WIDTH, HEIGHT, xOffset, yOffset)) && !enemy.dead) {
                                ghost.isDead = true;
                                //ghost.finished = true;
                            }
                        }
                    }


                    currentTime = System.nanoTime();
                    elapsedTime = (RESET_TIME - (currentTime - startTime))/ (double) SECONDS_TO_NANO;

                    if (!rewinding && (currentTime - startTime) >= RESET_TIME)
                    {
                        if (!playerDying)
                        {
                            System.out.println("PLAYER DYING");
                            playerDying = true;
                            player.deathFrame = 0;
                            player.lastDeathFrame = System.nanoTime();
                            goingUp = goingDown = goingLeft = goingRight = false;
                        }
                        rewinding = true;
                        rewindIndex = movementHistory.get(rewindCount).size();
                    }

                    //System.out.println(xOffset);
                    //System.out.println(yOffset);
                    //System.out.println(player.playerXOffset);
                    //System.out.println(player.playerYOffset);
                }
                break;  
            }
            case MENU:
            {
                break;
            }
        }
    }

    public static void draw(Graphics2D g2d) {
        switch (state)
        {
            case MENU:
            {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0,0,WIDTH,HEIGHT);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Serif", Font.BOLD, 32));
                g2d.drawString("PRESS ENTER TO START", WIDTH/2 - 150, HEIGHT/2 + 20);
                break;
            }
            case PLAYING:
            {
                //Tiles
                for (int i = 0; i < rows; i++) {

                    for (int j = 0; j < cols; j++) {

                        Tile tile = map[i][j];

                        if (tile != null) {

                            int x = tile.x + xOffset;
                            int y = tile.y + yOffset;

                            if (x + TILE_SIZE > 0 && x < WIDTH && y + TILE_SIZE > 0 && y < HEIGHT)
                            {
                                tile.draw(g2d, xOffset, yOffset);
                            }
                        }
                    }
                }

                //Objects        
                for (Items item : items)
                {
                    item.draw(g2d, xOffset, yOffset);
                }

                for (Door door : doors)
                {
                    door.draw(g2d, xOffset, yOffset);

                }


                //Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Serif", Font.BOLD, 32));
                g2d.drawString("Time: " + String.format("%.1f", Math.abs(elapsedTime)), 30, 50);
                g2d.drawString("Coins: " + String.valueOf(coins), 30, 100);

                for (Enemy enemy : enemies)
                {
                    enemy.draw(g2d, xOffset, yOffset);
                }

                //Ghosts
                g2d.setColor(Color.CYAN);
                for (Ghost ghost : ghosts) {
                    ghost.draw(g2d, WIDTH, HEIGHT, xOffset, yOffset);
                }

                //Player
                player.draw(g2d, WIDTH, HEIGHT);

                break;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (!rewinding && !interactHeld && !playerDying && !player.attacking)
        {
            if (e.getKeyCode() == KeyEvent.VK_S) goingDown = true;
            if (e.getKeyCode() == KeyEvent.VK_W) goingUp = true;
            if (e.getKeyCode() == KeyEvent.VK_A) goingLeft = true;
            if (e.getKeyCode() == KeyEvent.VK_D) goingRight = true;

            if (e.getKeyCode() == KeyEvent.VK_DOWN) goingDown = true;
            if (e.getKeyCode() == KeyEvent.VK_UP) goingUp = true;
            if (e.getKeyCode() == KeyEvent.VK_LEFT) goingLeft = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) goingRight = true;

            if (e.getKeyCode() == KeyEvent.VK_E) interactHeld = true;

            if (e.getKeyCode() == KeyEvent.VK_SPACE && !player.attacking) 
            {
                player.attacking = true;

                goingUp = false;
                goingDown = false;
                goingLeft = false;
                goingRight = false;
            }
        }

        if (state == MENU && e.getKeyCode() == KeyEvent.VK_ENTER)
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

    public static void pushOut(Door door, Movement frameMovement)
    {
        Rectangle playerBounds = player.getBounds(WIDTH, HEIGHT);

        Rectangle doorBounds = new Rectangle(door.x + xOffset, door.y + yOffset, door.width, door.height);

        if (!playerBounds.intersects(doorBounds)) return;


        Rectangle intersection = playerBounds.intersection(doorBounds);

        if (door.type.equals("vertical"))
        {
            // Push from left
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
            // Push from right
            else
            {
                if (player.playerXOffset < 0 || xOffset == -(rows * TILE_SIZE - WIDTH))
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
            // Push from top
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
            // Push from bottom
            else
            {
                if (player.playerYOffset < 0 || yOffset == -(cols * TILE_SIZE - HEIGHT))
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
}
