package me.aflak.bluetoothterminal;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class chooseNoteActivity extends AppCompatActivity {
    int freqChosen;
    String name = "Disconnected";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_note);
        String getExtra1 = getIntent().getStringExtra("value1");
        String getExtra2 = getIntent().getStringExtra("bt");
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        Button btn5 = (Button) findViewById(R.id.btn5);
        if(getExtra1!= null) {
            freqChosen = Integer.parseInt(getExtra1);
            if(freqChosen==100){
                prepareFrequency(100);
                setAllBlank();
                btn1.setBackgroundColor(Color.BLUE);
                btn1.setTextColor(Color.WHITE);
            }
            else if(freqChosen==200){
                prepareFrequency(200);
                setAllBlank();
                btn2.setBackgroundColor(Color.BLUE);
                btn2.setTextColor(Color.WHITE);
            }
            else if(freqChosen==300){
                prepareFrequency(300);
                setAllBlank();
                btn3.setBackgroundColor(Color.BLUE);
                btn3.setTextColor(Color.WHITE);
            }
            else if(freqChosen==400){
                prepareFrequency(400);
                setAllBlank();
                btn4.setBackgroundColor(Color.BLUE);
                btn4.setTextColor(Color.WHITE);
            }
            else if(freqChosen==0){
                prepareFrequency(0);
                setAllBlank();
                btn5.setBackgroundColor(Color.BLUE);
                btn5.setTextColor(Color.WHITE);
            }
        }
        if(getExtra2!= null) {
            name = getExtra2;
        }
    }
    //GCEA
    public void setFreq1(View view){
        prepareFrequency(100);
        setAllBlank();
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setBackgroundColor(Color.BLUE);
        btn1.setTextColor(Color.WHITE);
    }
    public void setFreq2(View view){
        prepareFrequency(200);
        setAllBlank();
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setBackgroundColor(Color.BLUE);
        btn2.setTextColor(Color.WHITE);
    }
    public void setFreq3(View view){
        prepareFrequency(300);
        setAllBlank();
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setBackgroundColor(Color.BLUE);
        btn3.setTextColor(Color.WHITE);
    }
    public void setFreq4(View view){
        prepareFrequency(400);
        setAllBlank();
        Button btn4 = (Button) findViewById(R.id.btn4);
        btn4.setBackgroundColor(Color.BLUE);
        btn4.setTextColor(Color.WHITE);
    }
    public void setFreq5(View view){
        prepareFrequency(0);
        setAllBlank();
        Button btn5 = (Button) findViewById(R.id.btn5);
        btn5.setBackgroundColor(Color.BLUE);
        btn5.setTextColor(Color.WHITE);
    }

    private void prepareFrequency(int freq){
        freqChosen = freq;
    }

    private void setAllBlank(){
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setBackgroundColor(Color.TRANSPARENT);
        btn3.setTextColor(Color.BLACK);
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1.setTextColor(Color.BLACK);
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setBackgroundColor(Color.TRANSPARENT);
        btn2.setTextColor(Color.BLACK);
        Button btn4 = (Button) findViewById(R.id.btn4);
        btn4.setBackgroundColor(Color.TRANSPARENT);
        btn4.setTextColor(Color.BLACK);
        Button btn5 = (Button) findViewById(R.id.btn5);
        btn5.setBackgroundColor(Color.TRANSPARENT);
        btn5.setTextColor(Color.BLACK);
    }

    public void done(View view){
        String note = null;
        if(freqChosen == 100){
            note = "NC";
        }
        else if(freqChosen == 200){
            note = "NE";
        }
        else if(freqChosen == 300){
            note = "NG";
        }
        else if(freqChosen == 400){
            note = "NA";
        }
        String msg = "S" + note + "E";
        if(note != null){
            Chat.b.send(msg);
        }
        Intent intent = new Intent(this, StartPage.class);
        intent.putExtra("value1", String.valueOf(freqChosen));
        Log.d("myTag", String.valueOf(freqChosen));
        intent.putExtra("bt", name);
        Log.d("myTag", name);
        startActivity(intent);
    }
}
