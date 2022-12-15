package snak_plus_plus;

import java.awt.Color;
import hsa2.GraphicsConsole;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;



public class Apple {
	
	BufferedImage apple_img;	
	boolean is_eaten = false;
	int sx,sy; // X-coord of the square that the apple is on and the Y-coord of the square that the apple is on
	int x, y;
	long time, last_move, move_wait;
	final int MOVE_WAIT;
	
	
	Apple(int n1, int n2, int move_wait, BufferedImage img) {
		sx = n1;
		sy = n2;
		this.move_wait = move_wait;
		this.MOVE_WAIT = move_wait;
		apple_img = img;
	}
	
	
	//          will be on screen
	//              \/   
	public boolean wbos(int sx, int sy, int HS, int VS) {
		if (0<=sx && sx<HS) {
			if(0<=sy && sy<VS) {
				return true;
			}
		}
		return false;
	}
	
	public void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		Pair apple_coords = square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.drawImage(apple_img, apple_coords.getFirst(), apple_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	public void move_to_sqr(int sx, int sy) {
		 // checks if enough time has passed since last move
		this.sx = sx;
		this.sy = sy;
		
	}
	
	public int[][] move(int dir, int[][] bboard, int HS, int VS) {
		if (!(System.currentTimeMillis() - last_move >= move_wait)) return bboard;
		switch(dir){
		case (37)://left arrow
			if(!this.wbos(this.sx-1, this.sy, HS, VS)) break; //Makes sure that the this won't exit the screen
			if(bboard[this.sy][this.sx-1] != 0 && bboard[this.sy][this.sx-1] != 3) break;
			if(bboard[this.sy][this.sx-1] == 3) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx-1,this.sy);
			bboard[this.sy][this.sx] = 1;
			break;
		case (38)://up arrow
			if(!this.wbos(this.sx, this.sy-1, HS, VS)) break;
			if(bboard[this.sy-1][this.sx] != 0 && bboard[this.sy-1][this.sx] != 3) break;
			if(bboard[this.sy-1][this.sx] == 3) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx, this.sy-1);
			bboard[this.sy][this.sx] = 1;
			break;
		case (39)://right arrow
			if(!this.wbos(this.sx+1, this.sy, HS, VS)) break;
			if(bboard[this.sy][this.sx+1] != 0 && bboard[this.sy][this.sx+1] != 3) break;
			if(bboard[this.sy][this.sx+1] == 3) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx+1,this.sy);
			bboard[this.sy][this.sx] = 1;
			break;
		case (40)://down arrow
			if(!this.wbos(this.sx, this.sy+1, HS, VS)) break;
			if(bboard[this.sy+1][this.sx] != 0 && bboard[this.sy+1][this.sx] != 3) break;
			if(bboard[this.sy+1][this.sx] == 3) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx,this.sy+1);
			bboard[this.sy][this.sx] = 1;
			break;
		}
		last_move =  System.currentTimeMillis();
		return bboard;
	}
	
	
	
	

	
	
	
	
	public static Pair square_to_coords(int sqX, int sqY, int WIDTH, int HEIGHT, int HS, int VS) { //Converts the square to coordinates on screen
		int x, y;
		x = (WIDTH/HS)*sqX;
		y = (HEIGHT/VS)*sqY;
		
		return new Pair(x, y);
		
	}
}
