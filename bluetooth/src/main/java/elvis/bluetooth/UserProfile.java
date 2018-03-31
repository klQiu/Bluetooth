package elvis.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        TextView t1 = (TextView)findViewById(R.id.email);
        TextView t2 = (TextView)findViewById(R.id.userId);
        TextView t3 = (TextView)findViewById(R.id.ins);
        TextView t4 = (TextView)findViewById(R.id.name);
        TextView t5 = (TextView)findViewById(R.id.freq);
        t1.setText(String.valueOf(Current.getCurUserEmail()));
        t2.setText(String.valueOf(Current.getCurUserID()));
        t3.setText(String.valueOf(StartPage.instrumrnt));
        t4.setText(String.valueOf(StartPage.name));
        t5.setText(String .valueOf(StartPage.frequency));
    }

    public void profileReturn(View view){
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
    }
}
