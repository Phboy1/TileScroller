package TileScroller;

import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;

import javax.imageio.ImageIO;

public class Player {
    static Image sprite;
    Clip walkClip;
    Clip interactClip;

    boolean walkSoundPlaying = false;


    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    int idleFrameAmount = 4;

    int frameAmount = 6;
    int frame = 0;
    long frameLength = 75000000L;
    long lastFrame = 0;
    boolean wasMoving;
    String lastDirection;

    static int attackX = 30;
    static int attackY = 30;
    static final int STANDARD_ATTACK_OFFSET = attackX/2;

    int directionDown = STANDARD_ATTACK_OFFSET - attackY;
    int directionUp = STANDARD_ATTACK_OFFSET + attackY;
    int directionLeft = STANDARD_ATTACK_OFFSET + attackX;
    int directionRight = STANDARD_ATTACK_OFFSET - attackX;

    boolean dying = false;
    int deathFrame = 0;
    int deathFrameAmount = 4;
    long deathFrameLength = 150000000L;
    long lastDeathFrame = 0;
    Image[] deathSprites = new Image[deathFrameAmount];

    boolean attacking = false;
    int attackFrame = 0;
    int attackFrameAmount = 4;
    long lastAttackFrame = 0;
    long attackFrameLength = 75000000L;
    long attackDelay = 400000000L;

    Image[] attackLeft = new Image[attackFrameAmount];
    Image[] attackRight = new Image[attackFrameAmount];
    Image[] attackUp = new Image[attackFrameAmount];
    Image[] attackDown = new Image[attackFrameAmount];

    Image[] walkLeft = new Image[frameAmount];
    Image[] walkRight = new Image[frameAmount];
    Image[] walkUp = new Image[frameAmount];
    Image[] walkDown = new Image[frameAmount];

    Image[] idle = new Image[frameAmount];

