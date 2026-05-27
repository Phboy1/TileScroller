package TileScroller;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static final int NO_TIMER_DOOR = 0;

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final int rows = 100;
    static final int cols = 100;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 40;

    static final int CAMERA_SPEED = 5;

    static final int RESET_TIME = 7000;

    static Tile[][] map = new Tile[rows][cols];

    static Player player = new Player(PLAYER_SIZE, CAMERA_SPEED);

    static int xOffset = 0;
    static int yOffset = 0;

    static CollisionChecker CollisionChecker;

    static boolean goingUp = false;
    static boolean goingDown = false;
    static boolean goingLeft = false;
    static boolean goingRight = false;

    static long startTime = System.currentTimeMillis();
    static long currentTime;
    static int rewindIndex;
    static int rewindCount = 0;

    static ArrayList<Ghost> ghosts = new ArrayList<>();

    static ArrayList<ArrayList<Movement>> movementHistory = new ArrayList<>();

    static ArrayList<Items> items = new ArrayList<>();

    static ArrayList<Door> doors = new ArrayList<>();

    static boolean rewinding = false;
    static final int REWIND_SPEED = 4;

    static {movementHistory.add(new java.util.ArrayList<>());}

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
            BufferedReader br = new BufferedReader(new FileReader("TileScroller/TileScroller/TileGrid.txt"));
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

        items.add(new Items(Color.YELLOW, 20 * TILE_SIZE, 7 * TILE_SIZE, "A"));
        items.add(new Items(Color.YELLOW, 15 * TILE_SIZE, 7 * TILE_SIZE, "B"));
        items.add(new Items(Color.YELLOW, 17 * TILE_SIZE, 7 * TILE_SIZE, "B"));

        items.add(new Items(Color.YELLOW, 27 * TILE_SIZE, 10 * TILE_SIZE, "T"));


        doors.add(new Door(31 * TILE_SIZE, 10 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "A", false, NO_TIMER_DOOR));
        doors.add(new Door(10 * TILE_SIZE, 5 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "A", true, NO_TIMER_DOOR));
        doors.add(new Door(18 * TILE_SIZE, 5 * TILE_SIZE, TILE_SIZE, 3 * TILE_SIZE, "B", true, 4));
        doors.add(new Door(28 * TILE_SIZE, 9 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, "T", true, 4));


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
        if (rewinding)
        {
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
                    startTime = System.currentTimeMillis();
                    ghosts.add(new Ghost(rewindCount, PLAYER_SIZE)); 
                    rewindCount++;
                    movementHistory.add(new java.util.ArrayList<>()); 
                    
                    //Resetting all ghosts
                    for (Ghost ghost : ghosts)
                    {
                        ghost.i = 0;
                        ghost.ghostX = 0;
                        ghost.ghostY = 0;
                        ghost.ghostCameraX = 0;
                        ghost.ghostCameraY = 0;
                        ghost.finished = false;
                    }

                    //Reset items
                    for (Items item : items)
                    {
                        item.activated = false;
                    }
                    rewinding = false;

                    break;
                }
            }
        }
        else
        {
            //Moving the player at the edges
            Movement frameMovement = new Movement(0, 0, 0, 0, false);

            // Reset all items first
            for (Items item : items)
            {
                item.activated = false;
            }

            //Update Ghosts only when NOT rewinding
            for (Ghost ghost : ghosts) {
                ghost.update();
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

            

            
            
            // Add the frame
            movementHistory.get(rewindCount).add(frameMovement);


            currentTime = System.currentTimeMillis();

            if (!rewinding && (currentTime - startTime) >= RESET_TIME)
            {
                rewinding = true;
                rewindIndex = movementHistory.get(rewindCount).size();
            }

            System.out.println(xOffset);
            System.out.println(yOffset);
            System.out.println(player.playerXOffset);
            System.out.println(player.playerYOffset);
        }



    }

    public static void draw(Graphics2D g2d) {

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
        g2d.drawString("Time: " + (double)(((double)currentTime - startTime)/1000), 30, 50);

        //Ghosts
        g2d.setColor(Color.CYAN);
        for (Ghost ghost : ghosts) {
            ghost.draw(g2d, WIDTH, HEIGHT, xOffset, yOffset);
        }

        //Player
        player.draw(g2d, WIDTH, HEIGHT);

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
        if (!rewinding && !interactHeld)
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

        if (e.getKeyCode() == KeyEvent.VK_E) { 
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

