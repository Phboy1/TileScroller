package TileScroller.TileScroller;

import java.awt.*;

public class Enemy {
    static final int TOWARDS_END = 1;
    static final int TOWARDS_START = -1;
    
    String type;
    
    int x;
    int y;

    int size = Culminating.TILE_SIZE;
    
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
    }

    public void reset()
    {
        x = startX;
        y = startY;
        direction = TOWARDS_END;
        switched  = false;
    }

    public void update()
    {
        if (type.equals("patrolling"))
        {
            int moveX = 0;
            int moveY = 0;

            if (startX == endX)
            {
                direction = (startY > endY ? -1 : 1);
                if (switched)
                {
                    moveY = direction * speed * TOWARDS_START;
                }
                else
                {
                    moveY = direction * speed * TOWARDS_END;
                }
            }
            
            if (startY == endY)
            {
                direction = (startX > endX ? -1 : 1);
                if (switched)
                {
                    moveX = direction * speed * TOWARDS_START;
                }
                else
                {
                    moveX = direction * speed * TOWARDS_END;
                }
                
            }

            if (canMove(x, y, moveX, moveY))
            {
                x += moveX;
                y += moveY;
            }


            if ((x >= endX && y >= endY) || (x <= startX && y <= startY) || !canMove(x, y, moveX, moveY))
            {
                switched = !switched;
            }
        }
        else if (type.equals("following"))
        {
            int moveX = 0;
            int moveY = 0;

            double angle = Math.atan2(Culminating.playerWorldY - y, Culminating.playerWorldX - x);

            moveX = (int) (Math.cos(angle) * speed);
            moveY = (int) (Math.sin(angle) * speed);

            if (canMove(x, y, moveX, moveY))
            {
                x += moveX;
                y += moveY;
            }
            else if (Culminating.playerWorldY < y && canMove(x, y, 0, -speed))
            {
                y -= speed;
            }
            else if (Culminating.playerWorldY > y && canMove(x, y, 0, speed))
            {
                y += speed;
            }
            else if (Culminating.playerWorldX > x && canMove(x, y, speed, 0))
            {
                x += speed;
            }
            else if (Culminating.playerWorldX < x && canMove(x, y, -speed, 0))
            {
                x -= speed;
            }
        }
        for (Items item : Culminating.items)
        {
            if (getBounds().intersects(item.getBounds(Culminating.xOffset, Culminating.yOffset)))
            {
                item.activated = true;
            }
        }
        
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x + Culminating.xOffset, y + Culminating.yOffset, size, size);
    }

    public boolean canMove(int x, int y, int moveX, int moveY)
    {
        int futureX = x + moveX;
        int futureY = y + moveY;

        

        Rectangle futureEnemy = new Rectangle(futureX, futureY, size, size);

        for (int row = 0; row < Culminating.rows; row++)
        {
            for (int col = 0; col < Culminating.cols; col++)
            {
                Tile tile = Culminating.map[row][col];

                if (tile.solid && futureEnemy.intersects(tile.x, tile.y, tile.size, tile.size))
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

    public void draw(Graphics2D g2d, int xOffset, int yOffset)
    {
        g2d.setColor(new Color(180, 0, 255));
        g2d.fillRect(x + xOffset, y + yOffset, size, size);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x + xOffset, y + yOffset, size, size);
    }

    public void pushOut(Door door)
    {
        Rectangle enemy = getBounds();
        Rectangle doorBounds = new Rectangle(door.x, door.y, door.width, door.height);

        if (!enemy.intersects(doorBounds))
        {
            return;
        }

        Rectangle intersection = enemy.intersection(doorBounds);

        if (door.type.equals("vertical"))
        {
            if (enemy.x < doorBounds.x)
            {
                x -= intersection.width;
            }
            else
            {
                x += intersection.width;
            }
        }
        else if (door.type.equals("horizontal"))
        {
            if (enemy.y < doorBounds.y)
            {
                y -= intersection.height;
            }
            else
            {
                y += intersection.height;
            }
        }
    }
}