    int directionX = STANDARD_ATTACK_OFFSET;
    int directionY = directionDown;

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
            System.out.println("IDLE IS WRONG");
        }

        try
        {
            //Death
            for (int i = 1; i <= deathFrameAmount; i++)
            {
                deathSprites[i-1] = ImageIO.read(new File("TileScroller/assets/playerDeath" + i + ".png"));
            } 

            idle[UP] = ImageIO.read(new File("TileScroller/assets/playerIdleUp.png"));
            idle[RIGHT] = ImageIO.read(new File("TileScroller/assets/playerIdleRight.png"));
            idle[DOWN] = ImageIO.read(new File("TileScroller/assets/playerIdleDown.png"));
            idle[LEFT] = ImageIO.read(new File("TileScroller/assets/playerIdleLeft.png"));
            //Walk
            for (int i = 1; i <= frameAmount; i++)
            {
                walkLeft[i-1] = ImageIO.read(new File("TileScroller/assets/playerWalkLeft" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkRight[i-1] = ImageIO.read(new File("TileScroller/assets/playerWalkRight" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkUp[i-1] = ImageIO.read(new File("TileScroller/assets/playerWalkUp" + i + ".png"));
            } 
            for (int i = 1; i <= frameAmount; i++)
            {
                walkDown[i-1] = ImageIO.read(new File("TileScroller/assets/playerWalkDown" + i + ".png"));
            }
            //Attack
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackLeft[i-1] = ImageIO.read(new File("TileScroller/assets/playerAttackLeft" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackRight[i-1] = ImageIO.read(new File("TileScroller/assets/playerAttackRight" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackUp[i-1] = ImageIO.read(new File("TileScroller/assets/playerAttackUp" + i + ".png"));
            } 
            for (int i = 1; i <= attackFrameAmount; i++)
            {
                attackDown[i-1] = ImageIO.read(new File("TileScroller/assets/playerAttackDown" + i + ".png"));
            } 
            
        } 
        catch(Exception e)
        {
            System.out.println("LOADING PLAYER TOO HARD");
        }

        try 
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("TileScroller/assets/walk.wav"));

            walkClip = AudioSystem.getClip();
            walkClip.open(audioInputStream);
            

            

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("TileScroller/assets/button.wav"));

            interactClip = AudioSystem.getClip();
            interactClip.open(audioInputStream);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CLICK SOUND FAILED");
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
                    deathFrame++;
                    lastDeathFrame = currentTime;

                    if (deathFrame >= deathFrameAmount)
                    {
                        deathFrame = 0;
                        Culminating.playerDying = false;
                    }
                }

                if (Culminating.playerDying)
                {
                    sprite = deathSprites[deathFrame];
                }
                else
                {
                    sprite = idle[DOWN];
                }
            }
            else if (!Culminating.rewinding)
            {
                deathFrame = 0;
                boolean moving = Culminating.goingDown || Culminating.goingUp || Culminating.goingLeft || Culminating.goingRight;
                updateWalkSound(moving);

                if (Culminating.goingDown) 
                {
                    directionX = STANDARD_ATTACK_OFFSET;
                    directionY = directionDown;
                }
                if (Culminating.goingUp)
                {
                    directionX = STANDARD_ATTACK_OFFSET;
                    directionY = directionUp;
                }
                if (Culminating.goingLeft) 
                {
                    directionX = directionLeft;
                    directionY = STANDARD_ATTACK_OFFSET;
                }
                if (Culminating.goingRight) 
                {
                    directionX = directionRight;
                    directionY = STANDARD_ATTACK_OFFSET;
                }

                
                
                if (attacking)
                {
                    long currentTime = System.nanoTime();
                    
                    attack();     
                    g2d.setColor(new Color(255, 0, 0));

                    //DEBUG ATTACK HITBOX
                    //g2d.fillRect(WIDTH / 2 - directionX - playerXOffset, HEIGHT / 2 - directionY - playerYOffset, attackX, attackY);

                    if (currentTime - lastAttackFrame > attackFrameLength)
                    {
                        attackFrame++;
                        lastAttackFrame = currentTime;

                        if (attackFrame > attackFrameAmount)
                        {
                            attackFrame = 0;
                            attacking = false;
                        }
                        System.out.println(attackFrame);
                    }

                    if (attacking)
                    {
                        if (lastDirection == null) lastDirection = "Down";

                        if (lastDirection.equals("Down")) sprite = attackDown[attackFrame];
                        if (lastDirection.equals("Up")) sprite = attackUp[attackFrame];
                        if (lastDirection.equals("Right")) sprite = attackRight[attackFrame];
                        if (lastDirection.equals("Left")) sprite = attackLeft[attackFrame];
                    }
                }
                else if (moving)
                {
                    long currentTime = System.nanoTime();
                    if (currentTime - lastFrame > frameLength)
                    {
                        frame = (frame + 1) % frameAmount;
                        lastFrame = currentTime;
                    }
                    if (Culminating.goingDown) sprite = walkDown[frame];
                    if (Culminating.goingUp) sprite = walkUp[frame];
                    if (Culminating.goingRight) sprite = walkRight[frame];
                    if (Culminating.goingLeft) sprite = walkLeft[frame];

                    wasMoving = true;
                    lastDirection = (Culminating.goingDown ? "Down" : (Culminating.goingUp ? "Up" : (Culminating.goingRight ? "Right" : "Left")));
                }
                else if (wasMoving)
                {
                    sprite = (lastDirection.equals("Up") ? idle[UP] : lastDirection.equals("Right") ? idle[RIGHT] : lastDirection.equals("Down") ? idle[DOWN] : idle[LEFT]);
                    wasMoving = false;
                }
                

                g2d.setColor(Color.RED);                
            }
            else
            {
                updateWalkSound(false); 
            }


            

            
        } 
        catch(Exception e)
        {
            System.out.println("LOADING PLAYER TOO HARD");
        }
        g2d.drawImage(sprite, WIDTH / 2 - size / 2 - playerXOffset, HEIGHT / 2 - size / 2 - playerYOffset, size, size, null);
        
        

        //DEBUG
        //int x = WIDTH / 2 - size / 2 - playerXOffset;
        //int y = HEIGHT / 2 - size / 2 - playerYOffset;

        //DEBUG PLAYER HITBOX
        //g2d.fillRect(x + hitboxXOffset, y + hitboxYOffset, HITBOX_SIZE_X, HITBOX_SIZE_Y);
    }

    public void attack()
    {
        Rectangle attackArea = new Rectangle(Culminating.WIDTH / 2 - directionX - playerXOffset, Culminating.HEIGHT / 2 - directionY - playerYOffset, attackX, attackY);
        for (Enemy enemy : Culminating.enemies)
        {
            if (attackArea.intersects(enemy.getBounds()) && !enemy.dead)
            {
                enemy.dead = true;
                Culminating.coins += (int) (Math.random() * Culminating.maxCoinDrop) + Culminating.minCoinDrop;
            }
        }
        
    }
//ricky was here
    public void reset() 
    {
        attacking = false;
        attackFrame = 0;
        lastAttackFrame = 0;

        dying = false;
        deathFrame = 0;
        lastDeathFrame = 0;

        wasMoving = false;
        frame = 0;
        lastFrame = 0;

        lastDirection = "Down";
        try 
        {
            sprite = ImageIO.read(new File("TileScroller/assets/playerIdleDown.png"));
        } catch (Exception e) {
            System.out.println("IDLE WRONG");
        }

    }

    public Rectangle getBounds(int WIDTH, int HEIGHT)
    {
        int x = WIDTH / 2 - size / 2 - playerXOffset;
        int y = HEIGHT / 2 - size / 2 - playerYOffset;

        return new Rectangle(x + hitboxXOffset , y + hitboxYOffset, HITBOX_SIZE_X, HITBOX_SIZE_Y);
    }

    public void updateWalkSound(boolean moving)
    {
   
        if (walkClip == null) return;

        if (moving)
        {
            if (!walkSoundPlaying)
            {
                walkClip.loop(Clip.LOOP_CONTINUOUSLY);
                walkSoundPlaying = true;
            }
        }
        else
        {
            if (walkSoundPlaying)
            {
                walkClip.stop();
                walkClip.setFramePosition(0);
                walkSoundPlaying = false;
            }
        }
    }

    public void playInteractSound()
    {
        if (interactClip == null) return;

        interactClip.setFramePosition(0); // rewind
        interactClip.start();             // play once
    }
}
