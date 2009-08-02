package fr.dox.sideralis.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class SplashView extends View {

	private Paint paintText;

	public SplashView(Context context) {
		super(context);
		initSplashView();
	}

	private void initSplashView() {
		paintText = new Paint();
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawText("Please wait", 20, 20, paintText);
	}

}
