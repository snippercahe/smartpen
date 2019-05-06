package com.jinke.smartpen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.readAndSave.SmartPenUnitils;
import com.google.common.collect.ArrayListMultimap;
import com.jinke.calligraphy.app.branch.Calligraph;
import com.jinke.calligraphy.app.branch.R;
import com.jinke.calligraphy.app.branch.Start;
import com.jinke.calligraphy.app.branch.UpLoad;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.gesture.Gesture;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class DealSmartPenGesture {
	Start activity;
	TimerTask mTimerTask=null;
    public com.jinke.smartpen.RecordingService.MyBinder recordService;
    public Intent intent; 
	 int recordSecond=6;
    public final ServiceConnection recordConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			recordService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			recordService=(com.jinke.smartpen.RecordingService.MyBinder) service;
		}
	};

	public void setDealSmartPenGesture(Start activity){
		this.activity=activity;
         intent = new Intent(activity, RecordingService.class);
	}
	
	
	
	private ArrayListMultimap<String, GesturePlaceAndResource> gesturePlaceContainer= ArrayListMultimap.create(); // Book=100笔迹数据
	private float delt=15;//判断手势是否在同一个地方的手势边框的冗余量；
	

/*	public String recogniseSmartPenGesture(MGesture gesture ,MGestureUnitils mGestureUnitils){
		ArrayList<MGesture> mGesturesContainer = null;
		MGesture predictionMGesture=null;
		try {
			mGesturesContainer = mGestureUnitils.load();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mGesturesContainer!=null) {
			Log.e("zgm", "1210：手势文件装载成功");
			predictionMGesture=mGestureUnitils.recogniseGeMGesture(gesture);
			
			if (predictionMGesture!=null) {
             String resultsString=predictionMGesture.getGestureName();
             return resultsString;
           
			}else {
				return null;
			}
	    }
		Log.e("zgm", "mGesturesContainer为空");
		return null;
	}
*/	
public String recogniseSmartPenGesture(SmartPenGesture gesture){
//	GestureLibrary gestureLibrary=GestureLibraries.fromFile("/sdcard/zgmgesture");
	if (activity.toolFun.gestureLibrary.load()) {
		Log.e("zgm", "1210：手势文件装载成功");
	Set<String> aSet=activity.toolFun.gestureLibrary.getGestureEntries();
	for (String string : aSet) {
		Log.e("zgm", "0113:"+string);
	}
	Gesture firstGesture=new Gesture();
	firstGesture.addStroke(gesture.getStrokes().get(0));
		ArrayList<Prediction> predictions=activity.toolFun.gestureLibrary.recognize(gesture);
        ArrayList<GestureScore> gestureScores=new ArrayList<DealSmartPenGesture.GestureScore>();
        for (Prediction prediction :predictions) {
			if (prediction.score>2.5) {
				gestureScores.add(new GestureScore(prediction.name, prediction.score));
	
			}
		}
        
        String gestureName = getHightestScoreGesture(gestureScores);
        return gestureName;
	    }else {
			return null;
		}
	}


	public void dealWithGesture(SmartPenGesture currentSmartPenGesture) {
		int situation = -1;
		String gestureName;
        activity.tag=getChirographyPositionInfo(currentSmartPenGesture, activity.toolFun.gCurPageID);//更新tag
        if(activity.tag == null)
        {
//        	Calligraph.my_toast("tag为空");0426
        	Log.e("0427", "tag为空");
        	return;
        }
    	if (currentSmartPenGesture==null) {
    		activity.showToast("手势为空");
    		return;
    	}        
		if (currentSmartPenGesture.getStrokesCount()==1) {
			if (isclick(currentSmartPenGesture)) {				
				float[] tempcenter=getGestureCenture(currentSmartPenGesture);
//				if (tempcenter[1]>140&&Calligraph.pigaihuanLayout.getVisibility()==View.VISIBLE) {					
//					Message msg = new Message();
//					situation =13;//单击
//					msg.what = 10;
//					Bundle bundleTrans = new Bundle();
//					bundleTrans.putInt("situ", situation);
//					bundleTrans.putFloat("yy", tempcenter[1]);
//					msg.setData(bundleTrans);
//					Start.transHandler.sendMessage(msg);
//					return;						
//				}

			}
		}
		// TODO Auto-generated method stub
//		currentSmartPenGesture.getStrokesCount();
//		Log.i("zgm","0122：currentSmartPenGesture.getStrokesCount()"+currentSmartPenGesture.getStrokesCount());
		if (currentSmartPenGesture.getStrokesCount()==2) {
			SmartPenGesture tempGesture=new SmartPenGesture();
			int clickTimes=0;
			float[] tempcenter= null ;
			for (int i = 0; i < 2; i++) {
				tempGesture.SmartPenGestureClearAllStroke();
				tempGesture.SmartPenGestureClearmBoundingBox();
				tempGesture.addStroke(currentSmartPenGesture.getStrokes().get(i));// 将每一笔手势都重新放入临时手势中
				if (isclick(tempGesture)) {
//					
					clickTimes++;
/*					if (tempcenter[1]>140) {
						
					}else
					{
						break;
					}*/

					
				}
				else {   
					break;
				}
			}
			if (clickTimes==2) {
				tempcenter=getGestureCenture(currentSmartPenGesture);
//				Start.pghCenterYOffset = (int) tempcenter[1];//0426
				Log.i("cahe1","temcenter = "+tempcenter[1]);
				if(Calligraph.pigaihuanLayout.getVisibility()==View.VISIBLE) {
					tempcenter[1]+=Start.pghCenterYOffset;//换掉128
				}
				if (tempcenter[1]>20) {//只要不是提交，都是应该弹出批改环
					if(Calligraph.pigaihuanLayout.getVisibility()==View.GONE){
						situation = 12;
						Message msg = new Message();
						msg.what = 50;
						Bundle bundleTrans = new Bundle();
						bundleTrans.putInt("situ", situation);
						bundleTrans.putFloat("yy", tempcenter[1]);
						msg.setData(bundleTrans);
						activity.transHandler.sendMessage(msg);
						return;
						}
					else if (Calligraph.pigaihuanLayout.getVisibility()==View.VISIBLE) {
						
						Message msg = new Message();
						situation =13;//双击消失批改环
						msg.what = 51;
						Bundle bundleTrans = new Bundle();
						bundleTrans.putInt("situ", situation);
						bundleTrans.putFloat("yy", tempcenter[1]);
						msg.setData(bundleTrans);
						activity.transHandler.sendMessage(msg);
						return;						
					}
					}
//					activity.showSound(R.raw.sanweiyuyi);


					else if (tempcenter[1]<20) {
						/**
						 * 提交作业的代码
						 */
						SmartPenUnitils.save(activity.toolFun.smartPenPage,"001-"+activity.currentPageName.substring(5));
						Log.i("getfile","001-"+activity.currentPageName.substring(5));
						final String ip = "123.206.16.114";
						boolean status = false;
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								/*
								 * boolean status=UpLoad.uploadFile("http://"+ip+
								 * "/index.php/Home/Index/upload","/sdcard/-1/" +
								 * "123-1.page");
								 */
								activity.dealingSomeThing=true;
								boolean status = UpLoad.uploadFile("http://118.24.109.3/Public/smartpen/upload.php",
										"/sdcard/-1/" + "001-"+activity.currentPageName.substring(5));
								UpLoad.uploadFile("http://118.24.109.3/Public/smartpen/upload.php", "/sdcard/pigairesult/001-0944-0-0-0.jpg");
								activity.dealingSomeThing=false;
								if (status) {
									activity.showSound(R.raw.upload_sucess);
									Message message = new Message();
									message.what = 0327;
									activity.barTextHandler.sendMessage(message);
								}else {
									activity.showSound(R.raw.upload_fail);	
								}
							}
						}).start();				
					}
				}
		}
		gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
