package com.example.guessthephrase2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var phraseView : TextView
    private lateinit var highestScoreView : TextView
    private lateinit var note : ConstraintLayout
    private lateinit var myRV : RecyclerView
    private lateinit var charButton : Button
    private lateinit var phraseButton : Button
    private lateinit var charEntry : EditText
    private lateinit var phraseEntry : EditText

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var list : ArrayList<Data>
    private lateinit var noRepeat : ArrayList<String>
    private lateinit var phrase : String
    private var countGussiesPhrase = 10
    private var countGussiesChar = 10
    private lateinit var stars : CharArray
    private lateinit var phraseString : String
    private var highestScore : Int = 0

    private val dbHelper by lazy { DBHelper(applicationContext) }


    private fun show(list:ArrayList<Data>){

        myRV.adapter = RecyclerViewAdapter(list)
        myRV.layoutManager = LinearLayoutManager(this)
        if(list.size!=0)
            myRV.smoothScrollToPosition(list.size - 1)

    }


    private fun saveNewChar(){
        phraseString="Phrase: "
        for(i in stars){
            phraseString += i
        }
        phraseView.text=phraseString
    }


    private fun playAgain(){

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("The Correct Phrase was $phrase \nWould You Like To Play Again:")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> this.recreate() }
        //.setNegativeButton("No"){dialog,_ -> dialog.cancel()}

        val alert=dialogBuilder.create()
        alert.setTitle("Game Over!!")
        alert.show()

    }


    private fun killButton(button:Button,edit:EditText){
        button.isEnabled = false
        button.isClickable = false
        edit.isEnabled = false
        edit.isClickable = false
    }


    private fun callData(){
        highestScore = sharedPreferences.getInt("HQ",0)
    }

    private fun saveData(){
        with(sharedPreferences.edit()) {
            putInt("HQ",highestScore)
            apply()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlertDialog.Builder(this)
            .setTitle("Do You Want to Add New Phrase or Continue to The Game")
            .setPositiveButton("Add New Phrase"){_,_ -> startActivity(Intent(this,AddNewPhrase::class.java))}
            .setNegativeButton("Play The Game"){alert,_ -> alert.cancel()}
            .show()


        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        callData()

        phraseView = findViewById(R.id.phraseHide)
        highestScoreView = findViewById(R.id.highestH)
        note = findViewById(R.id.mainL)
        charButton = findViewById(R.id.CharButton)
        phraseButton = findViewById(R.id.PhraseButton)
        charEntry = findViewById(R.id.CharEntry)
        phraseEntry = findViewById(R.id.PhraseEntry)
        myRV = findViewById(R.id.rvMain)

        highestScoreView.text="The Highest Score is $highestScore out of 10"

        val phraseList= dbHelper.gettingPhrases()
        val phraseOldList= listOf(
            "UP IN THE AIR",
            "KILL TWO BIRDS WITH ONE STONE",
            "PIECE OF CAKE",
            "BREAK A LEG"
        )
        for (phrase in phraseOldList){
            var check= false
            for (i in phraseList)
                if (phrase.contains(i,true))
                    check= true
            if (!check)
                dbHelper.addNewPhrase(phrase)
        }

        phrase=phraseList[Random.nextInt(phraseList.size)]
        stars=phrase.toCharArray()
        phraseString="Phrase: "
        for(i in stars){
            if(i != ' ') {
                stars[stars.indexOf(i)] = '*'
                phraseString+='*'
            }
            else {
                stars[stars.indexOf(i)] = ' '
                phraseString+=' '
            }
        }



        if(savedInstanceState!=null){
            list = arrayListOf()
            noRepeat = arrayListOf()
            countGussiesChar= savedInstanceState.getInt("countGussiesChar", 0)
            countGussiesPhrase= savedInstanceState.getInt("countGussiesPhrase", 0)
            val newList = savedInstanceState.getStringArrayList("RecycleView")!!
            val colorList = savedInstanceState.getIntegerArrayList("colorList")
            noRepeat = savedInstanceState.getStringArrayList("NoR")!!
            phrase = savedInstanceState.getString("answer")!!
            stars = savedInstanceState.getCharArray("phraseView")!!
            phraseString = savedInstanceState.getString("pString")!!
            for (i in 0 until newList.size)
                list.add(Data(newList[i], colorList!![i]))
        }
        else {
            list = arrayListOf()
            noRepeat = arrayListOf()
        }


        if(countGussiesChar<=0 && countGussiesPhrase<=0||!stars.contains('*')){
            countGussiesChar=10
            countGussiesPhrase=10
            list= arrayListOf()
            noRepeat = arrayListOf()
            phrase=phraseList[Random.nextInt(phraseList.size)]
            stars=phrase.toCharArray()
            phraseString="Phrase: "
            for(i in stars){
                if(i != ' ') {
                    stars[stars.indexOf(i)] = '*'
                    phraseString+='*'
                }
                else {
                    stars[stars.indexOf(i)] = ' '
                    phraseString+=' '
                }
            }
        }
        else
            show(list)

        phraseView.text=phraseString

        charButton.setOnClickListener {
            if(charEntry.text.isNotBlank()){
                if (charEntry.text.toString().uppercase() !in noRepeat) {
                    var index=0
                    for(i in phrase) {
                        if (i.toString() == charEntry.text.toString().uppercase())
                            stars[index] = i
                        index++
                    }

                    saveNewChar()
                    if(phrase.contains(charEntry.text.toString().uppercase())) {
                        list.add(Data("You Guessed ${charEntry.text.toString().uppercase()} Correct", Color.GREEN))
                        show(list)
                    }
                    else{
                        countGussiesChar--
                        list.add(Data("You Guessed ${charEntry.text.toString().uppercase()} Wrong\n" +
                                "$countGussiesChar Character Guesses Remaining",Color.RED))
                        show(list)

                    }
                    noRepeat.add(charEntry.text.toString().uppercase())
                    charEntry.text=null
                }
                else
                {
                    Snackbar.make(note, "Please Don't Repeat the Litter", Snackbar.LENGTH_LONG).show()
                    charEntry.text=null
                }
            }
            else {
                Snackbar.make(note, "Please Enter Valid Value", Snackbar.LENGTH_LONG).show()
                charEntry.text=null
            }
            if(countGussiesChar<=0&&countGussiesPhrase<=0||!stars.contains('*')) {
                if(highestScore<(countGussiesChar+countGussiesPhrase)/2)
                    highestScore=(countGussiesChar+countGussiesPhrase)/2
                saveData()
                playAgain()
            }
            if(countGussiesChar<=0)
                killButton(charButton,charEntry)
        }

        phraseButton.setOnClickListener {
            if(phraseEntry.text.isNotBlank()){
                var index=0
                if (phrase == phraseEntry.text.toString().uppercase()) {
                    list.add(Data("You Guessed ${phraseEntry.text.toString().uppercase()} Correct", Color.GREEN))
                    show(list)
                    for(i in phrase)
                        stars[index++]=i
                }
                else{
                    countGussiesPhrase--
                    list.add(Data("You Guessed ${phraseEntry.text.toString().uppercase()} Wrong\n" +
                            "$countGussiesPhrase Phrase Guesses Remaining",Color.RED))
                    show(list)

                }
                phraseEntry.text=null
            }
            else {
                Snackbar.make(note, "Please Enter Valid Value", Snackbar.LENGTH_LONG).show()
                phraseEntry.text=null
            }
            if(countGussiesChar<=0&&countGussiesPhrase<=0||!stars.contains('*')) {
                if(highestScore<(countGussiesChar+countGussiesPhrase)/2)
                    highestScore=(countGussiesChar+countGussiesPhrase)/2
                saveData()
                playAgain()
            }
            if(countGussiesPhrase<=0)
                killButton(phraseButton,phraseEntry)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("countGussiesChar",countGussiesChar)
        outState.putInt("countGussiesPhrase",countGussiesPhrase)
        val newList= arrayListOf<String>()
        val colorList= arrayListOf<Int>()
        for (i in list) {
            newList.add(i.entries)
            colorList.add(i.color)
        }
        outState.putIntegerArrayList("colorList",colorList)
        outState.putStringArrayList("RecycleView",newList)
        outState.putStringArrayList("NoR",noRepeat)
        outState.putString("answer",phrase)
        outState.putCharArray("phraseView",stars)
        outState.putString("pString",phraseString)
    }
}