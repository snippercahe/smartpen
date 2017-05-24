package com.jinke.calligraphy.app.branch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;


public class BigImageView extends ImageView {

	public BigImageView(Context context) {
		super(context);
        
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onDraw(Canvas canvas){
		Rect rec=canvas.getClipBounds();
		canvas.drawColor(Color.GRAY);
        rec.bottom--;
        rec.right--;
        Paint paint=new Paint();
        paint.setStrokeWidth(20);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rec, paint);

        
        
		super.onDraw(canvas);
	}

}
