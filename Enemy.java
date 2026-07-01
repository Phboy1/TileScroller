package TileScroller;

import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

public class Enemy {
    Image sprite;

    int frameAmount = 6;
    int frame = 0;
    long frameLength = 55000000L;
    long lastFrame = 0;
    boolean wasMoving;
    String lastDirection;

    
    int deathFrameAmount = 4;
    int deathFrame = 0;
    long deathFrameLength = 150000000L;
    long deathLastFrame = 0;

    static final int TOWARDS_END = 1;
    static final int TOWARDS_START = -1;
    static final int HITBOX_OFFSET_X = 30;
    static final int HITBOX_OFFSET_Y = 20;
    
    Image[] walkLeft = new Image[frameAmount];
    Image[] walkRight = new Image[frameAmount];
    Image[] walkUp = new Image[frameAmount];
    Image[] walkDown = new Image[frameAmount];

    Image[] death = new Image[deathFrameAmount];

    
    boolean dead = false;
    
    String type;
    
    int x;
    int y;

    int size = 80;
    
    int startX;
    int startY;
    int endX;
    int endY;
    int speed;

    boolean switched = false;
    
    int direction = TOWARDS_END;


    Enemy(int startX, int startY, int endX, int endY, int speed, String type)
    {
        this.speed = speed;
 
        this.x = startX * Culminating.TILE_SIZE;
        this.y = startY * Culminating.TILE_SIZE;
        this.startX = this.x;
        this.startY = this.y;
        this.endX = endX * Culminating.TILE_SIZE;
        this.endY = endY * Culminating.TILE_SIZE;
        this.type = type;
        this.frameLength = (long)(frameLength / speed);
        
        loadInAssets();
    }

    public void loadInAssets()
    {
        try 
        {
            sprite = ImageIO.read(Culminating.resource("TileScroller/assets/enemy001.png"));
            for (int i = 1; i <= frameAmount; i++)
            {
                walkLeft[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/enemyWalkLeft" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkRight[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/enemyWalkRight" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkUp[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/enemyWalkUp" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkDown[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/enemyWalkDown" + i + ".png"));
            }
            for (int i = 1; i <= deathFrameAmount; i++)
            {
                death[i-1] = ImageIO.read(Culminating.resource("TileScroller/assets/enemyDeath" + i + ".png"));
            }
        } 
        catch(Exception e) 
        {
            e.getStackTrace();
        }
    }

    public void reset()
    {
        x = startX;
        y = startY;
        direction = TOWARDS_END;
        switched  = false;
        dead = false;
        frame = 0;
        lastFrame = 0;
    }

    public void update()
    {
        if (dead) 
        {
            long currentTime = System.nanoTime();

            if (currentTime - deathLastFrame > deathFrameLength)
            {
                deathFrame++;
                deathLastFrame = currentTime;

                if (deathFrame >= deathFrameAmount)
                {
                    deathFrame = deathFrameAmount - 1;
                }
            }

            sprite = death[deathFrame % deathFrameAmount];
            return;
        }

        if (type.equals("patrolling"))
        {
            patrolling();
        }
        else if (type.equals("following"))
        {
            following();
        }
    }
    
    public void draw(Graphics2D g2d, int xOffset, int yOffset)
    {
        g2d.setColor(new Color(180, 0, 255, 200));
        g2d.drawImage(sprite, x + xOffset, y + yOffset, size, size, null);
        
        if (Culminating.debugging) g2d.fillRect(x + xOffset + HITBOX_OFFSET_X, y + yOffset + HITBOX_OFFSET_Y, size - 2 * HITBOX_OFFSET_X, size - 2 * HITBOX_OFFSET_Y);
    }

    public void patrolling()
    {
        int oldX = x;
        int oldY = y;

        int moveX = 0;
        int moveY = 0;

        if (startX == endX)
        {
            direction = (startY > endY ? -1 : 1);
            if (switched) moveY = direction * speed * TOWARDS_START;
            else moveY = direction * speed * TOWARDS_END;
        }
        else if (startY == endY)
        {
            direction = (startX > endX ? -1 : 1);
            if (switched) moveX = direction * speed * TOWARDS_START;
            else moveX = direction * speed * TOWARDS_END;
        }

        if (canMove(x, y, moveX, moveY))
        {
            x += moveX;
            y += moveY;
        }

        if (startX < endX || startY < endY)
        {
            if ((x >= endX && y >= endY) || (x <= startX && y <= startY) || !canMove(x, y, moveX, moveY))
            {
                switched = !switched;
            }
        }
        else if (startX > endX || startY > endY)
        {
            if ((x >= startX && y >= startY) || (x <= endX && y <= endY) || !canMove(x, y, moveX, moveY))
            {
                switched = !switched;
            }
        }

        animatePatrolling(oldX, oldY);
    }

