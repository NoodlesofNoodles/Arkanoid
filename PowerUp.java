import java.awt.*;

public class PowerUp {
    // class representing the "power-ups" that can spawn from Bricks in Arkanoid
    // contains information like its speed downwards, if it is still "alive", what kind of power it would give
    private Img images;
    private int x, y;   // x, y position of the power up
    private int type;   // int representing the effect that the power up will give
    private int speed;   // speed that it moves down
    private boolean visible;   // if the power up object is still active


    public PowerUp(int xPos, int yPos, int t){
        // constructor; takes in and sets the x y position, and type
        images = new Img();
        x = xPos;
        y = yPos;
        type = t;
        speed = 2;
        visible = true;
    }

    public void move(){
        // moves the power up downwards

        y += speed;

        if (y >= 600){   // gets rid of the power up if it leaves the game area
            visible = false;
        }
    }
    public boolean collides(Platform plat){
        // checks if the power up collides with the player's paddle and returns true if so
        Rectangle rect = new Rectangle(x, y, 32, 16);   // rect of the power up
        if (plat.getRect().intersects(rect)){
            return true;
        }
        return false;
    }
    public void draw(Graphics g, int curSprite){
        // draws the power up with the corresponding sprite based on its type and position in the animation, passed in as curSprite

        if (type == 3){
            g.drawImage(images.powerSprites.get(curSprite), x, y, null);
        }
        if (type == 1){
            g.drawImage(images.powerSprites.get(curSprite + 8), x, y, null);
        }
        if (type == 0){
            g.drawImage(images.powerSprites.get(curSprite + 16), x, y, null);
        }
        if (type == 4){
            g.drawImage(images.powerSprites.get(curSprite + 24), x, y, null);
        }
    }

    public int getType(){
        return type;
    }
    public boolean getVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }

}
