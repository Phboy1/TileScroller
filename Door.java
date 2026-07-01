package TileScroller;

import java.awt.*;

public class Door {
    static final long SECONDS_TO_NANO = 1000000000L;
    
    int x;
    int y;
    int width;
    int height;

    long startTime = 0;  
    long currentTime = 0;     
    double elaspedTime = 0; 
    long timerMax = 0;

    String id;

    boolean isOpen = false;
    String startPosition;

    String type;

    final Color CLOSED_DOOR = new Color(92, 88, 82);
    final Color OPEN_DOOR   = new Color(85, 82, 78, 140);

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
        this.timerMax = SECONDS_TO_NANO * timerMax;
    }

    public void update()
    {
        for (Items item : Culminating.items)
        {
            if (id.equals(item.id) && item.activated)
            {
                startTime = System.nanoTime();
                isOpen = (startPosition.equals("Open") ? false : true);
                break;
            }
            else
            {
                isOpen = (startPosition.equals("Closed") ? false : true);
            }
        }

        currentTime = System.nanoTime();

        if (timerMax > (currentTime - startTime) && timerMax != 0)
        {
            isOpen = true;
        }
        else if (timerMax < (currentTime - startTime) && timerMax != 0)
        {
            isOpen = false;
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