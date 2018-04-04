package elvis.bluetooth;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class chooseGuitarActivity extends AppCompatActivity {
    EditText a;
    EditText b;
    EditText c;
    EditText d;
    EditText e;
    EditText g;
    EditText name;
    String msg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_guitar);
        a = (EditText) findViewById(R.id.note_a);
        b = (EditText) findViewById(R.id.note_b);
        c = (EditText) findViewById(R.id.note_c);
        d = (EditText) findViewById(R.id.note_d);
        e  = (EditText) findViewById(R.id.note_e);
        g  = (EditText) findViewById(R.id.note_g);
        name  = (EditText) findViewById(R.id.name);
    }

    public void done(View view){
        msg = BTcombine(g.getText().toString(),c.getText().toString(),e.getText().toString(),a.getText().toString(), b.getText().toString(), d.getText().toString());
        System.out.println(msg);
        if(msg != null){
            Chat.b.send(msg);
        }
        Intent intent = new Intent(this, ListTuneActivity.class);
        startActivity(intent);
    }

    public void save(View view){
        msg = combine(g.getText().toString(),c.getText().toString(),e.getText().toString(),a.getText().toString(), b.getText().toString(), d.getText().toString());
        System.out.println(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Current.getCurUserEmail());
                BackEnd.addTuning(Current.getCurUserEmail(),"guitar",msg, name.getText().toString());
            }
        }).start();
        Intent intent = new Intent(this, ListTuneActivity.class);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this, ListTuneActivity.class);
        startActivity(intent);
    }

    private String BTcombine(String g, String c, String e, String a, String b, String d){
        String note = "6S" + e + a + c + g + b + d + "E";
        return note.toUpperCase();
    }

    private String combine(String g, String c, String e, String a, String b, String d){
        String note = e + " " + a + " " +  c + " " +  g + " " +  b + " " +  d ;
        return note.toUpperCase();
    }
}
