import java.awt.*;
import java.util.ArrayList;

public class Ball {
    // class representing the ball
    // contains information about its movement, location, speed, color, collision behaviour
    private Constants constants;
    private int x,y;   // x and y position of the ball
    private int vx, vy;   // velocity in the x and y directions
    private Rectangle rect;   // rectangle surrounding the ball
    private int width, height;   // width and height of the ball
    private boolean visible;   // false if the ball should be removed
    private boolean brickCollision;   // if the ball should bounce when hitting a brick
    private Color color;   // colour of your ball

    public Ball(int x, int y, int v1, int v2){
        // constructor for Ball
        // sets initial x, y, velocity based on the passed in parameters

        constants = new Constants();
        this.x = x;
        this.y = y;
        this.vx = v1;
        this.vy = v2;
        width = constants.BALLSIZE;
        height = constants.BALLSIZE;
        rect = new Rectangle(this.x, this.y, this.width, this.height);
        visible = true;
        brickCollision = true;
        color = new Color(0, 200, 200);
    }

    public void move(Rectangle plat, ArrayList<Brick> bricks){
        // controls the movement of the ball: moving based on the velocity and when to bounce

        // gets points of collision of the ball
        Point ballTop = new Point(x + width/2, y);
        Point ballBot = new Point(x + width/2, y + height);
        Point ballLeft = new Point(x, y + height/2);
        Point ballRight = new Point(x + width, y + height/2);

        // bouncing on the walls
        if (this.x >= constants.GAMEBGPOSX + constants.GAMEWIDTH - this.width || this.x <= constants.GAMEBGPOSX){
            bounceHorizontal();
        }
        if (this.y <= constants.GAMEBGPOSY){
            bounceVertical();
        }

        if (this.y >= constants.GAMEBGPOSY + constants.HEIGHT - this.height){   // set the ball to invisible if the ball goes off of the game area
            visible = false;
        }

        // collision with the paddle
        if (plat.contains(ballTop)){
            y = plat.y + plat.height + 1;   // places the ball back on top of the paddle to prevent clipping glitches with the platform
            bounceVertical();
        }
        else if(plat.contains(ballBot)){
            y = plat.y - height - 1;
            bounceVertical();
        }
        else if (plat.contains(ballRight)){
            x = plat.x - width - 1;
            bounceHorizontal();
        }
        else if (plat.contains(ballLeft)){
            x = plat.x + plat.width + 1;
            bounceHorizontal();
        }

        // collision with bricks
        for (int i = 0; i < bricks.size(); i++){
            Brick curBrick = bricks.get(i);
            Rectangle curRect = curBrick.getRect();   // rectangle of the current brick

            if (curRect.contains(ballTop) || curRect.contains(ballBot)){
                if (brickCollision){
                    bounceVertical();   // only bounces when brick collision is true
                }
                curBrick.setAlive(false);
                bricks.set(i, curBrick);   // sets the brick to be "hit"
            }
            if (curRect.contains(ballRight) || curRect.contains(ballLeft)){
                if (brickCollision){
                    bounceHorizontal();
                }
                curBrick.setAlive(false);
                bricks.set(i, curBrick);
            }
        }
        // move the ball
        this.x += this.vx;
        this.y += this.vy;
    }
    public void bounceHorizontal(){
        // changes the x velocity
        this.vx *= -1;
    }
    public void bounceVertical(){
        // changes the y velocity
        this.vy *= -1;
    }

    public void draw(Graphics g){
        // draws the ball
        g.setColor(color);
        g.fillOval(this.x, this.y, this.width, this.height);
    }

    public boolean getVisible(){
        return visible;
    }
    public Rectangle getRect(){
        rect = new Rectangle(x, y, width, height);   // update the rect
        return rect;
    }

    public void setX(int newX){
        x = newX;
    }
    public void setSpeed(int speed){
        // changes the magnitude of the velocity of the ball
        if (vx > 0){
            vx = speed;
        }
        else{
            vx = -speed;
        }
        if (vy > 0){
            vy = speed;
        }
        else{
            vy = -speed;
        }
    }
    public void setColor(Color color){
        this.color = color;
    }
    public void setBrickCollision(boolean collision){
        brickCollision = collision;
    }
}
