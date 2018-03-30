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
        //String getExtra1 = getIntent().getStringExtra("value1");
        //String getExtra2 = getIntent().getStringExtra("bt");
        //if(getExtra1 != null) {
         //   frequency = Integer.parseInt(getExtra1);
         //   System.out.println(frequency);
         //   Log.v(TAG, getExtra1);
        TextView t=(TextView)findViewById(R.id.freq);
        t.setText(String.valueOf(frequency));
        TextView t3 = (TextView)findViewById(R.id.ins);
        t3.setText(String.valueOf(instrumrnt));
        //}

        Button btn3 = (Button) findViewById(R.id.button3);
        //Button btn4 = (Button) findViewById(R.id.cut);
        if(connected == false){
            btn3.setEnabled(false);
            //btn4.setEnabled(false);
            TextView t2 = (TextView)findViewById(R.id.name);
            t2.setText("Disconnected");
        }
        else{
            btn3.setEnabled(true);
            //btn4.setEnabled(true);
            //name = getExtra2;
            //Log.v(TAG, getExtra2);
            TextView t1 = (TextView)findViewById(R.id.name);
            t1.setText(name);

        }
        /*
        if(getExtra2 != null) {
            if(!getExtra2.equals("Disconnected")){
                name = getExtra2;
                Log.v(TAG, getExtra2);
                TextView t = (TextView)findViewById(R.id.name);
                t.setText(name);
                btn.setEnabled(true);
            }
        }
        */
    }
    public void chooseNote(View view){
        Intent intent = new Intent(this, chooseNoteActivity.class);
//        intent.putExtra("value1", String.valueOf(frequency));
//        Log.d("myTag", String.valueOf(frequency));
//        intent.putExtra("bt", name);
//        Log.d("myTag", name);
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
//        intent.putExtra("value1", String.valueOf(frequency));
//        Log.d("myTag", String.valueOf(frequency));
//        intent.putExtra("bt", name);
//        Log.d("myTag", name);
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

//    public void cutConnection(){
//        StartPage.connected = false;
//        Intent intent = new Intent(this, StartPage.class);
//        intent.putExtra("value1", String.valueOf(frequency));
//        Log.d("myTag", String.valueOf(frequency));
//        intent.putExtra("bt", name);
//        Log.d("myTag", name);
//        try {
//            // Using Thread.sleep() we can add delay in our
//            // application in a millisecond time. For the example
//            // below the program will take a deep breath for one
//            // second before continue to print the next value of
//            // the loop.
//            Thread.sleep(2000);
//
//            // The Thread.sleep() need to be executed inside a
//            // try-catch block and we need to catch the
//            // InterruptedException.
//        } catch (InterruptedException ie) {
//            ie.printStackTrace();
//        }
//        startActivity(intent);
//        Chat.b.removeCommunicationCallback();
//        Chat.b.disconnect();
//        Chat.b = null;
//    }
}