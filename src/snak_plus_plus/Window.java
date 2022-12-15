package snak_plus_plus;

//MH remove unused imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import hsa2.GraphicsConsole;



class Window {
	
	//These constants should be static (and maybe final) so that you can access them easily from elsewhere.
	//The don't change with each Window object.
	static int HS = 30; //Number of Horizontal Squares (X)
	static int VS = 30; //Number of Vertical Squares   (Y)
	
	static int WIDTH = HS*25;
	static int HEIGHT = VS*25;
	static int REAL_HEIGHT = HEIGHT+30; //the height of the full window, this takes into account the size of the bar with the X that closes the window
	
	boolean switch_crlts = false;
	BufferedImage snake_win =  loadImage("Snake_eat_apple.png");
	BufferedImage apple_win =  loadImage("Snake_dizzy.png");
	BufferedImage swap_btn = loadImage("swap_ctrls_button.png");
	
	int time = (int) System.currentTimeMillis();
	
	int breath_step = 10;
	int step = 1;
	Color color11 = new Color(67, 154, 134); //MH. Poor name for a colour
	GraphicsConsole gc = new GraphicsConsole(WIDTH, HEIGHT);
	//boolean ran_setup = false; MH  this is NEVER USED.
	
	Font end_font = new Font("Roboto Slab", Font.PLAIN, (int) (HS*1.4));
	Font big_end_font = new Font("Roboto Slab", Font.BOLD, (int) (HS*1.8));
	Font small_end_font = new Font("Roboto Slab", Font.PLAIN, (int) (HS*0.7));
	
	int start_scrn_wait = 100;
	int last_strt_scrn = time;
	final int START_SCRN_WAIT = start_scrn_wait;
	
	//NOTE: there is no window constructor here. This is intentional.
	
	void drawBoard() {
//		gc.setStroke(1); //MH. This line can go into setup(). It's never changed
		gc.setColor(new Color(20, 20, 20));		
		for(int i=0; i < HS; i++) {
			for(int j=0; j < VS; j++) {
				gc.drawRect(MainGame.square_to_coords(i, j).x, MainGame.square_to_coords(i, j).y,(int) (WIDTH/HS), (int) (HEIGHT/VS));			
			}
		}
	}
	
	void drawLoadingscreen() {
		gc.setColor(new Color(30, 30, 30));
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		gc.setColor(new Color(67, 154, 134));
		gc.setFont(big_end_font);
		gc.drawString("Loading...",(int) (HS*7.8), 300);	
	}
	
	void drawEndScreen(String phrase, Color color, Color bg_color, boolean snake_won) {
		
		gc.setColor(bg_color);
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		if(breath_step > 65) step *= -1;
		else if (breath_step < 2) step *= -1;
		breath_step += step;
		
		
		Color color2 = color;
		Color color1 = color;
		for(int i = 0; i <(int)(breath_step); i++) {
			color1 = darker(color1, 0.99);
		}
		gc.setColor(color);
		gc.setFont(big_end_font);
		gc.setColor(color2);
		gc.drawString("Game Over!",(int) (HS*3.2), 180);
		gc.setFont(end_font);
		gc.setColor(color2);
		gc.drawString(phrase,(int) (HS*3.2), 240);
		gc.setFont(small_end_font);
		gc.setColor(color1);
		gc.drawString("Hold SPACE to review the board, ESC to exit, or R to restart.",(int) (HS*(HS/11)), 490);
		if (snake_won) gc.drawImage(snake_win, (int) (230), 350);
		else gc.drawImage(apple_win, (int) (200), 300);	
	} //MH. problems with spacing of }
	
	//FIXME: MH. Make a whole new GraphicsConsole for the intro screen. 
	//		 It doesn't make sense to overwrite the standard gc.
	void drawStartScreen() {
		gc.setColor(new Color(30, 30, 30));
		time = (int) System.currentTimeMillis();
		gc.fillRect(0, 0, WIDTH, HEIGHT); 		
		
		Color color2 = new Color(67, 154, 134);
		
		//Need a comment for this
		if (time-last_strt_scrn > 20) {
			if(breath_step > 68) step *= -1;
			else if (breath_step < 10) step *= -1;
			breath_step += step;
			
			color11 = darker(color2, breath_step*0.01+0.3);
			
			last_strt_scrn = (int) System.currentTimeMillis();
		}
		
		gc.setFont(big_end_font);
		gc.setColor(color2);
		gc.drawString("Snak ++",(int) (HS*3.7), 180);
		gc.setFont(end_font);
		gc.setColor(color11);
		gc.drawString("Press SPACE to start",(int) (HS*3.7), 240);
		gc.setFont(small_end_font);
		gc.setColor(color2);
		
		if (switch_crlts) gc.drawString("Controls are Arrow Keys for the Snak and WASD for the Apple",(int) (HS*(HS/11)), 490);
		else gc.drawString("Controls are WASD for the Snak and Arrow Keys for the Apple",(int) (HS*(HS/11)), 490);
	}
	
