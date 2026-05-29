package TileScroller;

import java.awt.*;
import java.util.ArrayList;

public class Ghost {
    int ghostX;
    int ghostY;
    int ghostCameraX;
    int ghostCameraY;
    int size;
    int ghostIndex;
    int i = 0;
    boolean finished = false;

    Ghost (int ghostIndex, int size)
    {
        this.ghostIndex = ghostIndex;
        this.size = size;
    }

    public void update()
    {
        if (finished) return;
        
        ArrayList<Movement> actions = Culminating.movementHistory.get(ghostIndex);

        if (i < actions.size())
        {
            ghostX -= actions.get(i).playerX;
            ghostY -= actions.get(i).playerY;
            ghostCameraX -= actions.get(i).cameraX;
            ghostCameraY -= actions.get(i).cameraY;

            if (actions.get(i).interacted)
            {
                System.out.println("GHOST INTERACTING: " + actions.get(i).interactedItemId);
                for (Items item : Culminating.items)
                {
                    if (item.id.equals(actions.get(i).interactedItemId))
                    {
                        //System.out.println("SETTING " + item.id + " TO TRUE");
                        item.activated = true;
                        
                    }
                }
            }

            i++;
        }
        else
        {
            
            finished = true;
        }
        //System.out.println("ghostIndex: " + ghostIndex + " rewindCount: " + Culminating.rewindCount + " actions: " + actions.size());
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT, int currentCameraX, int currentCameraY)
    {
        g2d.setColor(Color.CYAN);
        int drawX = WIDTH / 2 - size / 2 - ghostX + (currentCameraX - ghostCameraX);
        int drawY = HEIGHT / 2 - size / 2 - ghostY + (currentCameraY - ghostCameraY);
        g2d.fillRect(drawX, drawY, size, size);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Serif", Font.BOLD, 30));
        g2d.drawString(String.valueOf(ghostIndex), drawX + 10, drawY + 10);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT, int currentCameraX, int currentCameraY)
    {
        int drawX = WIDTH / 2 - size / 2 - ghostX + (currentCameraX - ghostCameraX);
        int drawY = HEIGHT / 2 - size / 2 - ghostY + (currentCameraY - ghostCameraY);
        return new Rectangle(drawX, drawY, size, size);
    }
    public boolean isClicked(int mouseX, int mouseY)
    {
        return getBounds(Culminating.WIDTH, Culminating.HEIGHT, Culminating.xOffset, Culminating.yOffset).contains(mouseX, mouseY);
    }
}
