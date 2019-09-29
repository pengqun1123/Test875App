package com.face.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用Android SQLite实现的用户信息管理器
 */
public class SQLiteUserManager extends SQLiteOpenHelper implements UserManager {

    private static final String DB_NAME = "user.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME_USER = "t_user";
    private static final String T_USER_CREATE_SQL = "create table " + TABLE_NAME_USER + "(" +
            "id integer primary key ," +
            "name text , " +
            "image_path text," +
            "feature text" +
            ")";
    private static final String[] T_USER_COLUMNS = {"id", "name", "image_path", "feature"};

    public SQLiteUserManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 可设置数据库文件目录的SQLite数据库构造器
     *
     * @param context
     * @param databaseDir 数据库存储目录
     */
    public SQLiteUserManager(Context context, @NonNull File databaseDir) {
        super(context, new File(databaseDir, DB_NAME).getAbsolutePath(), null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(T_USER_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public User findOne(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_NAME_USER, T_USER_COLUMNS, "id=?", new String[]{String.valueOf(id)}, null, null, null, "1")) {
            if (c.moveToFirst()) {
                return cursorToUser(c);
            }
        }
        return null;
    }

    @Override
    public List<User> find(int limit, int offset) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_NAME_USER, T_USER_COLUMNS, null, null, null, null, "id desc", "" + offset + "," + limit)) {
            List<User> result = new ArrayList<>();
            while (c.moveToNext()) {
                result.add(cursorToUser(c));
            }
            return result;
        }
    }

    @Override
    public long addOne(User user) {
        ContentValues v = new ContentValues();
        v.put("name", user.getName());
        v.put("image_path", user.getImagePath());
        v.put("feature", user.getFeatureString());
        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(TABLE_NAME_USER, null, v);
        user.setId(rowId);
        return rowId;
    }

    @Override
    public boolean deleteOne(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_USER, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    private User cursorToUser(Cursor c) {
        User user = new User();
        user.setId(c.getLong(0));
        user.setName(c.getString(1));
        user.setImagePath(c.getString(2));
        user.setFeatureString(c.getString(3));
        return user;
    }

}
