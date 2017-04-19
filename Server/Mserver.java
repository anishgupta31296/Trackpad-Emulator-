package server;




import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Mserver {
	
	private static ServerSocket server = null;
	private static Socket client = null;
	private static BufferedReader in = null;
	private static String line;
	private static boolean isConnected=true;
	private static Robot robot;
	private static final int SERVER_PORT = 8998;
 
	public static void main(String[] args) {
		boolean leftpressed=false;
		boolean rightpressed=false;
		System.out.println("Started");

 
	    try{
	    		robot = new Robot();
			server = new ServerSocket(SERVER_PORT);
			System.out.println("Connecting");
//Create a server socket on port 8998
			client = server.accept(); 
			System.out.println("Connected!!");
//Listens for a connection to be made to this socket and accepts it
			in = new BufferedReader(new InputStreamReader(client.getInputStream())); //the input stream where data will come from client
		}catch (IOException e) {
			System.out.println("Error in opening Socket");
			System.exit(-1);
		}catch (AWTException e) {
			System.out.println("Error in creating robot instance");
			System.exit(-1);
		}
			
		//read input from client while it is connected
	    while(isConnected){
	        try{
			line = in.readLine(); //read input from client
			System.out.println(line); //print whatever we get from client
			
			//if user clicks on next
			if(line.equalsIgnoreCase("next")){
				//Simulate press and release of key 'n'
				robot.keyPress(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_N);
			}
			//if user clicks on previous
			else if(line.equalsIgnoreCase("previous")){
				//Simulate press and release of key 'p'
				robot.keyPress(KeyEvent.VK_P);
				robot.keyRelease(KeyEvent.VK_P);		        	
			}
			//if user clicks on play/pause
			else if(line.equalsIgnoreCase("play")){    
				//Simulate press and release of spacebar
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
			}
			else if(line.equalsIgnoreCase("hold"))
			{
				robot.delay(350);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

			}
			else if(line.equalsIgnoreCase("release"))
			{
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

			}
			//input will come in x,y format if user moves mouse on mousepad
			else if(line.contains(",")){
				float movex=Float.parseFloat(line.split(",")[0]);//extract movement in x direction
				float movey=Float.parseFloat(line.split(",")[1]);//extract movement in y direction
				Point point = MouseInfo.getPointerInfo().getLocation(); //Get current mouse position
				float nowx=point.x;
				float nowy=point.y;
				robot.mouseMove((int)(nowx+movex),(int)(nowy+movey));//Move mouse pointer to new location
				int x=line.split(",").length;
				//System.out.println(x);
				if(x==4)
				{
				float y1=Float.parseFloat(line.split(",")[1]);
				float y2=Float.parseFloat(line.split(",")[3]);
				float y=(y1+y2)/2;
				int avg=(int)y/2;
				int z=Math.abs(avg);
				robot.mouseWheel(avg);
				/*for(int k=0;k<z;k++)
				{   if(avg>=0)
					robot.mouseWheel(1);
				else
					robot.mouseWheel(-1);
				}*/
				}
				if(x==5)
				{
					float d=Float.parseFloat(line.split(",")[4]);
					robot.keyPress(KeyEvent.VK_CONTROL);
					int val=-(int)d/5;
					robot.mouseWheel(val);
					robot.keyRelease(KeyEvent.VK_CONTROL);
				}

				
			}
			//if user taps on mousepad to simulate a left click
			else if(line.contains("left_click")){
				//Simulate press and release of mouse button 1(makes sure correct button is pressed based on user's dexterity)
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			}
			else if(line.contains("right_click")){
				//Simulate press and release of mouse button 1(makes sure correct button is pressed based on user's dexterity)
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			}
			else if(line.contains("Three"))
			{	robot.keyPress(KeyEvent.VK_WINDOWS);
				robot.keyPress(KeyEvent.VK_TAB);	
				robot.keyRelease(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_WINDOWS);
			
			}
			//Exit if user ends the connection
			else if(line.equalsIgnoreCase("exit")){
				isConnected=false;
				//Close server and client socket
				server.close();
				client.close();
			}
	        } catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
	        }
      	}
	}
}