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
                if (loginemail.getText().toString().equals("user") && loginpassword.getText().toString().equals("1234")) {
                    Toast.makeText(Login_jav.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_jav.this, Index_jav.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login_jav.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}