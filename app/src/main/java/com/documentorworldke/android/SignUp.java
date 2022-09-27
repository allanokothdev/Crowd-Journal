package com.documentorworldke.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final Context mContext = SignUp.this;
    private ProgressBar progressBar;
    private RelativeLayout container;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputEditText repeatPasswordTextInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        try {
            TextView privacyTextView = findViewById(R.id.privacyTextView);
            privacyTextView.setOnClickListener(this);
            TextView loginTextView = findViewById(R.id.loginTextView);
            Button button = findViewById(R.id.button);
            progressBar = findViewById(R.id.progressBar);
            container = findViewById(R.id.container);
            emailTextInputEditText = findViewById(R.id.emailTextInputEditText);
            passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);
            repeatPasswordTextInputEditText = findViewById(R.id.repeatPasswordTextInputEditText);
            loginTextView.setOnClickListener(this);
            button.setOnClickListener(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.privacyTextView){
            String url = "https://documentyourworld.wordpress.com/civic-voices-privacy-policy/";
            Intent indie = new Intent(Intent.ACTION_VIEW);
            indie.setData(Uri.parse(url));
            startActivity(indie);
        } else if (view.getId()==R.id.loginTextView){
            startActivity(new Intent(mContext, com.documentorworldke.android.Login.class));
        } else if (view.getId()==R.id.button){
            String email = emailTextInputEditText.getText().toString();
            String password = passwordTextInputEditText.getText().toString();
            String repeatPassword = repeatPasswordTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTextInputEditText.setError("Enter Correct Email");
                Toast.makeText(mContext,"Enter Correct Email",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(password) || password.length()<6) {
                passwordTextInputEditText.setError("Enter Password");
                Toast.makeText(mContext,"Enter Strong Password",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }else if (TextUtils.isEmpty(repeatPassword) || repeatPassword.length()<6) {
                repeatPasswordTextInputEditText.setError("Repeat Password");
                Toast.makeText(mContext,"Repeat Password",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (!password.toLowerCase().equals(repeatPassword.toLowerCase())){
                repeatPasswordTextInputEditText.setError("Enter Matching Password");
                Toast.makeText(mContext,"Enter Matching Password",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                sendVerificationEmail();
                            } else {
                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                assert message != null;
                                Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }

    private void sendVerificationEmail(){
        try {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(mContext, com.documentorworldke.android.CreateUserProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }, 1000);
                    }else {
                        Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}