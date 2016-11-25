package darkmessage.gwent.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Darkmessage on 06.11.2016.
 */

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    public FeedReaderContract() { }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Card";
        public static final String TABLE_ID = "Id";
        public static final String[] TABLE_STRING_COLUMNS = {"Faction", "Color", "Rarity", "Lane", "Loyalty", "Name", "Description", "ImagePath"};
        public static final String[] TABLE_INT_COLUMNS = {"Count"};
        public static final String[] TABLE_BITMAP_COLUMNS = {"Image"};
        public static final String TEXT_TYPE = "TEXT";
        public static final String INT_TYPE = "INTEGER";
        public static final String BLOB_TYPE = "BLOB";
    }
}
