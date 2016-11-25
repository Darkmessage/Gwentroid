package darkmessage.gwent.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.res.TypedArrayUtils;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

import darkmessage.gwent.R;
import darkmessage.gwent.activities.MainActivity;
import darkmessage.gwent.model.Card;
import darkmessage.gwent.model.Collection;

import static darkmessage.gwent.database.FeedReaderContract.FeedEntry.*;

/**
 * Created by Darkmessage on 06.11.2016.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static String SQL_CREATE_ENTRIES;
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    SQLiteDatabase db;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ( " + TABLE_ID + " TEXT PRIMARY KEY";

        for(String s:TABLE_STRING_COLUMNS){
            SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES + "," + s + " " + TEXT_TYPE;
        }
        for(String s:TABLE_INT_COLUMNS){
            SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES + "," + s + " " + INT_TYPE;
        }
        for(String s: TABLE_BITMAP_COLUMNS){
            SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES + "," + s + " " + BLOB_TYPE;
        }

        SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES + " );";

        db = getWritableDatabase();
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertCardsIntoDb(Context context, Collection collection){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        for(Card card:collection.getCards()) {
            values.put(TABLE_ID, card.getId());

            for (String s : TABLE_STRING_COLUMNS) {
                Method method;
                String value;

                try {
                    method = card.getClass().getMethod("get" + s);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    value = (String) method.invoke(card);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                values.put(s, value);
            }
            for (String s : TABLE_INT_COLUMNS) {
                Method method;
                int value;

                try {
                    method = card.getClass().getMethod("get" + s);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    value = (int) method.invoke(card);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                values.put(s, value);
            }
            for (String s : TABLE_BITMAP_COLUMNS) {
                Method method;
                Bitmap value;

                try {
                    method = card.getClass().getMethod("get" + s);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    value = (Bitmap) method.invoke(card);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                value.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                values.put(s, byteArray);
            }

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TABLE_NAME, null, values);
        }
    }

    public void getCardsFromDb(Context context, Collection collection){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = new String[TABLE_STRING_COLUMNS.length + TABLE_INT_COLUMNS.length + TABLE_BITMAP_COLUMNS.length + 1];
        projection[0] = TABLE_ID;
        System.arraycopy(TABLE_STRING_COLUMNS, 0, projection, 1, TABLE_STRING_COLUMNS.length);
        System.arraycopy(TABLE_INT_COLUMNS, 0, projection, TABLE_STRING_COLUMNS.length + 1, TABLE_INT_COLUMNS.length);
        System.arraycopy(TABLE_BITMAP_COLUMNS, 0, projection, TABLE_STRING_COLUMNS.length + TABLE_INT_COLUMNS.length + 1, TABLE_BITMAP_COLUMNS.length);

        Cursor cursor = db.query(
        TABLE_NAME,                     // The table to query
        projection,                     // The columns to return
        null,                           // The columns for the WHERE clause
        null,                           // The values for the WHERE clause
        null,                           // don't group the rows
        null,                           // don't filter by row groups
        "name"                            // The sort order
        );

        boolean moveOn = cursor.moveToFirst();

        while(moveOn){
            Card card = new Card();

            card.setId(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_ID)));

            for (String s : TABLE_STRING_COLUMNS) {
                Method method;

                String value = cursor.getString(cursor.getColumnIndexOrThrow(s));

                try {
                    method = card.getClass().getMethod("set" + s, String.class);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    method.invoke(card, value);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }
            }
            for (String s : TABLE_INT_COLUMNS) {
                Method method;

                int value = cursor.getInt(cursor.getColumnIndexOrThrow(s));

                try {
                    method = card.getClass().getMethod("set" + s, int.class);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    method.invoke(card, value);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }
            }
            for (String s : TABLE_BITMAP_COLUMNS) {
                Method method;

                byte[] byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(s));
                Bitmap value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                try {
                    method = card.getClass().getMethod("set" + s, Bitmap.class);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    method.invoke(card, value);
                } catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }
            }

            collection.addCard(card);

            moveOn = cursor.moveToNext();
        }
    }

    public void deleteTable(){
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void createTable(){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void updateCount(String id, int count){
        // New value for one column
        ContentValues values = new ContentValues();
        values.put("Count", count);

        // Which row to update, based on the title
        String selection = TABLE_ID + " = ?";
        String[] selectionArgs = { id };

        db.update(
        TABLE_NAME,
        values,
        selection,
        selectionArgs);
    }
}
