import java.awt.*;

public class Player {

    int playerXOffset = 0;
    int playerYOffset = 0;

    int size;
    int speed;

    public Player(int size, int speed) 
    {
        this.size = size;
        this.speed = speed;
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT) 
    {
        g2d.setColor(Color.GREEN);

        g2d.fillRect(WIDTH / 2 - size / 2 - playerXOffset, HEIGHT / 2 - size / 2 - playerYOffset, size, size);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT) 
    {
        return new Rectangle(WIDTH / 2 - size / 2 - playerXOffset, HEIGHT / 2 - size / 2 - playerYOffset, size, size);
    }
}