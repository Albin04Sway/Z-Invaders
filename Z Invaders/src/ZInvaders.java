import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;


public class ZInvaders extends JPanel implements ActionListener, KeyListener {

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; //used for aliens
        boolean used = false; //used for bullets

        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns; //32 * 16 = 512px
    int boardHeight = tileSize * rows; //21 * 16 = 512px;
    //JPanel Variables

    Image gokuImg;
    Image freizaImg;
    Image kidBuuImg;
    Image cellImg;
    Image brolyImg;
    Image blastImg;
    Image backgroundImg;
    //Sets Image Variables

    int gokuWidth = tileSize * 2;
    int gokuHeight = tileSize;
    int gokuX = tileSize * columns / 2 - tileSize;
    int gokuY = boardHeight - tileSize * 2;
    int gokuVelocityX = tileSize; //Goku's moving speed
    //Goku

    Block goku;

    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0; //Number of aliens to defeat
    int alienVelocityX = 1; //Aliens move 1 pixel at a time
    //Aliens

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize/3;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10;
    //bullets

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    ArrayList<Image> alienImgArray; //Stores the alien images, so they can be picked at random


        ZInvaders() {
        setPreferredSize(new Dimension (boardWidth, boardHeight)); //Creates Jpane's same dimensions as JFrame
        setBackground(Color.black); //Sets the background colour
        setFocusable(true);
        addKeyListener(this);

        gokuImg = new ImageIcon(getClass().getResource("./goku.png")).getImage();
        freizaImg = new ImageIcon(getClass().getResource("./freiza.png")).getImage();
        kidBuuImg = new ImageIcon(getClass().getResource("./kidbuu.png")).getImage();
        cellImg = new ImageIcon(getClass().getResource("./cell.png")).getImage();
        brolyImg = new ImageIcon(getClass().getResource("./broly.png")).getImage();
        blastImg = new ImageIcon(getClass().getResource("./blast.png")).getImage();
        backgroundImg= new ImageIcon(getClass().getResource("./1background.png")).getImage();
        //Assigns the Images into the variables

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(freizaImg);
        alienImgArray.add(kidBuuImg);
        alienImgArray.add(cellImg);
        alienImgArray.add(brolyImg);
        //Adds the alien images into the ArrayList

        goku = new Block(gokuX, gokuY, gokuWidth, gokuHeight, gokuImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this);
        createAliens();
        gameLoop.start();
        //game timer
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null); //background

        g.drawImage(goku.img, goku.x, goku.y, goku.width, goku.height, null);
        //Goku

        for (int i = 0; i < alienArray.size(); i++ )
        {
            Block alien = alienArray.get(i);

            if (alien.alive)
            {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }
        //Aliens

        g.setColor(Color.cyan);
        for (int i = 0; i < bulletArray.size(); i++)
        {
            Block bullet = bulletArray.get(i);
            if (!bullet.used)
            {
                g.drawImage(bullet.img, bullet.x, bullet.y, bullet.width, bullet.height, null);
            }
        }
        //bullet

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver)
        {
            g.drawString("Game Over: " + String.valueOf(score), 10, 35);
        }
        else
        {
            g.drawString(String.valueOf(score), 10, 35);
        }

        //score
    }

    public void move(){
        for (int i = 0; i < alienArray.size(); i++)
        {
            Block alien = alienArray.get(i);
            if (alien.alive)
            {
                alien.x += alienVelocityX;

                if (alien.x + alien.width >= boardWidth || alien.x <= 0)
                {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    for (int j = 0; j < alienArray.size(); j++)
                    {
                        alienArray.get(j).y += alienHeight;  
                    }
                    //move all aliens down by one row
                }
                //ife alien touches the borders

                if(alien.y >= goku.y)
                {
                    gameOver = true;
                }
            }
        }
        //aliens
        
        for (int i =0; i < bulletArray.size(); i++)
        {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            for (int j = 0; j < alienArray.size(); j++)
            {   
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien))
                {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount -- ;
                    score += 10;
                }
            }
            //bullet collision with aliens
        }
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0); //removes the first element of the array
        }
        //clear bullets
        
        if (alienCount == 0)
        {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2); //cap column at 16/2-2 = 6
            alienRows = Math.min(alienRows + 1, rows - 6); //cap row at 16-6 = 10
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1;
            createAliens();
            //increase the number of aliens in columns and rows by 1
        }
        //next level
        
    }
    //bullets

    public void createAliens(){
        Random random = new Random();
        for (int r = 0; r < alienRows; r++)
        {
            for (int c = 0; c < alienColumns; c++)
            {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c * alienWidth,
                    alienY + r * alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b){
        return  a.x < b.x + b.width &&  //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&  //a's top right corner passes b's top left corner
                a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;   //a's bottom left corner passes b's top left corner

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver)
        {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) //Any key to restart
        {
            goku.x = gokuX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && goku.x - gokuVelocityX >= 0)
        {
            goku.x -= gokuVelocityX; //Moves Goku left 1 tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && goku.x + goku.width + gokuVelocityX <= boardWidth)
        {
            goku.x += gokuVelocityX; //Moves Goku right 1 tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            Block bullet = new Block(goku.x + gokuWidth * 13/32, goku.y, bulletWidth, bulletHeight, blastImg );
            bulletArray.add(bullet);
        }
        
    }
    

    
    
}
