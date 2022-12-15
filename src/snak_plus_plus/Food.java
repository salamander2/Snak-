package snak_plus_plus;

import java.awt.Point;
import java.awt.image.BufferedImage;
import hsa2.GraphicsConsole;

public class Food {
	int sx, sy;
	boolean alive = false;
	
	BufferedImage food_img;
	Food(int sx, int sy, BufferedImage img){
		this.sx = sx;
		this.sy = sy;
		food_img = img;
	}
	
	public void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		if (!alive) return;
		Point apple_coords = MainGame.square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.drawImage(food_img, apple_coords.x, apple_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	
	
}
