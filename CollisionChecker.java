package TileScroller;

import java.awt.Rectangle;

public class CollisionChecker {

    public boolean canMove(Player player, int moveX, int moveY) {

        int buffer = Culminating.CAMERA_SPEED*2;
        int futureX = Culminating.WIDTH / 2 - player.size / 2 - player.worldX - moveX;
        int futureY = Culminating.HEIGHT / 2 - player.size / 2 - player.worldY - moveY;

        if (moveX > 0) futureX += buffer;
        if (moveX < 0) futureX -= buffer;

        if (moveY > 0) futureY += buffer;
        if (moveY < 0) futureY -= buffer;

        int hitboxOffset = player.hitboxOffset;
        int hitboxSize = Player.HITBOX_SIZE;

        Rectangle futureBounds = new Rectangle(futureX + hitboxOffset, futureY + hitboxOffset, hitboxSize, hitboxSize);

        for (int i = 0; i < Culminating.rows; i++) {
            for (int j = 0; j < Culminating.cols; j++) {

                Tile tile = Culminating.map[i][j];

                if (tile != null && tile.solid) {

                    Rectangle tileBounds = tile.getBounds(Culminating.xOffset, Culminating.yOffset);

                    if (futureBounds.intersects(tileBounds)) {
                        //System.out.println("HIT: " + tile.type + " @ " + tile.x + "," + tile.y);
                        //System.out.println("COLLIDING");
                        return false;
                    }
                }
            }
        }

        return true;
    }
}