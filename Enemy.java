import java.awt.*;
import java.util.ArrayList;

public class Enemy {
    // class representing the enemies in Arkanoid, containing information about their location, patterns of movement, speed, and direction
    private  Constants constants;
    private int x, y;
    private Rectangle rect;
    private int width, height;
    private int speed;   // velocity of the enemy
    private boolean blocked;   // boolean for if the enemy can no longer move down because of a brick in its way
    private int dir;   // the int representation for the direction the enemy goes if it is blocked
    private boolean visible;   // if the enemy has been "killed" or gone offscreen yet

    public Enemy(int spawnX, int spawnY){
        // constructor for the Enemy class; sets the x and y position based on the passed in parameters
        constants = new Constants();

        x = spawnX;
        y = spawnY;
        speed = 3;
        blocked = false;
        dir = 0;
        width = 30;
        height = 32;
        rect = new Rectangle(x, y, width, height);
        visible = true;
    }

    public void moveInBricks(ArrayList<Brick> bricks, Platform plat){
        // controls the movement of the enemy when it needs to avoid bricks in its way

        if (!checkBlock(bricks)){   // if it is not blocked by bricks, move straight down
            if (blocked){   // if it was blocked before and is no longer blocked, continue on with moving down
                blocked = false;
                dir = 0;
            }
            y += speed;
        }
        else{
            if (!blocked){   // if the enemy was not blocked before, it is now blocked
                blocked = true;
                if (plat.getRect().x >= x){   // chooses a direction to go in based on the player's position
                    dir = 3;
                }
                else{
                    dir = -3;
                }
            }
        }
        if (blocked){   // moves horizontally if blocked
            x += dir;
        }

    }
    public void moveAfterBricks(Platform plat){
        // controls movement of enemies when they don't have bricks to avoid
        // they go directly towards the player

        Point platPoint = new Point(plat.getRect().x, plat.getRect().y);
        // calculating how much to add to the x and y values
        if (x > platPoint.x){
            x -= speed *Math.cos(Math.PI/3);
            y += speed *Math.sin(Math.PI/3);
        }
        else if (x < platPoint.x){
            x += speed *Math.cos(Math.PI/3);
            y += speed *Math.sin(Math.PI/3);
        }
        else{
            y += speed;
        }

        if (y >= constants.GAMEBGPOSY + constants.GAMEHEIGHT){   // removes the enemy if it's out of the game area
            visible = false;
        }
    }
    public boolean checkBlock(ArrayList<Brick> bricks){
        // checks if a brick is blocking the enemy's movement

        rect = new Rectangle(x, y, width, height);
        Rectangle nextRect = new Rectangle(x, y + speed, width, height);   // the rect of the enemy if it were to continue at the current speed
        for (Brick brick: bricks){
            if (nextRect.intersects(brick.getRect())){
                return true;
            }
        }
        return false;
    }

    public boolean isPastBricks(ArrayList<Brick> bricks){
        // checks if the enemy has any bricks below it that it would need to avoid and returns true if so
        for (Brick brick: bricks){
            if (brick.getY() >= y){
                return false;
            }
        }
        return true;
    }

    public boolean collides(ArrayList<Ball> balls, Platform plat){
        // checks if an enemy collides with any of the things that would destroy it, and returns true if it does

        rect = new Rectangle(x, y, width, height);   // updates the rectangle
        for (Ball ball : balls){
            if (ball.getRect().intersects(rect)){
                return true;
            }
        }
        if (plat.getRect().intersects(rect)){
            return true;
        }
        return false;
    }

    public void setVisible(boolean vis){
        visible = vis;
    }   // sets the "visible" variable based on the passed in value
    public boolean getVisible(){
        return visible;
    }

    public void draw(Graphics g, Image curSprite){
        // draws the enemy using the passed in image and Graphics

        g.setColor(Color.RED);
        g.drawImage(curSprite, x, y, null);
    }
}
