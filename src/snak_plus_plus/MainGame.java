package snak_plus_plus;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainGame {

	static ArrayList<Block> blocks = new ArrayList<Block>(); //MH variables must be lowercase
	static double timeDbl; //MH: needs a comment. should be lowercase.
	static int[][] bboard = new int[Window.VS][Window.HS]; //back end board this is for logic reasons
	static Random rand = new Random();
	
	static Apple apple;
	static SnakeHead head;
	static Food food;
	
	// bboard is the back-end board that figures out what is where on the screen to figure out collisions
	static final int EMPTY = 0;
	static final int APPLE = 1; //player 2
	static final int FOOD = 2;
	static final int WALL = 5;
	static final int SNAKE_HEAD = 3;
	static final int SNAKE_BODY = 4;
	
	
	public static void main(String[] args) {
		
		//set up window
		boolean has_food = false;
		Window w = new Window();
		w.setup();
		w.drawLoadingscreen(); //MH. This is NOT the intro screen. It's an additional screen that does nothing.

		setupGameObjects();

		boolean show_start = true;
		boolean show_end = false;
		boolean end = false;
		boolean freeze = true;
		//String end_phraseString = "Apple Won the Game!";
		int sleep_time = 20;
		long time = System.currentTimeMillis();
		long prev_time = time;
		long last_tie = time;
		long last_food_spawn = time;

		//int last_tie = (int) System.currentTimeMillis();
		// Main loop
		while(true) { 
			time =  System.currentTimeMillis();
			timeDbl = (time - prev_time)/1000.0; //MH. Does not need casting.
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
				}
				
				if (!show_end) {
					w.drawBoard();
					apple.draw(w.gc, Window.WIDTH, Window.HEIGHT, Window.HS, Window.VS);
					head.draw(w.gc, Window.WIDTH, Window.HEIGHT, Window.HS, Window.VS);
					w.gc.setColor(new Color(80, 80, 80));
					if (food.alive) food.draw(w.gc, Window.WIDTH, Window.HEIGHT, Window.HS, Window.VS);

					for(int i = 0; i < blocks.size(); i++){
						blocks.get(i).draw(w.gc, Window.WIDTH, Window.HEIGHT, Window.HS, Window.VS);
						bboard[blocks.get(i).sy][blocks.get(i).sx] = 5;
						//Why is this in a FOR LOOP?!
						if (show_start) {
							w.start_scrn_wait = (int) (w.START_SCRN_WAIT*timeDbl);
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
				head = new SnakeHead(28, 25, 37, loadImage("Head_left.png"), loadImage("Head_right.png"), loadImage("Head_up.png"), loadImage("Head_down.png"));
				apple = new Apple(12, 2, 49, loadImage("apple.png"));
				bboard = new int[Window.VS][Window.HS];
				blocks = new ArrayList<Block>();
				for(int i = 0; i < Window.HS; i++) {
					for(int j = 0; j < Window.VS; j++) {
						bboard[j][i] = EMPTY;
					}
				}
				bboard = randomMaze(bboard);

				for(int i = 0; i < Window.HS; i++) {
					for(int j = 0; j < Window.VS; j++) {
						if (bboard[j][i] == WALL) blocks.add(new Block(i, j, loadImage("Block.png")));
					}
				}
				has_food = false;
				show_start = false;
				show_end = false;
				end = false;
				freeze = false;
				food = new Food (rand.nextInt(Window.VS), rand.nextInt(Window.HS), loadImage("banana.png"));
				food.alive = false;
				bboard[food.sy][food.sx] = FOOD;

			}

			bboard[apple.sy][apple.sx] = APPLE;
			Point pressed = w.get_key();
			if(!freeze) { // A boolean that is true when the game is over to prevent further player movement
				switch(pressed.x){
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
				bboard = apple.move(pressed.y, bboard);

				if (!has_food && time - last_food_spawn > 100 && !food.alive) { // spawns in another banana once it is eaten

					while (true) {
						food.sx = rand.nextInt(Window.HS);
						food.sy = rand.nextInt(Window.VS);
						if (bboard[food.sy][food.sx] == EMPTY) break;
					}
					food.alive = true;
					bboard[food.sy][food.sx] = FOOD;
					last_food_spawn = time;

				}

			}
			//Controlling The speed at which the players can move
			apple.move_wait = (int) (apple.MOVE_WAIT*timeDbl*60);
			head.move_wait = (int) (head.MOVE_WAIT*timeDbl*60);

		}

	}

	static void setupGameObjects() {

		//set up board
		for(int i = 0; i < Window.HS; i++) {
			for(int j = 0; j < Window.VS; j++) {
				bboard[j][i] = 0;
			}
		}
		bboard = randomMaze(bboard);// Loads Map

		//This is putting the blocks from the board into the arrayList
		for(int i = 0; i < Window.HS; i++) {
			for(int j = 0; j < Window.VS; j++) {
				if (bboard[j][i] == WALL) blocks.add(new Block(i, j, loadImage("Block.png")));
			}
		}
		//MH. This is copying the data from the blocks arraylist to the board.
		//But this is just done in reverse above!
		/*
		for(int i = 0; i < blocks.size(); i++) {
			bboard[blocks.get(i).sy][blocks.get(i).sx] = WALL;
		}
		*/

		//set up objects
		apple = new Apple(12, 2, 49, loadImage("apple.png"));
		head = new SnakeHead(28, 25, 37, loadImage("Head_left.png"), loadImage("Head_right.png"), loadImage("Head_up.png"), loadImage("Head_down.png"));
		food = new Food (rand.nextInt(Window.VS), rand.nextInt(Window.HS), loadImage("banana.png"));
		
		food.alive = false;
		bboard[food.sy][food.sx] = FOOD;
		
		
	}
	
	static int[][] randomMaze(int[][] bboard) {
		String filename = "maps.txt";
		InputStream inputStr = MainGame.class.getClassLoader().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStr)); 
        
        ArrayList<String> allMaps = new ArrayList<String>();
       
        try {
	        while (true) {
	        	String s = br.readLine();
	        	if (s == null) break;
	        	allMaps.add(s);
	        }
	        br.close();
        } catch (IOException e) {
        	System.out.println("ERROR: fatal IO exception");
        	System.out.println("Make sure that " + filename + " exists");
        	e.printStackTrace();
        	System.exit(0);
        }
        
        int number, randomNumber = rand.nextInt(allMaps.size());
        String line = allMaps.get(randomNumber);
        
        //parse the random map chosen.
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                number = line.charAt(i*30+j);
                number -= 48;
                if (bboard[i][j] == 0) bboard[i][j] = number;
            }
        }
       
        return bboard;
    }


	
	/*****************************************************************/
	/*  Utility methods that are used by other classes in this game. */
	/*****************************************************************/

	public static Point square_to_coords(int sqX, int sqY, int WIDTH, int HEIGHT, int HS, int VS) { //Converts the square to coordinates on screen
			return new Point((WIDTH/HS)*sqX, (HEIGHT/VS)*sqY);
	}
	
	/*public static Point square_to_coords(int sqX, int sqY) { //Converts the square to coordinates on screen
		int x, y;
		x = (Window.WIDTH/Window.HS)*sqX;
		y = (Window.HEIGHT/Window.VS)*sqY;
		
		return new Point(x, y);	
	}*/
	
	public static Point square_to_coords(int sqX, int sqY) {
		return square_to_coords(sqX, sqY, Window.WIDTH, Window.HEIGHT, Window.HS, Window.VS);	
	}
	
//  will be on screen. This is used for Apple and maybe SnakeHead
//              \/   
	public static boolean wbos(int sx, int sy, int HS, int VS) {
		if (0<=sx && sx<HS) {
			if(0<=sy && sy<VS) {
				return true;
			}
		}
		return false;
	}

	public static BufferedImage loadImage(String filename) {
		BufferedImage image = null;
		
		InputStream inputStr = MainGame.class.getClassLoader().getResourceAsStream(filename);

		if (inputStr != null) {
			try {
				image = ImageIO.read(inputStr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "An image failed to load: " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		return image;
	}
	

}