package snak_plus_plus;

import java.awt.Point;
import java.awt.image.BufferedImage;

import hsa2.GraphicsConsole;

public class Block {
	int sx, sy;
	BufferedImage block_img;
	Block(int n1, int n2, BufferedImage img){
		this.sx = n1;
		this.sy = n2;
		block_img = img;
	}
	
	public void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		Point apple_coords = MainGame.square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.drawImage(block_img, apple_coords.x, apple_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
	}
	
}
