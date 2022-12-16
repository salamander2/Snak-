package snak_plus_plus;

import java.awt.Color;
import hsa2.GraphicsConsole;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


class Apple {

	//Constants from MainGame
	static final int EMPTY = 0;
	static final int APPLE = 1; //player 2
	static final int FOOD = 2;
	static final int WALL = 5;
	static final int SNAKE_HEAD = 3;
	static final int SNAKE_BODY = 4;
	
	BufferedImage apple_img;	
	boolean is_eaten = false;
	int sx,sy; // X-coord of the square that the apple is on and the Y-coord of the square that the apple is on
	int x, y;
	long time, last_move, move_wait;
	final int MOVE_WAIT;  //MH: do not have to variables that differ only in case. 
	//MH. Even worse, one's  long and one's an int.
	
	
	Apple(int n1, int n2, int move_wait, BufferedImage img) {
		sx = n1;
		sy = n2;
		this.move_wait = move_wait;
		this.MOVE_WAIT = move_wait;
		apple_img = img;
	}
	
	void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		Point apple_coords = MainGame.square_to_coords(sx, sy, WIDTH, HEIGHT, Window.HS, Window.VS);
		gc.drawImage(apple_img, apple_coords.x, apple_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	void move_to_sqr(int sx, int sy) {
		 // checks if enough time has passed since last move.  MH ???
		this.sx = sx;
		this.sy = sy;	
	}
	
	int[][] move(int dir, int[][] bboard) {
		
		if (!(System.currentTimeMillis() - last_move >= move_wait)) return bboard;
		
		int a=-1, b=-1;
		
		switch(dir){
		case (37)://left arrow
			a=sx-1; b=sy;
			break;
		case (38)://up arrow
			a=sx; b=sy-1;
			break;
		case (39)://right arrow
			a=sx+1; b=sy;
			break;
		case (40)://down arrow
			a=sx; b=sy+1;
			break;
		}		
	
		if(MainGame.wbos(a,b, Window.HS, Window.VS)) {
			if(bboard[b][a] == EMPTY || bboard[b][a] == SNAKE_HEAD) {
				if(bboard[b][a] == SNAKE_HEAD) is_eaten = true;
				bboard[sx][sy] = EMPTY;
				//this.move_to_sqr(a,b);
				sx=a; sy=b;
				bboard[this.sy][this.sx] = APPLE;
			}
		}
		
		last_move =  System.currentTimeMillis();
		return bboard;
	}
	
	
	
}
