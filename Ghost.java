package TileScroller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Ghost {
    Image sprite;

    int frameAmount = 6;
    int frame = 0;
    long frameLength = 75000000L;
    long lastFrame = 0;
    boolean wasMoving;
    String lastDirection;

    long currentTime;

    int deathFrame = 0;
    int deathFrameAmount = 4;
    long deathFrameLength = 150000000L;
    long lastDeathFrame = 0;
    Image[] deathSprites = new Image[4];

    int ghostX;
    int ghostY;
    int ghostCameraX;
    int ghostCameraY;
    int size;
    int ghostIndex;
    int i = 0;
    boolean finished = false;
    boolean isDead = false;

    static final Color DEAD_COLOR = Color.DARK_GRAY;
    static final Color ALIVE_COLOR = Color.CYAN;

    Ghost (int ghostIndex, int size)
    {
        this.ghostIndex = ghostIndex;
        this.size = size;
        
        try{
            sprite = ImageIO.read(new File("TileScroller/assets/ghostIdleDown.png"));
        } catch(Exception e)
        {
            System.out.println("WRONG");
        }

        try
        {
            for (int i = 1; i <= deathFrameAmount; i++)
            {
                deathSprites[i-1] = ImageIO.read(new File("TileScroller/assets/ghostDeath" + i + ".png"));
            } 
        } 
        catch(Exception e)
        {
            System.out.println("LOADING GHOST TOO HARD");
        }
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

            if (isDead)
            {
                currentTime = System.nanoTime();
                if (currentTime - lastDeathFrame > deathFrameLength)
                {
                    if (deathFrame >= deathFrameAmount)
                    {
                        finished = true; 
                    }
                    else
                    {
                        deathFrame++;
                        lastDeathFrame = currentTime;
                    }
                    System.out.println(deathFrame);
                }                

                sprite = deathSprites[deathFrame-1];
            }
            else if (!Culminating.rewinding)
            {
                deathFrame = 0;
                boolean moving = false;

                if (actions.get(i).playerX != 0 || actions.get(i).playerY != 0 || actions.get(i).cameraX != 0 || actions.get(i).cameraY != 0) moving = true;

                
                try 
                {
                    if (moving)
                    {
                        currentTime = System.nanoTime();

                        if (currentTime - lastFrame > frameLength)
                        {
                            frame = (frame + 1) % frameAmount;
                            lastFrame = currentTime;
                        }

                        if (actions.get(i).cameraY > 0 || actions.get(i).playerY > 0) sprite = ImageIO.read(new File("TileScroller/assets/ghostWalkDown" + frame + ".png"));
                        if (actions.get(i).cameraY < 0 || actions.get(i).playerY < 0) sprite = ImageIO.read(new File("TileScroller/assets/ghostWalkUp" + frame + ".png"));
                        if (actions.get(i).cameraX > 0 || actions.get(i).playerX > 0) sprite = ImageIO.read(new File("TileScroller/assets/ghostWalkRight" + frame + ".png"));
                        if (actions.get(i).cameraX < 0 || actions.get(i).playerX < 0) sprite = ImageIO.read(new File("TileScroller/assets/ghostWalkLeft" + frame + ".png"));

                        wasMoving = true;
                        lastDirection = ((actions.get(i).cameraY > 0 || actions.get(i).playerY > 0) ? "Down" : ((actions.get(i).cameraY < 0 || actions.get(i).playerY < 0) ? "Up" : ((actions.get(i).cameraX > 0 || actions.get(i).playerX > 0) ? "Right" : "Left")));
                        }
                    else if (wasMoving)
                    {
                        sprite = ImageIO.read(new File("TileScroller/assets/ghostIdle" + lastDirection + ".png"));
                    }
                } catch (Exception e) {
                    System.out.println("GHOST TOO HARD");
                }
            }
            

            


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
            actions.add(new Movement(0, 0, 0, 0, false));
        }
        //System.out.println("ghostIndex: " + ghostIndex + " rewindCount: " + Culminating.rewindCount + " actions: " + actions.size());
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT, int currentCameraX, int currentCameraY)
    {
        if (!isDead)
        {   
            g2d.setColor(ALIVE_COLOR);
        }
        else
        {
            g2d.setColor(DEAD_COLOR);
        }
        int drawX = WIDTH / 2 - size / 2 - ghostX + (currentCameraX - ghostCameraX);
        int drawY = HEIGHT / 2 - size / 2 - ghostY + (currentCameraY - ghostCameraY);
        //g2d.fillRect(drawX, drawY, size, size);
        g2d.drawImage(sprite, drawX, drawY, size, size, null);


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
}
