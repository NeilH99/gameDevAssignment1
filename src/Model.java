import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import util.GameObject;
import util.Point3f;
import util.Vector3f;

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
public class Model {
	
	 private  GameObject Player;
	 private  GameObject Text;
	private  GameObject TitleText;
	private Controller controller = Controller.getInstance();
	 private  CopyOnWriteArrayList<GameObject> EnemiesList  = new CopyOnWriteArrayList<GameObject>();
	 private  CopyOnWriteArrayList<GameObject> BulletList  = new CopyOnWriteArrayList<GameObject>();
	 private  CopyOnWriteArrayList<GameObject> PlatformList  = new CopyOnWriteArrayList<GameObject>();
	 private boolean left = false;
	 private boolean right = true;
	 AudioStream audioStream = null;
	private int Score=0;
	 private int jumpSpeed=0;
	 private boolean boost=false;
	 private int scoreCheck =0;
	 private String name = "Default";
	 private boolean loadCheck = true;
	 private AudioStream backgroundMusic =null;

	public Model() {
		 //setup game world 
		//Player
		Player= new GameObject("res/Woodcutter_idle.png",80,80,new Point3f(500,450,0));
		
		Text = new GameObject("res/transparent.png",200,80,new Point3f(400,200,0));
		TitleText = new GameObject("res/transparent.png",200,80,new Point3f(400,120,0));

	}
	
	// This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly. 
	public void gamelogic() 
	{
		// Player Logic first 
		playerLogic(); 
		
		// Enemy Logic next
		//enemyLogic();
		// Bullets move next 
		bulletLogic();
		// interactions between objects 
		
		platformLogic();
		
		checkScore();

		scrollLogic();
		
		drawText();
		
		checkEnd();
		
		enemyLogic();
		
	}


	private void enemyLogic() {

		for (GameObject temp : EnemiesList)
		{
		    // Move enemies 
			temp.getCentre().ApplyVector(new Vector3f(1,0,0));
			 
			 
			//see if they get to the top of the screen ( remember 0 is the top 
			if (temp.getCentre().getX()>850)  // current boundary need to pass value to model
			{
				EnemiesList.remove(temp);
				
				// enemies win so score decreased 
			} 
		}
		Random rand = new Random();
		int randCheck = rand.nextInt(150-Score);
		if (randCheck == 1 && Score >=10)
		{
			int randNum = rand.nextInt(450)+10;
			EnemiesList.add(new GameObject("res/bird.png",60,60,new Point3f(0,randNum,0)));
		}
		
		for (GameObject temp : EnemiesList) 
		{
		for (GameObject Bullet : BulletList) 
		{
			if ( (Bullet.getCentre().getX()>=temp.getCentre().getX()-Bullet.getWidth()/2 && Bullet.getCentre().getX()<temp.getCentre().getX()+temp.getWidth()-55)
					&& ( (Bullet.getCentre().getY()-60 <temp.getCentre().getY() ) && (Bullet.getCentre().getY()+30 >temp.getCentre().getY()) ) )
			{
				EnemiesList.remove(temp);
				BulletList.remove(Bullet);
			}  
		}
		}
		
		for (GameObject temp : EnemiesList) 
		{


			if ( (Player.getCentre().getX()>=temp.getCentre().getX()-Player.getWidth()/2 && Player.getCentre().getX()<temp.getCentre().getX()+temp.getWidth()-55)
					&& ( (Player.getCentre().getY()-30 <temp.getCentre().getY() ) && (Player.getCentre().getY()+30 >temp.getCentre().getY()) ) )
			{
				Player.getCentre().ApplyVector(new Vector3f(3,0,0));
			}
		}
		
	}
	
	private void bulletLogic() {
		// move bullets
	  
		for (GameObject temp : BulletList) 
		{
		    //check to move them
			if(left) {
				BulletList.get(0).setTextureLocation("res/water.png");
				temp.getCentre().ApplyVector(new Vector3f(-5,0,0));
			}
			else if(right) {
				BulletList.get(0).setTextureLocation("res/waterRight.png");
				temp.getCentre().ApplyVector(new Vector3f(5,0,0));
			}
			
			//see if they hit anything 
			
			//see if they get to the top of the screen ( remember 0 is the top 
			if (temp.getCentre().getX()==0 || temp.getCentre().getX()>=900)
			{
			 	BulletList.remove(temp);
			} 
		} 
		
	}

