package linked.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Start extends AppCompatActivity implements View.OnClickListener {

    //Layout Declarations
    EditText username, password;
    Button register_button, login_button;
    ImageView logo_button;
    MediaPlayer mp;

    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Used for Firebase Analytics
                if (user != null)
                    Log.d("EmailPassword", "onAuthStateChanged:signed_in:" + user.getUid());
                else
                    Log.d("EmailPassword", "onAuthStateChanged:signed_out");
            }
        };

        //Layout
        username = (EditText) findViewById(R.id.usernameField);
        password = (EditText) findViewById(R.id.passwordField);
        register_button = (Button) findViewById(R.id.signupButton);
        login_button = (Button) findViewById(R.id.signinButton);
        logo_button = (ImageView) findViewById(R.id.Logo);
        mp = MediaPlayer.create(this, R.raw.bark);

        // When a click event happens, onClick method is called (implements the interface)
        register_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
        logo_button.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == register_button)
            startActivity(new Intent(Start.this, CreateAccount.class));
        else if (v == login_button)
                userLogin(v);
        else if(v == logo_button)
            mp.start();
    }

    private void userLogin(View view) {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        //Error check for empty boxes
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass))
            Toast.makeText(Start.this, R.string.empty_field, Toast.LENGTH_SHORT).show();
        else {
            // Create user with FirebaseAuth here
            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(Start.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("EmailPassword", "signInWithEmail:failed", task.getException());
                                Toast.makeText(Start.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("EmailPassword", "signInWithEmail:onComplete:" + task.isSuccessful());
                                static_variable_CLASS.User_ID = task.getResult().getUser().getUid();

                                final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference pathTOaccount = root.child("All_Accounts_Basic_Info").child("User_ID");

                                pathTOaccount.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                        for (DataSnapshot c : children){

                                            if(c.getKey().equals(static_variable_CLASS.User_ID)){
                                                if(c.child("account_type").getValue().equals("User")) {
                                                    startActivity(new Intent(Start.this, UserMenu.class));
                                                    break;
                                                }
                                                else {
                                                    DatabaseReference pathTobusiness = root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID);
                                                    pathTobusiness.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                                            for (DataSnapshot c : children) {
                                                                if(c.getKey().equals("business_name"))
                                                                    static_variable_CLASS.bname = c.getValue().toString();
                                                                if(c.getKey().equals("business_address"))
                                                                    static_variable_CLASS.baddress = c.getValue().toString();
                                                            }
                                                            startActivity(new Intent(Start.this, BusinessMenu.class));
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                //startActivity(new Intent(Start.this, BusinessMenu.class));
                                /*
                                    Check for account type through database
                                    String account_type = getAccountType();
                                    if (account_type == "business")
                                        startActivity(new Intent(Start.this, BusinessMenu.class));
                                    else if (account_type == "user")
                                        startActivity(new Intent(Start.this, UserMenu.class));
                                */
                            }
                        }
                    });
        }
    }
}


