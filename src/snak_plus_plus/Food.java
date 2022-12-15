package snak_plus_plus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

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
		Pair apple_coords = Apple.square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.drawImage(food_img, apple_coords.getFirst(), apple_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	
	
}
