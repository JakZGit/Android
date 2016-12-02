package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    int counter = 0;
    private final Handler handler = new Handler();
    private Runnable run;

    static final String GAME_STATUS = "status";
    static final String WORD = "word";
    private boolean restored = false;
    String computerWord="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        TextView textV = (TextView) findViewById(R.id.ghostText);
        TextView labelV = (TextView) findViewById(R.id.gameStatus);
        if (savedInstanceState != null) {
            restored = true;

            // Restore value of members from saved state
            textV.setText(savedInstanceState.getString(WORD));
            labelV.setText(savedInstanceState.getString(GAME_STATUS));

            if(labelV.getText()==USER_TURN){
                userTurn = true;
            }
            else if(labelV.getText() == COMPUTER_TURN){
//                handler.removeCallbacks(run);
                computerTurn();
            }

        }else{
            textV.setText("");
            labelV.setText("");
        }

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        if(restored == true)
            restored = false;
        else {
            onStart(null);
        }
    }

//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        restored = true;
//        super.onRestoreInstanceState(savedInstanceState);
//        TextView textV = (TextView) findViewById(R.id.ghostText);
//     TextView labelV = (TextView) findViewById(R.id.gameStatus);
//        // Restore state members from saved instance
//        labelV.setText(savedInstanceState.getString(GAME_STATUS));
//        textV.setText(savedInstanceState.getString(WORD));
//    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        TextView textV = (TextView) findViewById(R.id.ghostText);
        TextView labelV = (TextView) findViewById(R.id.gameStatus);

        savedInstanceState.putString(GAME_STATUS,(String)labelV.getText());
        savedInstanceState.putString(WORD,(String)textV.getText());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
//        userTurn = true;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            Button b = (Button) findViewById(R.id.button1);
            b.setEnabled(true);
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("onKeyUp", "userTurn is "+userTurn);
        if(userTurn==false){
            return false;
        }
        String s= "";
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView text1 = (TextView) findViewById(R.id.gameStatus);
        char c = (char)event.getUnicodeChar();
        if(Character.isLetter(c)){
            s = (String) text.getText()+c;
            text.setText(s);
            computerTurn();
         //   if(dictionary.isWord(s)){
                //text1.setText(s+" is a word!");
           // }
            return true;
        }
//        return false;
        return super.onKeyUp(keyCode, event);
        }



    public void clickedRestart(View view){

        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        text = (TextView) findViewById(R.id.gameStatus);
        text.setText("Starting game");
        onStart(null);

    }


    public void clickedChallenge(View view){
        TextView word = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        String s = (String)word.getText();
        if(s.length()>=4) {
            userTurn = false;
            if (dictionary.isWord(s)) {
                label.setText("User challenges! The prefix is a word! User wins!");
            } else if (dictionary.getAnyWordStartingWith(s) != null) {
                label.setText("User challenges but the fragment is actually a prefix of a word! The word " +
                        dictionary.getAnyWordStartingWith(s) + " can be formed. Computer wins!");
            } else if (dictionary.getAnyWordStartingWith(s) == null) {
                label.setText("User challenges the word successfully! User wins!");
            }
        }
    }
    private  void computerTurn() {
        counter ++;
        Log.d("Inside Computer Turn", "counter: " +counter);
         TextView label = (TextView) findViewById(R.id.gameStatus);
         TextView word = (TextView) findViewById(R.id.ghostText);
        // Do computer turn stuff then make it the user's turn again
        final Button b = (Button) findViewById(R.id.button1);
        b.setEnabled(false);
                userTurn = false;
        Log.d("computerTurn", "Setting userTurn to  "+ userTurn);
                label.setText(COMPUTER_TURN);
                String s = (String) word.getText();
        Log.d("computerTurn", "S is  "+ s);
                //Log.d("TAG", s);
                int x = s.length();
                if (s.length() >= 4 && dictionary.isWord(s)) {
                    Log.d("Is a word", "computerTurn:inside if ");
                    label.setText(s + " is a word! Computer declares victory!");
                    handler.removeCallbacks(run);
                    return;
//                } else if (s.length() > 0 && dictionary.getAnyWordStartingWith(s) != null) {
                } else if (s.length() > 0 && dictionary.getGoodWordStartingWith(s) != null) {
//                    s = dictionary.getAnyWordStartingWith(s);
                    s = dictionary.getGoodWordStartingWith(s);
                    s = s.substring(0,x+1);
                    Log.d("Else if", "returned s is " + s);
                    //word.setText(s);
//                } else if (s.length() > 0 && dictionary.getAnyWordStartingWith(s) == null) {
                } else if (s.length() > 0 && dictionary.getGoodWordStartingWith(s) == null) {
                    Log.d("2nd Else if", "returned s is " + s);
                    label.setText("Computer challenges! " + s + " is not a prefix of a word! Computer declares victory!");
                    handler.removeCallbacks(run);
                    return;
                } else if (s.length() == 0) {
//                    s = dictionary.getAnyWordStartingWith("");
                    s = dictionary.getGoodWordStartingWith("");
                    Log.d("3rd Else if", "returned s is " + s + " ");
                    char c = s.charAt(0);
                    s = c + "";
                    //word.setText(s);
                } else {
                    int n = s.length();
//                    s = dictionary.getAnyWordStartingWith(s);
                    s = dictionary.getGoodWordStartingWith(s);
                    s = s.substring(0,x+1);
                    //word.setText(s);
                    Log.d("Else", "returned s is " + s.charAt(n));
                }

//        final String k = s;
        computerWord = s;
        run = new Runnable() {
            // @Override
         public void run() {
             TextView label = (TextView) findViewById(R.id.gameStatus);
             TextView word = (TextView) findViewById(R.id.ghostText);
             word.setText(computerWord);
             userTurn = true;
             Log.d("computerTurn", "Setting userTurn to  "+ userTurn);
             label.setText(USER_TURN);
             b.setEnabled(true);
            }};
        handler.postDelayed(run,2700); //adds it to queue and starts the run
    }
}