    public void animatePatrolling(int oldX, int oldY)
    {
        boolean moving = (x != oldX || y != oldY);

        if (moving)
        {
            if (Math.abs(x - oldX) > Math.abs(y - oldY)) 
            {
                lastDirection = (x > oldX) ? "Right" : "Left";
            }
            else 
            {
                lastDirection = (y > oldY) ? "Down" : "Up";
            }

            long currentTime = System.nanoTime();
            if (currentTime - lastFrame > frameLength)
            {
                frame = (frame + 1) % frameAmount;
                lastFrame = currentTime;
            }

            wasMoving = true;
            if (lastDirection == null) lastDirection = "Down";
            if (lastDirection.equals("Down")) sprite = walkDown[frame];
            if (lastDirection.equals("Up")) sprite = walkUp[frame];
            if (lastDirection.equals("Left")) sprite = walkLeft[frame];
            if (lastDirection.equals("Right")) sprite = walkRight[frame];
        }
        else
        {
            frame = 0;
            wasMoving = false;
        }
    }

    public void following()
    {
        int moveX = 0;
        int moveY = 0;

        double angle = Math.atan2(Culminating.playerWorldY - y, Culminating.playerWorldX - x);

        moveX = (int) Math.round((Math.cos(angle) * speed));
        moveY = (int) Math.round((Math.sin(angle) * speed));

        if (canMove(x, y, moveX, moveY))
        {
            x += moveX;
            y += moveY;
        }

        if (Math.abs(moveX) > Math.abs(moveY))
        {
            if (moveX > 0) lastDirection = "Right";
            else if (moveX < 0) lastDirection = "Left";
        }
        else if (Math.abs(moveX) < Math.abs(moveY))
        {
            if (moveY > 0) lastDirection = "Down";
            else if (moveY < 0) lastDirection = "Up";
        }

        if (Culminating.playerWorldY < y && canMove(x, y, 0, -speed)) y -= speed;
        if (Culminating.playerWorldY > y && canMove(x, y, 0, speed)) y += speed;
        if (Culminating.playerWorldX > x && canMove(x, y, speed, 0)) x += speed;
        if (Culminating.playerWorldX < x && canMove(x, y, -speed, 0)) x -= speed;

        animateFollowing();

        for (Items item : Culminating.items)
        {
            if (getBounds().intersects(item.getBounds(Culminating.xOffset, Culminating.yOffset)))
            {
                item.activated = true;
                Culminating.player.playInteractSound();
            }
        }
    }

    public void animateFollowing()
    {
        long currentTime = System.nanoTime();
        if (currentTime - lastFrame > frameLength)
        {
            frame = (frame + 1) % frameAmount;
            lastFrame = currentTime;
        }

        wasMoving = true;
        if (lastDirection == null) lastDirection = "Down";
        if (lastDirection.equals("Down")) sprite = walkDown[frame];
        if (lastDirection.equals("Up")) sprite = walkUp[frame];
        if (lastDirection.equals("Left")) sprite = walkLeft[frame];
        if (lastDirection.equals("Right")) sprite = walkRight[frame];
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x + Culminating.xOffset + HITBOX_OFFSET_X, y + Culminating.yOffset + HITBOX_OFFSET_Y, size - 2* HITBOX_OFFSET_X, size - 2*HITBOX_OFFSET_Y);
    }

    public boolean canMove(int x, int y, int moveX, int moveY)
    {
        int futureX = x + moveX;
        int futureY = y + moveY;

        Rectangle futureEnemy = new Rectangle(futureX + HITBOX_OFFSET_X, futureY + HITBOX_OFFSET_Y, size - 2*HITBOX_OFFSET_X, size - 2*HITBOX_OFFSET_Y);

        for (int row = 0; row < Culminating.rows; row++)
        {
            for (int col = 0; col < Culminating.cols; col++)
            {
                Tile tile = Culminating.map[row][col];
                if (tile != null && tile.solid && futureEnemy.intersects(tile.x, tile.y, tile.size, tile.size))
                {
                    return false;
                }
            }
        }
        for (Door door : Culminating.doors)
        {
            if (!door.isOpen)
            {
                if (futureEnemy.intersects(new Rectangle(door.x, door.y, door.width, door.height)))
                {
                    pushOut(door);
                    return false;
                }
            }
        }
        return true;
    }

    public void pushOut(Door door)
    {
        Rectangle enemy = new Rectangle(x + HITBOX_OFFSET_X, y + HITBOX_OFFSET_Y, size - 2*HITBOX_OFFSET_X, size - 2*HITBOX_OFFSET_Y);
        Rectangle doorBounds = new Rectangle(door.x, door.y, door.width, door.height);

        if (!enemy.intersects(doorBounds)) return;

        Rectangle intersection = enemy.intersection(doorBounds);

        if (door.type.equals("vertical"))
        {
            if (enemy.x < doorBounds.x) x -= intersection.width;
            else x += intersection.width;
        }
        else if (door.type.equals("horizontal"))
        {
            if (enemy.y < doorBounds.y) y -= intersection.height;
            else y += intersection.height;
        }
    }
}
