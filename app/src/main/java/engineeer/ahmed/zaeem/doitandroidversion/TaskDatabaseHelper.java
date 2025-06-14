package engineeer.ahmed.zaeem.doitandroidversion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_STATUS = "status";
    private static final String COL_CATEGORY = "category";
    private static final String COL_PUBLISHED_DATE = "publishedDate";
    private static final String COL_START_DATE = "startDate";
    private static final String COL_END_DATE = "endDate";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COL_CATEGORY_NAME = "name";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TASKS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_STATUS + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_PUBLISHED_DATE + " TEXT, " +
                COL_START_DATE + " TEXT, " +
                COL_END_DATE + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (" +
                COL_CATEGORY_NAME + " TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, task.getUsername());
        values.put(COL_TITLE, task.getTitle());
        values.put(COL_DESCRIPTION, task.getDescription());
        values.put(COL_STATUS, task.getStatus());
        values.put(COL_CATEGORY, task.getCategory());
        values.put(COL_PUBLISHED_DATE, task.getPublishedDate());
        values.put(COL_START_DATE, task.getStartDate());
        values.put(COL_END_DATE, task.getEndDate());
        db.insert(TABLE_TASKS, null, values);
    }

    public List<Task> getTasks(String username, String status, String search) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USERNAME + "=?";
        List<String> args = new ArrayList<>();
        args.add(username);
        if (!status.equals("All")) {
            selection += " AND " + COL_STATUS + "=?";
            args.add(status);
        }
        if (search != null && !search.isEmpty()) {
            selection += " AND (" + COL_TITLE + " LIKE ? OR " + COL_DESCRIPTION + " LIKE ?)";
            args.add("%" + search + "%");
            args.add("%" + search + "%");
        }
        Cursor cursor = db.query(TABLE_TASKS, null, selection, args.toArray(new String[0]), null, null, COL_CATEGORY);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PUBLISHED_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))
                );
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Task task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PUBLISHED_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))
            );
            cursor.close();
            return task;
        }
        cursor.close();
        return null;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, task.getTitle());
        values.put(COL_DESCRIPTION, task.getDescription());
        values.put(COL_STATUS, task.getStatus());
        values.put(COL_CATEGORY, task.getCategory());
        values.put(COL_START_DATE, task.getStartDate());
        values.put(COL_END_DATE, task.getEndDate());
        db.update(TABLE_TASKS, values, COL_ID + "=?", new String[]{String.valueOf(task.getId())});
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COL_CATEGORY_NAME}, null, null, null, null, COL_CATEGORY_NAME);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }
    public void insertCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY_NAME, category);
        db.insert(TABLE_CATEGORIES, null, values);
    }
    public boolean categoryExists(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, COL_CATEGORY_NAME + "=?", new String[]{category}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
