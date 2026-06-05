package TileScroller;

import java.awt.*;

public class Shop {
    
    static final int BUTTON_HORIZONTAL_MARGIN = 80;
    static final int BUTTON_HEIGHT= 45;
    static final int BUTTON_X_BOTTOM_MARGIN = 15;
    static final int SHOP_TITLE_BUTTON_BUFFER = 115;
    static final int CORNER_CUBE_SIZE = 15;

    static boolean addGhost = false;
    static int addGhostAmount = 0;

    static String[] shopItems = {"Borrowed Time", "Midas Touch", "Release the Undead", "Hard Bargain"};
    static String[] shopDescriptions = {"+1 Second of Time in the Jungle", "Enemies drop more gold", "+1 Ghost", "Cheaper shop prices"};
    static int[] shopPrices = {8, 14, 1, 45};

    public void update(Graphics2D g2d, int shopWidth, int shopHeight)
    {

        int shopX = (Culminating.WIDTH-shopWidth)/2;
        int shopY = (Culminating.HEIGHT-shopHeight)/2;

        g2d.setColor(new Color(20,20,20,200));

        g2d.fillRect(0,0,Culminating.WIDTH, Culminating.HEIGHT);

        g2d.setColor(new Color(46, 37, 25));
        g2d.fillRoundRect(shopX, shopY, shopWidth, shopHeight, 8, 8);
        g2d.setColor(new Color(107, 90, 62));
        g2d.drawRoundRect(shopX, shopY, shopWidth, shopHeight, 8, 8);
        g2d.drawRoundRect(shopX + 1, shopY + 1, shopWidth - 2, shopHeight - 2, 8, 8);
        g2d.drawRoundRect(shopX + 3, shopY + 3, shopWidth - 6, shopHeight - 6, 8, 8);
        g2d.drawRoundRect(shopX + 7, shopY + 7, shopWidth - 14, shopHeight - 14, 6, 6);
        g2d.drawRoundRect(shopX + 1, shopY + 1, shopWidth - 2, shopHeight - 2, 8, 8);

        g2d.fillRect(shopX, shopY,CORNER_CUBE_SIZE, CORNER_CUBE_SIZE);
        g2d.fillRect(shopX+shopWidth-CORNER_CUBE_SIZE, shopY,CORNER_CUBE_SIZE, CORNER_CUBE_SIZE);
        g2d.fillRect(shopX, shopY + shopHeight - CORNER_CUBE_SIZE,CORNER_CUBE_SIZE, CORNER_CUBE_SIZE);
        g2d.fillRect(shopX+shopWidth-CORNER_CUBE_SIZE, shopY + shopHeight - CORNER_CUBE_SIZE,CORNER_CUBE_SIZE, CORNER_CUBE_SIZE);

        g2d.setFont(new Font("Serif", Font.ITALIC, 32));
        g2d.setColor(new Color(160, 146, 74));
        FontMetrics titleFont = g2d.getFontMetrics();
        String title = "~ The Shop ~";
        g2d.drawString(title, Culminating.WIDTH / 2 - titleFont.stringWidth(title) / 2, shopY + 60);

        g2d.setFont(new Font("Serif", Font.PLAIN, 20));
        String goldText = Culminating.coins + " gold";
        FontMetrics goldFont = g2d.getFontMetrics();
        g2d.drawString(goldText, Culminating.WIDTH / 2 - goldFont.stringWidth(goldText) / 2, shopY + 90);

        
        g2d.setFont(new Font("Serif", Font.PLAIN, 40));
        g2d.setColor(new Color(107, 90, 62));
        String dashedLine = "_______________";
        FontMetrics dashedFont = g2d.getFontMetrics();
        g2d.drawString(dashedLine, Culminating.WIDTH / 2 - dashedFont.stringWidth(dashedLine) / 2, shopY + 95);

        

        for (int i = 0; i < shopItems.length; i++)
        {
            boolean isHovered = false;
            int buttonWidth = shopWidth - 2 * BUTTON_HORIZONTAL_MARGIN;
            int buttonY = shopY + SHOP_TITLE_BUTTON_BUFFER + i * (BUTTON_HEIGHT + BUTTON_X_BOTTOM_MARGIN);
            int buttonX = shopX + (shopWidth-buttonWidth)/2;
            int buttonHeight = BUTTON_HEIGHT;
            Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, BUTTON_HEIGHT);

            if (button.contains(Culminating.mouseX, Culminating.mouseY)) 
            {
                isHovered = true;
            }


            buttonX = isHovered ? buttonX - 3 : buttonX;
            buttonY = isHovered ? buttonY - 1 : buttonY;
            buttonWidth = isHovered ? buttonWidth + 6 : buttonWidth;
            buttonHeight = isHovered ? BUTTON_HEIGHT + 2 : BUTTON_HEIGHT;

            if (Culminating.coins >= shopPrices[i])
            {
                g2d.setColor(new Color(95, 72, 28));
            }
            else
            {
                g2d.setColor(new Color(36, 29, 18));
            }

            g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);

