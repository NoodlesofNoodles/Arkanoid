import java.awt.*;

public class Platform {
    // class representing the paddle that the player controls
    // contains information on its location, its bounding rectangle, sprite
    Constants constants;
    Img images;
    private int x, y;   // x and y position on the screen
    private Rectangle rect;
    private int width, height;   // width and height of the paddle
    private int sprite;   // the current sprite to be drawn of the paddle
    public Platform(int x){
        // constructor of the paddle; sets the starting location of the paddle to the passed in x value

        constants = new Constants();
        images = new Img();

        this.x = x;
        this.y = 590;
        this.width = 66;
        this.height = constants.PLATHEIGHT;
        this.rect = new Rectangle(this.x, this.y, this.width, this.height);
        sprite = 0;
    }

    public void move(int mx){
        // moves the platform to the passed in x value of the player's mouse, centered
        // will not move past the bounds of the game area

        if (mx >= constants.GAMEBGPOSX && mx <= constants.GAMEBGPOSX + constants.GAMEWIDTH - width){
            this.x = mx;
        }
    }
    public void draw(Graphics g){
        // draws the platform sprite with the passed in Graphics object

        if (sprite == 0){
            g.drawImage(images.plat,x, y, null);
        }
        if (sprite == 1){
            g.drawImage(images.platLong, x, y, null);
        }
    }

    public Rectangle getRect(){
        // updates the rect of the platform and then returns it
        this.rect = new Rectangle(this.x, this.y, this.width, this.height);
        return this.rect;
    }
    public void setX(int newX){
        x = newX;
    }
    public void setWidth(int newWidth){
        width = newWidth;
    }
    public void setSprite(int s){
        sprite = s;
    }   // changes the sprite of the platform, used to change between the long and short platform sprites
}
