import java.awt.*;

public class Tile {

    int x;
    int y;

    int size;

    String type;

    boolean solid;

    public Tile(int x, int y, int size, String type) {

        this.x = x;
        this.y = y;

        this.size = size;

        this.type = type;

        if (type.equals("#")) {
            solid = true;
        }
        else {
            solid = false;
        }
    }

    public void draw(Graphics2D g2d, int xOffset, int yOffset) {

        int drawX = x + xOffset;
        int drawY = y + yOffset;

        if (type.equals("#")) {
            g2d.setColor(Color.GRAY);
        }
        else {
            g2d.setColor(Color.BLACK);
        }

        g2d.fillRect(drawX, drawY, size, size);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(drawX, drawY, size, size);
    }

    public Rectangle getBounds(int xOffset, int yOffset) {

        return new Rectangle(x + xOffset, y + yOffset, size, size);
    }
}