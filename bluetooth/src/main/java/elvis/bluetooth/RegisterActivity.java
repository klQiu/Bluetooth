package elvis.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    public PopupWindow warnWindow;
    User newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //adjust hint font for password
        EditText password1 = (EditText) findViewById(R.id.RegPswEt);
        EditText password2 = (EditText) findViewById(R.id.RegRePswEt);
        password1.setTypeface(Typeface.DEFAULT);
        password1.setTransformationMethod(new PasswordTransformationMethod());
        password2.setTypeface(Typeface.DEFAULT);
        password2.setTransformationMethod(new PasswordTransformationMethod());

        //add popup window for warning msg
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View warnLayout = inflater.inflate(R.layout.warn, null);
        warnWindow = new PopupWindow(warnLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
    }

    public void regAccout(View view){
        EditText emailEt = (EditText) findViewById(R.id.RegEmailEt);
        EditText pswEt = (EditText) findViewById(R.id.RegPswEt);
        EditText rePswEt = (EditText) findViewById(R.id.RegRePswEt);
        String email = emailEt.getText().toString();
        String psw = pswEt.getText().toString();
        String rePsw = rePswEt.getText().toString();

        if(!email.contains("@") || !email.contains(".")) {
            emailEt.setText("");
            pswEt.setText("");
            rePswEt.setText("");
            TextView warning = (TextView)warnWindow.getContentView().findViewById(R.id.warnText);
            warning.setText("Email format is not correct. Please try again.");
            warnWindow.showAtLocation(findViewById(R.id.regLayout), Gravity.TOP, 0, 0);
            warnWindow.update(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else if(!psw.equals(rePsw)){
            pswEt.setText("");
            rePswEt.setText("");
            TextView warning = (TextView)warnWindow.getContentView().findViewById(R.id.warnText);
            warning.setText("The passwords don't match.Please try again.");
            warnWindow.showAtLocation(findViewById(R.id.regLayout), Gravity.TOP, 0, 0);
            warnWindow.update(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else{
            String encryptedEmail = Encryption.encryption(email);
            String encryptedPsw = Encryption.encryption(psw);
            newUser = new User(encryptedEmail, encryptedPsw);
            final boolean[] success = new boolean[1];
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    success[0] = BackEnd.addUser(newUser);
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!success[0]){
                emailEt.setText("");
                pswEt.setText("");
                rePswEt.setText("");
                TextView warning = (TextView)warnWindow.getContentView().findViewById(R.id.warnText);
                warning.setText("The email has been used.");
                warnWindow.showAtLocation(findViewById(R.id.regLayout), Gravity.TOP, 0, 0);
                warnWindow.update(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return;
            }
            this.finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