//		Log.i("zgm", "0122:"+gestureName);
		if(gestureName == null){
//			activity.updateUsingInfo("未识别的手势",activity.ORDERSTATE);
//			Log.i("zgm","0122：currentSmartPenGesture.getStrokesCount()"+currentSmartPenGesture.getStrokesCount());
			return;
		}

		
		Log.e("zgm", "0122:12"+gestureName);
//		activity.updateUsingInfo("您画的手势是："+gestureName,activity.ORDERSTATE);
		if (gestureName.equals("录音")) {
			if(activity==null){  return; }
			
			/*
			 * 以下代码主要是获得录音手势去掉尾巴的的边界矩形
			 */
			ArrayList<GestureStroke>   gestureStrokes=currentSmartPenGesture.getStrokes();//录音手势只有1笔，但还是要用for循环
			RectF mainBoundingBox=null;
			for(GestureStroke gestureStroke:gestureStrokes){
			    int endIndex= getTailIndex("录音",gestureStroke);	
			    mainBoundingBox=getBoundingBox(endIndex, gestureStroke);
			}
			activity.toolFun.isDealPenPoint=false;
			if(Start.currentPageName==null) {
				activity.showToast("没有加载作业，请尝试点击不同页面");
			}
			String sounderFileName="001-"+Start.currentPageName.substring(5,Start.currentPageName.lastIndexOf("-")+1)+Start.tag.get(0)+".mp3";
			File sounderFile=new File("/sdcard/-1/"+sounderFileName);
			if( sounderFile.exists()){
              playAudio(sounderFile);
			Message msgb = new Message();
			msgb.what=30;//播放录音
			activity.transHandler.sendMessage(msgb);	//播放录音
			return;
			}
			/*
			 * 以上代码主要是获得录音手势去掉尾巴的的边界矩形
			 */
/*			if(gesturePlaceContainer.get(gestureName).size()==0){
				//还没有录过音，开始第一个录音
			return;
			}else */
			{//代码块，匹配手势用
				activity.toolFun.isDealPenPoint=false;
				Log.e("zgm", "1218:"+gestureName);
/*				List<GesturePlaceAndResource> a = gesturePlaceContainer.get(gestureName);
				if (a.size()>0) {
					Log.e("zgm","0108:"+a.size()+":"+a.get(0).rectF);	
				}*/

/*				for (GesturePlaceAndResource gesturePlaceAndResource : gesturePlaceContainer.get(gestureName)) {
					if (isAlmostEqual(mainBoundingBox,gesturePlaceAndResource.getGesturePlace())) {
						playAudio(gesturePlaceAndResource);
						Message msgb = new Message();
						msgb.what=30;//播放录音
						activity.transHandler.sendMessage(msgb);
                          return;
						
					}
				}*/
			}
			
			{//代码块，没有匹配到手势，那么就开始录音相关的操作
			Log.e("zgm","1216：没有匹配到录音手势");
		
//			activity.updateUsingInfo("没有匹配到录音手势,两秒之后听见滴的一声后请录音",activity.ORDERSTATE);
			recordAndio( mainBoundingBox);
			Message msgr = new Message();
			msgr.what=20;
			activity.transHandler.sendMessage(msgr);
/*			activity.showSound(R.raw.recordstart);
			activity.showVibrator();// 震动
			long starttime=System.currentTimeMillis();
//			这里之所以写成死循环，是故意为了阻塞
			while(System.currentTimeMillis()-starttime<2000){
				//空循环等待
			}*/
			
//			activity.showSound(R.raw.startrecord);
//			activity.showVibrator();// 震动

			
/*            final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance();
            fragment.show(activity.getSupportFragmentManager(), RecordAudioDialogFragment.class.getSimpleName());
            fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
                @Override
                public void onCancel() {
                    SharedPreferences sharePreferences = activity.getSharedPreferences("sp_name_audio", Service.MODE_PRIVATE);
//                    final String filePath = sharePreferences.getString("audio_path", "");
                    String filePath=fragment.getRecordFilePath(fragment.recordService);
                    Log.e("zgm", "1223:filePath:"+filePath);
                    long elpased = fragment.getRecordFileElpased(fragment.recordService);
                    Log.e("zgm", "1223:elpased="+elpased);
                    String gestureNameString="录音";
                	
        			RecordingService.MyBinder myRecordBinder=(MyBinder) fragment.recordService;
        			
        			String resourcePathString=fragment.getRecordFilePath(myRecordBinder);
        			if (filePath==null) {
        				activity.runOnUIThread("没有录音");
						return;
						
					}
//        			long  resourceElpased=fragment.getRecordFileElpased(myRecordBinder);
					Log.e("zgm","1223:filePath"+filePath+" elpased:" +
							elpased+" boundingBox:"+boundingBox);

        			GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,boundingBox);
        			gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
        			activity.runOnUIThread("录音完成，音频文件路径："+filePath);
        			fragment.dismiss();
                }
            });	
*/
			}//代码块完
			return;
			
		}
		if (gestureName.equals("对")&&currentSmartPenGesture.getStrokesCount()==1) {
			situation=0;
		}
		else if(gestureName.equals("错")&&currentSmartPenGesture.getStrokesCount()==2)
			situation = 1;
		else{
		SmartPenGesture tempGesture=new SmartPenGesture();
		int tempsituation=-1;
		for (int i = 0; i < currentSmartPenGesture.getStrokesCount(); i++) {//拆分手势，进行单笔识别
			tempGesture.SmartPenGestureClearAllStroke();
			tempGesture.SmartPenGestureClearmBoundingBox();
			tempGesture.addStroke( currentSmartPenGesture.getStrokes().get(i));//将每一笔手势都重新放入临时手势中进行识别
			gestureName=recogniseSmartPenGesture(tempGesture);
			if (gestureName==null) {
				continue;
			}
//			if (gestureName.equals("录音")) {
//				situation=1;
//				tempIndex=i;
//				break;
//			}
			if (gestureName.equals("对")) {
				tempsituation=2;
//				tempIndex=i;
				break;
			}
		}
		switch (tempsituation) {
		case 1:
			if(gestureName.equals("错")&&currentSmartPenGesture.getStrokesCount()==2)
				situation = 1;
			break;
		case 2:
			switch (currentSmartPenGesture.getStrokesCount()) {
			case 2:
				gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
				if (gestureName!=null&&gestureName.equals("错")) {
//					activity.updateUsingInfo("您画的是判题类手势:错",activity.ORDERSTATE);
					situation=1;
			
				}
				else 
				situation=2;
//				activity.updateUsingInfo("您画的是判题类手势:半对",activity.ORDERSTATE);
				break;
			case 3:
//				activity.updateUsingInfo("您画的是判题类手势:半对2",activity.ORDERSTATE);
				situation=3;
				break;
			case 4:
//				activity.updateUsingInfo("您画的是判题类手势:半对3",activity.ORDERSTATE);
				situation=4;
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
		}	
/*		else if(gestureName.equals("对")){
			situation = 0;
		}
		else if(gestureName.equals("错")){
			situation = 1;
		}
		else if(gestureName.equals("对")&&currentSmartPenGesture.getStrokesCount()==1){
			situation = 0;
		}
		else if(gestureName.equals("错")&&currentSmartPenGesture.getStrokesCount()==2){
			situation = 1;
		}
		else if((gestureName.equals("半对1")&&currentSmartPenGesture.getStrokesCount()==2)||(gestureName.equals("对")&&currentSmartPenGesture.getStrokesCount()==2)){
			situation = 2;
		}
		else if((gestureName.equals("半对2")&&currentSmartPenGesture.getStrokesCount()==3)||currentSmartPenGesture.getStrokesCount()==3){
			situation = 3;
		}
		else if((gestureName.equals("半对3")&&currentSmartPenGesture.getStrokesCount()==4)||currentSmartPenGesture.getStrokesCount()==4){
			situation = 4;
		}
*/
//		else situation = 100;
//		 situation = 100;
		Message msg = new Message();
		
		msg.what = 10;
		Bundle bundleTrans = new Bundle();
		bundleTrans.putInt("situ", situation);
		bundleTrans.putFloat("yy", currentSmartPenGesture.getStrokes().get(0).points[1]);
		Log.i("crash","yy="+currentSmartPenGesture.getStrokes().get(0).points[1]);
		Log.i("crash","situation="+situation);
		Log.i("crash","---"+activity.c.pos[2]);
		
		msg.setData(bundleTrans);
		activity.transHandler.sendMessage(msg);
//		activity.c.setTrans(situation,);
		
	}
	
	
	
public String getHightestScoreGesture(ArrayList<GestureScore> arrayList){
	if (arrayList.size()==0) {
		return null;
	}
	double hightestScore=-5;
	String  gestureName=null;
	for (int i=0;i<arrayList.size();i++) {
		if (arrayList.get(i).getGestureScore()>hightestScore) {
			hightestScore=arrayList.get(i).getGestureScore();
			gestureName=arrayList.get(i).getGestureNmae();
		}
	}
	return gestureName;	
}	
private	 boolean isAlmostEqual(RectF rectF1,RectF rectF2){
	float leftDistance=Math.abs(rectF1.left-rectF2.left);
//	float topDistance=Math.abs(rectF1.top-rectF2.top);
//	float rightDistance=Math.abs(rectF1.right-rectF2.right);
	float bottomDistance=Math.abs(rectF1.bottom-rectF2.bottom);			
	if (bottomDistance<delt&&leftDistance<delt) {
		return true;
	}
	return false;	
}

public class GestureScore{
	String gestureName="";
	double gestureScore=0;
	public GestureScore(String gestureName, double score) {
		// TODO Auto-generated constructor stub
		this.gestureName=gestureName;
		this.gestureScore=score;
	}
	public String getGestureNmae() {
		return gestureName;
		
	}
	public double getGestureScore(){
		return gestureScore;
		
	}
}	

private class GesturePlaceAndResource{
	String gestureNameString="";
	String resourcePathString="";
	long    resourceElpased=0;
    final	RectF rectF;
	
	GesturePlaceAndResource(String gestureNameString,String resourcePathString,long resourceElpased,RectF rectF){
		this.gestureNameString=gestureNameString;
		this.resourcePathString=resourcePathString;
		this.resourceElpased=resourceElpased;//播放时长
		this.rectF=rectF;
	}
public String getGestureNameString(){
	return gestureNameString;
}
public String getResourcePath(){
	return resourcePathString;
}
public RectF getGesturePlace(){
	return this.rectF;
}

public long getresourceElpased(){
	return resourceElpased;
}
}

private void onRecord(boolean start) {
    if (start) {

//        Toast.makeText(activity, "开始录音...", Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
        if (!folder.exists()) {
            //folder /SoundRecorder doesn't exist, create the folder
            folder.mkdir();
        }

        //start Chronometer
//        mChronometerTime.setBase(SystemClock.elapsedRealtime());
//        mChronometerTime.start();
        //start RecordingService
        activity.startService(intent);
        if (activity.bindService(intent,recordConnection,Service.BIND_AUTO_CREATE)) {
			Log.e("zgm", "1217:绑定成功！");
		}else {
			Log.e("zgm", "1217:绑定失败！ "+activity.getClass().getName());
		}
//        
        
        //keep screen on while recording
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    } else {
//        Toast.makeText(activity, "录音结束...", Toast.LENGTH_SHORT).show();
        activity.unbindService(recordConnection);
        activity.stopService(intent);
    }
}

public void playAudio(GesturePlaceAndResource gesturePlaceAndResource){
//	activity.updateUsingInfo("播放相关资源"+gesturePlaceAndResource.getResourcePath(),activity.ORDERSTATE);
Log.e("zgm","1218：播放相关资源"+gesturePlaceAndResource.getResourcePath());
final MediaPlayer mMediaPlayer = new MediaPlayer();
try {
    mMediaPlayer.setDataSource(gesturePlaceAndResource.getResourcePath());
    mMediaPlayer.prepare();

} catch (IOException e) {
    Log.e("zgm", "prepare() failed");
}
mMediaPlayer.start();
new Thread(new Runnable() {
	
	@Override
	public void run() {
		long tempStartTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-tempStartTime<3*1000) {
			//空循环
		}
//		activity.updateUsingInfo("留言播放结束",activity.ORDERSTATE);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        activity.toolFun.isDealPenPoint=true;
	}
}).start();
return;
/*						com.example.pencon.RecordingItem recordingItem = new com.example.pencon.RecordingItem();
//播放相关资源
recordingItem.setLength((int) gesturePlaceAndResource.getresourceElpased());
recordingItem.setFilePath(gesturePlaceAndResource.getResourcePath());
com.example.pencon.PlaybackDialogFragment fragmentPlay = com.example.pencon.PlaybackDialogFragment.newInstance(recordingItem);
fragmentPlay.show(activity.getSupportFragmentManager(), com.example.pencon.PlaybackDialogFragment.class.getSimpleName());
*/	

}
public void playAudio(File file ){
//	activity.updateUsingInfo("播放相关资源"+gesturePlaceAndResource.getResourcePath(),activity.ORDERSTATE);
//Log.e("zgm","1218：播放相关资源"+gesturePlaceAndResource.getResourcePath());
final MediaPlayer mMediaPlayer = new MediaPlayer();
try {
    mMediaPlayer.setDataSource(file.getAbsolutePath());
    mMediaPlayer.prepare();

} catch (IOException e) {
    Log.e("zgm", "prepare() failed");
}
mMediaPlayer.start();
new Thread(new Runnable() {
	
	@Override
	public void run() {
		long tempStartTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-tempStartTime<3*1000) {
			//空循环
		}
//		activity.updateUsingInfo("留言播放结束",activity.ORDERSTATE);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        activity.toolFun.isDealPenPoint=true;
	}
}).start();
return;
/*						com.example.pencon.RecordingItem recordingItem = new com.example.pencon.RecordingItem();
//播放相关资源
recordingItem.setLength((int) gesturePlaceAndResource.getresourceElpased());
recordingItem.setFilePath(gesturePlaceAndResource.getResourcePath());
com.example.pencon.PlaybackDialogFragment fragmentPlay = com.example.pencon.PlaybackDialogFragment.newInstance(recordingItem);
fragmentPlay.show(activity.getSupportFragmentManager(), com.example.pencon.PlaybackDialogFragment.class.getSimpleName());
*/	

}

