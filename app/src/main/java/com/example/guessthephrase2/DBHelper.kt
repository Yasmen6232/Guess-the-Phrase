package com.example.guessthephrase2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?= "GuessThePhrase",
    factory: SQLiteDatabase.CursorFactory?= null,
    version: Int= 1,
    val tableName: String= "phrases"
) : SQLiteOpenHelper(context, name, factory, version) {

    private val sqLiteDatabase: SQLiteDatabase= writableDatabase

    override fun onCreate(sql: SQLiteDatabase?) {
        sql?.execSQL("create table $tableName (Phrase Text)" )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun addNewPhrase(phrase: String): Long{
        val contentValue= ContentValues()
        contentValue.put("Phrase",phrase)
        return sqLiteDatabase.insert(tableName,null,contentValue)
    }

    fun gettingPhrases(): ArrayList<String>{
        return try{
            val phrases= arrayListOf<String>()
            val cursor =
                sqLiteDatabase.query(tableName, arrayOf("Phrase"), null, null, null, null, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                phrases.add(cursor.getString(cursor.getColumnIndexOrThrow("Phrase")))
                cursor.moveToNext()
            }
            phrases
        } catch (e:Exception){
            arrayListOf("Error")
        }
    }

}