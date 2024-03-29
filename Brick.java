import java.awt.*;

public class Brick {
    // class representing the blocks the ball can break
    // contains information about the location, if it has been broken yet, it's color, dimensions
    Constants constants;
    private int x, y;   // x, y position of the brick
    private Rectangle rect;   // rect of the brick
    private boolean alive;   // if the brick has been "hit"
    private Color colour;
    private int width;
    private int height;
    // different colours of bricks
    private final Color[] COLS = {new Color(20, 240, 20), new Color(0, 100, 255), new Color(255, 0, 0)};

    public Brick(int x, int y, int col){
        constants = new Constants();
        this.x = x;
        this.y = y;
        this.width = constants.BRICKWIDTH;
        this.height = constants.BRICKHEIGHT;
        this.rect = new Rectangle(this.x, this.y, width, height);
        this.alive = true;
        this.colour = COLS[col -1];
    }

    public void draw(Graphics g){
        // draw the brick
        g.setColor(colour);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public void setAlive(boolean a){
        alive = a;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public Rectangle getRect(){
        return this.rect;
    }
    public boolean getAlive(){
        return alive;
    }
}