	private void playerLogic() {
		
		// smoother animation is possible if we make a target position  // done but may try to change things for students  
		 
		//check for movement and if you fired a bullet 
		
		boolean onPlat = false;
		
		for(GameObject temp : PlatformList) {
			if((Player.getCentre().getX()>=temp.getCentre().getX()-Player.getWidth()/2+5 && Player.getCentre().getX()<temp.getCentre().getX()+temp.getWidth()-10)
					&& ( (Player.getCentre().getY()==(temp.getCentre().getY()-temp.getHeight()-Player.getHeight()/2 -13)) || (Player.getCentre().getY()-1==(temp.getCentre().getY()-temp.getHeight()-Player.getHeight()/2 -13))) ) {
				onPlat = true;
				boost =true;
			}
		}
		if(!onPlat) {
			Player.getCentre().ApplyVector( new Vector3f(0,-2,0));
		}
		  
		if(Controller.getInstance().isKeyAPressed() || Controller.getInstance().isKeyLeftPressed())
		{
			Player.setTextureLocation("res/Woodcutter_runLeft.png");
			Player.getCentre().ApplyVector( new Vector3f(-2,0,0));
			if(BulletList.size()==0) {
				left = true;
				right= false;
			}	
		}
		else {
			if(left){
				Player.setTextureLocation("res/Woodcutter_idleLeft.png");
			}
			if(right){
				Player.setTextureLocation("res/Woodcutter_idle.png");
			}
		}
		
		if(Controller.getInstance().isKeyDPressed() || Controller.getInstance().isKeyRightPressed())
		{
			Player.setTextureLocation("res/Woodcutter_runRight.png");
			Player.getCentre().ApplyVector( new Vector3f(2,0,0));
			if(BulletList.size()==0) {
				left = false;
				right= true;
			}
		}
			
		if(Controller.getInstance().isKeyWPressed() || Controller.getInstance().isKeyUpPressed())
		{
			if(BulletList.size()<1) {
				CreateBullet();
			}
			
			//Player.getCentre().ApplyVector( new Vector3f(0,2,0));
		}
		
		if(Controller.getInstance().isKeySPressed() || Controller.getInstance().isKeyDownPressed())
		{
			if(Score>=18) {
				if(boost) {
					jumpSpeed=13;
					boost = false;
				}
			}
		}
		
		if(Controller.getInstance().isKeySpacePressed())
		{
			if(onPlat) {
				jumpSpeed=13;
				Sound("res/jumpSound.wav");
				AudioPlayer.player.start(audioStream);
			}
		} 
		if(jumpSpeed>1) {
			if(left){
				Player.setTextureLocation("res/Woodcutter_jumpLeft.png");
			}
			else{
				Player.setTextureLocation("res/Woodcutter_jump.png");
			}
			Player.getCentre().ApplyVector( new Vector3f(0,jumpSpeed,0));
			jumpSpeed--;
		}
	}
	
	private void platformLogic() {
		int i = 0;
		for(GameObject temp : PlatformList) {
			i++;
			if(Score < 18) {	
			float diff = Player.getCentre().getY() - temp.getCentre().getY();
				if(diff<-150 && i<2) {
					if(PlatformList.get(i+1).getCentre().getY()>100) {
					Random rand = new Random();
					int posNeg = rand.nextBoolean() ? -1 : 1;
					int randNum = rand.nextInt(60)+120 * posNeg;
						if(PlatformList.get(i+1).getCentre().getX()+randNum<0 || PlatformList.get(i+1).getCentre().getX()+randNum>700) {
							randNum = randNum*-1;
						}
					PlatformList.add(new GameObject("res/platform4.png",150,20,new Point3f(PlatformList.get(i+1).getCentre().getX()+randNum,PlatformList.get(i+1).getCentre().getY()-50,0)));
					PlatformList.remove(temp);
					Score++;
					}
				}
			}
			else {
				float diff = Player.getCentre().getY() - temp.getCentre().getY();
				if(Player.getCentre().getY() >150){
				if(diff<-250 && i<2) {
					if (PlatformList.get(i + 1).getCentre().getY() > 100) {
						Random rand = new Random();
						int posNeg = rand.nextBoolean() ? -1 : 1;
						int randNum = rand.nextInt(90) + 130 * posNeg;
						if (PlatformList.get(i + 1).getCentre().getX() + randNum < 0 || PlatformList.get(i + 1).getCentre().getX() + randNum > 700) {
							randNum = randNum * -1;
						}
						PlatformList.add(new GameObject("res/platform4.png", 150, 20, new Point3f(PlatformList.get(i + 1).getCentre().getX() + randNum, PlatformList.get(i + 1).getCentre().getY() - 100, 0)));
						PlatformList.remove(temp);
						Score++;
					}
				}
				}
			}
		}
	}
	
	private void scrollLogic() {
		if(Player.getCentre().getY() < 5) {
			scoreCheck =0;
			PlatformList.clear();
			EnemiesList.clear();
			Player.getCentre().ApplyVector(new Vector3f(0,-2,0));
			Controller.getInstance().setKeySpacePressed(false);
			if(Score<18) {
				makePlats(50);
			}
			else {
				makePlats(100);
			}
			Random rand = new Random();
			int randNum = rand.nextInt(450)+10;
			EnemiesList.add(new GameObject("res/bird.png",60,60,new Point3f(0,randNum,0)));
			randNum = rand.nextInt(450)+10;
			EnemiesList.add(new GameObject("res/bird.png",60,60,new Point3f(0,randNum,0)));

		}
	}
	
