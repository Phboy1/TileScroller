package TileScroller.TileScroller;

public class Movement {
    int playerX;
    int playerY;
    int cameraX;
    int cameraY;
    boolean interacted;
    String interactedItemId = null;

    Movement (int playerX, int playerY, int cameraX, int cameraY, boolean interacted)
    {
        this.playerX = playerX;
        this.playerY = playerY;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.interacted = interacted;
    }
}
