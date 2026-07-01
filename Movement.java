package TileScroller;

public class Movement {
    int playerX;
    int playerY;
    int cameraX;
    int cameraY;
    boolean interacted;
    boolean attacking;
    String facing;
    String interactedItemId = null;

    Movement (int playerX, int playerY, int cameraX, int cameraY, boolean interacted, boolean attacking, String facing)
    {
        this.playerX = playerX;
        this.playerY = playerY;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.interacted = interacted;
        this.attacking = attacking;
        this.facing = facing;
    }
}