public void recordAndio(final RectF boundingBox){
	
//	activity.updateUsingInfo("请录音",activity.ORDERSTATE);
	onRecord(true);
	activity.dealingSomeThing=true;
	recordSecond=6;
	if (mTimerTask!=null) {
		mTimerTask.cancel();
	}
	 mTimerTask=new TimerTask() {		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (recordSecond>=0) {
				Message msg=new Message();
				msg.what=401;//录音开始
				msg.obj=recordSecond;
				activity.transHandler.sendMessage(msg);
				recordSecond--;
				return;
			}
			else {
				recordSecond=6;
				onRecord(false);//结束录音
				Message msg=new Message();
				msg.what=40;//录音结束
				activity.transHandler.sendMessage(msg);
				activity.showSound(R.raw.endrecord);
//				activity.showSound(R.raw.endrecord);
//				activity.showVibrator();// 震动
				if (recordService==null) {
					return;
//					activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
				}
				String filePath=recordService.getService().getMFilePath();
				
				activity.toolFun.soundPathString=filePath;
	    		if (filePath==null) {
//					activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
					return;
					
				}
				 long elpased =recordService.getService().getMFileElpased();
				 String gestureNameString="录音";
				 if(boundingBox!=null){
					 RectF tempRectF=new RectF(boundingBox);
		    		 GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,tempRectF);
		    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
	 
					 
					 
				 }

//	    		 activity.updateUsingInfo("录音完成，音频文件路径："+filePath,activity.ORDERSTATE);
	    		 activity.toolFun.isDealPenPoint=true;
					activity.timerInStartActivity.cancel();
					activity.timerInStartActivity.purge();
					activity.timerInStartActivity=null;
			}
			
		}
	};
