package com.example.helpdementia;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Sign_up extends Activity {
    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.sign_up);

        TextView logintxt, fname, signemail, signpassword, repassword;
        Button signbtn;


        signbtn = findViewById(R.id.signbtn);
        logintxt = findViewById(R.id.logintxt);
        fname = findViewById(R.id.fname);
        signemail = findViewById(R.id.signemail);
        signpassword = findViewById(R.id.signpassword);
        repassword = findViewById(R.id.repassword);

        logintxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login_jav.class);
            startActivity(intent);
        });

        signbtn.setOnClickListener(view -> {
            String uname = fname.getText().toString();
            String uemail = signemail.getText().toString();
            String upassword = signpassword.getText().toString();
            String repass = repassword.getText().toString();
            Database db = new Database(getApplicationContext(),"Help_Dementia",null,1);
            if(uname.length()==0|| uemail.length()==0 || upassword.length()==0 || repass.length()==0 )
            {
                Toast.makeText(this, "please fill all details", Toast.LENGTH_SHORT).show();
            }
            if(upassword.compareTo(repass)==0)
            {
                if(upassword.length()>8)
                {
                    db.Sign_up(uname,uemail,upassword);
                    Toast.makeText(this, "Registerd Sucessfully ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Login_jav.class));
                }
                else {
                    Toast.makeText(this, "your password must contains 8 character", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "password didn't match", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
