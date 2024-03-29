import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Img {
    // stores all the images used in the game
    public Image title = new ImageIcon("Images/ArkanoidArcTitle.png").getImage();
    public Image titleText = new ImageIcon("Images/TitleText.png").getImage();

    public Image bgTexture = new ImageIcon("Images/BGTexture.png").getImage();
    public Image borderLeft = new ImageIcon("Images/border_left.png").getImage();
    public Image borderRight = new ImageIcon("Images/border_right.png").getImage();
    public Image borderTop = new ImageIcon("Images/border_top.png").getImage();
    public Image plat = new ImageIcon("Images/platform.png").getImage().getScaledInstance(66, 20, Image.SCALE_DEFAULT);
    public Image platLong = new ImageIcon("Images/platformLong.png").getImage().getScaledInstance(96, 14, Image.SCALE_DEFAULT);
    public Image platIcon = new ImageIcon("Images/platform.png").getImage();
    public ArrayList<Image> enemySprites = new ArrayList<Image>();
    public ArrayList<Image> powerSprites = new ArrayList<Image>();


    public Img(){
        // constructor gets the images that can be done in a loop
        for (int i = 1; i <= 8; i++){   // gets the sprites and scales them accordingly
            enemySprites.add(new ImageIcon("Images/Sprites/Enemy" + i + ".png").getImage().getScaledInstance(30, 32, Image.SCALE_DEFAULT));
        }
        for (int i = 0; i <= 31; i++){
            if (i < 10){
                powerSprites.add(new ImageIcon("Images/Sprites/Powerup_0" + i + ".png").getImage().getScaledInstance(32, 16, Image.SCALE_DEFAULT));

            }
            else{
                powerSprites.add(new ImageIcon("Images/Sprites/Powerup_" + i + ".png").getImage().getScaledInstance(32, 16, Image.SCALE_DEFAULT));
            }
        }
    }

}
