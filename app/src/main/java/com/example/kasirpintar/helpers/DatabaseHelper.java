package com.example.kasirpintar.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kasirpintar.GlobalApplication;
import com.example.kasirpintar.model.ItemInfo;
import com.getbase.android.schema.Schemas;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;
    private Context mContext;

    private static final String DB_NAME = "com.example.kasirpintar";
    private static final int DB_VERSION = 1;
    private static final int TABLE_REVISION = 1;

    private class Tables {
        private static final String TABLE_ITEM = "items";
    }

    private class Item {
        private static final String ITEM_SKU = "sku";
        private static final String ITEM_DATE = "date";
        private static final String ITEM_NAME = "name";
        private static final String ITEM_STOCK = "stock";
        private static final String ITEM_IMAGE = "image";
    }

    public static DatabaseHelper getInstance(Context c) {
        if (mInstance == null) {
            return new DatabaseHelper(c);
        }
        return mInstance;
    }

    private DatabaseHelper(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
        this.mContext = mContext;
    }

    private static final Schemas SCHEMA = Schemas.Builder
            .currentSchema(TABLE_REVISION,
                    new Schemas.TableDefinition(
                            Tables.TABLE_ITEM,
                            ImmutableList.<Schemas.TableDefinitionOperation>builder()
                            .add(
                                    new Schemas.AddColumn(Item.ITEM_SKU, "INTEGER PRIMARY KEY AUTOINCREMENT"),
                                    new Schemas.AddColumn(Item.ITEM_DATE, "VARCHAR(50) NOT NULL"),
                                    new Schemas.AddColumn(Item.ITEM_NAME, "VARCHAR(50) NOT NULL"),
                                    new Schemas.AddColumn(Item.ITEM_STOCK, "INTEGER NOT NULL"),
                                    new Schemas.AddColumn(Item.ITEM_IMAGE, "VARCHAR(100) NOT NULL")
                            )
                            .build()
                    )
            ).build();

    @Override
    public void onCreate(SQLiteDatabase db) {
        Schemas.Schema currentSchema = SCHEMA.getCurrentSchema();
        for (String table : currentSchema.getTables()) {
            db.execSQL(currentSchema.getCreateTableStatement(table));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SCHEMA.upgrade(mContext, db, oldVersion, newVersion);
    }

    public void addItems(String name, int stock, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Item.ITEM_DATE, GlobalApplication.singleton.getDate(null, null, null));
        v.put(Item.ITEM_NAME, name);
        v.put(Item.ITEM_STOCK, stock);
        v.put(Item.ITEM_IMAGE, image);
        db.insert(Tables.TABLE_ITEM, null, v);
        db.close();
    }

    public ArrayList<ItemInfo> getItems() {
        ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
        String query = "SELECT * FROM " + Tables.TABLE_ITEM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                mItemInfoList.add(new ItemInfo(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getBlob(4)));
            } while (c.moveToNext());
        }
        c.close();
        return mItemInfoList;
    }

    public void updateItems(int sku, String name, int stock, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Item.ITEM_NAME, name);
        v.put(Item.ITEM_STOCK, stock);
        v.put(Item.ITEM_IMAGE, image);
        db.update(Tables.TABLE_ITEM, v, Item.ITEM_SKU + "=" + sku, null);
        db.close();
    }

    public void deleteItems(int sku) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Tables.TABLE_ITEM, Item.ITEM_SKU + " =?", new String[]{String.valueOf(sku)});
        db.close();
    }

    public int getTotalItems(String sku) {
        int totalItem = 0;
        if (sku != null && !sku.equals("")) {
            String query = "SELECT SUM(stock) FROM " + Tables.TABLE_ITEM + " WHERE " + Item.ITEM_SKU;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                totalItem = c.getInt(0);
//                do {
//                    totalItem = totalItem + (c.getInt(c.getColumnIndex(Item.ITEM_STOCK)));
//                } while (c.moveToNext());
            }
            c.close();
        }
        return totalItem;
    }
}
