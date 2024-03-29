import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game extends JFrame {
    // creates a JFrame that contains the panel and the game inside it
    GamePanel panel = new GamePanel();
    public Game(){
        super("Arkanoid");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("Images/platform.png").getImage());
        setVisible(true);
        setDefaultCloseOperation(3);
        this.add(panel);
        pack();
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
    }
}

class GamePanel extends JPanel implements ActionListener, MouseListener{
    // GamePanel is a JPanel class where Arkanoid's game loop runs
    // calls all movement/behaviour methods and paints to the screen

    private boolean click;   // boolean representing if the mouse has been released in this loop
    private Constants constants;   // stores the constants in the game for easy access
    private Img images;   // stores the images
    private Timer timer;   // timer controlling the passage of time
    private int screen;   // int representing the screen that the game is on
    private boolean lvlStart;   // if the current level has been started yet; stops everything from moving if false

    // arrays that represent the bricks in each of the levels
    private final int[][] BLVL1 = {{2, 2, 0, 1, 1, 1, 0, 2, 2},
                              {1, 1, 0, 1, 2, 1, 0, 1, 1},
                              {3, 3, 0, 3, 2, 3, 0, 3, 3},
                              {3, 3, 0, 3, 2, 3, 0, 3, 3},
                              {1, 1, 0, 1, 2, 1, 0, 1, 1},
                                {1, 1, 0, 1, 1, 1, 0, 1, 1}};
    private final int[][] BLVL2 = {{0, 1, 2, 3, 1, 3, 2, 1, 0},
                                    {0, 1, 2, 3, 1, 3, 2, 1, 0},
                                    {0, 1, 2, 3, 2, 3, 2, 1, 0},
                                    {0, 1, 2, 3, 2, 3, 2, 1, 0},
                                    {0, 1, 2, 3, 2, 3, 2, 1, 0},
                                    {0, 0, 2, 3, 2, 3, 2, 0, 0},
                                    {0, 0, 0, 3, 1, 3, 0, 0, 0},
                                    {0, 0, 0, 3, 1, 3, 0, 0, 0},
                                    {0, 0, 0, 0, 1, 0, 0, 0, 0},};
    // lists storing the actual Brick objects created based on the arrays above
    private ArrayList<Brick> bricksLVL1;
    private ArrayList<Brick> bricksLVL2;

    private ArrayList<Ball> balls;   // stores all the Ball objects

    private ArrayList<PowerUp> powerUps;   // stores all the PowerUp objects
    private int[] powerTimer = {0, 0, 0, 0};   // each element in the array represents the amount of time the respective powerup has left to run
    private int curPowerSprite;   // number representing the current sprite the power ups are on

    private ArrayList<Enemy> enemies;   // stores all the Enemy objects
    private int curEnemySprite;   // the number of the current sprite of the enemies
    private Platform plat;   // the paddle the user controls
    private int platWidth;   // the current width of the paddle
    int lives;
    int score;
    int highScore;
    int timePassed;   // total time in milliseconds that have passed since the game started running



