package com.hse.touristhelper.qr.storage;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 10.05.2016.
 */
public class QRCodeStorageHelper extends SQLiteOpenHelper {

    private static final String DATABASE_QR_CODES = "qr_codes";
    private static final int DATABASE_VERSION = 1;
    private static final String QR_CODES_TABLE = "qr_codes_table";
    private static final String CREATE_TABLE_NOTE = "create table qr_codes_table"
            + "("
            + "_id" + " integer primary key autoincrement, "
            + "type" + " integer not null, "
            + "content" + " text not null, "
            + "created_time" + " integer not null " + ")";

    public QRCodeStorageHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public QRCodeStorageHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS + qr_codes_table");
        onCreate(db);
    }
}
