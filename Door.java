package TileScroller;

import java.awt.*;

public class Door {
    int x;
    int y;
    int width;
    int height;

    String id;

    boolean isOpen = false;

    String type;

    final Color CLOSED_DOOR = new Color(255, 0, 0);
    final Color OPEN_DOOR = new Color(255, 0, 0, 150);

    Door (int x, int y, int width, int height, String id, boolean isOpen)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        this.isOpen = isOpen;
        this.type = (width > height ? "horizontal" : "vertical");
    }

    public void update()
    {
        isOpen = false;

        for (Items item : Culminating.items)
        {
            if (id.equals(item.id) && item.activated)
            {
                isOpen = true;
                break;
            }
        }
    }

    public void draw(Graphics2D g2d, int xOffset, int yOffset)
    {
        if (isOpen)
        {
            g2d.setColor(OPEN_DOOR);
        }
        else if (!isOpen)
        {
            g2d.setColor(CLOSED_DOOR);
        }
        g2d.fillRect(x + xOffset, y + yOffset, width, height);
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }
}
