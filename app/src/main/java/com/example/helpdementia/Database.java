package com.example.helpdementia;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="Create table user(username text,email text,password text)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
    public void Sign_up(String username,String email,String password )
    {
        ContentValues cv = new ContentValues();
        cv.put("username",username);
        cv.put("Email",email);
        cv.put("password",password);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert("user",null,cv);
    }
    public int Login_jav(String email,String password)
    {
        String str[]=new String[2];
        str[0]=email;
        str[1]=password;
        int result=0;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cv = db.rawQuery("select * from user where email=? and password=?",str);
        if(cv.moveToFirst())
        {
            result=1;
        }

        return result;

    }

}

