package com.example.listacontatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Importar Log

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "contactsManager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT" +
                    ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Criando tabela " + TABLE_CONTACTS);
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade: Atualizando banco de dados da versão " + oldVersion + " para " + newVersion + ". Dados antigos serão perdidos.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public long addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, contact.getName());
            values.put(COLUMN_PHONE, contact.getPhone());
            values.put(COLUMN_EMAIL, contact.getEmail());
            id = db.insert(TABLE_CONTACTS, null, values);
            Log.d(TAG, "addContact: Contato adicionado com ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "addContact: Erro ao adicionar contato", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public Contact getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Contact contact = null;
        try {
            cursor = db.query(TABLE_CONTACTS,
                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_EMAIL},
                    COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                contact = new Contact(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                );
                Log.d(TAG, "getContact: Contato encontrado: " + contact.getName());
            } else {
                Log.d(TAG, "getContact: Contato não encontrado com ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "getContact: Erro ao buscar contato", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return contact;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + COLUMN_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                    );
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "getAllContacts: Total de contatos buscados: " + contactList.size());
        } catch (Exception e) {
            Log.e(TAG, "getAllContacts: Erro ao buscar todos os contatos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return contactList;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, contact.getName());
            values.put(COLUMN_PHONE, contact.getPhone());
            values.put(COLUMN_EMAIL, contact.getEmail());
            rowsAffected = db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())});
            Log.d(TAG, "updateContact: Linhas afetadas: " + rowsAffected + " para o contato ID: " + contact.getId());
        } catch (Exception e) {
            Log.e(TAG, "updateContact: Erro ao atualizar contato", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return rowsAffected;
    }

    public boolean deleteContact(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "deleteContact: Linhas afetadas: " + rowsAffected + " para o ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "deleteContact: Erro ao deletar contato", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return rowsAffected > 0;
    }
}