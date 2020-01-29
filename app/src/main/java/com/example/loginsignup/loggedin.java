package com.example.loginsignup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class loggedin extends AppCompatActivity {
    TextView uGreet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);
        Intent intent = getIntent();
        String uname = intent.getStringExtra("user");
        uGreet = findViewById(R.id.userGreet);
        uGreet.append(uname);
    }
}
