package com.example.helpdementia;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login_jav extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        EditText loginemail;
        EditText loginpassword;
        Button loginBtn;
        TextView signuptxt;


        loginemail = findViewById(R.id.loginemail);
        loginpassword = findViewById(R.id.loginpassword);
        loginBtn = findViewById(R.id.loginBtn);
        signuptxt = findViewById(R.id.signuptxt);


        signuptxt.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String uemail = loginemail.getText().toString();
//                String upassword = loginpassword.getText().toString();
//                Database db = new Database(getApplicationContext(), "Help_Dementia", null, 1);
//                if (uemail.length() == 0 || upassword.length() == 0) {
//                    Toast.makeText(Login_jav.this, "please fill username and password", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (db.Login_jav(uemail, upassword) == 1) {
//                        Toast.makeText(Login_jav.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(Login_jav.this, MainActivity.class));
//
//                        Intent intent = new Intent(Login_jav.this, MainActivity.class);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(Login_jav.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//
                startActivity(new Intent(Login_jav.this, MainActivity.class));


            }
        });

    }
}