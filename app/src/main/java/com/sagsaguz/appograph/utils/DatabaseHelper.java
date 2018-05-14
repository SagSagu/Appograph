package com.sagsaguz.appograph.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME="FriendsDatabase";
    public static final String TABLE_NAME="FriendsTable";
    public static final String NAME="name";
    public static final String DOB="dob";
    public static final String PHONE="phone";
    public static final String ALTERNATEPHONE="alternatePhone";
    public static final String EMAIL="email";
    public static final String WHATSAPP="whatsapp";
    public static final String HOBBIES="hobbies";
    public static final String OTHERS="others";
    public static final String IMAGE="image";
    public static final String COVERIMAGE="coverImage";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+NAME+" VARCHAR, "+DOB+" VARCHAR, "+PHONE+" INTEGER PRIMARY KEY, "+ALTERNATEPHONE+" VARCHAR, "+EMAIL+" VARCHAR, "+WHATSAPP+" VARCHAR, "+HOBBIES+" VARCHAR, "+OTHERS+" VARCHAR, "+IMAGE+" VARCHAR, "+COVERIMAGE+" VARCHAR)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void addUser(String name, String dob, String phone, String email, String whatsapp, String hobbies) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(DOB, dob);
        values.put(PHONE, phone);
        values.put(EMAIL, email);
        values.put(WHATSAPP, whatsapp);
        values.put(HOBBIES, hobbies);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addUser(Friends friends) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, friends.getName());
        values.put(DOB, friends.getDob());
        values.put(PHONE, friends.getPhone());
        values.put(ALTERNATEPHONE, friends.getAlternate_phone());
        values.put(EMAIL, friends.getEmail());
        values.put(WHATSAPP, friends.getWhatsapp());
        values.put(HOBBIES, friends.getHobbies());
        values.put(OTHERS, friends.getOthers());
        //byte[] data = getBitmapAsByteArray(friends.getImage());
        values.put(IMAGE, friends.getImage());
        //data = getBitmapAsByteArray(friends.getCoverImage());
        values.put(COVERIMAGE, friends.getCoverImage());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Friends> getUser(String phone){
        Cursor cursor = null;
        List<Friends> details = new ArrayList<>();
        // Opening SQLite database read permission.
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PHONE + " = " + phone;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            //Bitmap imageBitmap = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex(IMAGE)), 0, cursor.getBlob(cursor.getColumnIndex(IMAGE)).length);
            //Bitmap coverImageBitmap = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex(COVERIMAGE)), 0, cursor.getBlob(cursor.getColumnIndex(COVERIMAGE)).length);
            Friends friends = new Friends(cursor.getString(cursor.getColumnIndex(NAME)),
                    cursor.getString(cursor.getColumnIndex(DOB)),
                    cursor.getString(cursor.getColumnIndex(PHONE)),
                    cursor.getString(cursor.getColumnIndex(ALTERNATEPHONE)),
                    cursor.getString(cursor.getColumnIndex(EMAIL)),
                    cursor.getString(cursor.getColumnIndex(WHATSAPP)),
                    cursor.getString(cursor.getColumnIndex(HOBBIES)),
                    cursor.getString(cursor.getColumnIndex(OTHERS)),
                    cursor.getString(cursor.getColumnIndex(IMAGE)),
                    cursor.getString(cursor.getColumnIndex(COVERIMAGE)));
            details.add(friends);
            return details;
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Friends> getAllUsers(){
        Cursor cursor = null;
        List<Friends> details = new ArrayList<>();
        // Opening SQLite database read permission.
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //Bitmap imageBitmap = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex(IMAGE)), 0, cursor.getBlob(cursor.getColumnIndex(IMAGE)).length);
                    //Bitmap coverImageBitmap = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex(COVERIMAGE)), 0, cursor.getBlob(cursor.getColumnIndex(COVERIMAGE)).length);
                    Friends friends = new Friends(cursor.getString(cursor.getColumnIndex(NAME)),
                            cursor.getString(cursor.getColumnIndex(DOB)),
                            cursor.getString(cursor.getColumnIndex(PHONE)),
                            cursor.getString(cursor.getColumnIndex(ALTERNATEPHONE)),
                            cursor.getString(cursor.getColumnIndex(EMAIL)),
                            cursor.getString(cursor.getColumnIndex(WHATSAPP)),
                            cursor.getString(cursor.getColumnIndex(HOBBIES)),
                            cursor.getString(cursor.getColumnIndex(OTHERS)),
                            cursor.getString(cursor.getColumnIndex(IMAGE)),
                            cursor.getString(cursor.getColumnIndex(COVERIMAGE)));
                    details.add(friends);
                } while (cursor.moveToNext());
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return details;
    }

    public void updateName(String phone, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateDOB(String phone, String dob){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOB, dob);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateContact(String phone, String alternatePhone, String whatsApp, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALTERNATEPHONE, alternatePhone);
        contentValues.put(WHATSAPP, whatsApp);
        contentValues.put(EMAIL, email);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateHobbies(String phone, String hobbies){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOBBIES, hobbies);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateOthers(String phone, String others){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OTHERS, others);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateProfilePic(String phone, String profilePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //byte[] data = getBitmapAsByteArray(bitmap);
        contentValues.put(IMAGE, profilePath);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateCoverPic(String phone, String coverPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //byte[] data = getBitmapAsByteArray(bitmap);
        contentValues.put(COVERIMAGE, coverPath);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    /*public void updateProfilePic(String phone, Bitmap bitmap){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        byte[] data = getBitmapAsByteArray(bitmap);
        contentValues.put(IMAGE, data);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }

    public void updateCoverPic(String phone, Bitmap bitmap){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        byte[] data = getBitmapAsByteArray(bitmap);
        contentValues.put(COVERIMAGE, data);
        db.update(TABLE_NAME, contentValues, PHONE+"="+phone, null);
        db.close();
    }*/

    public int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int cnt  = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return cnt;
    }

    public Integer deleteUser(String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME , PHONE+"=?", new String[]{phone});
    }

    // Checking Email is already exists or not.
    public String checkingUserAlreadyExistsOrNot(String phone){

        // Opening SQLite database write permission.
        SQLiteDatabase db = this.getWritableDatabase();

        // Adding search email query to cursor.
        Cursor cursor = db.query(TABLE_NAME, null, " " + PHONE + "=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                List<Friends> details = getUser(phone);
                // Closing cursor.
                cursor.close();
                return details.get(0).getName();
            }
        }
        return "new";
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

}
