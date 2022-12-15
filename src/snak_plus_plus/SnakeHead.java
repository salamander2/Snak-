package snak_plus_plus;

import java.awt.Color;
import java.awt.Point;
//import java.lang.invoke.ConstantBootstraps;
import java.util.ArrayList;
import java.awt.image.BufferedImage;


import hsa2.GraphicsConsole;

public class SnakeHead{
	
	//Constants from MainGame
	static final int EMPTY = 0;
	static final int APPLE = 1; //player 2
	static final int FOOD = 2;
	static final int WALL = 5;
	static final int SNAKE_HEAD = 3;
	static final int SNAKE_BODY = 4;
		
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
		Point head_coords = MainGame.square_to_coords(sx, sy, WIDTH, HEIGHT, HS, VS);
		gc.setColor(new Color(67, 154, 134));
		gc.drawImage(this.head_img, head_coords.x, head_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
		gc.setStroke(4);
		for(int part = 0; part < num_parts; part ++) {
			Point body_coords = MainGame.square_to_coords(body.get(part).sx, body.get(part).sy, WIDTH, HEIGHT, HS, VS);
			gc.fillRect(body_coords.x, body_coords.y,(int) (WIDTH/HS),(int) (HEIGHT/VS));
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
			bboard[sy][sx] = SNAKE_BODY; // Changing the head location to a body part
			bboard[sy-1][sx] = SNAKE_HEAD; // Updating the new head location on the bboard
			Point old_coords = new Point (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = EMPTY; 
			this.old_sy = this.sy;
			this.sy -= 1;
			
			body.get(part_to_move).sx = old_coords.x;
			body.get(part_to_move).sy = old_coords.y;
			this.last_dir = "up";
		}
		if(this.down) {
			
			if (cols < sy+2) {
				return bboard;
			}
			this.head_img = this.head_down;
			bboard = block_cases(bboard, sx, sy+1);
			bboard[sy][sx] = SNAKE_BODY; // Changing the head location to a body part
			bboard[sy+1][sx] = SNAKE_HEAD;
			Point old_coords = new Point (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = EMPTY;
			this.old_sy = this.sy;
			this.sy += 1;

			
			body.get(part_to_move).sx = old_coords.x;
			body.get(part_to_move).sy = old_coords.y;
			this.last_dir = "down";
		}
		if(this.left) {
			
			if (0 > sx-1) {
				return bboard;
			} 
			this.head_img = this.head_left;
			bboard = block_cases(bboard, sx-1, sy);
			bboard[sy][sx] = SNAKE_BODY; // Changing the head location to a body part
			bboard[sy][sx-1] = SNAKE_HEAD;
			Point old_coords = new Point (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = EMPTY;
			this.old_sx = this.sx;
			this.sx -= 1;
			body.get(part_to_move).sx = old_coords.x;
			body.get(part_to_move).sy = old_coords.y;
			this.last_dir = "left";
		}
		if(this.right) {
			
			if (rows < sx+2 ){
				return bboard;
			} 
			this.head_img = this.head_right;
			bboard = block_cases(bboard, sx+1, sy);
			bboard[sy][sx] = SNAKE_BODY; // Changing the head location to a body part
			bboard[sy][sx+1] = SNAKE_HEAD;
			Point old_coords = new Point (this.sx, this.sy);
			bboard[body.get(part_to_move).sy][body.get(part_to_move).sx] = EMPTY;
			this.old_sx = this.sx;
			this.sx += 1;

			body.get(part_to_move).sx = old_coords.x;
			body.get(part_to_move).sy = old_coords.y;
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
			case EMPTY:
				return bboard;
			case APPLE:
				this.won = true;
				return bboard;
			case FOOD:
				this.body.add( new SnakeBody(this.sx, this.sy, 5));
				this.num_parts+=1;
				this.body.get(num_parts-1).is_new = true;
				this.ate = true;
				return bboard;
			case SNAKE_BODY:
				this.bonked = true;
				return bboard;
			case WALL:
				this.bonked = true;
				return bboard;
		}
		return bboard;
	}

}
