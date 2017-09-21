package com.example.DA.rebound;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by darrelladjei on 14/10/2015.
 */
public class MainThread extends Thread {

    public static  final String TAG= MainActivity.class.getSimpleName();


    private int FPS= 50;
    private SurfaceHolder mSurfaceHolder;
    private MainActivity.Game mGame;
    private  boolean running;
    public  static Canvas mCanvas;
    boolean paused=false;


    public MainThread(SurfaceHolder surfaceHolder, MainActivity.Game game){
        super();
        mSurfaceHolder = surfaceHolder;
        mGame = game;

    }


    @Override
    public  void run(){
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime =0;
        int frameCount=0;
        long targetTime=1000/FPS;


        while(running){
            startTime = System.nanoTime();
            mCanvas=null;

            //try locking the Canvas for pixel editing

            try{
                mCanvas = mSurfaceHolder.lockCanvas();

                synchronized (mSurfaceHolder){
                    //Log.v(TAG,"flopping");
                    mGame.update();
                    mGame.Draw(mCanvas);
                }



            }catch (Exception e){
                e.printStackTrace();
                Log.v(TAG, "flopped");
            }
            while(running && paused){
                mGame.Draw(mCanvas);

                try {
                    this.sleep(50);
                }catch (InterruptedException e){
                    Log.v(TAG,"peak");
                }


            }

            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime= targetTime-timeMillis;

            try{

                this.sleep(waitTime);

            }catch (Exception e){
                Log.v(TAG,"also flopped");
            }

            finally {
                if(mCanvas!=null){
                    try{

                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            totalTime +=System.nanoTime()-startTime;
            frameCount++;
            if(frameCount==30){
                frameCount=0;

            }
        }
    }
    public void setRunning(boolean b){

        this.running=b;


    }
    public void setPaused(boolean b){
        paused=b;

    }

    public  boolean getPaused(){

        return paused;
    }
}
