import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import util.UnitTests;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */ 



public class MainWindow {
	 private static  JFrame frame = new JFrame("Scrape The Sky");   // Change to the name of your game
	 private static   Model gameworld= new Model();
	 private static   Viewer canvas = new  Viewer( gameworld);
	private static KeyListener Controller =new Controller()  ;
	 private static   int TargetFPS = 100;
	 private static boolean startGame= false;
	private static boolean highScoreCheck= false;
	private static   JLabel BackgroundImageForStartMenu ;
	private static final JTextArea highscorestext = new JTextArea();
	static JButton startMenuButton = new JButton("Start Game");
	static JButton HighScoreMenuButton = new JButton("High Scores");
	static JTextField field = new JTextField("Enter Name");
	static JButton EnterNameMenuButton = new JButton("Enter");
	static JButton closeScores = new JButton("Close");
	private static int loadCheck =0;

	public MainWindow() {
	        frame.setSize(1000, 650);  // you can customise this later and adapt it to change on size.
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //If exit // you can modify with your way of quitting , just is a template.
	        frame.setLayout(null);
	        frame.add(canvas);  
	        canvas.setBounds(0, 0, 1000, 650);
			   canvas.setBackground(new Color(255,255,255)); //white background  replaced by Space background but if you remove the background method this will draw a white screen 
		      canvas.setVisible(false);   // this will become visible after you press the key. 

	          // start button 
	        startMenuButton.addActionListener(new ActionListener()
	           { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					gameworld.resetGame();
					startMenuButton.setVisible(false);
					BackgroundImageForStartMenu.setVisible(false); 
					canvas.setVisible(true); 
					canvas.addKeyListener(Controller);    //adding the controller to the Canvas  
	            canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
					startGame=true;
				}});

	        EnterNameMenuButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					gameworld.setName(field.getText());
				}
			});

		HighScoreMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highScoreCheck =true;
			}
		});

		closeScores.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highScoreCheck =false;
				loadCheck=0;
			}
		});


	        startMenuButton.setBounds(400, 500, 200, 40);
	        HighScoreMenuButton.setBounds(610, 500, 200, 40);
	        EnterNameMenuButton.setBounds(250,500,80,40);
	        field.setBounds(100,500,150,40);
		closeScores.setBounds(400,500,80,40);
		highscorestext.setBounds(350,50,300,400);


		closeScores.setVisible(false);
		highscorestext.setVisible(false);

		//loading background image
	        File BackroundToLoad = new File("res/startscreen.png");  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
			try {
				 
				 BufferedImage myPicture = ImageIO.read(BackroundToLoad);
				 BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
				 BackgroundImageForStartMenu.setBounds(0, 0, 1000, 650);
				frame.add(BackgroundImageForStartMenu); 
			}  catch (IOException e) { 
				e.printStackTrace();
			}   
			 
			frame.add(startMenuButton,0);
			frame.add(HighScoreMenuButton,1);
			frame.add(field, 2);
			frame.add(EnterNameMenuButton,3);
			frame.add(closeScores,4);
		frame.add(highscorestext,5);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws FileNotFoundException {
		MainWindow ScrapetheSky = new MainWindow();  //sets up environment 
		while(true)   //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple 
		{ 
			//swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it 
			
			int TimeBetweenFrames =  1000 / TargetFPS;
			long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames; 
			
			//wait till next time step 
		 while (FrameCheck > System.currentTimeMillis()){} 
		 			
		 	startGame = gameworld.checkEnd();
			
			if(startGame) {
				 gameloop();
			}
			else if(highScoreCheck){
				highScoreMenu();
			}
			else{
				startScreen();
			}
			
			//UNIT test to see if framerate matches 
		 UnitTests.CheckFrameRate(System.currentTimeMillis(),FrameCheck, TargetFPS); 
			  
		}
		
		
	} 
	//Basic Model-View-Controller pattern 
	private static void gameloop() { 
		// GAMELOOP  
		
		// controller input  will happen on its own thread 
		// So no need to call it explicitly 
		
		// model update   
		gameworld.gamelogic();
		// view update 
		
		  canvas.updateview(); 
		
		// Both these calls could be setup as  a thread but we want to simplify the game logic for you.  
		//score update  
		 frame.setTitle("Scrape The Sky");
		 		
	}
	
	private static void startScreen() {
		
		startMenuButton.setVisible(true);
		HighScoreMenuButton.setVisible(true);
		field.setVisible(true);
		EnterNameMenuButton.setVisible(true);
		BackgroundImageForStartMenu.setVisible(true);
		canvas.setVisible(false);
		canvas.addKeyListener(Controller);    //adding the controller to the Canvas  
		canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
		startGame=false;

		closeScores.setVisible(false);
		highscorestext.setVisible(false);
	}

	private static void highScoreMenu() throws FileNotFoundException {

		if(loadCheck >0){
			return;
		}
		startMenuButton.setVisible(false);
		HighScoreMenuButton.setVisible(false);
		BackgroundImageForStartMenu.setVisible(true);
		field.setVisible(false);
		EnterNameMenuButton.setVisible(false);
		canvas.setVisible(false);
		canvas.addKeyListener(Controller);    //adding the controller to the Canvas
		canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
		startGame=false;

		highscorestext.setText("");
		highscorestext.setEditable(false);
		TreeMap<Integer, List<String>> highestScores = new TreeMap<Integer, List<String>>();
		File file = new File("res/highscore");
		Scanner myReader = new Scanner(file);
		String text = "Default";
		while (myReader.hasNextLine()) {
			text = myReader.nextLine();
			String[] playerScores = text.split(": ");
			Integer score = Integer.valueOf(playerScores[1]);
			List<String> playerList = null;

			// check if a player with this score already exists
			if ((playerList = highestScores.get(score)) == null) { // if NOT,
				playerList = new ArrayList<String>(1); // CREATE a new list
				playerList.add(playerScores[0]);
				highestScores.put(Integer.valueOf(playerScores[1]), playerList);
			} else { // if YES, ADD to the existing list
				playerList.add(playerScores[0]);
			}
		}
		int i =0;
		for (Integer score : highestScores.descendingKeySet()) {
			for (String player : highestScores.get(score)) {
				if (i <= 9) {
					highscorestext.append(player + ": " + score + "\n");
					i++;
				}
			}
		}

		highscorestext.insert("The Top 10 scores \n\n", 0);

		//Got a lot of help from this stack overflow page https://stackoverflow.com/questions/16989277/how-do-i-sort-a-text-file-by-the-highest-number-using-java

		myReader.close();

		highscorestext.setOpaque(true);
		highscorestext.setForeground(Color.BLACK);
		highscorestext.setBackground(Color.WHITE);
		highscorestext.setFont(new Font("Serif", Font.BOLD, 20));
		highscorestext.setBorder(BorderFactory.createLineBorder(Color.black));


		closeScores.setVisible(true);
		highscorestext.setVisible(true);
		loadCheck++;
	}

}