if (activity.timerInStartActivity==null) {
	activity.timerInStartActivity=new Timer();
}	activity.timerInStartActivity.schedule(mTimerTask, 0, 1000);
/*	Message msg=new Message();	
	msg.what=401;//录音开始
	activity.transHandler.sendMessage(msg);
//	activity.updateUsingInfo("录音当中，录音3s，请留言",activity.ORDERSTATE);
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long temStartTime=System.currentTimeMillis();
			while (System.currentTimeMillis()-temStartTime<6*1000) {
			}

			onRecord(false);//结束录音
			Message msg=new Message();
			msg.what=40;//录音结束
			activity.transHandler.sendMessage(msg);
			activity.showSound(R.raw.endrecord);
//			activity.showSound(R.raw.endrecord);
//			activity.showVibrator();// 震动
			if (recordService==null) {
				return;
//				activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
			}
			String filePath=recordService.getService().getMFilePath();
			
			activity.toolFun.soundPathString=filePath;
    		if (filePath==null) {
//				activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
				return;
				
			}
			 long elpased =recordService.getService().getMFileElpased();
			 String gestureNameString="录音";
			 if(boundingBox!=null){
				 RectF tempRectF=new RectF(boundingBox);
	    		 GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,tempRectF);
	    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
 
				 
				 
			 }

//    		 activity.updateUsingInfo("录音完成，音频文件路径："+filePath,activity.ORDERSTATE);
    		 activity.toolFun.isDealPenPoint=true;
		}
	}).start();	
	
*/	
}

