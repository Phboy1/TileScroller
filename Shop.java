package TileScroller;

import java.awt.*;

public class Shop {
    
    static final int BUTTON_HORIZONTAL_MARGIN = 80;
    static final int BUTTON_HEIGHT= 45;
    static final int BUTTON_X_BOTTOM_MARGIN = 15;
    static final int SHOP_TITLE_BUTTON_BUFFER = 115;
    static final int CORNER_CUBE_SIZE = 15;

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

        String[] shopItems = {"+1 Second", "Speed", "Luck", "Avin Chiu"};
        String[] shopPrices = {"30g", "50g", "40g", "67g"};

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
            buttonY = isHovered ? buttonY - 3 : buttonY;
            buttonWidth = isHovered ? buttonWidth + 6 : buttonWidth;
            buttonHeight = isHovered ? BUTTON_HEIGHT + 6 : BUTTON_HEIGHT;

            g2d.setColor(new Color(36, 29, 18));
            g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
            g2d.setColor(new Color(90, 69, 48));
            g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 6, 6);
            g2d.drawRoundRect(buttonX + 2, buttonY + 2, buttonWidth - 4, buttonHeight - 4, 6, 6);

            g2d.setColor(new Color(107, 140, 62));
            g2d.fillRect(buttonX, buttonY + 1, 4, buttonHeight - 2);

            g2d.setFont(new Font("Bahnschrift", Font.BOLD, 15));
            g2d.setColor(new Color(200, 176, 104));
            if (isHovered) g2d.drawString(shopItems[i], buttonX + 16, buttonY + 28);
            else g2d.drawString(shopItems[i], buttonX + 16, buttonY + 26);


            g2d.setFont(new Font("Bahnschrift", Font.BOLD, 15));
            FontMetrics priceFont = g2d.getFontMetrics();
            int priceWidth = priceFont.stringWidth(shopPrices[i]) + 16;
            int priceX = buttonX + buttonWidth - priceWidth - 10;
            int priceY = buttonY + (buttonHeight - 26) / 2;
            g2d.setColor(new Color(30, 24, 16));
            g2d.fillRoundRect(priceX, priceY, priceWidth, 26, 4, 4);
            g2d.setColor(new Color(107, 90, 62));
            g2d.drawRoundRect(priceX, priceY, priceWidth, 26, 4, 4);

            // Price text
            g2d.setColor(new Color(212, 168, 67));
            g2d.drawString(shopPrices[i], priceX + 8, priceY + 18);
            

            if (isHovered) buttonY += 3;

            if (i == shopItems.length - 1)
            {
                g2d.setFont(new Font("Serif", Font.PLAIN, 40));
                g2d.setColor(new Color(107, 90, 62));
                g2d.drawString(dashedLine, Culminating.WIDTH / 2 - dashedFont.stringWidth(dashedLine) / 2, shopY + buttonY - BUTTON_HEIGHT - BUTTON_X_BOTTOM_MARGIN);
            }
        }

    }
}
