package TileScroller;

import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

public class Player {
    static Image sprite;

    int frameAmount = 6;
    int frame = 0;
    long frameLength = 75000000L;
    long lastFrame = 0;
    boolean wasMoving;
    String lastDirection;

    boolean dying = false;
    int deathFrame = 0;
    int deathFrameAmount = 4;
    long deathFrameLength = 150000000L;
    long lastDeathFrame = 0;
    Image[] deathSprites = new Image[4];


    int playerXOffset;
    int playerYOffset;

    int size;
    int speed;

    static final int HITBOX_SIZE_X = 20;
    static final int HITBOX_SIZE_Y = 30;
    int hitboxXOffset;
    int hitboxYOffset;

    public Player(int size, int speed) {
        this.size = size;
        this.speed = speed;
        this.hitboxXOffset = (size - HITBOX_SIZE_X) / 2;
        this.hitboxYOffset = (size - HITBOX_SIZE_Y) / 2 + (size - HITBOX_SIZE_Y) / 12;

        try 
        {
            sprite = ImageIO.read(new File("TileScroller/assets/playerIdleDown.png"));
        } catch(Exception e) {
            System.out.println("FAILED IDLE: " + e.getMessage());
        }

        try
        {
            for (int i = 1; i <= deathFrameAmount; i++)
            {
                deathSprites[i-1] = ImageIO.read(new File("TileScroller/assets/playerDeath" + i + ".png"));
            } 
        } 
        catch(Exception e)
        {
            System.out.println("LOADING PLAYER TOO HARD");
        }
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT) 
    {
        try
        {
            if (Culminating.playerDying)
            {
                long currentTime = System.nanoTime();
                if (currentTime - lastDeathFrame > deathFrameLength)
                {
                    if (deathFrame >= deathFrameAmount)
                    {
                        deathFrame = 0;
                        Culminating.playerDying = false;
                    }
                    deathFrame++;
                    lastDeathFrame = currentTime;
                }

                sprite = deathSprites[deathFrame];
            }
            else if (!Culminating.rewinding)
            {
                deathFrame = 0;
                boolean moving = Culminating.goingDown || Culminating.goingUp || Culminating.goingLeft || Culminating.goingRight;

                if (moving)
                {
                    long currentTime = System.nanoTime();
                    if (currentTime - lastFrame > frameLength)
                    {
                        frame = (frame + 1) % frameAmount;
                        lastFrame = currentTime;
                    }
                    if (Culminating.goingDown) sprite = ImageIO.read(new File("TileScroller/assets/playerWalkDown" + frame + ".png"));
                    if (Culminating.goingUp) sprite = ImageIO.read(new File("TileScroller/assets/playerWalkUp" + frame + ".png"));
                    if (Culminating.goingRight) sprite = ImageIO.read(new File("TileScroller/assets/playerWalkRight" + frame + ".png"));
                    if (Culminating.goingLeft) sprite = ImageIO.read(new File("TileScroller/assets/playerWalkLeft" + frame + ".png"));

                    wasMoving = true;
                    lastDirection = (Culminating.goingDown ? "Down" : (Culminating.goingUp ? "Up" : (Culminating.goingRight ? "Right" : "Left")));
                }
                else if (wasMoving)
                {
                    sprite = ImageIO.read(new File("TileScroller/assets/playerIdle" + lastDirection + ".png"));
                    wasMoving = false;
                }
            }


            

            
        } 
        catch(Exception e)
        {
            
            System.out.println("LOADING PLAYER TOO HARD");
        }
        g2d.setColor(new Color(255, 0, 0));
        g2d.drawImage(sprite, WIDTH / 2 - size / 2 - playerXOffset, HEIGHT / 2 - size / 2 - playerYOffset, size, size, null);

        //DEBUG

        //int x = WIDTH / 2 - size / 2 - playerXOffset;
        //int y = HEIGHT / 2 - size / 2 - playerYOffset;
        //g2d.fillRect(x+ hitboxXOffset, y + hitboxYOffset, HITBOX_SIZE_X, HITBOX_SIZE_Y);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT)
    {
        int x = WIDTH / 2 - size / 2 - playerXOffset;
        int y = HEIGHT / 2 - size / 2 - playerYOffset;

        return new Rectangle(x + hitboxXOffset , y + hitboxYOffset, HITBOX_SIZE_X, HITBOX_SIZE_Y);
    }
}
