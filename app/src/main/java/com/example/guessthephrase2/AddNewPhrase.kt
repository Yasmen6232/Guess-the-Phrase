package com.example.guessthephrase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddNewPhrase: AppCompatActivity() {

    private val dbHelper by lazy { DBHelper(applicationContext) }
    private lateinit var saveButton: Button
    private lateinit var phraseEntry: EditText
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_phrase)

        saveButton= findViewById(R.id.button)
        phraseEntry= findViewById(R.id.PhraseEntry)
        backButton= findViewById(R.id.backButton)

        backButton.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }

        saveButton.setOnClickListener{
            if (phraseEntry.text.isNotBlank()){
                val wrongCode: Long=-1
                val result= dbHelper.addNewPhrase(phraseEntry.text.toString().uppercase())
                if (wrongCode != result)
                    Toast.makeText(this,"Phrase was Saved",Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                phraseEntry.text.clear()
            }
            else
                Toast.makeText(this,"Please enter Valid Value",Toast.LENGTH_SHORT).show()
        }

    }
}