package com.quaranfood.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.starter.R;

public class Login extends AppCompatActivity {
    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), ViewRequestsActivity.class);
        startActivity(intent);
    }

public void onClick (View view)
{

    EditText usernameEditText = (EditText) findViewById(R.id.editText);
    EditText passwordEditText = (EditText) findViewById(R.id.editText2);

    ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
            if (user != null) {
                Log.i("Login","ok!");
                showUserList();
            } else {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
    }
}
