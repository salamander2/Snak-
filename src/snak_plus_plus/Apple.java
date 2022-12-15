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
		Point apple_coords = MainGame.square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.drawImage(apple_img, apple_coords.x, apple_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	void move_to_sqr(int sx, int sy) {
		 // checks if enough time has passed since last move.  MH ???
		this.sx = sx;
		this.sy = sy;
		
	}
	
	int[][] move(int dir, int[][] bboard, int HS, int VS) {
		
		if (!(System.currentTimeMillis() - last_move >= move_wait)) return bboard;
		
		switch(dir){
		case (37)://left arrow
			if(!MainGame.wbos(this.sx-1, this.sy, HS, VS)) break; //Makes sure that the this won't exit the screen
			if(bboard[this.sy][this.sx-1] != EMPTY && bboard[this.sy][this.sx-1] != SNAKE_HEAD) break;
			if(bboard[this.sy][this.sx-1] == SNAKE_HEAD) is_eaten = true;
			bboard[this.sy][this.sx] = EMPTY;
			this.move_to_sqr(this.sx-1,this.sy);
			bboard[this.sy][this.sx] = APPLE;
			break;
		case (38)://up arrow
			if(!MainGame.wbos(this.sx, this.sy-1, HS, VS)) break;
			if(bboard[this.sy-1][this.sx] != 0 && bboard[this.sy-1][this.sx] != SNAKE_HEAD) break;
			if(bboard[this.sy-1][this.sx] == SNAKE_HEAD) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx, this.sy-1);
			bboard[this.sy][this.sx] = APPLE;
			break;
		case (39)://right arrow
			if(!MainGame.wbos(this.sx+1, this.sy, HS, VS)) break;
			if(bboard[this.sy][this.sx+1] != 0 && bboard[this.sy][this.sx+1] != SNAKE_HEAD) break;
			if(bboard[this.sy][this.sx+1] == SNAKE_HEAD) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx+1,this.sy);
			bboard[this.sy][this.sx] = APPLE;
			break;
		case (40)://down arrow
			if(!MainGame.wbos(this.sx, this.sy+1, HS, VS)) break;
			if(bboard[this.sy+1][this.sx] != 0 && bboard[this.sy+1][this.sx] != SNAKE_HEAD) break;
			if(bboard[this.sy+1][this.sx] == SNAKE_HEAD) is_eaten = true;
			bboard[this.sy][this.sx] = 0;
			this.move_to_sqr(this.sx,this.sy+1);
			bboard[this.sy][this.sx] = APPLE;
			break;
		}
		last_move =  System.currentTimeMillis();
		return bboard;
	}
	
	
	
}
