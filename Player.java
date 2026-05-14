import java.awt.*;

public class Player {

    int playerXOffset = 0;
    int playerYOffset = 0;

    int worldX;
    int worldY;

    int size;
    int speed;

    static final int HITBOX_SIZE = 30;

    
    int hitboxOffset = (size - HITBOX_SIZE)/2;

    public Player(int size, int speed) {
        this.size = size;
        this.speed = speed;

        this.hitboxOffset = (size - HITBOX_SIZE) / 2;
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT) {

        int x = WIDTH / 2 - size / 2 - playerXOffset;
        int y = HEIGHT / 2 - size / 2 - playerYOffset;


        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, size, size);

        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + hitboxOffset, y + hitboxOffset, HITBOX_SIZE, HITBOX_SIZE);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT) {

        int x = WIDTH / 2 - size / 2 - playerXOffset;
        int y = HEIGHT / 2 - size / 2 - playerYOffset;

        return new Rectangle(x + hitboxOffset, y + hitboxOffset, HITBOX_SIZE, HITBOX_SIZE);
    }
}