    public GamePanel() {
        // constructor for the GamePanel, sets all the initial conditions of the game

        constants = new Constants();
        images = new Img();

        this.screen = constants.MENU;   // sets the start screen to be the menu
        click = false;
        setPreferredSize(new Dimension(constants.WIDTH, constants.HEIGHT));
        this.setFocusable(true);
        requestFocus();
        addMouseListener(this);


        balls = new ArrayList<Ball>();
        balls.add(new Ball(200, 590 - constants.BALLSIZE, 5, 5));   // adds the starting Ball

        plat = new Platform(250);   // add the player's paddle
        platWidth = 66;

        // getting the Bricks from the map
        bricksLVL1 = new ArrayList<Brick>();
        bricksLVL2 = new ArrayList<Brick>();
        createBricks(bricksLVL1, BLVL1);
        createBricks(bricksLVL2, BLVL2);

        powerUps = new ArrayList<PowerUp>();
        curPowerSprite = 0;   // sprite starts on sprite 0

        enemies = new ArrayList<Enemy>();
        curEnemySprite = 0;

        lives = constants.LIVES;
        score = 0;
        highScore = 0;
        lvlStart = false;

        timer = new Timer(constants.DELAY, this);
        timer.start();
        timePassed = 0;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // main game loop; movement and drawing is called every loop here

        timePassed += constants.DELAY;   // updates the amount of time that has passed
        if (screen == constants.MENU){
            if (click){
                screen = constants.LVL1;

                createBricks(bricksLVL1, BLVL1);   // re-creates the bricks if they were destroyed in previous playthroughs
                lives = constants.LIVES;
                reset();   // resets the game to its initial conditions
            }
        }

        else if (screen == constants.LVL1){
            if (lives == 0){
                screen = constants.DEATH;

                // try-catch for if the file is missing, other exceptions, etc.
                try {
                    writeScore();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (balls.isEmpty()){   // if there are no more balls in the list, that means the player has lost all of them
                lives -= 1;
                lvlStart = false;   // brings the game back to the "unstarted" state so that the player can choose when to start
                reset();
            }
            if (bricksLVL1.isEmpty()){   // means that the player has hit all the bricks, moving on to the next level
                screen = constants.LVL2;
                createBricks(bricksLVL2, BLVL2);   // re-sets all the bricks in level 2
                reset();
                lvlStart = false;
            }

            if (lvlStart){
                moveLVL(bricksLVL1);   // the movement after the player starts the level
                addEnemy();   // has a chance of adding an enemy every loop
            }
            else{
                if (click){
                    lvlStart = true;
                }
                moveStart();   // the movement in the game before starting the level
            }
            removeBrick(bricksLVL1, 100);   // removes "hit" bricks
            setPower();   // applies the effects of the power ups
            countPower();   // counts the time left for each power up
        }

        else if (screen == constants.LVL2){
            if (lives == 0){
                screen = constants.DEATH;
                try {
                    writeScore();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (balls.isEmpty()){
                lives -= 1;
                lvlStart = false;
                reset();
            }
            if (bricksLVL2.isEmpty()){
                screen = constants.END;
                try {
                    writeScore();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                lvlStart = false;
            }

            if (lvlStart){
                moveLVL(bricksLVL2);
                addEnemy();
            }
            else{
                if (click){
                    lvlStart = true;
                }
                moveStart();
            }
            removeBrick(bricksLVL2, 150);
            setPower();
            countPower();
        }

        else if (screen == constants.DEATH){
            if (click){
                screen = constants.MENU;
            }
        }

        else if (screen == constants.END){
            if (click){
                screen = constants.MENU;
            }
        }

        repaint();
        click = false;   // resets the click state every loop
    }

    @Override
    public void paint(Graphics g) {
        // handles all the painting methods and calls them accordingly

        if (screen == constants.MENU){
            paintMenu(g);
        }
        if (screen == constants.LVL1){
            paintLVL(g, bricksLVL1);
        }
        if (screen == constants.LVL2){
            paintLVL(g, bricksLVL2);
        }
        if (screen == constants.DEATH){
            paintDeath(g);
        }
        if (screen == constants.END){
            paintEnd(g);
        }
    }
    public void drawBackground(Graphics g){
        // draws the background objects that don't interact with the level

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 700);
        for (int i = 0; i < lives - 1; i++){
            g.drawImage(images.platIcon, constants.GAMEBGPOSX + 40*i, constants.HEIGHT - 60, null);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 48));
        g.drawString(String.valueOf(score), 265, 670);

        g.drawImage(images.bgTexture, constants.GAMEBGPOSX, constants.GAMEBGPOSY, null);
        g.drawImage(images.borderLeft, constants.GAMEBGPOSX - images.borderLeft.getWidth(null), constants.GAMEBGPOSY + images.borderTop.getHeight(null), null);
        g.drawImage(images.borderRight, constants.GAMEBGPOSX + images.bgTexture.getWidth(null), constants.GAMEBGPOSY + images.borderTop.getHeight(null), null);
        g.drawImage(images.borderTop, constants.GAMEBGPOSX - images.borderLeft.getWidth(null), constants.GAMEBGPOSY, null);
    }

    // MENU
    public void paintMenu(Graphics g){
        // draws the menu

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 700);
        if (timePassed%1500 > 0 && timePassed%1500 < 1000){   // makes the title bob up and down
            g.drawImage(images.title, 0, 20, null);
        }
        else{
            g.drawImage(images.title, 0, 0, null);
        }
        g.drawImage(images.titleText, 0, 0, null);
    }


    // LVL
    public void moveLVL(ArrayList<Brick> bricks) {
        // controls the behaviour of all the changing parts in a level; the paddle, the ball, the bricks, the enemies, the power ups
        // calls the needed movement and removal methods for them

        Point mx = getMousePosition();
        if (mx != null){
            plat.move(mx.x - platWidth/2);   // moves the platform to the player's mouse position
        }
        for (int i = balls.size()-1; i >= 0; i--){
            if (!balls.get(i).getVisible()){   // removes the balls that have gone off the screen, which have their "visible" field set to false
                balls.remove(i);
            }
            else{
                balls.get(i).move(plat.getRect(), bricks);   // moves the balls
            }
        }
        for (int i = powerUps.size()-1; i >= 0; i--){
            if (!powerUps.get(i).getVisible()){
                powerUps.remove(i);
            }
            else{
                powerUps.get(i).move();

                if (powerUps.get(i).collides(plat)){
                    powerUps.get(i).setVisible(false);
                    powerTimer[powerUps.get(i).getType()] = constants.DELAY * 500;   // if a collision with a power up occurs, adds time to the respective timer
                }
            }
        }
        for (int i = enemies.size() - 1; i >= 0; i--){
            if (!enemies.get(i).getVisible()){
                enemies.remove(i);
            }
            else{
                if (enemies.get(i).isPastBricks(bricks)){
                    enemies.get(i).moveAfterBricks(plat);   // switches to the movement pattern enemies use when it no longer needs to avoid bricks
                }
                else{
                    enemies.get(i).moveInBricks(bricks, plat);   // the movement pattern the enemy uses when there are still bricks it needs to go around
                }

                if (enemies.get(i).collides(balls, plat)){
                    enemies.get(i).setVisible(false);
                    score += 200;   // adds extra score for killing enemies
                }
            }

        }
    }

    public void moveStart(){
        // lets the player choose when and where to release the ball

        Point mx = getMousePosition();
        if (mx != null){
            plat.move(mx.x - platWidth/2);

            if (mx.x >= constants.GAMEBGPOSX + platWidth/ 2 && mx.x <= constants.GAMEBGPOSX + constants.GAMEWIDTH - platWidth/ 2) {
                balls.get(0).setX(mx.x - constants.BALLSIZE/2);   // moves the ball with the paddle
            }
        }
    }

    public void removeBrick(ArrayList<Brick> bricks, int scorePerBrick){
        // gets rid of "hit" bricks and adds to the score, and decides whether to spawn a power up

        for (int i = bricks.size() - 1; i >= 0; i--){
            if (!bricks.get(i).getAlive()){
                score += scorePerBrick;

                double powerChance = Math.random();
                if (powerChance > 0.85){   // fifteen percent chance to spawn power up
                    int power = (int) Math.floor(Math.random()*4);    // equal chance for each type of power up

                    if (power == constants.EXTEND){
                        PowerUp tempPower = new PowerUp(bricks.get(i).getX(), bricks.get(i).getY(), constants.EXTEND);
                        powerUps.add(tempPower);
                    }
                    if (power == constants.NEWBALL){
                        PowerUp tempPower = new PowerUp(bricks.get(i).getX(), bricks.get(i).getY(), constants.NEWBALL);
                        powerUps.add(tempPower);
                    }
                    if (power == constants.SLOWBALL){
                        PowerUp tempPower = new PowerUp(bricks.get(i).getX(), bricks.get(i).getY(), constants.SLOWBALL);
                        powerUps.add(tempPower);
                    }
                    if (power == constants.NOCOLLIDE){
                        PowerUp tempPower = new PowerUp(bricks.get(i).getX(), bricks.get(i).getY(), constants.NOCOLLIDE);
                        powerUps.add(tempPower);
                    }
                }
                bricks.remove(i);
            }

        }
    }

    public void paintLVL(Graphics g, ArrayList<Brick> brickList){
        // does all the drawing needed in the levels

        drawBackground(g);
        for (int i = brickList.size() - 1; i >= 0; i--){
            brickList.get(i).draw(g);
        }
        for (int i = powerUps.size() - 1; i >= 0; i--){
            if (timePassed%(constants.DELAY*10) == 0){
                if (curPowerSprite < 7){
                    curPowerSprite += 1;   // changing the sprite of the power up every ten loops
                }
                else{
                    curPowerSprite = 0;
                }
            }
            powerUps.get(i).draw(g, curPowerSprite);

        }
        for (int i = 0; i < balls.size(); i++){
            balls.get(i).draw(g);
        }
        for (Enemy enemy:enemies){
            if (timePassed%(constants.DELAY*10) == 0){
                if (curEnemySprite < 7){
                    curEnemySprite += 1;   // changing the enemy sprite
                }
                else{
                    curEnemySprite = 0;
                }
            }
            enemy.draw(g, images.enemySprites.get(curEnemySprite));
        }
        plat.draw(g);
    }

    // end
    public void paintEnd(Graphics g){
        // draws the ending screen that appears once beating all levels

        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 600, 700);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 72));
        g.drawString("YOU WON!", 150, 200);
        g.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 36));
        g.drawString("Score: " + String.valueOf(score), 220, 350);
        g.setColor(Color.CYAN);
        g.drawString("High Score: " + String.valueOf(highScore), 190, 300);
        g.drawString("CLICK TO CONTINUE", 150, 600);

    }

    // death
    public void paintDeath(Graphics g){
        // draws death screen

        g.setColor(new Color(240, 40, 40));
        g.fillRect(0, 0, 600, 700);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 72));
        g.drawString("YOU DIED!", 165, 200);
        g.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 36));
        g.drawString("Score: " + String.valueOf(score), 240, 350);
        g.setColor(Color.YELLOW);
        g.drawString("High Score: " + String.valueOf(highScore), 190, 300);
        g.drawString("CLICK TO CONTINUE", 150, 600);
    }

    // other
    public void createBricks(ArrayList<Brick> bricks, int[][] map){
        // adds new Bricks to the bricks list based on the entered arrays

        bricks.clear();
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map[i].length; j++){
                if (map[i][j] != 0){
                    Brick tempBrick = new Brick(j*constants.BRICKWIDTH + constants.GAMEBGPOSX + (500 - 9* constants.BRICKWIDTH)/2, i*constants.BRICKHEIGHT + constants.GAMEBGPOSY + 60, map[i][j]);
                    bricks.add(tempBrick);
                }

            }
        }
    }

    public void addEnemy(){
        // has a chance of adding a new Enemy to the list

        double chance = Math.random();
        double location = Math.random();
        if (chance > 0.997){   // 0.3% chance of spawning an enemy in each loop
            if (location > 0.5){   // spawns the enemies in two different places
                enemies.add(new Enemy(100, 40));
            }
            else{
                enemies.add(new Enemy(400, 40));
            }
        }
    }

    public void reset(){
        // clears all the balls, powerups, and enemies, and put the paddle and ball back in the middle of the screen
        // i.e. resets to initial conditions

        Arrays.fill(powerTimer, 0);
        powerUps.clear();
        enemies.clear();
        balls.clear();
        plat.setX(constants.WIDTH/2);
        balls.add(new Ball(constants.WIDTH/2 + 33, 590 - constants.BALLSIZE, 5, 5));
    }

    public void countPower(){
        // counts down the time left for each power up

        for (int i = 0; i < powerTimer.length; i++){
            if (powerTimer[i] > 0){
                powerTimer[i] -= constants.DELAY;
            }
        }
    }
    public void setPower(){
        // applies the effect of each power up

        if (powerTimer[constants.EXTEND] > 0){   // extends the paddle
            plat.setWidth(96);
            plat.setSprite(1);   // changes the sprite of the paddle to match the length
        }
        else{
            plat.setWidth(66);
            plat.setSprite(0);
        }

        if (powerTimer[constants.SLOWBALL] > 0){   // slows the ball down
            for (Ball ball : balls) {
                ball.setSpeed(2);
            }
        }
        else{
            for (Ball ball : balls) {
                ball.setSpeed(5);
            }
        }
        if (powerTimer[constants.NEWBALL] == constants.DELAY*500){   // adds two new balls in play
            balls.add(new Ball(300, 500, -5, -5));  // differing velocities make them go in different directions
            balls.add(new Ball(300, 400, 5, -5));
        }
        if (powerTimer[constants.NOCOLLIDE] > 0){   // makes it so that the balls do not bounce when hitting bricks
            for (Ball ball: balls){
                ball.setBrickCollision(false);
                ball.setColor(new Color(130, 220, 30));   // makes the ball green as an indicator
            }
        }
        else if(powerTimer[constants.NOCOLLIDE] == 0){
            for (Ball ball: balls){
                ball.setBrickCollision(true);
                ball.setColor(new Color(0, 200, 200));
            }
        }

    }

    public void writeScore() throws IOException {
        // gets all the scores in the highscore file and writes the current score to it if it is higher than the last one

        Scanner highScores = new Scanner(new File("highscores.txt"));
        ArrayList<Integer> scores = new ArrayList<Integer>();
        while (highScores.hasNext()){
            scores.add(Integer.valueOf(highScores.nextLine()));
        }
        highScore = scores.get(scores.size() - 1);
        if (score > scores.get(scores.size() - 1)){
            scores.add(score);
            highScore = score;
        }
        PrintWriter scoreWriter = new PrintWriter(new BufferedWriter(new FileWriter("highscores.txt")));

        for (int s : scores){
            scoreWriter.println(s);   // re-writing all the scores to the highscore file
        }
        scoreWriter.close();
    }


    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {
        click = true;
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
