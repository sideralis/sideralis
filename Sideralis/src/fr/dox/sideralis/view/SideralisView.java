package fr.dox.sideralis.view;

import fr.dox.sideralis.R;
import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.ConstellationCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.data.StarCatalog;
import fr.dox.sideralis.object.ConstellationObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.projection.plane.Zenith;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SideralisView extends View {
	public static final double AOV = 45;
	private float bearing;
	private Paint circlePaint;
	private String northString;
	private String southString;
	private String eastString;
	private String westString;
	private Paint textPaint;
	private int textHeight;
	private Paint markerPaint;
	private Paint paintStar;
	private Paint paintConstellations;
	private Paint paintMessier;
	/** The sky */
	private Sky mySky;
	/** My position */
//	private Position myPosition;
    /** Table to store x and y position on screen of stars */
    private ScreenCoord[] screenCoordStar;
    /** The projection use to convert azimuth and height to a plan (x & y) */
    private Zenith projection;
    private int starBaseColor,starIncColor;
	private ScreenCoord[] screenCoordMessier;
    
	/**
	 * 
	 * @param context
	 */
	public SideralisView(Context context) {
		super(context);
		initSideralisView();
	}
	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SideralisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSideralisView();
	}
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public SideralisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSideralisView();
	}
	/**
	 * 
	 */
	private void initSideralisView() {
		setFocusable(true);
		
		// Create paint object
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(R.color.background_color);
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		Resources r = this.getResources();
		northString = r.getString(R.string.north);
		southString = r.getString(R.string.south);
		eastString = r.getString(R.string.east);
		westString = r.getString(R.string.west);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(r.getColor(R.color.text_color));
		
		starBaseColor = r.getColor(R.color.star_color_base);
		starIncColor = r.getColor(R.color.star_color_inc);
		
		textHeight = (int)textPaint.measureText("yY");
		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(r.getColor(R.color.marker_color));

        paintStar = new Paint();
        
        paintConstellations = new Paint();
        paintConstellations.setColor(r.getColor(R.color.constellation_color));
        
        paintMessier = new Paint();
        paintMessier.setColor(r.getColor(R.color.messier_color));

		// Create Sideralis object
//        myPosition = ((Sideralis)this.getContext()).getMyPosition();
        mySky = ((Sideralis)this.getContext()).getMySky();
        
		screenCoordMessier = new ScreenCoord[mySky.getNumberOfMessierObjects()];
		for (int i=0;i<mySky.getNumberOfMessierObjects();i++) 
			screenCoordMessier[i] = new ScreenCoord();
		screenCoordStar = new ScreenCoord[mySky.getNumberOfStars()];
		for (int i=0;i<mySky.getNumberOfStars();i++)
			screenCoordStar[i] = new ScreenCoord();
		projection = new Zenith(0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureWidth = measure(widthMeasureSpec);
		int measureHeight = measure(heightMeasureSpec);
		
		int d = Math.min(measureWidth,measureHeight);
		setMeasuredDimension(d,d);
	}
	/**
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measure(int measureSpec) {
		int result = 0;
		
		// Decode the measurement specification
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified
			result = 200;
		} else {
			// As you want to fill the available space return always the full available bounds
			result = specSize;
		}
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		int px = getMeasuredWidth() /2;
		int py = getMeasuredHeight() /2;
		
		int radius = Math.min(px,py);
		
		// Draw the background
		canvas.drawCircle(px, py, radius, circlePaint);
		
		// Rotate our perspective so that the top is facing the current bearing
		canvas.save();
		canvas.rotate(-bearing,px,py);
		
		int textWidth = (int)textPaint.measureText("W");
		int cardinalX = px - textWidth/2;
		int CardinalY = py - radius + textHeight;
		
		// Draw the marker every 15 degrees and text every 45
		for (int i=0;i<24;i++) {
			// Draw a marker
			canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint);
			canvas.save();
			canvas.translate(0, textHeight);
			
			// Draw the cardinal points
			if (i%6==0) {
				String dirString = "";
				switch (i) {
				case 0:
					dirString = northString;
					int arrowY = 2*textHeight;
					canvas.drawLine(px,arrowY,px-5,3*textHeight,markerPaint);
					canvas.drawLine(px,arrowY,px+5,3*textHeight,markerPaint);
					break;
				case 6: 
					dirString = westString;
					break;
				case 12:
					dirString  = southString;
					break;
				case 18:
					dirString = eastString;
					break;
				}
				canvas.drawText(dirString, cardinalX, CardinalY, textPaint);
			} 
			canvas.restore();
			canvas.rotate(15,px,py);	
		}
		canvas.restore();
		// Draw stars
		project(radius,px,py);
		drawStars(canvas);
		drawConstellations(canvas);
		drawMessier(canvas);
	}
    /**
     * Draw stars
     * @param canvas the Graphics object
     */
    private void drawStars(Canvas canvas) {
        int nbOfStars = mySky.getNumberOfStars();
        
        for (int k = 0; k < nbOfStars; k++) {
            if (screenCoordStar[k].isVisible()) {
                // Star is above horizon
                int mag = (int) (StarCatalog.getStar(k).getMag());
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
            co = mySky.getConstellations().getOptConstellation(i);
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
        for (int k = 0; k < mySky.getNumberOfMessierObjects(); k++) {
            if (screenCoordMessier[k].isVisible()) {
                // Messier object is above horizon
                 canvas.drawPoint(screenCoordMessier[k].x, screenCoordMessier[k].y,paintMessier);
            }
        }
    }
    /**
     * Calculate the the x and y positions of all objects on screen
     * Used to accelerate display because calculation is not needed at each display
     */
    public void project(int radius,int px,int py) {
        for (int k = 0; k < mySky.getNumberOfStars(); k++) {
            screenCoordStar[k].setVisible(false);
                // For a zenith view
            if (mySky.getStar(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordStar[k].setVisible(true);
                screenCoordStar[k].x = (short) (px + projection.getX(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight())*radius);
                screenCoordStar[k].y = (short) (py + projection.getY(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight())*radius);
            }
        }
        
        for (int k = 0; k < mySky.getNumberOfMessierObjects(); k++) {
            screenCoordMessier[k].setVisible(false);
                // For a zenith view
            if (mySky.getMessier(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordMessier[k].setVisible(true);
                screenCoordMessier[k].x = (short) (px + projection.getX(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight())*radius);
                screenCoordMessier[k].y = (short) (py + projection.getY(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight())*radius);
            }
        }
    }
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
    
}