public int  getTailIndex(String gestureName,GestureStroke gestureStroke){
	if (!gestureName.equals("录音")) {
		return -1;
	}
	if (gestureStroke.points.length<6) {
		return -1;
	}
	boolean first=true;
	boolean second=false,third=false;
	int index=-1;
	float x1,y1,x2,y2;
for (int i = 0; i < gestureStroke.points.length/2-1; i++) {
	x1=gestureStroke.points[i*2];
	y1=gestureStroke.points[i*2+1];
	x2=gestureStroke.points[(i+1)*2];
	y2=gestureStroke.points[(i+1)*2+1];
	if(first){
		if ((x2-x1)>0&&(y2-y1)<0) {
			first=false;
			second=true;
			third=false;
		}
	}
		if (second) {
			if ((x2-x1)<0&&(y2-y1)>0) {
				first=false;
				second=false;
				third=true;
			}
	}
		if(third) {
			if ((x2-x1)>0&&(y2-y1)<0) {
               index=i;
               return index;
			}
		
	}
}
	
	return -1;
	
}

public RectF getBoundingBox(int endIndex,GestureStroke gestureStroke){
	if (endIndex<0||gestureStroke.points.length==0) {
		return null;
	}
	RectF boundingBox=new RectF();
	for (int i = 0;i < gestureStroke.points.length/2; i++) {
		if (i<=endIndex) {
			if (i==0) {
				boundingBox.left=gestureStroke.points[i*2];
				boundingBox.right=boundingBox.left;
				boundingBox.top=gestureStroke.points[i*2+1];
				boundingBox.bottom=boundingBox.top;
			}
			boundingBox.union(gestureStroke.points[i*2],gestureStroke.points[i*2+1]);
			continue;
		}
		break;
	}
	return boundingBox;
}/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：还未进行调试(可能有bug,暂时还没有使用)，对点序列中的相邻两个点直接进行插值，使其x坐标连续(每一个整数都能对应一个x坐标).
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-20 上午11:26:58
 * @param gestureStroke
 * @return
 */

