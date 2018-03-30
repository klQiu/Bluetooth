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

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    PopupWindow popUpWindow;
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    String email;
    String password;
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //adjust hint font for password
        EditText password = (EditText) findViewById(R.id.passwordText);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        //add popup window for warning msg
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View warnLayout = inflater.inflate(R.layout.warn, null);
        popUpWindow = new PopupWindow(warnLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
    }

    public void login(View view) {
        EditText emailText = (EditText) findViewById(R.id.emailText);
        EditText passText  = (EditText) findViewById(R.id.passwordText);
        email = emailText.getText().toString();
        password = passText.getText().toString();
        if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            emailText.setText("");
            passText.setText("");
            //set warning msg
            TextView warning = (TextView)popUpWindow.getContentView().findViewById(R.id.warnText);
            warning.setText("Email format is not correct. Please try again.");
            popUpWindow.showAtLocation(findViewById(R.id.loginLayout), Gravity.TOP, 0, 0);
            popUpWindow.update(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return;
        }
        newUser = new User();
        final String encryptedEmail = Encryption.encryption(email);
        final String encryptedPassword = Encryption.encryption(password);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                User rUser = BackEnd.checkLogin(encryptedEmail, encryptedPassword);

                if(rUser != null) {
                    System.out.println("start");
                    newUser.setID(rUser.getID());
                    newUser.setEmail(Encryption.decryption(rUser.getEmail()));
                    newUser.setPassword(Encryption.decryption(rUser.getPassword()));
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(newUser.getEmail().equals("")) {
            emailText.setText("");
            passText.setText("");
            //set warning msg
            TextView warning = (TextView)popUpWindow.getContentView().findViewById(R.id.warnText);
            warning.setText("Email and Password don't match. Please try again.");
            popUpWindow.showAtLocation(findViewById(R.id.loginLayout), Gravity.TOP, 0, 0);
            popUpWindow.update(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else{
            /* Login success, safe to use the returned user object */

            //remember user for this session
            Current.addCurUser(newUser, getApplicationContext());
            this.finish();
            Intent intent = new Intent(this, StartPage.class);
            startActivity(intent);
        }
    }

    public void showReg(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void dismissWarn(View view) {
        popUpWindow.dismiss();
    }
}
