package com.jinke.calligraphy.app.branch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class DatabaseOp {
	public static int proNo = 20;
	public static String path = "/sdcard/homework.db";
	
	public static SQLiteDatabase createDatabase() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		return db;
	}
	
	public static void createTable(SQLiteDatabase db){
		String check_table = "create table if not exists usertable(_id integer primary key autoincrement, " +
				"quesNo INTEGER, a INTEGER, b INTEGER, c INTEGER, d INTEGER, e INTEGER)";
		db.execSQL(check_table);

//		
//		ContentValues[] cValue = new ContentValues[proNo];
//		for(int i=0; i<proNo; i++) {
//			cValue[i].put("quesNo", i+1);
//			cValue[i].put("a", 0);
//			cValue[i].put("b", 0);
//			cValue[i].put("c", 0);
//			cValue[i].put("d", 0);
//			cValue[i].put("e", 0);
//			db.insert("check_table", null, cValue[i]);
//		}		
	}
	
	public static void initDb(SQLiteDatabase db) {
		Log.i("sqldb", "init");
		ContentValues cValue[] = new ContentValues[proNo];
		for(int i=0; i<proNo; i++) {
			cValue[i]= new ContentValues();
			cValue[i].put("quesNo", i+1);
			cValue[i].put("a", 0);
			cValue[i].put("b", 0);
			cValue[i].put("c", 0);
			cValue[i].put("d", 0);
			cValue[i].put("e", 0);
			db.insert("usertable", null, cValue[i]);
		}
	}
	
	public static void update(SQLiteDatabase db, int qNo, int a, int b, int c, int d, int e) {
		Log.i("sqldb", "update in DatabaseOp" + qNo + " " + a + " " + b + c+d+e);
		ContentValues cValue = new ContentValues();
		cValue.put("quesNo", qNo);
		Log.i("sqldb", "qNo in update " + qNo);
		cValue.put("a", a);
		cValue.put("b", b);
		cValue.put("c", c);
		cValue.put("d", d);
		cValue.put("e", e);
		String whereClause = "_id=?";
		String[] whereArgs = {String.valueOf(qNo+1)};
		db.update("usertable", cValue, whereClause, whereArgs);
	}
	
	public static void clcDatabase(SQLiteDatabase db){
		ContentValues[] cValue = new ContentValues[proNo];
		for(int i=0; i<proNo; i++) {
			cValue[i] = new ContentValues();
			Log.i("sqldb", " clcDatabase "  + i + "proNo" + proNo);
			cValue[i].put("quesNo", i+1);
			Log.i("sqldb", " quesNo");
			cValue[i].put("a", 0);
			cValue[i].put("b", 0);
			cValue[i].put("c", 0);
			cValue[i].put("d", 0);
			cValue[i].put("e", 0);
			String whereClause = "_id=?";
			String[] whereArgs = {String.valueOf(i+1)};
			db.update("usertable", cValue[i], whereClause, whereArgs);
		}		
	}
	
	public static int[] readDatabase(SQLiteDatabase db, int qNo) {
		int result[] = {0, 0, 0,0,0};
		Cursor cursor = db.query("usertable", null, null, null, null, null, null);
		if(!(cursor == null)) {
			cursor.moveToFirst();
			cursor.move(qNo);
			result[0] = cursor.getInt(2);
			result[1] = cursor.getInt(3);
			result[2] = cursor.getInt(4);
			result[3] = cursor.getInt(5);
			result[4] = cursor.getInt(6);
//			Log.i("sqldb", "read right result" + qNo + ": " + cursor.getInt(2));
		}
//		if(cursor.moveToFirst()) {
//			for(int i=0; i<cursor.getColumnCount(); i++) {
//				cursor.move(i);
//				for(int j=0; j<result.length; j++) 
//				{
//					result[j] = cursor.getInt(j+2); //第一列id，第二列quesNo，第三列right
//					Log.i("sqldb", "readDatabaseResult result[" + j +"] =" + result[j]);
//				}
//			}
//		}
		return result;
	}
}
