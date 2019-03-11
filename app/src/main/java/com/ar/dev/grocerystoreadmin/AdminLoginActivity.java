package com.ar.dev.grocerystoreadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etUsername = findViewById(R.id.etAdminUsername);
        etPassword = findViewById(R.id.etAdminPassword);
    }

    public void onClickedAdminLogin(View view) {

        if (TextUtils.isEmpty(etUsername.getText()) && TextUtils.isEmpty(etPassword.getText())) {
            etUsername.setError("Username is required");
            etPassword.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(etUsername.getText())) {
            etUsername.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Password is required");
            return;
        }

        if (etUsername.getText().toString().equals("admin") && etPassword.getText().toString().equals("admin")) {

            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }else{
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
        }
    }
}
