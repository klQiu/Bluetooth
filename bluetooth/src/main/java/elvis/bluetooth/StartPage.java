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
    public static String instrumrnt = "---";
    public static boolean insUpdated = false;
    public static int frequency = 0;
    public static String name = "Disconnected";
    private static final String TAG = "StartPage";
    public static boolean connected = false;
    public static String tuning = "---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        TextView t = (TextView)findViewById(R.id.ins);
        t.setText(String.valueOf(instrumrnt));
        TextView tu = (TextView)findViewById(R.id.button2);
        tu.setText(String.valueOf(tuning));

        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn4 = (Button) findViewById(R.id.ins);
        btn4.setEnabled(false);
        if(connected == false){
            btn3.setEnabled(false);
            btn2.setEnabled(false);
        }
        else{
            btn3.setEnabled(true);
            btn2.setEnabled(true);
        }
        //for test, should be deleted!!!!1
        btn2.setEnabled(true);
    }
    public void chooseNote(View view){
        Intent intent = new Intent(this, ListTuneActivity.class);
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
        String msg = "S" + note + "E";
        Chat.b.send(msg);
    }

}