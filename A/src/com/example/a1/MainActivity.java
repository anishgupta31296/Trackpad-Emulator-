package com.example.a1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Context context;
    Button playPauseButton;
    Button nextButton;
    Button previousButton;
    TextView mousePad;

    private boolean isConnected=false;
    private boolean mouseMoved=false;
    private Socket socket;
    private PrintWriter out;

    private float initX =0,rX=0;
    private float initY =0,rY=0;
    private float disX =0,rdX=0;
    private float disY =0,rdY=0;
    int l=1,r=1;
    int pointerCount;
    int pointerId, pointerIndex;
    float fingerOneX,fingerOneY,fingerTwoX, fingerTwoY;
    int click=0,h=0;
    long clickTime=0, lastClickTime=10,delta=100;
    long clickTime1=10000, lastClickTime1=0,delta1=200;
    int start=0,fclick=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this; //save the context to show Toast messages

        //Get references of all buttons
        playPauseButton = (Button)findViewById(R.id.playPauseButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        previousButton = (Button)findViewById(R.id.previousButton);

        //this activity extends View.OnClickListener, set this as onClickListener
        //for all buttons
        playPauseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        //Get reference to the TextView acting as mousepad
        mousePad = (TextView)findViewById(R.id.mousePad);

        //capture finger taps and movement on the textview
        mousePad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isConnected && out!=null){
             /*   	     	switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN: out.println("touch");
                    case MotionEvent.ACTION_MOVE: out.println("motion");
                    case MotionEvent.ACTION_POINTER_DOWN: out.println("s touch");
                    case MotionEvent.ACTION_UP: out.println("click");
                    case MotionEvent.ACTION_POINTER_UP: out.println("s click");
                  
                    case MotionEvent.ACTION_CANCEL:out.println("cancel");
                }
               */
                	//out.println("Inside"+start);
                //	out.println("l="+l);
                	
                	if(start==1)
                	{	clickTime = System.currentTimeMillis();
                	out.println("RT:"+clickTime);
                	 if (clickTime - lastClickTime >delta&&h==0)
                 		{out.println("hold");
                 		h=1;
                 		
                 		}
                	}
                	if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && r==1)
                	{  //ACTION UP
                		if(pointerCount==3)
                			out.println("Three");
                		else
                		{out.println("right_click");
                		click=0;}
                    }
                	else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
                	{
                	
                		pointerCount = event.getPointerCount();
                	
                		   for(int i = 0; i < pointerCount; ++i)
                        {
                            pointerIndex = i;
                            pointerId = event.getPointerId(pointerIndex);
                            Log.d("pointer id - move",Integer.toString(pointerId));
                           /* if(pointerId == 0)
                            {
                                
                                fingerOneX = event.getX(pointerIndex);
                                fingerOneY = event.getY(pointerIndex);
                            }*/
                            if(pointerId == 1)
                            {
                                
                            	 rX = event.getX(pointerIndex);
                                 rY = event.getY(pointerIndex);
                            }
                        }
                        
                      r=1; 
                      click=0;                	//out.println(rX+"right"+rY);
                    
                	}
                	else
                		;
                     if(event.getActionMasked() == MotionEvent.ACTION_UP && (l==1||h==1))
                     {  //ACTION UP
                    	 if(h==1)
                         {out.println("release");
                         h=0;fclick=0;          click=0;  start=0;           
                         }
                    	 else
                    		 {click=0; start=0;
                    		 lastClickTime1 =System.currentTimeMillis();
                         	out.println("LT1:"+lastClickTime1);
                         	fclick=1;
                    	 out.println(Constants.MOUSE_LEFT_CLICK);}
                        
                        
                    }
                	else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){ //ACTION DOWN
                		int pointerCount = event.getPointerCount();
                		if(pointerCount==1){
                		disX = event.getX()- initX; //Mouse movement in x direction
                        disY = event.getY()- initY; //Mouse movement in y direction
                        /*set init to new position so that continuous mouse movement
                        is captured*/
                        initX = event.getX();
                        initY = event.getY();
                        if(disX !=0|| disY !=0){
                            out.println(disX +","+ disY);
                            l=0;//send mouse movement to server
                            click=0; start=0;

                        }
                        mouseMoved=true;
                        
                		}
                		if(pointerCount==2){
                			
                			double d1=Math.sqrt(Math.pow(initX-rX, 2)+ Math.pow(initY-rY, 2));
                            for(int i = 0; i < pointerCount; ++i)
                            {   
                                pointerIndex = i;
                                pointerId = event.getPointerId(pointerIndex);
                                Log.d("pointer id - move",Integer.toString(pointerId));
                                if(pointerId == 0)
                                {
                                	disX = event.getX(pointerIndex)- initX; 
                                    disY = event.getY(pointerIndex)- initY; 
                                    initX = event.getX(pointerIndex);
                                    initY = event.getY(pointerIndex);
                                }
                                if(pointerId == 1)
                                {
                                	rdX = event.getX(pointerIndex)- rX; 
                                    rdY = event.getY(pointerIndex)- rY; 
                                    rX = event.getX(pointerIndex);
                                    rY = event.getY(pointerIndex);
                                }
                            }
                            
                            if((disY*rdY)>=0)
                            {
                            if(disX!=0||disY!=0||rdX!=0||rdY!=0) 
                            {
                            	out.println(disX +","+ disY+","+rdX+","+rdY);
                            	l=0;r=0;
                                click=0;

                            }
                            }
                            else
                            {
                    			double d2=Math.sqrt(Math.pow(initX-rX, 2)+ Math.pow(initY-rY, 2));
                    			double d=d2-d1;
                            	out.println(disX +","+ disY+","+rdX+","+rdY+","+d);

                            }
                		}

                    }   
                	else if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
                	{	if(fclick==1)
                	{	clickTime1 = System.currentTimeMillis();
                	out.println("RT1:"+clickTime1);
                	fclick=0;
                	if (clickTime1 - lastClickTime1 <delta1)
                		click=1;}
                	else
                		fclick=0;
                		
                		initX =event.getX();
                        initY =event.getY();
                        mouseMoved=false;
                        out.println("touch");
                        l=1;
                        
                        if(click==1)
                        {
                        	lastClickTime =System.currentTimeMillis();
                        	out.println("LT:"+lastClickTime);
                        start=1;
                        }
                	}	
                	else
                		;
                    
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_connect) {
            ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
            connectPhoneTask.execute(Constants.SERVER_IP); //try to connect to server in another thread
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //OnClick method is called when any of the buttons are pressed
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPauseButton:
                if (isConnected && out!=null) {
                    out.println(Constants.PLAY);//send "play" to server
                }
                break;
            case R.id.nextButton:
                if (isConnected && out!=null) {
                    out.println(Constants.NEXT); //send "next" to server
                }
                break;
            case R.id.previousButton:
                if (isConnected && out!=null) {
                    out.println(Constants.PREVIOUS); //send "previous" to server
                }
                break;
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(isConnected && out!=null) {
            try {
                out.println("exit"); //tell server to exit
                socket.close(); //close socket
            } catch (IOException e) {
                Log.e("remotedroid", "Error in closing socket", e);
            }
        }
    }

    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(params[0]);
                socket = new Socket(serverAddr, Constants.SERVER_PORT);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            isConnected = result;
            Toast.makeText(context,isConnected?"Connected to server!":"Error while connecting",Toast.LENGTH_LONG).show();
            try {
                if(isConnected) {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true); //create output stream to send data to server
                }
            }catch (IOException e){
                Log.e("remotedroid", "Error while creating OutWriter", e);
                Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }
}