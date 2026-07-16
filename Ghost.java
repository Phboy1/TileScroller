package TileScroller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Ghost {
    Image sprite;

    static int frameAmount = 6;
    int frame = 0;
    long frameLength = 75000000L;
    long lastFrame = 0;
    boolean wasMoving;
    String lastDirection;

    static int attackFrameAmount = 4;
    long attackFrameLength = 150000000L;
    long attackLastFrame = 0;

    long currentTime;

    int deathFrame = 0;
    static int deathFrameAmount = 4;
    long deathFrameLength = 150000000L;
    long lastDeathFrame = 0;
    static Image[] deathSprites = new Image[deathFrameAmount];

    static Image[] walkLeft = new Image[frameAmount];
    static Image[] walkRight = new Image[frameAmount];
    static Image[] walkUp = new Image[frameAmount];
    static Image[] walkDown = new Image[frameAmount];
    
    static Image[] attackLeft = new Image[attackFrameAmount];
    static Image[] attackRight = new Image[attackFrameAmount];
    static Image[] attackUp = new Image[attackFrameAmount];
    static Image[] attackDown = new Image[attackFrameAmount];
    
    static int attackX = 30;
    static int attackY = 30;
    static final int STANDARD_ATTACK_OFFSET = attackX/2;

    int directionDown = STANDARD_ATTACK_OFFSET - attackY;
    int directionUp = STANDARD_ATTACK_OFFSET + attackY;
    int directionLeft = STANDARD_ATTACK_OFFSET + attackX;
    int directionRight = STANDARD_ATTACK_OFFSET - attackX;

    int ghostX;
    int ghostY;
    int ghostCameraX;
    int ghostCameraY;
    int size;
    int ghostIndex;
    int i = Culminating.ghostStart;
    boolean finished = false;
    boolean isDead = false;

    boolean isAttacking = false;
    boolean attackLocked = false;
    int attackFrame = 0;
    long lastAttackFrame = 0;

    static final int HITBOX_SIZE_X = 20;
    static final int HITBOX_SIZE_Y = 30;

    int hitboxXOffset;
    int hitboxYOffset;
    
    int directionX = STANDARD_ATTACK_OFFSET;
    int directionY = directionDown;

    static final Color DEAD_COLOR = Color.DARK_GRAY;
    static final Color ALIVE_COLOR = Color.CYAN;

    boolean walkSoundPlaying = false;

    Ghost (int ghostIndex, int size)
    {
        this.ghostIndex = ghostIndex;
        this.size = size;
        this.hitboxXOffset = (size - HITBOX_SIZE_X) / 2;
        this.hitboxYOffset = (size - HITBOX_SIZE_Y) / 2 + (size - HITBOX_SIZE_Y) / 12;
        
        loadAssets();
    }

    public void loadAssets()
    {
        try
        {
            sprite = ImageIO.read(Culminating.resource("TileScroller/assets/ghostIdleDown.png"));
        } 
        catch(Exception e)
        {
            e.getStackTrace();
        }

        try
        {
            for (int i = 1; i <= deathFrameAmount; i++)
            {
                deathSprites[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostDeath" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkLeft[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostWalkLeft" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkRight[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostWalkRight" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkUp[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostWalkUp" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkDown[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostWalkDown" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackLeft[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostAttackLeft" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackRight[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostAttackRight" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackUp[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostAttackUp" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackDown[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/ghostAttackDown" + i + ".png"));
            } 
        } 
        catch(Exception e)
        {
            e.getStackTrace();
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
            boolean attacking = actions.get(i).attacking;

            if (isDead)
            {
                ghostDead();
            }      
            else if (!Culminating.rewinding)
            {
                deathFrame = 0;
                boolean moving = false;

                if (actions.get(i).facing != null) moving = true;

                if (lastDirection == null) lastDirection = "Down";

                hitboxDirection();

                try 
                {
                    if (!isDead && attacking && !attackLocked)
                    {
                        isAttacking = true;
                        attackLocked = true;
                        attackFrame = 0;
                        lastAttackFrame = System.nanoTime();
                    }
                    if (isAttacking)
                    {
                        attacking();
                    }
                    else if (moving)
                    {
                        movingAnimation(actions);
                    
                    }
                    else if (wasMoving)
                    {
                        sprite = ImageIO.read(Culminating.resource("TileScroller/assets/ghostIdle" + lastDirection + ".png"));
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }    
            ghostInteraction(actions);
        }
        else
        {
            actions.add(new Movement(0, 0, 0, 0, false, false, null));
        }
    }

    public void draw(Graphics2D g2d, int WIDTH, int HEIGHT, int currentCameraX, int currentCameraY)
    {
        if (isAttacking)
        {
            g2d.setColor(new Color(255, 0, 0));
            if (Culminating.debugging) g2d.fillRect(WIDTH / 2 - directionX - ghostX + (currentCameraX - ghostCameraX), HEIGHT / 2 - directionY - ghostY + (currentCameraY - ghostCameraY), attackX, attackY);
        }

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
        g2d.drawImage(sprite, drawX, drawY, size, size, null);
        if (Culminating.debugging) g2d.drawRect(drawX, drawY, size, size);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Bahnscrift", Font.BOLD, 30));
        g2d.drawString(String.valueOf(ghostIndex + 1), drawX + 32, drawY + 25);
    }

    public Rectangle getBounds(int WIDTH, int HEIGHT, int currentCameraX, int currentCameraY)
    {
        int drawX = WIDTH / 2 - size / 2 - ghostX + (currentCameraX - ghostCameraX) + hitboxXOffset;
        int drawY = HEIGHT / 2 - size / 2 - ghostY + (currentCameraY - ghostCameraY) + hitboxYOffset;
        return new Rectangle(drawX, drawY, HITBOX_SIZE_X, HITBOX_SIZE_Y);
    }

    public void ghostDead()
    {
        currentTime = System.nanoTime();

        if (currentTime - lastDeathFrame > deathFrameLength)
        {
            deathFrame++;

            if (deathFrame >= deathFrameAmount)
            {
                finished = true;
                deathFrame = deathFrameAmount - 1;
            }

            lastDeathFrame = currentTime;
        }

        sprite = deathSprites[deathFrame];
    }

    public void hitboxDirection()
    {
        if (lastDirection.equals("Down")) 
        {
            directionX = STANDARD_ATTACK_OFFSET;
            directionY = directionDown;
        }
        if (lastDirection.equals("Up"))
        {
            directionX = STANDARD_ATTACK_OFFSET;
            directionY = directionUp;
        }
        if (lastDirection.equals("Left")) 
        {
            directionX = directionLeft;
            directionY = STANDARD_ATTACK_OFFSET;
        }
        if (lastDirection.equals("Right")) 
        {
            directionX = directionRight;
            directionY = STANDARD_ATTACK_OFFSET;
        }
    }

    public void attacking()
    {
        currentTime = System.nanoTime();

        if (currentTime - lastAttackFrame > attackFrameLength)
        {
            Rectangle attackArea = new Rectangle(Culminating.WIDTH / 2 - directionX - ghostX + (Culminating.xOffset - ghostCameraX), Culminating.HEIGHT / 2 - directionY - ghostY + (Culminating.yOffset - ghostCameraY), attackX, attackY);

            for (Enemy enemy : Culminating.enemies)
            {
                if (attackArea.intersects(enemy.getBounds()) && !enemy.dead)
                {
                    enemy.dead = true;
                    Culminating.playDeathSound();
                    Culminating.coins += (int) (Math.random() * Culminating.maxCoinDrop) + Culminating.minCoinDrop;
                }
            }

            attackFrame++;
            lastAttackFrame = currentTime;

            if (attackFrame >= attackFrameAmount)
            {
                attackFrame = 0;
                attackLocked = false;
                isAttacking = false;
            }
        }
        if (lastDirection == null) lastDirection = "Down";

        if (lastDirection.equals("Up"))
        {
            sprite = attackUp[attackFrame];
        }
        if (lastDirection.equals("Down"))
        {
            sprite = attackDown[attackFrame];
        }
        if (lastDirection.equals("Left"))
        {
            sprite = attackLeft[attackFrame];
        }
        if (lastDirection.equals("Right"))
        {
            sprite = attackRight[attackFrame];                            
        }
    }

    public void movingAnimation(ArrayList<Movement> actions)
    {
        currentTime = System.nanoTime();

        if (currentTime - lastFrame > frameLength)
        {
            frame = (frame + 1) % frameAmount;
            lastFrame = currentTime;
        }

        if (actions.get(i).cameraY > 0 || actions.get(i).playerY > 0) sprite = walkDown[frame];
        if (actions.get(i).cameraY < 0 || actions.get(i).playerY < 0) sprite = walkUp[frame];
        if (actions.get(i).cameraX > 0 || actions.get(i).playerX > 0) sprite = walkRight[frame];
        if (actions.get(i).cameraX < 0 || actions.get(i).playerX < 0) sprite = walkLeft[frame];

        wasMoving = true;
        lastDirection = ((actions.get(i).cameraY > 0 || actions.get(i).playerY > 0) ? "Down" : ((actions.get(i).cameraY < 0 || actions.get(i).playerY < 0) ? "Up" : ((actions.get(i).cameraX > 0 || actions.get(i).playerX > 0) ? "Right" : "Left")));
    }

    public void ghostInteraction(ArrayList<Movement> actions)
    {
        boolean prevInteracted = false;
            
        if (i > 0) prevInteracted = actions.get(i - 1).interacted;

        if (actions.get(i).interacted && !prevInteracted)
        {
            Culminating.playSound("TileScroller/assets/button.wav");
        }

        if (actions.get(i).interacted)
        {
            for (Items item : Culminating.items)
            {
                if (item.id.equals(actions.get(i).interactedItemId))
                {
                    item.activated = true;
                }
            }
        }

        i++;
    }
}
