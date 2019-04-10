package com.abhinay.alumnies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class sign_up extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final String CHAT_PREFS = "ChatPrefs";
    static final String DISPLAY_NAME_KEY = "username";
    static final String DISPLAY_EMAIL_KEY = "emailId";
    static final String DISPLAY_PASSWORD_KEY = "password";

    private AutoCompleteTextView m_signUp_username;
    private AutoCompleteTextView m_signUp_emailId;
    private EditText m_signUp_password;
    private EditText m_signUp_confirm_password;
    private EditText m_current_city;
    private EditText m_branch;
    private Boolean m_is_alumni;
    private Spinner spinner;
//    private RadioGroup m_radio_group;
//    private RadioButton alumni_button;
//    private RadioButton student_button;

    private String branchSelected;
    private String alumniOrStrudent = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        m_signUp_username = (AutoCompleteTextView) findViewById(R.id.signUp_username);
        m_signUp_emailId = (AutoCompleteTextView) findViewById(R.id.signUp_emailId);
        m_signUp_password = (EditText) findViewById(R.id.signUp_password);
        m_signUp_confirm_password = (EditText) findViewById(R.id.signUp_confirm_password);
        m_current_city = (EditText) findViewById(R.id.current_city);

//        m_radio_group = (RadioGroup) findViewById(R.id.radioButtonGroup);
//        alumni_button = (RadioButton) findViewById(R.id.alumniButton);
//        student_button = (RadioButton) findViewById(R.id.studentButton);

        spinner = (Spinner) findViewById(R.id.branch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpClick(View v){
        attemptRegistration();
    }

    public void attemptRegistration(){
        m_signUp_emailId.setError(null);
        m_signUp_password.setError(null);
        m_current_city.setError(null);

        String email = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();
        String city = m_current_city.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!TextUtils.isEmpty(password) && !isPasswordValid(password)){
            m_signUp_password.setError("Password too short or does not match");
            focusView = m_signUp_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            m_signUp_emailId.setError("This field is required");
            focusView = m_signUp_emailId;
            cancel = true;
        } else if (!isEmailValid(email)) {
            m_signUp_emailId.setError("This email address is invalid");
            focusView = m_signUp_emailId;
            cancel = true;
        }

        if(TextUtils.isEmpty(city)){
            m_current_city.setError("Please enter your city name");
            focusView = m_current_city;
            cancel = true;
        }

        if(alumniOrStrudent == null){
            showErrorDialog("please select 'alumni' or 'student'");
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();

        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = m_signUp_confirm_password.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4;
    }

    private void createFirebaseUser(){
        String email = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FlashChat", "createUser onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d("FlashChat", "user creation failed");
                            showErrorDialog("Registration attempt failed");
                        } else {
                            saveDisplayName();
                            Intent intent = new Intent(sign_up.this, sign_in.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });

    }

    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()){
            case R.id.alumniButton:
                if(checked){
                    alumniOrStrudent = "alumni";
                    Log.d("radio", alumniOrStrudent);
                }
                break;

            case R.id.studentButton:
                if(checked){
                    alumniOrStrudent = "student";
                    Log.d("radio", alumniOrStrudent);
                }
                break;
        }
    }

    private void saveDisplayName() {
        String displayName = m_signUp_username.getText().toString();
        String emailId = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
        prefs.edit().putString(DISPLAY_EMAIL_KEY, emailId).apply();
        prefs.edit().putString(DISPLAY_PASSWORD_KEY, password).apply();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                branchSelected = "Electronics";
                break;
            case 1:
                branchSelected = "Computer Science";
                break;
            case 2:
                branchSelected = "Mechanical";
                break;
            case 3:
                branchSelected = "Civil";
                break;
            case 4:
                branchSelected = "Petroleum";
                break;
            case 5:
                branchSelected = "Chemical";
                break;
        }
        Log.d("spinner", branchSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
