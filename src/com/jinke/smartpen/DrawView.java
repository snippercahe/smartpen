package com.jinke.smartpen;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.widget.ImageView.ScaleType;



//新建一个类继承View
public class DrawView extends View{
	private int mov_x;//声明起点坐标
	private int mov_y;
	public Paint paint;//声明画笔
	public Canvas canvas;//画布
	public Bitmap bitmap;//位图

	private int blcolor=Color.RED;
	private int bwidth=3;


	private Context mcontext;
	private ScaleType mScaleType;
	private Matrix mMatrix;

	public DrawView(Context context)
	{
		super(context);
		this.mcontext = context;
		initDraw();
	}
//画位图
@Override
protected void onDraw(Canvas canvas)
{
//super.onDraw(canvas);
	Paint bmpPaint = new Paint();
	canvas.drawBitmap(bitmap, 0, 0, bmpPaint);
}

public void initDraw()
{
	paint=null;
	//bitmap=null;
    mMatrix = new Matrix();
    mScaleType  = ScaleType.FIT_CENTER;
	canvas=null;
	paint=new Paint(Paint.DITHER_FLAG);//创建一个画笔]
	//	bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888); //设置位图的宽高
	//bitmap = Bitmap.createBitmap(1200, 1824, Bitmap.Config.ARGB_8888); //设置位图的宽高
    //bitmap = Bitmap.createBitmap(1550, 2320, Bitmap.Config.ARGB_8888); //设置位图的宽高
    if(bitmap == null){
		bitmap = Bitmap.createBitmap(1900, 2300, Bitmap.Config.ARGB_8888); //设置位图的宽高
}

	canvas=new Canvas();
//	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//	canvas.drawPaint(paint);
//	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	///canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	canvas.setBitmap(bitmap);

	paint.setStyle(Style.STROKE);//设置非填充
	//  paint.setStrokeWidth(bwidth);//笔宽5像素
	//  paint.setColor(blcolor);//设置为红笔
	paint.setAntiAlias(true);//锯齿不显示
	paint.setDither(true);  //防抖动
	paint.setColor(Color.RED);
	invalidate();
}
public void setVcolor(int vlcolor)
{
	blcolor=vlcolor;
 	paint.setColor(blcolor);
}
public void setVwidth(int vwidth)
{
	bwidth=vwidth;
	paint.setStrokeWidth(bwidth);
}

public void DrawDestroy(){
	if(bitmap != null && !bitmap.isRecycled()){        
		bitmap.recycle(); 
        bitmap = null; 
	}

	paint=null;
	canvas=null;
}
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        }

        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            setWillNotCacheDrawing(mScaleType == ScaleType.CENTER);            
            requestLayout();
            invalidate();
        }
    }

/*
//触摸事件
@Override
public boolean onTouchEvent(MotionEvent event) {
if (event.getAction()==MotionEvent.ACTION_MOVE) {//如果拖动
 canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);//画线
 invalidate();
}
if (event.getAction()==MotionEvent.ACTION_DOWN) {//如果点击
 mov_x=(int) event.getX();
 mov_y=(int) event.getY();
 Log.d("USB_HOST", "x:"+mov_x+"y:"+mov_y);
 canvas.drawPoint(mov_x, mov_y, paint);//画点
 invalidate();
}
mov_x=(int) event.getX();
mov_y=(int) event.getY();
//return true;
return super.onTouchEvent(event);
}
*/
}
