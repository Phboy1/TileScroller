import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class Culminating extends Canvas implements KeyListener, MouseListener, MouseMotionListener {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static final int rows = 38;
    static final int cols = 40;

    static final int TILE_SIZE = 40;
    static final int PLAYER_SIZE = 40;

    static final int CAMERA_SPEED = 10;

    static String[][] map = new String[rows][cols];

    static int xOffset = 0;
    static int yOffset = 0;

    static int playerXOffset = 0;
    static int playerYOffset = 0;

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
                    map[i][j] = character[i];
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

    // --- GAME ENGINE METHODS ---

    public static void update() {

        //Moving the Background
        if (xOffset > 0) xOffset = 0;
        if (yOffset > 0) yOffset = 0;
        if (xOffset < -(rows*TILE_SIZE - WIDTH)) xOffset = -(rows*TILE_SIZE - WIDTH);
        if (yOffset < -((cols)*TILE_SIZE - HEIGHT)) yOffset = -((cols)*TILE_SIZE - HEIGHT);

        //Moving the player at the edges
        if (xOffset == 0 && goingLeft) playerXOffset += CAMERA_SPEED;
        if (yOffset == 0 && goingUp) playerYOffset += CAMERA_SPEED;
        if (xOffset == -(rows * TILE_SIZE - WIDTH) && goingRight) playerXOffset -= CAMERA_SPEED;
        if (yOffset == -(cols * TILE_SIZE - HEIGHT) && goingDown) playerYOffset -= CAMERA_SPEED;
            
        if (playerXOffset > 0 && goingRight) 
        {
            playerXOffset -= CAMERA_SPEED;
        } 
        else if (goingRight)
        {
            xOffset -= CAMERA_SPEED;
        } 

        if (playerYOffset > 0 && goingDown) 
        {
            playerYOffset -= CAMERA_SPEED;
        } 
        else if (goingDown)
        {
            yOffset -= CAMERA_SPEED;
        } 
        if (playerXOffset < 0 && goingLeft) 
        {
            playerXOffset += CAMERA_SPEED;
        } 
        else if (goingLeft)
        {
            xOffset += CAMERA_SPEED;
        } 
        if (playerYOffset < 0 && goingUp) 
        {
            playerYOffset += CAMERA_SPEED;
        } 
        else if (goingUp)
        {
            yOffset += CAMERA_SPEED;
        } 

        //Boundaries
        if (playerXOffset > (WIDTH-PLAYER_SIZE)/2) playerXOffset = (WIDTH-PLAYER_SIZE)/2;
        if (playerYOffset > (HEIGHT-PLAYER_SIZE)/2) playerYOffset = (HEIGHT-PLAYER_SIZE)/2;
        if (playerXOffset < -((WIDTH - PLAYER_SIZE)/2)) playerXOffset = -((WIDTH - PLAYER_SIZE)/2);
        if (playerYOffset < -((HEIGHT - PLAYER_SIZE)/2)) playerYOffset = -((HEIGHT - PLAYER_SIZE)/2);


        System.out.println(playerXOffset);
    }

    public static void draw(Graphics2D g2d) {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (map[i][j] != null && map[i][j].equals("#"))
                {
                    g2d.setColor(Color.GRAY);
                }
                else if (map[i][j] != null && map[i][j].equals("_"))
                {
                    g2d.setColor(Color.BLACK);
                }

                int x = i * TILE_SIZE + xOffset;
                int y = j * TILE_SIZE + yOffset;

                if (x + TILE_SIZE > 0 && x < WIDTH &&
                    y + TILE_SIZE > 0 && y < HEIGHT)
                {
                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                    g2d.setColor(Color.WHITE);
                    g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        g2d.setColor(Color.GREEN);
        g2d.fillRect(WIDTH/2 - PLAYER_SIZE/2 - playerXOffset, HEIGHT/2 - PLAYER_SIZE/2 - playerYOffset, PLAYER_SIZE, PLAYER_SIZE);
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
