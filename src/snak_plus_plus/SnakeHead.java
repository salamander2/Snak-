package snak_plus_plus;

import java.awt.Color;
//import java.lang.invoke.ConstantBootstraps;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import hsa2.GraphicsConsole;

public class SnakeHead{
	int sx, sy;
	int old_sx, old_sy;
	int x, y;
	boolean up, down, left, right;
	int num_parts = 0;
	long time, last_move, move_wait;
	final int MOVE_WAIT;
	int part_to_move = 0;
	int old_part_to_move = 0;
	int body_stroke = 6;
	String last_dir = "None";
	boolean switch_to_new_part = false;
	boolean won = false;
	boolean bonked = false;
	boolean ate = false;
	boolean died = false;
	BufferedImage head_right;
	BufferedImage head_left;
	BufferedImage head_up;
	BufferedImage head_down;
	BufferedImage head_img = head_right;
	ArrayList<SnakeBody> body = new ArrayList<SnakeBody>();
	static final int FOOD = 2;
	
			
	SnakeHead(int n1, int n2, int move_wait, BufferedImage img_l, BufferedImage img_r, BufferedImage img_u, BufferedImage img_d) {
		sx = n1;
		sy = n2;
		this.move_wait = move_wait;
		this.MOVE_WAIT = move_wait;
		body.add( new SnakeBody(sx, sy, 5));
		num_parts += 1;
		body.add( new SnakeBody(sx, sy, 5));
		num_parts += 1;
		body.add( new SnakeBody(sx, sy, 5));
		num_parts += 1;
		old_sx = sx;
		old_sy = sy;
		head_left = img_l;
		head_right = img_r;
		head_up = img_u;
		head_down = img_d;
	}
	
	public boolean wbos(int sx, int sy, int HS, int VS) {
		if (0<=sx && sx<HS) {
			if(0<=sy && sy<VS) {
				return true;
			}
		}
		return false;
	}
	
	
	public void dir_manager(String dir) {
		this.up = false;
		this.down = false;
		this.left = false;
		this.right = false;
		switch(dir) {
		case "up" :
			if (last_dir.equals("down")) {
				this.down = true;
				break;
			}
			this.up = true;
			break;
		case "down" :
			if (last_dir.equals("up")) {
				this.up = true;
				break;
			}
			this.down = true;
			break;
		case("left"):
			if (last_dir.equals("right")) {
				this.right = true;
				break;
			}
			this.left = true;
			break;
		case("right"):
			if (last_dir.equals("left")) {
				this.left = true;
				break;
			}
			this.right = true;
			break;
		}
	}
	
	public void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		Pair head_coords = square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.setColor(new Color(67, 154, 134));
		gc.drawImage(this.head_img, head_coords.getFirst(), head_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
		gc.setStroke(4);
		for(int part = 0; part < num_parts; part ++) {
			Pair body_coords = square_to_coords(body.get(part).sx, body.get(part).sy, WIDTH, HEIGHT, HS, VS);
			gc.fillRect(body_coords.getFirst(), body_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
		}
	}
	
	/*
	public void draw(GraphicsConsole gc, int WIDTH, int HEIGHT, int HS, int VS) {
		int step = (int) ((WIDTH/HS)/move_wait);
		Pair head_coords = square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.setColor(new Color(67, 154, 134));
		gc.drawImage(this.head_img, head_coords.getFirst(), head_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
		gc.setStroke(4);
		for(int part = 0; part < num_parts; part ++) {
			Pair body_coords = square_to_coords(body.get(part).sx, body.get(part).sy, WIDTH, HEIGHT, HS, VS);
			gc.drawRect(body_coords.getFirst(), body_coords.getSecond(),(int) (WIDTH/HS),(int) (HEIGHT/VS));
		}
	}
	 */
	
	int[][] move(int[][] bboard) {
		if (!( System.currentTimeMillis() - last_move >= move_wait)) return bboard; // checks if enough time has passed since last move
		int rows = bboard.length;
		int cols = bboard[0].length;

		for(int i = 0; i < num_parts-1; i++) {
			body.get(i).part = 5;
		}
		body.get(part_to_move).part = 1;
		if(this.up) {
			
			if (0 > sy-1) {
				return bboard;
			}
			this.head_img = this.head_up;
			bboard = block_cases(bboard, sx, sy-1);
			bboard[sy][sx] = 4; // Changing the head location to a body part
			bboard[sy-1][sx] = 3; // Updating the new head location on the bboard
			Pair old_coords = new Pair (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = 0; 
			this.old_sy = this.sy;
			this.sy -= 1;
			
			body.get(part_to_move).sx = old_coords.getFirst();
			body.get(part_to_move).sy = old_coords.getSecond();
			this.last_dir = "up";
		}
		if(this.down) {
			
			if (cols < sy+2) {
				return bboard;
			}
			this.head_img = this.head_down;
			bboard = block_cases(bboard, sx, sy+1);
			bboard[sy][sx] = 4; // Changing the head location to a body part
			bboard[sy+1][sx] = 3;
			Pair old_coords = new Pair (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = 0;
			this.old_sy = this.sy;
			this.sy += 1;

			
			body.get(part_to_move).sx = old_coords.getFirst();
			body.get(part_to_move).sy = old_coords.getSecond();
			this.last_dir = "down";
		}
		if(this.left) {
			
			if (0 > sx-1) {
				return bboard;
			} 
			this.head_img = this.head_left;
			bboard = block_cases(bboard, sx-1, sy);
			bboard[sy][sx] = 4; // Changing the head location to a body part
			bboard[sy][sx-1] = 3;
			Pair old_coords = new Pair (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = 0;
			this.old_sx = this.sx;
			this.sx -= 1;
			body.get(part_to_move).sx = old_coords.getFirst();
			body.get(part_to_move).sy = old_coords.getSecond();
			this.last_dir = "left";
		}
		if(this.right) {
			
			if (rows < sx+2 ){
				return bboard;
			} 
			this.head_img = this.head_right;
			bboard = block_cases(bboard, sx+1, sy);
			bboard[sy][sx] = 4; // Changing the head location to a body part
			bboard[sy][sx+1] = 3;
			Pair old_coords = new Pair (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = 0;
			this.old_sx = this.sx;
			this.sx += 1;

			body.get(part_to_move).sx = old_coords.getFirst();
			body.get(part_to_move).sy = old_coords.getSecond();
			this.last_dir = "right";
			
		}
		if(num_parts-1 == part_to_move) this.part_to_move = 0;//restarting the part to move int when it reaches the amount of parts
		else this.part_to_move += 1;
		last_move = System.currentTimeMillis();
		body.get(part_to_move).part = 4;
		
		return bboard;
	}
	
	int[][] block_cases(int[][] bboard, int sx, int sy) {
//		System.out.println(">> " + bboard[sy][sx]);
		switch(bboard[sy][sx]) {
			case(0):
				return bboard;
			case(1):
				this.won = true;
				return bboard;
			case FOOD:
				this.body.add( new SnakeBody(this.sx, this.sy, 5));
				this.num_parts+=1;
				this.body.get(num_parts-1).is_new = true;
				this.ate = true;
				return bboard;
			case(4):
				this.bonked = true;
				return bboard;
			case(5):
				this.bonked = true;
				return bboard;
		}
		return bboard;
	}
	
	private Pair square_to_coords(int sqX, int sqY, int WIDTH, int HEIGHT, int HS, int VS) { //Converts the square to coordinates on screen
		int x, y;
		x = (WIDTH/HS)*sqX;
		y = (HEIGHT/VS)*sqY;
		
		return new Pair(x, y);
	}
}
