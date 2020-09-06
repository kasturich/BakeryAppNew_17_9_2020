package com.mi5.bakeryappnew.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Created by Kasturi Chiplunkar.
 * Created On - 3-8-2020
 * Use - To create local database.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bakeryDB.db";
    public static final String TABLE_ITEM = "items";
    public static final String TABLE_FINAL_BILL = "finalBill";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_UNIT = "unit";
    public static final String TABLE_SCHEME = "scheme";
    public static final String TABLE_ORDER = "finalOrder";

    public static final String id = "id";
    public static final String itemId = "itemId";
    public static final String itemName = "itemName";
    public static final String itemAmount = "itemAmount";
    public static final String unitId = "unitId";
    public static final String unitName = "unitName";
    public static final String catId = "catId";
    public static final String catName = "catName";
    public static final String outLateId = "outLateId";
    public static final String outLateName = "outLateName";
    public static final String companyId = "companyId";
    public static final String fromDate = "fromDate";
    public static final String toDate = "toDate";
    public static final String discount = "discount";
    public static final String schemeId = "schemeId";
    public static final String orderId = "orderId";
    public static final String orderDate = "orderDate";
    public static final String orderTime = "orderTime";
    public static final String totalQuantity = "totalQuantity";
    public static final String totalAmount = "totalAmount";
    public static final String guestName = "guestName";
    public static final String guestContactNo = "guestContactNo";
    public static final String guestAddress = "guestAddress";
    public static final String orderJson = "orderJson";
    public static final String orderFlag = "orderFlag";
    public static final String finalDiscountedAmount = "finalDiscountedAmount";
    public static final String paymentMode = "paymentMode";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_UNIT +" ("+ id +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + unitId + " VARCHAR(255),"
                + unitName + " VARCHAR(255),"
                + companyId +" VARCHAR(255) )");

        db.execSQL("create table " + TABLE_CATEGORY +" ("+ id +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + catId + " VARCHAR(255),"
                + catName + " VARCHAR(255),"
                + companyId +" VARCHAR(255) )");

        db.execSQL("create table " + TABLE_ITEM +" ("+ id +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + itemId + " VARCHAR(255),"
                + itemName + " VARCHAR(255),"
                + itemAmount + " VARCHAR(255),"
                + catId + " VARCHAR(255),"
                + catName + " VARCHAR(255),"
                + unitId + " VARCHAR(255),"
                + unitName + " VARCHAR(255),"
                + companyId +" VARCHAR(255) )");

        db.execSQL("create table " + TABLE_SCHEME +" ("+ id +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + companyId + " VARCHAR(255),"
                + schemeId + " VARCHAR(255),"
                + catId + " VARCHAR(255),"
                + catName + " VARCHAR(255),"
                + outLateId + " VARCHAR(255),"
                + outLateName + " VARCHAR(255),"
                + discount + " VARCHAR(255),"
                + fromDate + " VARCHAR(255),"
                + toDate + " VARCHAR(255))");

        db.execSQL("create table " + TABLE_ORDER +" ("+ id +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + outLateId + " VARCHAR(255),"
                + companyId + " VARCHAR(255),"
                + orderId + " VARCHAR(255),"
                + orderDate + " VARCHAR(255),"
                + orderTime + " VARCHAR(255),"
                + guestName + " VARCHAR(255),"
                + guestContactNo + " VARCHAR(255),"
                + guestAddress + " VARCHAR(255),"
                + orderJson + " VARCHAR(255),"
                + totalQuantity + " VARCHAR(255),"
                + totalAmount + " VARCHAR(255),"
                + schemeId + " VARCHAR(255),"
                + discount + " VARCHAR(255),"
                + finalDiscountedAmount + " VARCHAR(255),"
                + paymentMode + " VARCHAR(255),"
                + orderFlag + " VARCHAR(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_UNIT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SCHEME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ORDER);
        onCreate(db);
    }

    public boolean insertItemData(String itemIdValue,
                                  String itemNameValue, String itemAmountValue,
                                  String catNameValue, String catIdValue,
                                  String unitIdValue, String unitNameValue,
                                  String companyIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(itemId,itemIdValue);
        contentValues.put(itemName,itemNameValue);
        contentValues.put(itemAmount,itemAmountValue);
        contentValues.put(catId, catIdValue);
        contentValues.put(catName, catNameValue);
        contentValues.put(unitId, unitIdValue);
        contentValues.put(unitName, unitNameValue);
        contentValues.put(companyId, companyIdValue);

        long result = db.insert(TABLE_ITEM,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllItemData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ITEM,null);
        return res;
    }

    public Cursor getAllItemDataFromIemId(String itemIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ITEM +
                " where " + itemId +" = " + itemIdValue ,null);
        return res;
    }

    public Cursor getAllCategoryItemData(String catPrimaryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ITEM +
                " where " + catId +" = " + catPrimaryId,null);
        return res;
    }

    public void deleteAllFromItem()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_ITEM,null,null);
        db.execSQL("delete from "+ TABLE_ITEM);
        db.close();
    }

    public boolean insertUnitData(String unitIdValue, String unitNameValue,
                                  String companyIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(unitId,unitIdValue);
        contentValues.put(unitName,unitNameValue);
        contentValues.put(companyId, companyIdValue);

        long result = db.insert(TABLE_UNIT,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllUnitData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_UNIT,null);
        return res;
    }

    public void deleteAllFromUnit()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_ITEM,null,null);
        db.execSQL("delete from "+ TABLE_UNIT);
        db.close();
    }

    public boolean insertCategoryData(String catIdValue,
                                      String catNameValue, String companyIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(catId,catIdValue);
        contentValues.put(catName,catNameValue);
        contentValues.put(companyId, companyIdValue);

        long result = db.insert(TABLE_CATEGORY,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllCategoryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_CATEGORY,null);
        return res;
    }

    public void deleteAllFromCategory()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_ITEM,null,null);
        db.execSQL("delete from "+ TABLE_CATEGORY);
        db.close();
    }

    public boolean insertSchemeData(String companyIdValue, String schemeIdValue,
                                  String catIdValue, String catNameValue,
                                  String outletIdValue, String outletNameValue,
                                  String discountValue, String fromDateValue,
                                  String toDateValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(companyId, companyIdValue);
        contentValues.put(schemeId,schemeIdValue);
        contentValues.put(catId, catIdValue);
        contentValues.put(catName, catNameValue);
        contentValues.put(outLateId, outletIdValue);
        contentValues.put(outLateName, outletNameValue);
        contentValues.put(discount, discountValue);
        contentValues.put(fromDate, fromDateValue);
        contentValues.put(toDate, toDateValue);


        long result = db.insert(TABLE_SCHEME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllSchemeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SCHEME,null);
        return res;
    }

    public Cursor getAllSchemeDataFromSchemeId(String schemeIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SCHEME
                + " where "+ schemeId + " = " + schemeIdValue,null);
        return res;
    }

    public void deleteAllFromScheme()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_ITEM,null,null);
        db.execSQL("delete from "+ TABLE_SCHEME);
        db.close();
    }

    public boolean insertFinalOrderData(String outletIdValue, String companyIdValue,
                                        String orderIdValue, String orderDateValue,
                                        String orderTimeValue, String guestNameValue,
                                        String guestContactNoValue, String guestAddressValue,
                                        String orderJsonValue, String totalQuantityValue,
                                        String totalAmountValue, String schemeIdValue,
                                        String schemeDiscountAmountValue,
                                        String finalDiscountedAmountValue,
                                        String paymentModeValue, String orderFlagValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(outLateId, outletIdValue);
        contentValues.put(companyId, companyIdValue);
        contentValues.put(orderId, orderIdValue);
        contentValues.put(orderDate, orderDateValue);
        contentValues.put(orderTime, orderTimeValue);
        contentValues.put(guestName, guestNameValue);
        contentValues.put(guestContactNo, guestContactNoValue);
        contentValues.put(guestAddress, guestAddressValue);
        contentValues.put(orderJson, orderJsonValue);
        contentValues.put(totalQuantity, totalQuantityValue);
        contentValues.put(totalAmount, totalAmountValue);
        contentValues.put(schemeId, schemeIdValue);
        contentValues.put(discount, schemeDiscountAmountValue);
        contentValues.put(finalDiscountedAmount, finalDiscountedAmountValue);
        contentValues.put(paymentMode, paymentModeValue);
        contentValues.put(orderFlag, orderFlagValue);


        long result = db.insert(TABLE_ORDER,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllOrderData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ORDER,null);
        return res;
    }

    public void deleteAllFromOrder()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_ITEM,null,null);
        db.execSQL("delete from "+ TABLE_ORDER);
        db.close();
    }

    public Cursor getSellRecordFromDate(String orderDateValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ORDER +
                " where "+ orderDate +" = '" + orderDateValue + "'",null);
        return res;
    }
}