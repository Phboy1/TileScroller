package TileScroller;
import java.io.*;
import java.util.*;

public class Level 
{
    public String name;
    public Tile[][] map;
    public ArrayList<Enemy> enemies;
    public ArrayList<Door> doors;
    public ArrayList<Items> items;
    public long maxTime;
    public int spawnX;
    public int spawnY;
    public int startingGhosts;

    public Level(long maxTime, int width, int height)
    {
        this.maxTime = maxTime;
        this.enemies = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = new Tile[width][height];
    }
}
