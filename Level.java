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

    public Level(String name, long maxTime, int width, int height)
    {
        this.name = name;
        this.maxTime = maxTime;
        this.enemies = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = new Tile[width][height];
    }
}
