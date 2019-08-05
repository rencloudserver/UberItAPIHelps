package com.example.ausbookingappclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity implements View.OnClickListener {
    EditText phoneNumber, verificationCode;
    String phoneNo, verifyCode, verificationId;
    private FirebaseAuth mAuth;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");
        setContentView(R.layout.activity_verification);
        findViewById(R.id.submitNumber).setOnClickListener(this);
        findViewById(R.id.submitCode).setOnClickListener(this);
        phoneNumber = findViewById(R.id.addNumberBox);
        verificationCode = findViewById(R.id.addVerificationCodeBox);
        activity = this;

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submitNumber:
                submitNumber();
                break;
            case R.id.submitCode:
                submitCode();
                break;
        }
    }

    void submitCode() {
        if(verificationCode.getText() != null) {
            verifyCode = verificationCode.getText().toString();
            verifyVerificationCode(verifyCode);
        }else{
            verificationCode.setText("enter valid number");
        }
    }

    void submitNumber() {
        if(phoneNumber.getText() != null) {
            phoneNo = phoneNumber.getText().toString();
            Toast.makeText(Verification.this, phoneNo, Toast.LENGTH_SHORT).show();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+61777888999",
                    60,
                    TimeUnit.SECONDS,
                    activity,
                    mCallbacks
            );
            verificationCode.setText("987321");
        }else{
            phoneNumber.setText("enter valid number");
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    //String code = phoneAuthCredential.getSmsCode();
                        phoneNumber.setText("987321");
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.e("firebase", Objects.requireNonNull(e.getLocalizedMessage()));
                    Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                }
            };

    void verifyVerificationCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Verification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(Verification.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            String message = "verification failed";
                            if(task.getException() instanceof
                            FirebaseAuthInvalidCredentialsException){
                                message = "invalid code";
                            }
                        }
                    }
                });
    }
}
