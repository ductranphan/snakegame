
//PROGRAM STARTS
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.FileNotFoundException;  
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Snake extends JFrame implements ActionListener, KeyListener {

//Declaring variables for the game
   public static final int CANVAS_WIDTH = 600;                  //Grid width
   public static final int CANVAS_HEIGHT = 600;                  //Grid heigth
   public static final int UNIT_SIZE = 25;                        //Length of the tiles in the game
   public static final int GAME_UNIT = (CANVAS_WIDTH * CANVAS_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);      //Number of tiles of the whole grid
   public static final int DELAY = 75;             //Speed of the game 
   public int body = 1;            //Size of the snake
   char direction = 'P';            //Direction of the snake
   int appleX;               //X-coordiantes of the apple
   int appleY;                //Y-coordiantes of the apple
   Timer timer;                    //Timer for the game
   Random random;                //Randome intialized

   public Shape[] shapes = new Shape[2];           //Apple being declared as a Shape

//Delaring STATE of the game 
   private static int STATE = -1;     
   public static final int PLAY = 1;
   public static final int END = 2;
   public static final int MENU = 3;
   public static final int WIN = 4;
   public static final int INTRO = 5;

   ArrayList<Integer> keys = new ArrayList<Integer>();    //keys arraylist

   private Rectangle2D.Double[] snake;       //An array for the snake's bodies

   private DrawCanvas canvas;           //Game's canvas

//Declare buttons
   JButton btnStart, btnIntro, btnMenu, btnLeader;    

   private int score = 0;        //Game's score   
   private int highScore = 1;
   private String HS; 
   private String nameHighScore;
//Declare images 
   BufferedImage Apple;
   BufferedImage snakeImg;
   BufferedImage snakeDown;
   BufferedImage snakeUp;
   BufferedImage snakeLeft;
   BufferedImage snakeRight;
   BufferedImage loseScreen;   
//Declare musics
   Clip munch;
   Clip MenuMusic;
   Clip GameMusic;
   Clip IntroMusic;
   Clip LoseMusic;
   
   private static final String HIGHSCORE_FILE = "HighScore.txt";

//Method to randomize the apple's location
   public void random() {
      appleX = (int) (Math.random() * (CANVAS_WIDTH / UNIT_SIZE)) * UNIT_SIZE;    //Initialize apple's X-coordinates
      appleY = (int) (Math.random() * (CANVAS_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;    //Initialize apple's Y-coordinates
   
   //If the location is on the snake's body, it will be initialize again until it's not anymore
      for (int i = 0; i < body - 1; i++) {
         if (appleX == snake[i].x && appleY == snake[i].y) {
            appleX = (int) (Math.random() * (CANVAS_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = (int) (Math.random() * (CANVAS_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
         }
      }
      shapes[1] = new Rectangle2D.Double(appleX, appleY, UNIT_SIZE, UNIT_SIZE); //Intialize the apple's location and its size
   }

//Method to intialize the snake's location and bodies
   public void Snob() {
      snake = new Rectangle2D.Double[GAME_UNIT];            //Declare snake's bodies array
      snake[0] = new Rectangle2D.Double(UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);     //When game starts, the snake head will be at the top left
      
      //Each of snake's bodies will have the coordiantes of the one before it
      for (int i = 1; i < body; i++) {
         snake[i] = new Rectangle2D.Double(snake[0].x - (i * UNIT_SIZE), snake[0].y, UNIT_SIZE, UNIT_SIZE);
      }
   }

//Method to reset the body when player eats an apple
   public void reset() {
   //Set the bodies' coordinates
      snake[body - 1] = new Rectangle2D.Double(snake[body - 2].x, snake[body - 2].y, UNIT_SIZE, UNIT_SIZE);
   }

//Constructor method
   public Snake() {
   
   //Load Images and resize them
      Apple = loadImage("images/apple.png");
      Apple = resizeImage(Apple, UNIT_SIZE, UNIT_SIZE);
   
      snakeDown = loadImage("images/snakeDown.png");
      snakeDown = resizeImage(snakeDown, UNIT_SIZE, UNIT_SIZE);
   
      snakeUp = loadImage("images/snakeUp.png");
      snakeUp = resizeImage(snakeUp, UNIT_SIZE, UNIT_SIZE);
   
      snakeLeft = loadImage("images/snakeLeft.png");
      snakeLeft = resizeImage(snakeLeft, UNIT_SIZE, UNIT_SIZE);
   
      snakeRight = loadImage("images/snakeRight.png");
      snakeRight = resizeImage(snakeRight, UNIT_SIZE, UNIT_SIZE);
   
      loseScreen = loadImage("images/loseScreen.png");
      loseScreen = resizeImage(loseScreen, UNIT_SIZE * 8, UNIT_SIZE * 8);
   
   //Load audio and resize them
      munch = MixerAudio.load("images/munch4.wav");
      MenuMusic = MixerAudio.load("images/MenuSound.wav");
      GameMusic = MixerAudio.load("images/GameMusic.wav");
      IntroMusic = MixerAudio.load("images/IntroMusic.wav");
      LoseMusic = MixerAudio.load("images/WinMusic.wav");
   
   //Game starts at menu screen
      STATE = MENU;
   
   //Starts timer
      timer = new Timer(DELAY, this);
      timer.start();
   
   //Set up random location for apple and snake's location
      random();
      Snob();
   
   //Declare button panel 
      JPanel btnPanel = new JPanel(new FlowLayout());
   
   //Initialize buttons
      btnStart = new JButton("Start/Restart");
      btnIntro = new JButton("Instructions");
      btnMenu = new JButton("Menu");
      btnLeader = new JButton("Leaderboard");
   
   //Add buttons to panel 
      btnPanel.add(btnStart);
      btnPanel.add(btnIntro);
      btnPanel.add(btnMenu);
      btnPanel.add(btnLeader);
   
   //Add buttons' functions
      btnStart.addActionListener(this);
      btnIntro.addActionListener(this);
      btnMenu.addActionListener(this);
      btnLeader.addActionListener(this);
   
   //draw canvas 
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      canvas = new DrawCanvas();
      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
   
   //Add canvas and button at bottom of screen
      cp.add(btnPanel, BorderLayout.SOUTH);
      cp.add(canvas);
      
   //Add other functions for the window
      addKeyListener(this);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Snake Game");
   
      pack();
      setVisible(true);
   }

//Method to check collisions
   public static boolean isHitDetected(Shape a, Shape b) {
      Area areaA = new Area(a);
      areaA.intersect(new Area(b));
      return !areaA.isEmpty();
   }

//Method to update game's coordinates
   public void updateCoordinates() {
   
   //When the game is being played, keys will be used to move the snake around
      if (STATE == PLAY) {
         if (keys.contains(KeyEvent.VK_RIGHT) || keys.contains(KeyEvent.VK_D)) {
            direction = 'R'; 
         } 
         else if (keys.contains(KeyEvent.VK_LEFT) || keys.contains(KeyEvent.VK_A)) {
            direction = 'L';
         
         } 
         else if (keys.contains(KeyEvent.VK_DOWN) || keys.contains(KeyEvent.VK_S)) {
            direction = 'D';
         } 
         else if (keys.contains(KeyEvent.VK_UP) || keys.contains(KeyEvent.VK_W) ) {
            direction = 'U';
         }
         
         //If the snake eats the apple
         if (isHitDetected(shapes[1], snake[0])) {
            MixerAudio.play(munch);         //Play eat audio
            score++;             //Score increase by 1
            body++;             //Snake's length increase by 1
            reset();           //Update another snake's body 
            random();         //Initialize new apple's location
         }
      
      //Updates snake's bodies location
         for (int i = body - 1; i > 0; i--) {
            snake[i].x = snake[i - 1].x;
            snake[i].y = snake[i - 1].y;
         }
      
      //Switch case used to make the snake stay going the same directio without stopping unless other key is pressed
         switch (direction) {
            case 'U':
               snake[0].y -= UNIT_SIZE;        //Snake moves up
               break;
            case 'D':
               snake[0].y += UNIT_SIZE;        //Snake moves down
               break;
            case 'L':
               snake[0].x -= UNIT_SIZE;        //Snake moves left
               break;
            case 'R':
               snake[0].x += UNIT_SIZE;        //Snake moves right
               break;
         }
      
      //If the snake collides with itself when its length is more than 2, player loses
         for (int i = body - 1; i > 2; i--) {
            if (body > 2 && isHitDetected(snake[i], snake[0])) {
               STATE = END;              
               stopClip(GameMusic);    
               MixerAudio.play(LoseMusic);    //Play losing music :(( 
            }
         }
      
       //If the snake collides with wall, player loses
         if (snake[0].x >= CANVAS_WIDTH || snake[0].x < 0 || snake[0].y < 0 || snake[0].y >= CANVAS_HEIGHT) {
            STATE = END;
            stopClip(GameMusic);
            MixerAudio.play(LoseMusic);
         }
         
      }
   }

//Method when actions are performed
   public void actionPerformed(ActionEvent e) {
   
   //When game starts, update method starts
      if (e.getSource() == timer) {
         updateCoordinates();         
      } 
      
      //Button menu lets player go back to menu 
      else if (e.getSource() == btnMenu && STATE != PLAY){
         stopClip(IntroMusic);
         stopClip(LoseMusic); 
         stopClip(GameMusic);
         STATE = MENU;
      }
      
      //Exit button exits
      else if (e.getSource() == btnLeader) {
         stopClip(IntroMusic);
         stopClip(LoseMusic); 
         stopClip(GameMusic);
         stopClip(MenuMusic);
         STATE = WIN;
      }
      
      //Start button starts/restarts the game
      else if (e.getSource() == btnStart) {
         stopClip(MenuMusic);
         stopClip(IntroMusic);
         stopClip(LoseMusic); 
         STATE = PLAY;
         
         //resets everything
         score = 0;
         body = 1;
         direction = 'P';
         random();
         Snob(); 
         updateCoordinates();
         
      //Intro button switches to Instructions screen
      } else if (e.getSource() == btnIntro && STATE != PLAY) {
         stopClip(MenuMusic);  
         stopClip(GameMusic);
         stopClip(LoseMusic);
         STATE = INTRO;
         MixerAudio.play(IntroMusic); 
      }
      if (STATE == END) {
         if (score > highScore) {
            String playerName = JOptionPane.showInputDialog("Congratulations! You've achieved the highest score. Enter your name:");
            highScore = score;
            nameHighScore = playerName;
            writeHighScore(highScore, nameHighScore);
         } else {
            readHighScore();
         }
      }         
      requestFocus();
      canvas.repaint();
   }

//Drawing class 
   class DrawCanvas extends JPanel {
   
   //Set up font, paint, draw stuff
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;
         g2d.setFont(new Font("TimesRoman", Font.PLAIN, 40));
      
      //If player loses
         if (STATE == END) {
         //Draws screen when lost
            setBackground(Color.WHITE);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 80));
            g2d.drawString("You Lost", (CANVAS_WIDTH / 2) - 150, 250);         
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 40));
            g2d.drawString("Score is: " + score, CANVAS_WIDTH / 2 - 85, (CANVAS_HEIGHT / 2) + 50);  //Display score
            g2d.drawImage(loseScreen, 200, 400, null);
         }
          
         else if (STATE == WIN){
            setBackground(Color.WHITE);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 40));
            g2d.drawString("Leaderboard: ", (CANVAS_WIDTH / 2) - 200, 250);         
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g2d.drawString("#1 " + nameHighScore + ": " + highScore, CANVAS_WIDTH / 2 - 200, (CANVAS_HEIGHT / 2) + 50);  //Display score            
         }
         //If player starts at menu screen or press menu button, draw menu screen
         else if (STATE == MENU) {
            MixerAudio.play(MenuMusic);
            g2d.setFont(new Font("Jokerman", Font.PLAIN, 80));
            setBackground(Color.BLACK);
            g2d.setColor(Color.GREEN);
            g2d.drawString("Snake Game", 75, 250);
            snakeRight = resizeImage(snakeRight, UNIT_SIZE * 8, UNIT_SIZE * 8);
            Apple = resizeImage(Apple, UNIT_SIZE * 6, UNIT_SIZE * 6);
         
            g2d.drawImage(snakeRight, 0, 350, null);
            g2d.drawImage(Apple, 450, 370, null);
         
         //Resize images back to normal
            snakeRight = resizeImage(snakeRight, UNIT_SIZE, UNIT_SIZE);
            Apple = resizeImage(Apple, UNIT_SIZE, UNIT_SIZE);
         }
         //If player press instructions, draw instruction screem
         else if (STATE == INTRO) {
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            setBackground(Color.BLACK);
            g2d.setColor(Color.GREEN);
            g2d.drawString("OBJECTIVE:", 25, 75);
            g2d.drawString("  - Guide the snake to eat apple and grow longer", 25, 100);
            g2d.drawString("CONTROLS:", 25, 150);
            g2d.drawString("  - Move using arrow keys or 'W', 'A', 'S', 'D'", 25, 175);
            g2d.drawString("GAMEPLAY:", 25, 225);
            g2d.drawString("  - The snake moves continuously in the direction of the last", 25, 250);
            g2d.drawString("pressed arrow key", 25, 275);
            g2d.drawString("  - Eating apple increases the snake's length", 25, 300);
            g2d.drawString("  - Avoid crashing into the walls or the snake's own body", 25, 325);
            g2d.drawString("WINNING:", 25, 375);
            g2d.drawString("  - Reach the highscore", 25, 400);
            g2d.drawString("LOSING:", 25, 450);
            g2d.drawString("  - Game ends if the snake collides with itself or the walls", 25, 475);
            g2d.drawString(" ***CAN'T PRESS ''Instructions'' WHEN PLAYING", 25, 525);
         } 
         if (STATE == MENU) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g2d.drawString("High Score belongs to " + nameHighScore+ ": " + highScore, 0, CANVAS_HEIGHT - 5);
         }
         //If player press play, draw the game screen
         else if (STATE == PLAY) {
            MixerAudio.play(GameMusic);
            setBackground(Color.BLACK);
            g2d.setColor(Color.GRAY);
         
         //Draw the body as it increases
            for (int i = 1; i < body; i++) {
               g2d.setPaint(Color.GREEN);
               g2d.fillOval((int) snake[i].x, (int) snake[i].y, UNIT_SIZE, UNIT_SIZE);
            }
         
         //Draws the score 
            g2d.setPaint(Color.WHITE);
            g2d.drawString("Score: " + score, 50, 50);            
         //Draws the image for apple
            g2d.drawImage(Apple, appleX, appleY, null);
         
         //Draws 4 different directions of the head when the snake is going to each directions
            if (direction == 'R') {
               g2d.drawImage(snakeRight, (int) snake[0].x, (int) snake[0].y, null);
            } else if (direction == 'L') {
               g2d.drawImage(snakeLeft, (int) snake[0].x, (int) snake[0].y, null);
            } else if (direction == 'D') {
               g2d.drawImage(snakeDown, (int) snake[0].x, (int) snake[0].y, null);
            } else if (direction == 'U') {
               g2d.drawImage(snakeUp, (int) snake[0].x, (int) snake[0].y, null);
            }
            else if (direction == 'P') {
               g2d.drawImage (snakeRight, (int) snake[0].x, (int) snake[0].y, null);
            }
         }
      }
   }

