package com.example.loginsignup;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;


public class MainActivity extends AppCompatActivity {

    EditText user,pass,email;
    Button login,register;
    String uname;
    int i = 0;
    String api_url = "http://192.168.1.138/LAMP/androidlog/api.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (EditText) findViewById(R.id.editName);
        pass = (EditText) findViewById(R.id.editPassword);
        email = (EditText) findViewById(R.id.editEmail);

        login = (Button) findViewById(R.id.btnSignIn);
        register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    email.setVisibility(View.VISIBLE);
                    login.setText("Back");
                    register.setText("Create New Account");
                    i = 1;
                } else {

                    //Exec Registration
                    if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
                    } else {
                        ExecCalls exec = new ExecCalls() {
                            protected void onPostExecute(JSONObject json) {
                                try {
                                    if (json != null) {
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                        if (json.getString("success").equals("1")) {
                                            email.setVisibility(View.GONE);
                                            login.setText("Sign In");
                                            register.setText("Register");
                                            i = 0;
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        String hashedPass = getMd5(pass.getText().toString());
                        exec.execute(user.getText().toString(), hashedPass, email.getText().toString());

                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 1) {
                    email.setVisibility(View.GONE);
                    login.setText("Sign In");
                    register.setText("Register");
                    i = 0;
                } else {
                    if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
                    } else {
                        uname = user.getText().toString();
                        //Exec Login
                        ExecCalls exec = new ExecCalls() {
                            protected void onPostExecute(JSONObject json) {
                                try {
                                    if (json != null) {
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                        if (json.getString("success").equals("1")) {
                                            //Successful login

                                            Intent intent = new Intent(MainActivity.this, loggedin.class);
                                            intent.putExtra("user",uname);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        String hashedPass = getMd5(pass.getText().toString());
                        exec.execute(user.getText().toString(), hashedPass, "");
                    }
                }
            }
        });
    }

    public static String getMd5(String input)
    {
        String hashtext = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return hashtext;
    }

    private class ExecCalls extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args){
            JSONObject json = null;
            try {
                URL url = new URL(api_url);
                String preData = "user=" + args[0] + "&pass=" + args[1] + "&email=" + args[2];
                byte[] postDataBytes = preData.getBytes(Charset.forName("UTF-8"));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.getOutputStream().write(postDataBytes);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = in.readLine()) != null) {
                    sb.append(output);
                }
                String res = sb.toString();
                json = new JSONObject(res);


            }catch(Exception e){
                e.printStackTrace();
            }
            return json;
        }

    }
}


