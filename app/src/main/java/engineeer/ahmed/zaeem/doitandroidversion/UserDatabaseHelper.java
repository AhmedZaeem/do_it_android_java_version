package engineeer.ahmed.zaeem.doitandroidversion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COL_USERNAME = "username";
    private static final String COL_PHONE = "phone";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FONT_SIZE = "font_size";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USERNAME + " TEXT PRIMARY KEY, " +
                COL_PHONE + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_FONT_SIZE + " TEXT DEFAULT 'Medium')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void insertUser(String username, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PHONE, phone);
        values.put(COL_PASSWORD, password);
        values.put(COL_FONT_SIZE, "Medium"); // default font size
        db.insert(TABLE_USERS, null, values);
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password}, null, null, null);
        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    public String getPhoneByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_PHONE}, COL_USERNAME + "=?", new String[]{username}, null, null, null);
        String phone = "";
        if (cursor.moveToFirst()) {
            phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
        }
        cursor.close();
        return phone;
    }

    public void setFontSizeForUser(String username, String fontSize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FONT_SIZE, fontSize);
        db.update(TABLE_USERS, values, COL_USERNAME + "=?", new String[]{username});
    }

    public String getFontSizeForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_FONT_SIZE}, COL_USERNAME + "=?", new String[]{username}, null, null, null);
        String fontSize = "Medium";
        if (cursor.moveToFirst()) {
            fontSize = cursor.getString(cursor.getColumnIndexOrThrow(COL_FONT_SIZE));
        }
        cursor.close();
        return fontSize;
    }
}
