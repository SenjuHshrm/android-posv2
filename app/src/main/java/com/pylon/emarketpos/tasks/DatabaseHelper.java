package com.pylon.emarketpos.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "logged_user.db";
    private static final String TABLE_NAME = "user";
    private static final String  COL_0 = "user_id";
    private static final String  COL_1 = "name";
    private static final int DB_VERSION= 1;
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_0 + " TEXT," + COL_1 + " TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE ip_address(IP TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ip_address");
        onCreate(sqLiteDatabase);
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select name from " + TABLE_NAME,null);
        return res;
    }
    public boolean insertData(String name, String id){
        boolean res;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contVal = new ContentValues();
        contVal.put(COL_0,id);
        contVal.put(COL_1,name);
        long response = db.insert(TABLE_NAME,null,contVal);
        if(response == -1){
            res = false;
        }else{
            res = true;
        }
        return res;
    }
    public void deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
    public Cursor selectIP(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM ip_address", null);
        return res;
    }
    public boolean saveIP(String ip){
        boolean res;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contVal = new ContentValues();
        contVal.put("IP",ip);
        db.execSQL("DELETE FROM ip_address");
        long response = db.insert("ip_address",null,contVal);
        if(response == -1){
            res = false;
        }else{
            res = true;
        }
        return res;
    }
    public String getID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor query = db.rawQuery("select " + COL_0 + " from " + TABLE_NAME,null);
        StringBuilder strBuilder = new StringBuilder();
        while(query.moveToNext()){
            strBuilder.append(query.getString(0));
        }
        return strBuilder.toString();
    }
}
