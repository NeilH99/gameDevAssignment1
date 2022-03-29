import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.GameObject;

import java.util.Random;


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
 
 * Credits: Kelly Charles (2020)
 */ 
public class Viewer extends JPanel {
	private long CurrentAnimationTime= 0; 
	
	Model gameworld =new Model(); 
	Image backImage = null;
	 
	public Viewer(Model World) {
		this.gameworld=World;
		File TextureToLoad = new File("res/skyscrapper.png");
		try {
			this.backImage = ImageIO.read(TextureToLoad);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// TODO Auto-generated constructor stub
	}



	public Viewer(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public Viewer(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public void updateview() {
		
		this.repaint();
		// TODO Auto-generated method stub
		
	}
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		CurrentAnimationTime++; // runs animation time step 
		
		
		//Draw player Game Object 
		int x = (int) gameworld.getPlayer().getCentre().getX();
		int y = (int) gameworld.getPlayer().getCentre().getY();
		int width = (int) gameworld.getPlayer().getWidth();
		int height = (int) gameworld.getPlayer().getHeight();
		String texture = gameworld.getPlayer().getTexture();
		
		//Draw background 
		drawBackground(g);
		
		drawScore(100,600,gameworld.getScore(),g);

		drawText((int)gameworld.getText().getCentre().getX(),(int)gameworld.getText().getCentre().getY(), (int)gameworld.getText().getWidth(), (int)gameworld.getText().getHeight(),gameworld.getText().getTexture(),g);

		drawText((int)gameworld.getTitleText().getCentre().getX(),(int)gameworld.getTitleText().getCentre().getY(), (int)gameworld.getTitleText().getWidth(), (int)gameworld.getTitleText().getHeight(),gameworld.getTitleText().getTexture(),g);

		gameworld.getPlatforms().forEach((temp) -> 
		{
			drawPlatform((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
	    }); 
		
		gameworld.getEnemies().forEach((temp) -> 
		{
			drawEnemies((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
	    });
		
		
		//Draw Bullets 
		// change back 
		gameworld.getBullets().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		});

		drawPlayer(x, y, width, height, texture,g);
	}
	
	private void drawEnemies(int x, int y, int width, int height, String texture, Graphics g) {
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time 
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31  
			int currentPositionInAnimation= (((int) (CurrentAnimationTime%32 )/10)*32); //slows down animation so every 10 frames we get another frame so every 100ms
			g.drawImage(myImage, x,y, x+width, y+height, currentPositionInAnimation  , 0, currentPositionInAnimation+31, 32, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Birds from https://craftpix.net/freebies/free-street-animal-pixel-art-asset-pack/?num=1&count=11&sq=birds&pos=4
		
	}

	private void drawBackground(Graphics g)
	{
		g.drawImage(backImage, 0,0, 1000, 1000, 0 , 0, 1000, 1000, null);

		//I made the background in paint 3d
	}
	
	private void drawBullet(int x, int y, int width, int height, String texture,Graphics g)
	{
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			//64 by 128 
			 g.drawImage(myImage, x,y, x+width, y+height, 0 , 0, 63, 127, null);

			//WaterBullets from http://pixelartmaker.com/art/1d1399cc0af9586

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void drawPlayer(int x, int y, int width, int height, String texture,Graphics g) { 
		
		//int currentPositionInAnimation= ((int) (CurrentAnimationTime%4 )*32); //slows down animation so every 10 frames we get another frame so every 100ms 
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			
		int currentPositionInAnimation= ((int) ((CurrentAnimationTime% 48)/12)*48); //slows down animation so every 10 frames we get another frame so every 100ms 
		g.drawImage(myImage, x,y,x+width,y+height,currentPositionInAnimation,0,currentPositionInAnimation+48,48, null);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Player from https://craftpix.net/freebies/free-3-character-sprite-sheets-pixel-art/
		//g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
		
	}
	
	private void drawPlatform(int x, int y, int width, int height, String texture,Graphics g)
	{
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			//64 by 128 
			g.drawImage(myImage, x,y,width,height, null); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawText(int x, int y, int width, int height, String texture,Graphics g)
	{
		File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE 
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			//64 by 128 
			g.drawImage(myImage, x,y,width,height, null); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawScore(int x, int y, int score,Graphics g){
		String string = "Score "+score;
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		g.setColor(Color.RED);
		g.drawString(string, x, y );
	}
	
		 
	 

}