	public Color brighter(Color color1, double FACTOR) {
        int r = color1.getRed();
        int g = color1.getGreen();
        int b = color1.getBlue();
        int alpha = color1.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255),
                         Math.min((int)(g/FACTOR), 255),
                         Math.min((int)(b/FACTOR), 255),
                         alpha);
    }
	
	public Color darker(Color color1, double FACTOR) {
        return new Color(Math.max((int)(color1.getRed()  *FACTOR), 0),
                         Math.max((int)(color1.getGreen()*FACTOR), 0),
                         Math.max((int)(color1.getBlue() *FACTOR), 0),
                         color1.getAlpha());
	}
	

	
	Point get_key(GraphicsConsole gc) {
		int num1 = 0, num2 = 0; //these are the keys that are being pressed num1 is the snake (player 1) the num2 is the apple (player 2)
  		
		
		if (!switch_crlts) {
			int key = gc.getLastWASD();
			switch(key) {
			case 87: //w
				num1 = 87;
				break;
			case 65: //a
				num1 = 65;
				break;
			case 83: //s
				num1 = 83;
				break;
			case 68: //d
				num1 = 68;
				break;
			
		
			}
			int key2 = gc.getLastArrow();
			switch(key2) {
			case 37: //left arrow
				num2 = 37;
				break;
			case 38: //up arrow
				num2 = 38;
				break;
			case 39: //right arrow
				num2 = 39;
				break;
			case 40: //down arrow
				num2 = 40;
				break;
			}
		} else {
			int key = gc.getLastArrow();
			switch(key) {
			case 38: //w
				num1 = 87;
				break;
			case 37: //a
				num1 = 65;
				break;
			case 40: //s
				num1 = 83;
				break;
			case 39: //d
				num1 = 68;
				break;
			
		
			}
			int key2 = gc.getLastWASD();
			switch(key2) {
			case 65: //left arrow
				num2 = 37;
				break;
			case 87: //up arrow
				num2 = 38;
				break;
			case 68: //right arrow
				num2 = 39;
				break;
			case 83: //down arrow
				num2 = 40;
				break;
			}
		}
		if(gc.isKeyDown(27)) System.exit(0);
		
		Point pressed = new Point(num1, num2);
		
		
		return pressed;
	}
	
	
	/*
	void readConfig(String filename) {
		try {
			URL url = this.getClass().getResource("/" + filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream())); 

			String line = null;
			while( (line = br.readLine()) != null) {
				//ignore lines that begin with '
				if (line.charAt(0) == '\'') continue;
				String[] data = line.trim().split(",");
				
				//do something with each line that you read
				System.out.println(line);
				//we could read more lines for more circles, but this is just proof of concept.
			}

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could not open " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Some unknown IO exception occurred.");
			e.printStackTrace();
			System.exit(0);
		}
	}
	*/
	
	public int[][] randomMaze(int[][] bboard) throws IOException {
		
		URL url = this.getClass().getResource("/maps.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream())); 
        
        int fileLength = 0;

        // figure out how many lines (mazes) there are in the file
        while (true) {
        	if (br.readLine() != null) fileLength++;
        	else break;
        }
        System.out.println(fileLength);
        br.close();
        //debug
        

        Random random = new Random();
        int number, randomNumber = random.nextInt(fileLength);
        BufferedReader maps = new BufferedReader(new InputStreamReader(url.openStream())); 
        String line = null;
        // move to the line with the randomly chosen maze
        System.out.println(randomNumber);
        for (int i = 0; i < randomNumber+1; i++) {
        	line = maps.readLine();
        }
        
        // grab the maze on that line
        
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                number = line.charAt(i*30+j);
                number -= 48;
                if (bboard[i][j] == 0) bboard[i][j] = number;
            }
        }
        maps.close();
        return bboard;
    }

	
	public void setup() { //Creates window container
		gc.setTitle("Snak ++");
		gc.setLocationRelativeTo(null);
		gc.setVisible(true);
		gc.setLocation(0, 0); //MH. This overrides setLocationRelativeTo null
		gc.enableMouse();
		gc.enableMouseMotion();
		gc.setResizable(false);
		gc.setAntiAlias(true);
		gc.setBackgroundColor(Color.BLACK);
		gc.clear();
		gc.setColor(new Color(80, 80, 80));
		gc.setStroke(1);
		//ran_setup = true;
	}
	
	
	public BufferedImage loadImage(String filename) {
		URL url = this.getClass().getResource("/" + filename);
		Image image = null;
		if (url != null) {
			try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "An image failed to load: " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		

		BufferedImage bim = new BufferedImage( image.getWidth(null), image.getHeight(null),  BufferedImage.TYPE_INT_ARGB);
		bim.getGraphics().drawImage(image, 0, 0, null);
		
		return bim;
	}
	
	
}
