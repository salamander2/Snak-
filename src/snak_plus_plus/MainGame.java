package snak_plus_plus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import java.io.IOException;


public class MainGame {
	
	
	public static void main(String[] args) throws IOException {
		boolean has_food = false;
		Window w = new Window();
		w.setup();
		w.drawLoadingscreen();
		
		Apple apple = new Apple(12, 2, 49, w.loadImage("apple.png"));
		
		
		ArrayList<Block> Blocks = new ArrayList<Block>();
		
		
		
		
		SnakeHead head = new SnakeHead(28, 25, 37, w.loadImage("Head_left.png"), w.loadImage("Head_right.png"), w.loadImage("Head_up.png"), w.loadImage("Head_down.png"));
		double Dt;
		
		
		int[][] bboard = new int[w.VS][w.HS]; //back end board this is for logic reasons
		
		
		
		
		for(int i = 0; i < w.HS; i++) {
			for(int j = 0; j < w.VS; j++) {
				bboard[j][i] = 0;
			}
		}
		bboard = w.randomMaze(bboard);// Loads Map
		
		for(int i = 0; i < w.HS; i++) {
			for(int j = 0; j < w.VS; j++) {
				if (bboard[j][i] == 5) Blocks.add(new Block(i, j, w.loadImage("Block.png")));
			}
		}
		
		boolean show_start = true;
		boolean show_end = false;
		boolean end = false;
		boolean freeze = true;
		String end_phraseString = "Apple Won the Game!";
		int sleep_time = 20;
		long time = System.currentTimeMillis();
		long prev_time = System.currentTimeMillis();
		long last_tie = System.currentTimeMillis();
		
		long last_food_spawn = time;
		
		Random rand = new Random();
		Food food = new Food (rand.nextInt(w.VS), rand.nextInt(w.HS), w.loadImage("banana.png"));
		food.alive = false;
		bboard[food.sy][food.sx] = 2;

		
		

		for(int i = 0; i < Blocks.size(); i++) {
			bboard[Blocks.get(i).sy][Blocks.get(i).sx] = 5;
		}
		//loading map
		
		
		
		
		System.out.println(Arrays.deepToString(bboard));
		/*
		 * bboard is the back-end board that figures out what is where on the screen to figure out collisions
		 * 0 -> Empty Square
		 * 1 -> Player2 Apple
		 * 2 -> Food for Snake
		 * 3 -> Snake Head
		 * 4 -> Snake body part
		 * 5 -> Obstacle (wall of some sorts)
		 */
		
		//int last_tie = (int) System.currentTimeMillis();
		while(true) { // Main loop starts here
			time =  System.currentTimeMillis();
			Dt = (double) ((time - prev_time)/1000.0);
			prev_time = time;
			
//			System.out.println(Arrays.deepToString(bboard));
			//CTRL-SPACE
			w.gc.sleep(sleep_time);
			synchronized(w.gc) {
				w.gc.clear();
				if (show_end) {					
					
					if (head.bonked) w.drawEndScreen("Snake died by BONK!",new Color(232, 72, 85) , new Color(33, 31, 34), false);
					else if (head.won || apple.is_eaten) {
						w.drawEndScreen("Snake Won by CHOMP!!!", new Color(67, 154, 134), new Color(33, 31, 34), true);
						apple.is_eaten = true;
					}
				}if (!show_end) {
					w.drawBoard();
					apple.draw(w.gc, w.WIDTH, w.HEIGHT, w.HS, w.VS);
					head.draw(w.gc, w.WIDTH, w.HEIGHT, w.HS, w.VS);
					w.gc.setColor(new Color(80, 80, 80));
					if (food.alive) food.draw(w.gc, w.WIDTH, w.HEIGHT, w.HS, w.VS);
					
					for(int i = 0; i < Blocks.size(); i++){
						Blocks.get(i).draw(w.gc, w.WIDTH, w.HEIGHT, w.HS, w.VS);
						bboard[Blocks.get(i).sy][Blocks.get(i).sx] = 5;
					if (show_start) {
						w.start_scrn_wait = (int) (w.START_SCRN_WAIT*Dt);
						w.drawStartScreen();
					}
					}
				}
			}
			
			
//			if (w.gc.isKeyDown(90) && (int) (System.currentTimeMillis()) - last_tie > 300){
//				int x1, y1;
//				x1 = (int) w.gc.getMouseX()/25;
//				y1 = (int) w.gc.getMouseY()/25;
//				
//				System.out.println("x: " + w.gc.getMouseX()+ "  y: "+w.gc.getMouseY());
//				
//				Blocks.add(new Block(x1, y1));
//				if (bboard[y1][x1] == 5) bboard[y1][x1] = 0;
//				else bboard[y1][x1] = 5;
//				last_tie = (int) System.currentTimeMillis();
//			}
//			if (w.gc.isKeyDown(88)) System.out.println(Arrays.deepToString(bboard));
			
			
			
			
			
			if ((head.bonked || head.won || apple.is_eaten) & !freeze) { // checks if the game is over
				end = true;
				show_end = true;
				freeze = true;
			}
			if (end && !w.gc.isKeyDown(32) && !show_end) show_end = true; // shows the board once the game is over and everything is frozen
			else if (w.gc.isKeyDown(32) && show_end) show_end = false;
			
			if (!end && w.gc.isKeyDown(32) && !show_end) { // starts the game initially
				show_start = false;
				freeze = false;
			}
			
			if (head.ate) {// if the snake ate, delete the food and add another body part to the snake
				head.ate = false;
				last_food_spawn = time;
				food.alive = false;
//				head.switch_to_new_part = true;
			}
			
			if (show_start & w.gc.isKeyDown(83) & time-last_tie > 250) {
				if (w.switch_crlts) w.switch_crlts = false;
				else w.switch_crlts = true;
				last_tie = time;
				
			}
			
			
			if (end && w.gc.isKeyDown('R')) { // Restarts the game
				head = new SnakeHead(28, 25, 37, w.loadImage("Head_left.png"), w.loadImage("Head_right.png"), w.loadImage("Head_up.png"), w.loadImage("Head_down.png"));
				apple = new Apple(12, 2, 49, w.loadImage("apple.png"));
				bboard = new int[w.VS][w.HS];
				Blocks = new ArrayList<Block>();
				for(int i = 0; i < w.HS; i++) {
					for(int j = 0; j < w.VS; j++) {
						bboard[j][i] = 0;
					}
				}
				bboard = w.randomMaze(bboard);
				
				for(int i = 0; i < w.HS; i++) {
					for(int j = 0; j < w.VS; j++) {
						if (bboard[j][i] == 5) Blocks.add(new Block(i, j, w.loadImage("Block.png")));
					}
				}
				has_food = false;
				show_start = false;
				show_end = false;
				end = false;
				freeze = false;
				food = new Food (rand.nextInt(w.VS), rand.nextInt(w.HS), w.loadImage("banana.png"));
				food.alive = false;
				bboard[food.sy][food.sx] = 2;
				
			}
			
			
			
			
			bboard[apple.sy][apple.sx] = 1;
			Pair pressed = w.get_key(w.gc);
			if(!freeze) { // A boolean that is true when the game is over to prevent further player movement
				
				
				
			
			
				switch(pressed.getFirst()){
				case (87)://w
					head.dir_manager("up");
					break;
				case (65)://a
					head.dir_manager("left");
					break;
				case (83)://s
					head.dir_manager("down");
					break;
				case (68)://d
					head.dir_manager("right");
					break;
				}
				
				
				bboard = head.move(bboard);
				bboard = apple.move(pressed.getSecond(), bboard, w.HS, w.VS);
				
				if (!has_food && time - last_food_spawn > 100 && !food.alive) { // spawns in another banana once it is eaten
					
					while (true) {
					food.sx = rand.nextInt(w.HS);
					food.sy = rand.nextInt(w.VS);
					if (bboard[food.sy][food.sx] == 0) break;
					}
					food.alive = true;
					bboard[food.sy][food.sx] = 2;
					last_food_spawn = time;

				}
				
			}
			//Controlling The speed at which the players can move
			apple.move_wait = (int) (apple.MOVE_WAIT*Dt*60);
			head.move_wait = (int) (head.MOVE_WAIT*Dt*60);
			
			}
			
		}
			
	}