	public boolean checkEnd() {
		boolean play = true;
		if(Player.getCentre().getY() > 700) {
			play = false;
			AudioPlayer.player.stop(backgroundMusic);

			if(Score>0 && loadCheck) {
				File file = new File("res/highscore");
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(file, true));
					writer.write("\n" + name + ": " + Score);
					writer.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				loadCheck = false;
			}
		}
		return play;
	}
	
	
	public void resetGame() {
		PlatformList.clear();
		loadCheck=true;
		Score= 0;
		scoreCheck =0;
		EnemiesList.clear();
		Controller.getInstance().setKeyWPressed(false);
		Controller.getInstance().setKeyAPressed(false);
		Controller.getInstance().setKeySPressed(false);
		Controller.getInstance().setKeyDPressed(false);
		Controller.getInstance().setKeySpacePressed(false);
		makePlats(50);
		PlatformList.remove(0);
		PlatformList.add(0, new GameObject("res/platform4.png",800,20,new Point3f(100,550,0)));

		try{
		FileInputStream file = new FileInputStream("res/test2.wav");
		backgroundMusic = new AudioStream(file);
		AudioPlayer.player.start(backgroundMusic);
		file = new FileInputStream("res/jumpSound.wav");
		audioStream = new AudioStream(file);
		}
		catch (IOException e){
		}
	}
	
	private void makePlats(int yDiff) {
		
		Random rand = new Random();
		GameObject lastPLat = new GameObject("res/platform4.png",150,20,new Point3f(500,550,0));

		if(PlatformList.size()>0) {
			lastPLat = PlatformList.get(2);
		}

		int posNeg = rand.nextBoolean() ? -1 : 1;
		int randNum = rand.nextInt(60)+120* posNeg;
		if(lastPLat.getCentre().getX()+randNum<0 || lastPLat.getCentre().getX()+randNum>700) {
			randNum = randNum*-1;
		}
		
		int randNum2 = rand.nextInt(60)+120* posNeg;
		if(lastPLat.getCentre().getX()+randNum+randNum2<0 || lastPLat.getCentre().getX()+randNum+randNum2>700) {
			randNum2 = randNum2*-1;
		}
		Player.getCentre().setX(lastPLat.getCentre().getX()+50);
		Player.getCentre().setY(550-40-Player.getHeight()/2);
		PlatformList.add(new GameObject("res/platform4.png",150,20,new Point3f(lastPLat.getCentre().getX(),550,0)));
		PlatformList.add(new GameObject("res/platform4.png",150,20,new Point3f(lastPLat.getCentre().getX()+randNum,550-yDiff,0)));
		PlatformList.add(new GameObject("res/platform4.png",150,20,new Point3f(lastPLat.getCentre().getX()+randNum+randNum2,550-yDiff*2,0)));
	
	}
	
	private void checkScore() {
		if(Player.getCentre().getY()<430 && Player.getCentre().getY()>425) {
			if(scoreCheck<1) {
				Score+=1;
				scoreCheck++;
			}
		}
		if(Player.getCentre().getY()<50 && Player.getCentre().getY()>0) {
			if(scoreCheck<2) {
				scoreCheck++;
				Score++;
			}
		}
		if(Player.getCentre().getY()<10 && Player.getCentre().getY()>0) {
			if(scoreCheck<3) {
				scoreCheck++;
				Score++;
			}
		}
	}
	
	private void drawText() {
		if(Score>=0 && Score <=1) {
			TitleText.setTextureLocation("res/titleText.png");
			Text.setTextureLocation("res/startText.png");
		}
		else if(Score>9 && Score <=15) {
			TitleText.setTextureLocation("res/transparent.png");
			Text.setTextureLocation("res/birdText.png");
		}
		else if(Score>=20 && Score <=22) {
			TitleText.setTextureLocation("res/transparent.png");
			Text.setTextureLocation("res/Text.png");
		}
		else {
			TitleText.setTextureLocation("res/transparent.png");
			Text.setTextureLocation("res/transparent.png");

		}
	}

	private void CreateBullet() {
		BulletList.add(new GameObject("res/water.png",64,80,new Point3f(Player.getCentre().getX(),Player.getCentre().getY()+40,0)));
		
	}

	public GameObject getPlayer() {
		return Player;
	}
	
	public GameObject getText() {
		return Text;
	}

	public GameObject getTitleText() {
		return TitleText;
	}
	
	public CopyOnWriteArrayList<GameObject> getPlatforms() {
		return PlatformList;
	}

	public CopyOnWriteArrayList<GameObject> getEnemies() {
		return EnemiesList;
	}
	
	public CopyOnWriteArrayList<GameObject> getBullets() {
		return BulletList;
	}

	public int getScore() { 
		return Score;
	}

	private void Sound(String soundString){
		try{
			InputStream file = new FileInputStream(soundString);
			audioStream = new AudioStream(file);
		}
		catch (IOException e){
		}
	}


	public void setName(String name) {
		this.name = name;
	}
}


