package TileScroller;

import java.awt.*;

public class Items {
    int x;
    int y;
    int width = Culminating.TILE_SIZE;
    int height = Culminating.TILE_SIZE;

    Color color;

    boolean position = false;
    boolean activated = false;


    Items (Color color, int x, int y)
    {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }


    public Color getColor() {
        if (activated)
        {
            return Color.BLUE;
        }
        else
        {
            return Color.YELLOW;
        }
    }

    public void draw(Graphics2D g2d, int xOffset, int yOffset)
    {
        g2d.setColor(getColor());
        g2d.fillOval(x + xOffset, y + yOffset, width, height);
    }

    public boolean isTouchingPlayer(Player player)
    {
        Rectangle playerBounds = player.getBounds(Culminating.WIDTH, Culminating.HEIGHT);

        Rectangle itemBounds = new Rectangle(x + Culminating.xOffset, y + Culminating.yOffset, width, height);

        return playerBounds.intersects(itemBounds);
    }
}
