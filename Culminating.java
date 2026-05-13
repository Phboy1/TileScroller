import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final int rows = 38*2;
    static final int cols = 40;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 40;

    static final int CAMERA_SPEED = 10;

    static Tile[][] map = new Tile[rows][cols];

    static int xOffset = 0;
    static int yOffset = 0;

    static Player player = new Player(PLAYER_SIZE, CAMERA_SPEED);

    static boolean goingUp = false;
    static boolean goingDown = false;
    static boolean goingLeft = false;
    static boolean goingRight = false;



    public static void main(String[] args) {
        JFrame frame = new JFrame("👾");
        Culminating game = new Culminating();
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
                    map[i][j] = new Tile(i*TILE_SIZE, j*TILE_SIZE, TILE_SIZE, character[i]);
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

        int testXOffset = xOffset;
        int testYOffset = yOffset;
        int testPlayerXOffset = player.playerXOffset;
        int testPlayerYOffset = player.playerYOffset;

                // LEFT
        if (goingLeft)
        {
            // if player is offset right, recenter first
            if (player.playerXOffset < 0)
            {
                testPlayerXOffset += CAMERA_SPEED;
            }
            else
            {
                // move player if camera at edge
                if (xOffset == 0)
                {
                    testPlayerXOffset += CAMERA_SPEED;
                    
                }
                else
                {
                    testXOffset += CAMERA_SPEED;
                    
                }
            }
        }

        // RIGHT
        if (goingRight)
        {
            // if player is offset left, recenter first
            if (player.playerXOffset > 0)
            {
                testPlayerXOffset -= CAMERA_SPEED;
            }
            else
            {
                // move player if camera at edge
                if (xOffset == -(rows * TILE_SIZE - WIDTH))
                {
                    testPlayerXOffset -= CAMERA_SPEED;
                }
                else
                {
                    testXOffset -= CAMERA_SPEED;
                }
            }
        }

        // UP
        if (goingUp)
        {
            if (player.playerYOffset < 0)
            {
                testPlayerYOffset += CAMERA_SPEED;
            }
            else
            {
                if (yOffset == 0)
                {
                    testPlayerYOffset += CAMERA_SPEED;
                }
                else
                {
                    testYOffset += CAMERA_SPEED;
                }
            }
        }

        // DOWN
        if (goingDown)
        {
            if (player.playerYOffset > 0)
            {
                testPlayerYOffset -= CAMERA_SPEED;
            }
            else
            {
                if (yOffset == -(cols * TILE_SIZE - HEIGHT))
                {
                    testPlayerYOffset -= CAMERA_SPEED;
                }
                else
                {
                    testYOffset -= CAMERA_SPEED;
                }
            }
        }

        // CAMERA BOUNDS
        if (testXOffset > 0)
        {
            testXOffset = 0;
        }

        if (testYOffset > 0)
        {
            testYOffset = 0;
        }

        if (testXOffset < -(rows * TILE_SIZE - WIDTH))
        {
            testXOffset = -(rows * TILE_SIZE - WIDTH);
        }

        if (testYOffset < -(cols * TILE_SIZE - HEIGHT))
        {
            testYOffset = -(cols * TILE_SIZE - HEIGHT);
        }

        //Boundaries
        if (!isColliding(testXOffset, testYOffset,testPlayerXOffset,testPlayerYOffset))
        {
            xOffset = testXOffset;
            yOffset = testYOffset;
            player.playerXOffset = testPlayerXOffset;
            player.playerYOffset = testPlayerYOffset;
            System.out.println("playerXOffset: " + player.playerXOffset);
        }

        

        //if (player.playerXOffset > (WIDTH-PLAYER_SIZE)/2) player.playerXOffset = (WIDTH-PLAYER_SIZE)/2;
        //if (player.playerYOffset > (HEIGHT-PLAYER_SIZE)/2) player.playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
        //if (player.playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) player.playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
        //if (player.playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) player.playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);

        


        System.out.println(player.playerXOffset);
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

        player.draw(g2d, WIDTH, HEIGHT);
    }

    public static boolean isColliding(int testXOffset, int testYOffset, int testPlayerXOffset, int testPlayerYOffset)
    {
        Rectangle playerBounds = new Rectangle(WIDTH / 2 - PLAYER_SIZE / 2 + testPlayerXOffset, HEIGHT / 2 - PLAYER_SIZE / 2 + testPlayerYOffset, PLAYER_SIZE, PLAYER_SIZE);

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                Tile tile = map[i][j];

                if (tile != null && tile.solid)
                {
                    Rectangle tileBounds = new Rectangle(tile.x + testXOffset, tile.y + testYOffset, TILE_SIZE, TILE_SIZE);

                    if (playerBounds.intersects(tileBounds))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
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
        if (e.getKeyCode() == KeyEvent.VK_S) goingDown = true;
        if (e.getKeyCode() == KeyEvent.VK_W) goingUp = true;
        if (e.getKeyCode() == KeyEvent.VK_A) goingLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_D) goingRight = true;

        if (e.getKeyCode() == KeyEvent.VK_DOWN) goingDown = true;
        if (e.getKeyCode() == KeyEvent.VK_UP) goingUp = true;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) goingLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) goingRight = true;

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
