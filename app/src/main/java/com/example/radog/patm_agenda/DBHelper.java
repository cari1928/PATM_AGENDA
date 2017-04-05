package com.example.radog.patm_agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radog on 03/04/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    //DATABASE - GENERAL INFORMATION
    private static final String DB_NAME = "agenda.db";
    private static final int VERSION = 1;

    //TABLE CONTACTS - STRUCTURE
    private static final String TABLE_CONTACT = "contact";
    //COLUMN NAMES
    private static final String ID_CONTACT = "idContact";
    private static final String NAME_CONTACT = "nameC";
    private static final String PHONE_CONTACT = "phone";
    private static final String EMAIL_CONTACT = "email";

    //TABLE EVENT - STRUCTURE
    private static final String TABLE_EVENT = "event";
    //COLUMN NAMES
    private static final String ID_EVENT = "idEvent";
    private static final String NAME_EVENT = "nameE";
    private static final String DESCRIPTION_EVENT = "descE";
    private static final String DATE_EVENT = "dateE";

    //TABLE EVENTS-CONTACT
    private static final String TABLE_PEOPLE = "people";
    //COLUMN NAMES
    private static final String ID_PEOPLE = "idPeople";

    //TO CREATE TABLE CONTACT
    private static final String CREATE_CONTACT = "CREATE TABLE " + TABLE_CONTACT + " ( "
            + ID_CONTACT + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME_CONTACT + " VARCHAR(100), "
            + PHONE_CONTACT + " VARCHAR(25), "
            + EMAIL_CONTACT + " VARCHAR(100) );";

    //TO CREATE TABLE EVENT
    private static final String CREATE_EVENT = "CREATE TABLE " + TABLE_EVENT + " ( "
            + ID_EVENT + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME_EVENT + " VARCHAR(100), "
            + DESCRIPTION_EVENT + " VARCHAR(150),"
            + DATE_EVENT + " TEXT );";

    //TO CREATE TABLE PEOPLE
    private static final String CREATE_PEOPLE = "CREATE TABLE " + TABLE_PEOPLE + " ( "
            + ID_PEOPLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_CONTACT + " INTEGER, "
            + ID_EVENT + " INTEGER, "
            + "FOREIGN KEY(" + ID_CONTACT + ") REFERENCES " + TABLE_CONTACT + "(" + ID_CONTACT + "), "
            + "FOREIGN KEY(" + ID_EVENT + ") REFERENCES " + TABLE_EVENT + "(" + ID_EVENT + ") );";


    SQLiteDatabase myDB;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT);
        db.execSQL(CREATE_EVENT);
        db.execSQL(CREATE_PEOPLE);

        db.execSQL("INSERT INTO " + TABLE_EVENT + "(" + NAME_EVENT + ", " + DESCRIPTION_EVENT + " , " + DATE_EVENT + ") "
                + "VALUES('HELLO WORLD!!!', 'WELCOME', '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDB() {
        myDB = getWritableDatabase();
    }

    public void closeDB() {
        if (myDB != null && myDB.isOpen()) {
            myDB.close();
        }
    }

    public long insertEvent(String name, String desc, String date) {
        ContentValues objCV = new ContentValues();

        objCV.put(NAME_EVENT, name);
        objCV.put(DESCRIPTION_EVENT, desc);
        objCV.put(DATE_EVENT, date);

        return myDB.insert(TABLE_EVENT, null, objCV);
    }

    public long insertContact(String name, String email, String phone) {
        ContentValues objCV = new ContentValues();

        objCV.put(NAME_CONTACT, name);
        objCV.put(EMAIL_CONTACT, email);
        objCV.put(PHONE_CONTACT, phone);

        return myDB.insert(TABLE_CONTACT, null, objCV);
    }

    public long insertPeople(int idEvent, int idContact) {
        long result;
        ContentValues objCV = new ContentValues();

        objCV.put(ID_EVENT, idEvent);
        objCV.put(ID_CONTACT, idContact);

        myDB.execSQL("PRAGMA foreign_keys=ON;");
        result = myDB.insert(TABLE_PEOPLE, null, objCV);
        myDB.execSQL("PRAGMA foreign_keys=OFF;");

        return result;
    }

    public long updateEvent(int idEvent, String name, String desc, String date) {
        ContentValues objCV = new ContentValues();
        String where;

        objCV.put(NAME_EVENT, name);
        objCV.put(DESCRIPTION_EVENT, desc);
        objCV.put(DATE_EVENT, date);

        where = ID_EVENT + "=" + idEvent;

        return myDB.update(TABLE_EVENT, objCV, where, null);
    }

    public List<String> selectPeople(int idEvent, int idContact) {
        List<String> registers = new ArrayList<>();

        Cursor c = myDB.rawQuery("SELECT * FROM " + TABLE_PEOPLE +
                " WHERE " + ID_EVENT + "=" + idEvent +
                " AND " + idContact + "=" + idContact, null);
        if (c.moveToFirst()) {
            do {
                registers.add(c.getString(0));
            } while (c.moveToNext());
        } else {
            return null;
        }

        return registers;
    }

    public long deletePeople(int idEvent) {
        String where = ID_EVENT + "=" + idEvent;
        return myDB.delete(TABLE_PEOPLE, where, null);
    }

    public long deleteEvent(int idEvent) {
        String where = ID_EVENT + "=" + idEvent;
        return myDB.delete(TABLE_EVENT, where, null);
    }

    public List<String> selectContact(String name, String phone, String email) {
        List<String> registers = new ArrayList<>();

        Cursor c = myDB.rawQuery("SELECT * FROM contact WHERE nameC='" + name + "' AND phone='" + phone + "' AND email='" + email + "'", null);
        if (c.moveToFirst()) {
            do {
                registers.add(c.getString(0) + "-" + c.getString(1) + "-" + c.getString(2) + "-" + c.getString(3));
            } while (c.moveToNext());
        } else {
            return null;
        }

        return registers;
    }

    public List<String> selectContacts(int idEvent) {
        List<String> registers = new ArrayList<>();

        Cursor c = myDB.rawQuery("SELECT * FROM contact " +
                "INNER JOIN people ON contact.idContact = people.idContact " +
                "WHERE idEvent=" + idEvent, null);
        if (c.moveToFirst()) {
            do {
                registers.add(c.getString(1) + "-" + c.getString(2) + "-" + c.getString(3));
            } while (c.moveToNext());
        } else {
            return null;
        }

        return registers;
    }

    public List<String> selectEvent(String name, String desc, String date) {
        List<String> registers = new ArrayList<>();

        Cursor c = myDB.rawQuery("SELECT * FROM event " +
                "WHERE " + NAME_EVENT + "='" + name + "' " +
                "AND " + DESCRIPTION_EVENT + "='" + desc + "' " +
                "AND " + DATE_EVENT + "='" + date + "'", null);
        if (c.moveToFirst()) {
            do {
                registers.add(c.getString(0));
            } while (c.moveToNext());
        } else {
            return null;
        }

        return registers;
    }

}
