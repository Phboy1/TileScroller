import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final int rows = 100;
    static final int cols = 100;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 40;

    static final int CAMERA_SPEED = 5;

    static final int RESET_TIME = 10000;

    static Tile[][] map = new Tile[rows][cols];

    static int xOffset = 0;
    static int yOffset = 0;

    static Player player = new Player(PLAYER_SIZE, CAMERA_SPEED);

    static CollisionChecker CollisionChecker;

    static boolean goingUp = false;
    static boolean goingDown = false;
    static boolean goingLeft = false;
    static boolean goingRight = false;

    static long startTime = System.currentTimeMillis();
    static long currentTime;

    static java.util.ArrayList<Movement> movementHistory = new java.util.ArrayList<>();

    static boolean rewinding = false;
    static final int REWIND_SPEED = 4;



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

        } catch (Exception e)
        {
            System.out.println("SOMETHING WENT WRONG WITH THE FILE!!!!!!");
        }

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
                if (movementHistory.size() > 0)
                {
                    Movement movement = movementHistory.remove(movementHistory.size() - 1);
                    player.playerXOffset += movement.playerX;
                    player.playerYOffset += movement.playerY;
                    xOffset += movement.cameraX;
                    yOffset += movement.cameraY;
                }
                else
                {
                    rewinding = false;
                    startTime = System.currentTimeMillis();

                    break;
                }
            }
        }
        else
        {
             //Moving the Background
            if (xOffset > 0) xOffset = 0;
            if (yOffset > 0) yOffset = 0;
            if (xOffset < -(rows*TILE_SIZE - WIDTH)) xOffset = -(rows*TILE_SIZE - WIDTH);
            if (yOffset < -((cols)*TILE_SIZE - HEIGHT)) yOffset = -((cols)*TILE_SIZE - HEIGHT);

            //Moving the player at the edges
            if (xOffset == 0 && goingLeft && CollisionChecker.canMove(player, -CAMERA_SPEED, 0)) 
            {
                player.playerXOffset += CAMERA_SPEED;
                movementHistory.add(new Movement(-CAMERA_SPEED,0, 0, 0));
            }
            if (yOffset == 0 && goingUp && CollisionChecker.canMove(player, 0, -CAMERA_SPEED))
            {
                player.playerYOffset += CAMERA_SPEED;
                movementHistory.add(new Movement(0,-CAMERA_SPEED, 0, 0));

            }
            if (xOffset == -(rows * TILE_SIZE - WIDTH) && goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
            {
                player.playerXOffset -= CAMERA_SPEED;
                movementHistory.add(new Movement(CAMERA_SPEED,0, 0, 0));
            }
            if (yOffset == -(cols * TILE_SIZE - HEIGHT) && goingDown &&  CollisionChecker.canMove(player, 0, CAMERA_SPEED))
            {
                player.playerYOffset -= CAMERA_SPEED;
                movementHistory.add(new Movement(0,CAMERA_SPEED, 0, 0));
            }

            if (goingRight && CollisionChecker.canMove(player, CAMERA_SPEED, 0))
            {
                if (player.playerXOffset > 0)
                {
                    player.playerXOffset -= CAMERA_SPEED;
                    movementHistory.add(new Movement(CAMERA_SPEED,0, 0, 0));
                }
                else
                {
                    if (player.playerXOffset == 0)
                    {
                        xOffset -= CAMERA_SPEED;
                        movementHistory.add(new Movement(0,0, CAMERA_SPEED, 0));

                    } 
                }
            }

            if (goingDown && CollisionChecker.canMove(player, 0, CAMERA_SPEED))
            {
                if (player.playerYOffset > 0)
                {
                    player.playerYOffset -= CAMERA_SPEED;
                    movementHistory.add(new Movement(0,CAMERA_SPEED,0, 0));

                }
                else
                {
                    if (player.playerYOffset == 0)
                    {
                        yOffset -= CAMERA_SPEED;
                        movementHistory.add(new Movement(0, 0,0, CAMERA_SPEED));

                    } 
                }
            }
            if (goingLeft && CollisionChecker.canMove(player, -CAMERA_SPEED, 0))
            {
                if (player.playerXOffset < 0)
                {
                    player.playerXOffset += CAMERA_SPEED;
                    movementHistory.add(new Movement(-CAMERA_SPEED, 0,0, 0));

                }
                else
                {
                    if (player.playerXOffset == 0)
                    {
                        xOffset += CAMERA_SPEED;
                        movementHistory.add(new Movement(0, 0,-CAMERA_SPEED, 0));

                    } 
                }
            }
            if (goingUp && CollisionChecker.canMove(player, 0, -CAMERA_SPEED))
            {
                if (player.playerYOffset < 0)
                {
                    player.playerYOffset += CAMERA_SPEED;
                    movementHistory.add(new Movement(0, -CAMERA_SPEED,0, 0));

                }
                else
                {
                    if (player.playerYOffset == 0)
                    {
                        yOffset += CAMERA_SPEED;
                        movementHistory.add(new Movement(0, 0,0, -CAMERA_SPEED));
                    } 
                }
            }

            //Boundaries
            if (player.playerXOffset > (WIDTH-PLAYER_SIZE)/2) player.playerXOffset = (WIDTH-PLAYER_SIZE)/2;
            if (player.playerYOffset > (HEIGHT-PLAYER_SIZE)/2) player.playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
            if (player.playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) player.playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
            if (player.playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) player.playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);
            if (player.playerXOffset > (WIDTH-PLAYER_SIZE)/2) player.playerXOffset = (WIDTH-PLAYER_SIZE)/2;
            if (player.playerYOffset > (HEIGHT-PLAYER_SIZE)/2) player.playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
            if (player.playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) player.playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
            if (player.playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) player.playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);


            currentTime = System.currentTimeMillis();

            if (!rewinding && (currentTime - startTime) >= RESET_TIME)
            {
                rewinding = true;
            }
        }



    }

    public static void draw(Graphics2D g2d) {

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
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Serif", Font.BOLD, 32));
        g2d.drawString("Time: " + (double)(((double)currentTime - startTime)/1000), 30, 50);

        

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
        if (!rewinding)
        {
            if (e.getKeyCode() == KeyEvent.VK_S) goingDown = true;
            if (e.getKeyCode() == KeyEvent.VK_W) goingUp = true;
            if (e.getKeyCode() == KeyEvent.VK_A) goingLeft = true;
            if (e.getKeyCode() == KeyEvent.VK_D) goingRight = true;

            if (e.getKeyCode() == KeyEvent.VK_DOWN) goingDown = true;
            if (e.getKeyCode() == KeyEvent.VK_UP) goingUp = true;
            if (e.getKeyCode() == KeyEvent.VK_LEFT) goingLeft = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) goingRight = true;
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
        
    }
}