            if (Culminating.coins >= shopPrices[i])
            {
                g2d.setColor(new Color(160, 130, 50)); 
            }
            else
            {
                g2d.setColor(new Color(90, 69, 48));
            }
            g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 6, 6);
            g2d.drawRoundRect(buttonX + 2, buttonY + 2, buttonWidth - 4, buttonHeight - 4, 6, 6);

            g2d.setColor(new Color(107, 140, 62));
            g2d.fillRect(buttonX, buttonY + 1, 4, buttonHeight - 2);

            //Descriptions
            g2d.setFont(new Font("Serif", Font.ITALIC, 10));
            g2d.setColor(new Color(140, 120, 70));
            if (isHovered)
            {
                g2d.drawString(shopDescriptions[i], buttonX + 16, buttonY + 38);
            }
            else 
            {
                g2d.drawString(shopDescriptions[i], buttonX + 16, buttonY + 37);
            }

            g2d.setFont(new Font("Bahnschrift", Font.BOLD, 15));
            g2d.setColor(new Color(200, 176, 104));
            if (isHovered)
            {
                g2d.drawString(shopItems[i], buttonX + 16, buttonY + 25);
            }
            else 
            {
                g2d.drawString(shopItems[i], buttonX + 16, buttonY + 24);
            }


            g2d.setFont(new Font("Bahnschrift", Font.BOLD, 15));
            FontMetrics priceFont = g2d.getFontMetrics();
            int priceWidth = priceFont.stringWidth(String.valueOf(shopPrices[i]) + "g") + 16;
            int priceX = buttonX + buttonWidth - priceWidth - 10;
            int priceY = buttonY + (buttonHeight - 26) / 2;
            if (Culminating.coins >= shopPrices[i])
            {
                g2d.setColor(new Color(50, 38, 12));
            }
            else
            {
                g2d.setColor(new Color(30, 24, 16));
            }
            g2d.fillRoundRect(priceX, priceY, priceWidth, 26, 4, 4);
            g2d.setColor(new Color(107, 90, 62));
            g2d.drawRoundRect(priceX, priceY, priceWidth, 26, 4, 4);

            // Price text
            g2d.setColor(new Color(212, 168, 67));
            g2d.drawString(String.valueOf(shopPrices[i])+ "g", priceX + 8, priceY + 18);

            if (Culminating.clicked && button.contains(Culminating.mouseX, Culminating.mouseY) && Culminating.coins >= shopPrices[i] && Culminating.shopOpen)
            {
                Culminating.coins -= shopPrices[i];
                shopPrices[i] = (int) Math.round((shopPrices[i] * 1.5));

                if (shopItems[i].equals("Borrowed Time"))
                {
                    Culminating.secondTime += 3;
                    Culminating.resetTime = Culminating.secondTime * Culminating.SECONDS_TO_NANO;
                    Culminating.plusOneBrightness = 255;
                    Culminating.increasedTime = true;
                }
                if (shopItems[i].equals("Midas Touch"))
                {
                    if (Culminating.maxCoinDrop > Culminating.minCoinDrop - 1)
                    {
                        int choice = (int) (Math.random()*2);

                        if (choice == 0) Culminating.maxCoinDrop++;
                        else if (choice == 1) Culminating.minCoinDrop++;
                    }
                    else
                    {
                        Culminating.maxCoinDrop++;
                    }

                    System.out.println(Culminating.maxCoinDrop);
                    System.out.println(Culminating.minCoinDrop);
                }
                if (shopItems[i].equals("Release the Undead"))
                {
                    addGhost = true;
                    addGhostAmount++;
                    Culminating.plusGhostBrightness = 255;
                    Culminating.increasedGhost = true;
                }
                if (shopItems[i].equals("Hard Bargain"))
                {
                    for (int j = 0; j < shopPrices.length; j++)
                    {
                        if (!shopItems[j].equals("Hard Bargain"))
                        {
                            shopPrices[j] = (int) Math.floor(shopPrices[j] * 0.9);
                        }
                    }
                }

                Culminating.clicked = false;
            }
            

            if (isHovered) buttonY += 1;

            if (i == shopItems.length - 1)
            {
                g2d.setFont(new Font("Serif", Font.PLAIN, 40));
                g2d.setColor(new Color(107, 90, 62));
                g2d.drawString(dashedLine, Culminating.WIDTH / 2 - dashedFont.stringWidth(dashedLine) / 2, shopY + buttonY - BUTTON_HEIGHT - BUTTON_X_BOTTOM_MARGIN);
            }
        }

    }
}