/**
 * 判断某个手势是否是单击
 * @param currentSmartPenGesture
 * @return true：是单击；false:不是单击
 */
public boolean isclick(SmartPenGesture currentSmartPenGesture) {
if (currentSmartPenGesture.getStrokesCount()!=1) {
	return false;
}
RectF rectF=currentSmartPenGesture.getGestureBoundBoxRect();
	if (Math.abs(rectF.right-rectF.left)<3&&Math.abs(rectF.bottom-rectF.top)<3) {
		return true;
	}
	
	return false;
	
	
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：求得手势的中心点坐标(所有笔画的中心)
 * @date：2019-1-23 下午9:05:28
 * @param currentSmartPenGesture
 * @return:返回手势的中心点坐标
 */
public float[] getGestureCenture(SmartPenGesture currentSmartPenGesture){
	float[] averges=new float[2];
	averges[0]=0;//averges[0]存放的是中心点x的坐标
	averges[1]=0;//averges[1]存放的是中心点y的坐标
	int counter=0;

	for (GestureStroke gStroke:currentSmartPenGesture.getStrokes()) {
		for (int i =0;i< gStroke.points.length/2; i++) {
			counter++;
			averges[0]=averges[0]+gStroke.points[2*i];
			averges[1]=averges[1]+gStroke.points[2*i+1];
		}
	}
	averges[0]=averges[0]/counter;
	averges[1]=averges[1]/counter;
	return averges;	
}
 public float[]    linearInterpolation(GestureStroke gestureStroke){
	ArrayList<Float> interpolatedPoints=new ArrayList<Float>();
	if (gestureStroke.points.length<4) {
		return gestureStroke.points;
	}
	int x1,y1,x2,y2;
	for (int i = 0; i < gestureStroke.points.length/2-1; i=i+1) {
		x1=Math.round(gestureStroke.points[i*2]);
		y1=Math.round(gestureStroke.points[i*2+1]);
		x2=Math.round(gestureStroke.points[(i+1)*2]);
		y2=Math.round(gestureStroke.points[(i+1)*2+1]);
		if (i==0) {
			interpolatedPoints.add((float) x1);
			interpolatedPoints.add((float) y1);
		}
		if (x1==x2&&y1==y2) {//情况1
			interpolatedPoints.add((float) x2);
			interpolatedPoints.add((float) y2);
			continue;
		}
		if (x1==x2&&y1!=y2) {//情况2
			int tempy=y1;
			if (tempy<y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy+1);
					tempy=tempy+1;
				}
				continue;
			}
			if (tempy>y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy-1);
					tempy=tempy-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1==y2) {//情况3
			int tempx=x1;
			if (tempx<x2) {
				while(tempx<x2){
					interpolatedPoints.add((float) tempx+1);
					interpolatedPoints.add((float) y1);
					tempx=tempx+1; 
				}
				continue;
			}
			if (tempx>x2) {
				while(tempx>x2){
					interpolatedPoints.add((float) tempx-1);
					interpolatedPoints.add((float) y1);
					tempx=tempx-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1!=y2) {//情况4
		float bx1x2 =y1*(x2-x1)-x1*(y2-y1);
		int tempx=x1;
		float tempy;
		if (tempx<x2) {
			while (tempx<x2) {
				tempy=((tempx+1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx+1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx+1;
			}
           continue;
		}
		if (tempx>x2) {
			while (tempx>x2) {
				tempy=((tempx-1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx-1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx-1;
			}
           continue;
		}
		}
		
		
		
	}
	return null;
	
	
	
	
}         
 /**
  * 
  * @param currentSmartPenGesture 一笔手势，多笔不做处理
  * @return: tag[2],tag=null:不在任何区域 ;tag[0]:题号；tag[1]:题号对应题目的某个区域
  */
 public ArrayList<Integer> getChirographyPositionInfo(SmartPenGesture currentSmartPenGesture,int mPageID ) {
 /*	if (currentSmartPenGesture.getStrokesCount()!=1) {//不是一笔，直接返回null
 		return null;
 	}*/
 	float[] averages=getGestureCenture(currentSmartPenGesture);//averages[0]:x平均值;averages[1]:x平均值
 	//得到区域-题干区
 	averages[0]=(float) (averages[0]);
 	averages[1]=(float) (averages[1]);
 	ArrayList<Integer> tag=testxml.test( averages[0], averages[1],activity.toolFun.gCurBookID,activity.toolFun.gCurPageID);
//    Log.i("tag2", "tag2:"+tag.get(2));
//    Log.i("tag3", "tag3:"+tag.get(3));
 	//wsk 2019.4.26
 		//将返回来的tag转换为题号
 		ArrayList<Integer> temp = new ArrayList<Integer>();
 		if(tag == null)
 		{
 			return null;
 		}
 		
 		else
 		{
 			temp.add(HomeworkContnet(tag.get(0), activity.gCurBookID,activity.gCurPageID));
			temp.add(tag.get(1));
			temp.add(tag.get(2));
			temp.add(tag.get(3));
			temp.add(tag.get(4));
 		}
 		
 		Start.pghCenterYOffset = tag.get(2);
 		return temp;
 }
 
 static Document doc;
 static String pagexml;
 public int HomeworkContnet(int index,int BookID,int PageID)
 {
 	pagexml = "book_"+BookID+"_page_"+(PageID%20)+".xml";
 	
 	File file = new File("/sdcard/xml/" +pagexml);
 	try 
 	{
 		doc = Jsoup.parse(file, "UTF-8");
 	
 	 }
 	catch (IOException e) 
 	{
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	}
 	
 	Elements element = doc.getElementsByTag("itemnumber");
 	float tihao;
 	
 	if(index == 0 || index == 1)
 	{
 		return 0;
 	}

 	else 
 	{
 		tihao = Float.valueOf(element.get(index).text().toString());
 	}
 	
 	return (int)tihao;
 }
}
