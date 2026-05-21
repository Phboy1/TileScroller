package TileScroller;

import java.awt.*;

public class Player {

    int playerXOffset;
    int playerYOffset;

    int size;
    int speed;

    static final int HITBOX_SIZE = 30;
    int hitboxOffset;

    public Player(int size, int speed) {
        this.size = size;
        this.speed = speed;
        this.hitboxOffset = (size - HITBOX_SIZE) / 2;
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT) 
    {
        g2d.setColor(Color.RED);
        g2d.fillRect(WIDTH / 2 - size / 2 - playerXOffset, HEIGHT / 2 - size / 2 - playerYOffset, size, size);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT)
    {
        int x = WIDTH / 2 - size / 2 - playerXOffset;
        int y = HEIGHT / 2 - size / 2 - playerYOffset;

        return new Rectangle(x + hitboxOffset, y + hitboxOffset, HITBOX_SIZE, HITBOX_SIZE);
    }
}