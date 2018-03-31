package elvis.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import me.aflak.bluetooth.Bluetooth;

public class StartPage extends AppCompatActivity {
    public static String instrumrnt = "Unknown";
    public static boolean insUpdated = false;
    public static int frequency = 0;
    public static String name = "Disconnected";
    private static final String TAG = "StartPage";
    public static boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        TextView t=(TextView)findViewById(R.id.freq);
        t.setText(String.valueOf(frequency));
        TextView t3 = (TextView)findViewById(R.id.ins);
        t3.setText(String.valueOf(instrumrnt));


        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn2 = (Button) findViewById(R.id.button2);
        if(connected == false){
            btn3.setEnabled(false);
            btn2.setEnabled(false);
            TextView t2 = (TextView)findViewById(R.id.name);
            t2.setText("Disconnected");
        }
        else{
            btn3.setEnabled(true);
            btn2.setEnabled(true);
            TextView t1 = (TextView)findViewById(R.id.name);
            t1.setText(name);

        }
    }
    public void chooseNote(View view){
        Intent intent = new Intent(this, chooseNoteActivity.class);
        startActivity(intent);
    }
    public void connect(View view){
        Intent intent = new Intent(this, Select.class);
        intent.putExtra("value1", String.valueOf(frequency));
        Log.d("myTag", String.valueOf(frequency));
        intent.putExtra("bt", name);
        Log.d("myTag", name);
        startActivity(intent);
    }
    public void choosePicture(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
    public void userProfile(View view){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }
    public void sendFrequency(View view){
        String note = "T";
//        if(frequency == 100){
//            note = "C";
//        }
//        else if(frequency == 200){
//            note = "E";
//        }
//        else if(frequency == 300){
//            note = "G";
//        }
//        else if(frequency == 400){
//            note = "A";
//        }
        String msg = "S" + note + "E";
        Chat.b.send(msg);
    }

}