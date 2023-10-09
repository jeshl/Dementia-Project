package com.example.helpdementia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Dashboard extends Activity {
    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.dashboard);

        TextView getstart, dashboardtxt;
        getstart = findViewById(R.id.getstart);
        dashboardtxt = findViewById(R.id.dashboardtxt);

        dashboardtxt.setText("Help Dementia");
        dashboardtxt.setText("Always Remember !! The Dementia Patient Is Not Giving You The Hard Time. \nThey Are Having The Hard Time !!");

        getstart.setOnClickListener(view -> {
          startActivity(new Intent(this, Login_jav.class));;

        });
    }
}
