package TileScroller;

import java.awt.Rectangle;

public class CollisionChecker {

    public boolean canMove(Player player, int moveX, int moveY)
    {

    int hitboxOffset = player.hitboxOffset;
    int hitboxSize = Player.HITBOX_SIZE;

    int buffer = Culminating.CAMERA_SPEED * 2;
    int futureX = Culminating.WIDTH / 2 - player.size / 2 - player.playerXOffset - moveX;
    int futureY = Culminating.HEIGHT / 2 - player.size / 2 - player.playerYOffset - moveY;

    if (moveX > 0) futureX += buffer;
    if (moveX < 0) futureX -= buffer;
    if (moveY > 0) futureY += buffer;
    if (moveY < 0) futureY -= buffer;

    Rectangle futureBounds = new Rectangle(futureX + hitboxOffset, futureY + hitboxOffset, hitboxSize, hitboxSize);

    for (int i = 0; i < Culminating.rows; i++)
    {

        for (int j = 0; j < Culminating.cols; j++)
        {
            Tile tile = Culminating.map[i][j];

            if (tile != null && tile.solid)
            {
                Rectangle tileBounds = tile.getBounds(Culminating.xOffset, Culminating.yOffset);
                if (futureBounds.intersects(tileBounds))
                {
                    return false;
                }
            }
        }
    }

    for (Door door : Culminating.doors)
    {
        if (!door.isOpen)
        {
            Rectangle doorBounds = new Rectangle(door.x + Culminating.xOffset, door.y + Culminating.yOffset, door.width, door.height);
            if (futureBounds.intersects(doorBounds))
            {
                return false;
            }
        }
    }
    return true;
    }
}