//Key pressed method
   public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      //IF the snake body is more than 1, the head of the snake can't turn back 180 degrees and hit its own body
      if (snake[1] != null){
         if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && direction != 'D' && snake[0].x != snake[1].x) {
            direction = 'U';
         } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && direction != 'U' && snake[0].x != snake[1].x) {
            direction = 'D';
         
         } else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && direction != 'R' && snake[0].y != snake[1].y) {
            direction = 'L';
         
         } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && direction != 'L' && snake[0].y != snake[1].y) {
            direction = 'R';
         }
      }
      //If the body is only 1, the snake can turn 180 degrees (since there's no body)
      else {
         if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W)) {
            direction = 'U';
         } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
            direction = 'D';
         
         } else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) ) {
            direction = 'L';
         
         } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && direction != 'L' ) {
            direction = 'R';
         }
      }  
   }
   
   //Key released method
   public void keyReleased(KeyEvent evt) {
      if (keys.contains(evt.getKeyCode())) {
         keys.remove(Integer.valueOf(evt.getKeyCode()));
      }
   }

   public void keyTyped(KeyEvent e) {
   }

//Main method of the game
   public static void main(String[] args) {
      Snake game = new Snake();
      game.HS = game.readHighScore();
   }

//Method for the images
   public static BufferedImage loadImage(String path) {
      BufferedImage temp = null;
      try {
         temp = javax.imageio.ImageIO.read(new java.io.File(path));
      } catch (IOException e) {
         System.out.println("Unable to load image " + path);
         e.printStackTrace();
      }
      return temp;
   }

//Method for resizing the images
   public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
      Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
      BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = outputImage.createGraphics();
      g2d.drawImage(resultingImage, 0, 0, null);
      g2d.dispose();
      return outputImage;
   }

//Method for stopping the music 
   public static void stopClip(Clip clip) {
      if (clip != null && clip.isRunning()) {
         clip.stop();
      }
   }

   public String readHighScore() {
      String highScoreData = "0 NoName";  // Default value if file not found
      try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
         String line = reader.readLine();
         if (line != null) {
            highScoreData = line;
         }
      } catch (FileNotFoundException e) {
         System.out.println("HighScore file not found.");
      } catch (IOException e) {
         System.out.println("An error occurred while reading the HighScore file.");
         e.printStackTrace();
      }
      String[] parts = highScoreData.split(" ");
      highScore = Integer.parseInt(parts[1]);
      nameHighScore = parts[0];
      return highScoreData;
   }   
   private void writeHighScore(int highScore, String playerName) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
         writer.write(nameHighScore + " " + highScore);
      
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
//PROGRAM ENDS