package com.example.helpdementia;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Sign_up extends Activity {
    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.sign_up);

        TextView logintxt;

        logintxt = findViewById(R.id.logintxt);

        logintxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login_jav.class);
            startActivity(intent);
        });

    }

}
