package fr.dox.sideralis.view;

import fr.dox.sideralis.R;
import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.ConstellationCatalog;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ConstellationObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.projection.plane.Zenith;
import fr.dox.sideralis.projection.sphere.MoonProj;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SideralisView extends SurfaceView implements SurfaceHolder.Callback {
	
	private SurfaceHolder holder;
	
	/** 
	 * a reference to my sky
	 */
	private Sky mySky;
    /** The projection use to convert azimuth and height to a plan (x & y) */
    private Zenith projection;
    
    private int starBaseColor,starIncColor;

    /** Table to store x and y position on screen of stars */
    private ScreenCoord[] screenCoordStar;
	private ScreenCoord[] screenCoordMessier;
    private ScreenCoord screenCoordSun;
    private ScreenCoord screenCoordMoon;
    private ScreenCoord[] screenCoordPlanets;

    /** Default size in pixel for Moon and Sun */
    private final short SIZE_MOON_SUN = 4;

	private SideralisViewThread mySideralisViewThread;

    
	/**
	 * 
	 * @param context
	 */
	public SideralisView(Context context) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		mySideralisViewThread = new SideralisViewThread(context,holder);
	}
	/**
	 * 
	 * @param context
	 */
	public SideralisView(Context context, AttributeSet attrs) {
		super(context,attrs);
		holder = getHolder();
		holder.addCallback(this);
		mySideralisViewThread = new SideralisViewThread(context,holder);
	}
	
	public void pause() {
		if (mySideralisViewThread != null) {
			mySideralisViewThread.requestExitAndWait();
			mySideralisViewThread = null;
		}			
	}
	/**
	 * 
	 * @param counter
	 */
	public void setCounter(int counter) {
		mySideralisViewThread.setCounter(counter);
	}
    /**
     * Calculate the the x and y positions of all objects on screen
     * Used to accelerate display because calculation is not needed at each display
     */
    public void project() {
        // === Stars ===
        for (int k = 0; k < screenCoordStar.length; k++) {
            screenCoordStar[k].setVisible(false);
                // For a zenith view
            if (mySky.getStar(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordStar[k].setVisible(true);
                screenCoordStar[k].x = (short)projection.getX(projection.getVirtualX(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
                screenCoordStar[k].y = (short)projection.getY(projection.getVirtualY(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
            }
        }

        // === Messiers ===
        for (int k = 0; k < screenCoordMessier.length; k++) {
            screenCoordMessier[k].setVisible(false);
                // For a zenith view
            if (mySky.getMessier(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordMessier[k].setVisible(true);
                screenCoordMessier[k].x = (short)projection.getX(projection.getVirtualX(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
                screenCoordMessier[k].y = (short)projection.getY(projection.getVirtualY(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
            }
        }
        
        // === Sun ===
        screenCoordSun.setVisible(false);
        if (mySky.getSun().getHeight() > 0) {
            screenCoordSun.setVisible(true);
            screenCoordSun.x = (short)projection.getX(projection.getVirtualX(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
            screenCoordSun.y = (short)projection.getY(projection.getVirtualY(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
        }

        // === Moon ===
        screenCoordMoon.setVisible(false);
        if (mySky.getSun().getHeight() > 0) {
            screenCoordMoon.setVisible(true);
            screenCoordMoon.x = (short)projection.getX(projection.getVirtualX(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
            screenCoordMoon.y = (short)projection.getY(projection.getVirtualY(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
        }

        // === Planets ===
        for (int k = 0; k < screenCoordPlanets.length; k++) {
            screenCoordPlanets[k].setVisible(false);
                // For a zenith view
            if (mySky.getPlanet(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordPlanets[k].setVisible(true);
                screenCoordPlanets[k].x = (short)projection.getX(projection.getVirtualX(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
                screenCoordPlanets[k].y = (short)projection.getY(projection.getVirtualY(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
            }
        }
    }
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mySideralisViewThread != null) 
			mySideralisViewThread.onWindowsResize(width,height);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mySideralisViewThread.start();
			
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		pause();		
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mySideralisViewThread.doTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * 
	 * @author Bernard
	 * TODO add synchronized
	 */
	class SideralisViewThread extends Thread {
		private boolean run;
		private Context mContext;
		private SurfaceHolder mHolder;
		
		private int mState;
		public static final int STATE_STARTING=0;
		public static final int STATE_RUNNING=1;

	    /** Number of frames per second */
	    private static final int MAX_CPS = 10;
	    /** Time in millisecond between 2 frames */
	    private static final int MS_PER_FRAME = 1000 / MAX_CPS;
	    
	    private int counter;
	    private static final int COUNTER = 100;
	    
	    private int counterSplash;
	    private static final short COUNT0 = -15;                                             // Constant color red screen
	    private static final short COUNT1 = 0;                                              // Decreasing color from red to black
	    private static final short COUNT2 = 10;                                             // Constant color logo DoX
	    private static final short COUNT3 = 25;
	    private static final short COUNT4 = 35;

	    private int canvasWidth = 1;
	    private int canvasHeight = 1;

		/**
		 * All paint objects
		 */
		private Paint paintStar;
		private Paint paintConstellations;
		private Paint paintMessier;
		private Paint paintMoon;
		private Paint paintSun;
		private Paint paintHorizon;
		private Paint paintCardinal;
		private String northCardinalString,southCardinalString,eastCardinalString,westCardinalString;
		private String pleaseWaitString;
		private Paint[] paintPlanets;
		private Paint paintSplash;
		
		private Bitmap introImg;
		private Bitmap logoImg;
		private String versionName;
				
		public SideralisViewThread(Context context,SurfaceHolder holder) {
			super();
			mContext = context;
			mHolder = holder;
			initSideralisViewThread();
		}
		/**
		 * 
		 */
		private void initSideralisViewThread() {
			run = true;
			mState = STATE_STARTING;
			
			Resources r = mContext.getResources();
			// Create string
			northCardinalString = r.getString(R.string.north);
			southCardinalString = r.getString(R.string.south);
			eastCardinalString = r.getString(R.string.east);
			westCardinalString = r.getString(R.string.west);
			pleaseWaitString = r.getString(R.string.please_wait);
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi;
			try {
				pi = pm.getPackageInfo(mContext.getPackageName(), 0);
				versionName = pi.versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Create paint object
			paintHorizon = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintHorizon.setColor(r.getColor(R.color.horizon_color));
			paintHorizon.setStrokeWidth(1);
			paintHorizon.setStyle(Paint.Style.FILL_AND_STROKE);
									
	        paintStar = new Paint();
			starBaseColor = r.getColor(R.color.star_color_base);
			starIncColor = r.getColor(R.color.star_color_inc);
	        
	        paintConstellations = new Paint();
	        paintConstellations.setColor(r.getColor(R.color.constellation_color));
	        
	        paintMessier = new Paint();
	        paintMessier.setColor(r.getColor(R.color.messier_color));
	        
	        paintMoon = new Paint();
	        paintMoon.setColor(r.getColor(R.color.moon_color));
	        paintMoon.setStyle(Paint.Style.FILL);
	        
	        paintSun = new Paint();
	        paintSun.setColor(r.getColor(R.color.sun_color));
	        paintSun.setStyle(Paint.Style.FILL);
	        
	        paintPlanets = new Paint[Sky.NB_OF_SYSTEM_SOLAR_OBJECTS];
	        for (int i=0;i<Sky.NB_OF_SYSTEM_SOLAR_OBJECTS;i++) {
	        	paintPlanets[i] = new Paint();
	        	paintPlanets[i].setStyle(Paint.Style.FILL);
	        }
	        paintPlanets[Sky.JUPITER].setColor(r.getColor(R.color.jupiter_color));
	        paintPlanets[Sky.MERCURY].setColor(r.getColor(R.color.mercury_color));
	        paintPlanets[Sky.MARS].setColor(r.getColor(R.color.mars_color));
	        paintPlanets[Sky.SATURN].setColor(r.getColor(R.color.saturn_color));
	        paintPlanets[Sky.VENUS].setColor(r.getColor(R.color.venus_color));
	        
	        paintSplash = new Paint();
	        paintSplash.setColor(0xFF000000);
	        paintSplash.setTextAlign(Paint.Align.CENTER);
	        
	        paintCardinal = new Paint();
	        paintCardinal.setColor(r.getColor(R.color.cardinal_color));
	        paintCardinal.setTextAlign(Paint.Align.CENTER);

	        // Create Sideralis object
	        mySky = ((Sideralis)mContext).getMySky();
	        
			screenCoordMessier = new ScreenCoord[MessierCatalog.getNumberOfObjects()];
			for (int i=0;i<MessierCatalog.getNumberOfObjects();i++) 
				screenCoordMessier[i] = new ScreenCoord();
			screenCoordStar = new ScreenCoord[mySky.getStarsProj().length];
			for (int i=0;i<screenCoordStar.length;i++)
				screenCoordStar[i] = new ScreenCoord();
			
	        screenCoordSun = new ScreenCoord();
	        screenCoordMoon = new ScreenCoord();
	        screenCoordPlanets = new ScreenCoord[Sky.NB_OF_SYSTEM_SOLAR_OBJECTS];
	        for (int k=0;k<Sky.NB_OF_SYSTEM_SOLAR_OBJECTS;k++)
	            screenCoordPlanets[k] = new ScreenCoord();
			
			System.out.println("measure use");
			projection = new Zenith(getMeasuredHeight(),getMeasuredWidth());
			
			projection.setHeightDisplay(getMeasuredHeight());
			projection.setWidthDisplay(getMeasuredWidth());
			projection.setView();
			
			introImg = BitmapFactory.decodeResource(r, R.drawable.iya_logo);
			logoImg = BitmapFactory.decodeResource(r, R.drawable.dox);
			
			counter = COUNTER;
			counterSplash = COUNT0;
						
		}
		
		@Override
		public void run() {
	        long cycleStartTime;

	        while(run) {
	            cycleStartTime = System.currentTimeMillis();
	            // Check if we need to calculate position
	            if (mState == STATE_RUNNING) {
		            if (counter == 0) {
		                // Yes, do it in a separate thread
		                new Thread(mySky).start();
		                counter = COUNTER;
		            } else {
		                // No, decrease counter
		                counter--;
		            }
	            }
	            
				Canvas canvas = mHolder.lockCanvas();
				if (mState == STATE_RUNNING)
					draw(canvas);
				else if (mState == STATE_STARTING) 
					drawSplash(canvas);
				mHolder.unlockCanvasAndPost(canvas);
	            
				/* Thread is set to sleep if it remains some time before next frame */
	            long timeSinceStart = (System.currentTimeMillis() - cycleStartTime);
	            if (timeSinceStart < MS_PER_FRAME) {
	                try {
	                    Thread.sleep(MS_PER_FRAME - timeSinceStart);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
			}
		}
		
		/**
		 * @param counter the counter to set
		 */
		public void setCounter(int counter) {
			synchronized(mHolder) {
				this.counter = counter;
			}
		}
		/**
		 * 
		 */
		public void requestExitAndWait() {
			run = false;
			try {
				join();
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
		}
		/**
		 * 
		 * @param w
		 * @param h
		 */
		public void onWindowsResize(int w,int h) {
			synchronized (mHolder) {
				canvasWidth = w;
				canvasHeight = h;
				projection.setHeightDisplay(h);
				projection.setWidthDisplay(w);
				projection.setView();										
			}
		}
		/**
		 * 
		 * @param event
		 */
		public void doTouchEvent(MotionEvent event) {
			synchronized (mHolder) {
				if (mState == STATE_RUNNING){
					int action = event.getAction();
					switch (action) {
						case (MotionEvent.ACTION_MOVE):
							
							break;
						case (MotionEvent.ACTION_DOWN):
							break;
					}
				} else if (mySky.getProgress() == 100) {
					mState = STATE_RUNNING;
				}
			}
			
		}
		/**
		 * 
		 * @param canvas
		 */
		public void drawSplash(Canvas canvas) {
	        if (counterSplash<COUNT4+10)
	        	counterSplash++;
	        // =================================
	        // ====== Display intro Image ======
	        // =================================
	        if (counterSplash < COUNT1) {
	        	canvas.drawARGB(0xff,166, 34, 170);
				canvas.drawText(pleaseWaitString, canvasWidth/2, canvasHeight/2, paintSplash);
	        } else if (counterSplash < COUNT2) {	        	
	        	canvas.drawARGB(0xff,166 - counterSplash * 166 / COUNT2, 34 - counterSplash * 34 / COUNT2, 170 - counterSplash * 170 / COUNT2);
				canvas.drawBitmap(logoImg, canvasWidth/2 - logoImg.getWidth()/2, canvasHeight/2-logoImg.getHeight()/2, null);
	        } else if (counterSplash < COUNT3) {
	        	canvas.drawARGB(0xff,0, 0, 0);
				canvas.drawBitmap(logoImg, canvasWidth/2 - logoImg.getWidth()/2, canvasHeight/2-logoImg.getHeight()/2, null);
	        } else if (counterSplash < COUNT4) {
	        	canvas.drawARGB(0xff,0, 0, 0);
				canvas.drawBitmap(introImg, canvasWidth/2 - introImg.getWidth()/2, canvasHeight/2-introImg.getHeight()/2, null);
	            int c = counterSplash - COUNT3;
	            c = 0xff000000+(c * 166 / (COUNT4 - COUNT3)*0x100+c * 34 / (COUNT4 - COUNT3))*0x100 + c * 170 / (COUNT4 - COUNT3); 
	            paintSplash.setColor(c );
				canvas.drawText("SIDERALIS", canvasWidth/2, 20, paintSplash);
				canvas.drawText(" " + versionName, canvasWidth/2, canvasHeight-40, paintSplash);
	        } else {
	        	canvas.drawARGB(0xff,0, 0, 0);
				canvas.drawBitmap(introImg, canvasWidth/2 - introImg.getWidth()/2, canvasHeight/2-introImg.getHeight()/2, null);
				canvas.drawText("SIDERALIS", canvasWidth/2, 20, paintSplash);
				canvas.drawText(" " + versionName, canvasWidth/2, canvasHeight-40, paintSplash);
	        }
			
			
			int p = mySky.getProgress();
			if (p == 100 ) {
				canvas.drawText("Press any key or touch screen",canvasWidth/2,canvasHeight-20,paintSplash);
			}
//			Log.d("Bernard Counter",new Integer(counterSplash).toString());
//			Log.d("Bernard Progress",new Integer(p).toString());

		}
		
		public void draw(Canvas canvas) {
			if (mySky.isCalculationDone()) {
	            mySky.setCalculationDone(false);
	            mySky.setProgress(0);
	            canvas.drawARGB(0xff,0,0,0);
	            canvas.drawText("Wait",canvasWidth/2,canvasHeight/2,paintSplash);
	            project();				
			} else {
				// Draw horizon
				drawHorizon(canvas);
				// Draw constellations
				drawConstellations(canvas);
				// Draw stars
				drawStars(canvas);
				// Draw Messier objects
				drawMessier(canvas);
				// Draw Sun, Moon and planets
				drawSystemSolarObjects(canvas);
				// Draw progress bar
				canvas.drawLine(0, 0, mySky.getProgress(), 0, paintConstellations);
			}
		}
		/**
		 * 
		 * @param canvas
		 */
		private void drawHorizon(Canvas canvas) {
	        float x1,  y1,  x2,  y2,  x3,  y3,  x4,  y4;
	        // Zenith view
	        // Draw circle

	        x1 = projection.getX(0);
	        y1 = projection.getY(0);
	        
	        canvas.drawARGB(0xff0,0,0,0);
	        canvas.drawCircle(x1, y1, x1, paintHorizon);
	        
	        // Draw 'points cardinaux'
	        double rot = -projection.getRot();
	        x1 = projection.getX(Math.cos(rot) * .95);
	        y1 = projection.getY(Math.sin(rot) * .95);
	        x2 = projection.getX(Math.cos(rot + Math.PI) * .95);
	        y2 = projection.getY(Math.sin(rot + Math.PI) * .95);
	        x3 = projection.getX(Math.cos(rot + Math.PI / 2) * .95);
	        y3 = projection.getY(Math.sin(rot + Math.PI / 2) * .95);
	        x4 = projection.getX(Math.cos(rot + 3 * Math.PI / 2) * .95);
	        y4 = projection.getY(Math.sin(rot + 3 * Math.PI / 2) * .95);
	        canvas.drawText(westCardinalString, (int) x1, (int) y1 + paintCardinal.measureText(westCardinalString)/2 , paintCardinal);
	        canvas.drawText(eastCardinalString, (int) x2, (int) y2 + paintCardinal.measureText(eastCardinalString)/2, paintCardinal);
	        canvas.drawText(southCardinalString, (int) x3, (int) y3 +paintCardinal.measureText(southCardinalString)/2, paintCardinal);
	        canvas.drawText(northCardinalString, (int) x4, (int) y4 +paintCardinal.measureText(northCardinalString)/2, paintCardinal);		
		}
	    /**
	     * Draw stars
	     * @param canvas the Graphics object
	     */
	    private void drawStars(Canvas canvas) {
	        int nbOfStars = mySky.getStarsProj().length;
	        
	        for (int k = 0; k < nbOfStars; k++) {
	            if (screenCoordStar[k].isVisible()) {
	                // Star is above horizon
	                float magf = mySky.getStar(k).getObject().getMag();
	                int mag = (int) (magf);
	                if (mag > 5) {
	                    mag = 5;
	                }
	                if (mag < 0) {
	                    mag = 0;
	                // Select color of star
	                }
	                int col = starBaseColor - mag * starIncColor;
	                // Represent star as a dot
	                paintStar.setColor(col);
	                canvas.drawPoint(screenCoordStar[k].x, screenCoordStar[k].y, paintStar);
	            }
	        }
	    }
	    private void drawConstellations(Canvas canvas) {
	        short kStar1 = 0;
	        short kStar2 = 0;
	        int i,  j;
	        ConstellationObject co;
	        
	        // If constellations should be displayed
	        for (i = 0; i < ConstellationCatalog.getNumberOfConstellations(); i++) {
	            co = ConstellationCatalog.getConstellation(i);
	            // For all constellations
	            for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
	                kStar1 = co.getIdx(j);               						// Get index of stars in constellation
	                kStar2 = co.getIdx(j + 1);             						// Get index of stars in constellation
	                // Draw one part of the constellation
	                if (screenCoordStar[kStar1].isVisible() && screenCoordStar[kStar2].isVisible()) {
	                    // A line between 2 stars is displayed only if the 2 stars are visible.
	                        canvas.drawLine(screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, screenCoordStar[kStar2].x, screenCoordStar[kStar2].y,paintConstellations);
	                }
	            }
	        }
	    }
	    /**
	     * Draw the Messier objects
	     * @param canvas
	     */
	    private void drawMessier(Canvas canvas) {
	        for (int k = 0; k < screenCoordMessier.length; k++) {
	            if (screenCoordMessier[k].isVisible()) {
	                // Messier object is above horizon
	                 canvas.drawPoint(screenCoordMessier[k].x, screenCoordMessier[k].y,paintMessier);
	            }
	        }
	    }
	    /**
	     * Draw the sun, the moon and planets from our solar system
	     * @param canvas
	     */
	    private void drawSystemSolarObjects(Canvas canvas) {
	        // -------------------------
	        // ------ Display moon -----
	        if (screenCoordMoon.isVisible()) {
	            int z = (int) (projection.getZoom() * SIZE_MOON_SUN);
	            RectF rec = new RectF(screenCoordMoon.x - z,screenCoordMoon.y - z,screenCoordMoon.x + z,screenCoordMoon.y + z);
	            switch (mySky.getMoon().getPhase()) {
	                case MoonProj.FIRST:
	                	canvas.drawArc(rec,-100,200,true,paintMoon);
	                    break;
	                case MoonProj.LAST:
	                	canvas.drawArc(rec,80,200,true,paintMoon);
	                    break;
	                case MoonProj.NEW:
	                	canvas.drawArc(rec,0,360,true,paintMoon);
	                    break;
	                case MoonProj.FULL:
	                	canvas.drawArc(rec,0,360,true,paintMoon);
	                    break;
	            }
	        }
	        // -------------------------
	        // ------ Display sun -----
	        if (screenCoordSun.isVisible()) {
	            int z = (int) (projection.getZoom() * SIZE_MOON_SUN);
	        	canvas.drawCircle(screenCoordSun.x,screenCoordSun.y,z,paintSun);
	        }
	        // ---------------------------
	        // ------ Display planets -----
	        for (int k=0;k<screenCoordPlanets.length;k++) {
	            if (screenCoordPlanets[k].isVisible()) {
	                canvas.drawCircle(screenCoordPlanets[k].x, screenCoordPlanets[k].y , 4, paintPlanets[k]);
	            }
	        }
	    }
		
	}
    
}

