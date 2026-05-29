package TileScroller;

import java.awt.*;

public class Door {
    static final int FRAMES_PER_SECOND = 30;
    
    int x;
    int y;
    int width;
    int height;

    int timer = 0;        
    int timerMax = 0;

    String id;

    boolean isOpen = false;
    String startPosition;

    String type;

    final Color CLOSED_DOOR = new Color(255, 0, 0);
    final Color OPEN_DOOR = new Color(255, 0, 0, 150);

    Door (int x, int y, int width, int height, String id, boolean isOpen, int timerMax)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        this.isOpen = isOpen;
        this.startPosition = (isOpen ? "Open" : "Closed");
        this.type = (width >= height ? "horizontal" : "vertical");
        this.timerMax = FRAMES_PER_SECOND * timerMax;
    }

    public void update()
    {
        for (Items item : Culminating.items)
        {
            if (id.equals(item.id) && item.activated)
            {
                isOpen = (startPosition.equals("Open") ? false : true);
                break;
            }
            else
            {
                isOpen = (startPosition.equals("Closed") ? false : true);
            }
        }

        if (isOpen && timerMax > 0)
        {
            timer++;

            if (timer > timerMax)
            {
                isOpen = false;
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
