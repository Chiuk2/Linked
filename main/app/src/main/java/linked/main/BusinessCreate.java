package linked.main;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusinessCreate extends AppCompatActivity implements View.OnClickListener{

    // Layout Declarations
    EditText fullname, email, business, address, password, confirm;
    ImageButton img_button;
    Button submit_button;
    Uri uri;    //store image
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();


    // Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener  mAuthListener;

    private String longitude, latitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_create);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    Log.d("EmailPassword", "onAuthStateChanged:signed_in:" + user.getUid());
                else
                    Log.d("EmailPassword", "onAuthStateChanged:signed_out");
            }
        };

        // Layout
        fullname = (EditText) findViewById(R.id.emailText2);
        email = (EditText) findViewById(R.id.cPassText2);
        business = (EditText) findViewById(R.id.nameText2);
        address = (EditText) findViewById(R.id.passText2);
        password = (EditText) findViewById(R.id.passText);
        confirm = (EditText) findViewById(R.id.cPassText);
        img_button = (ImageButton) findViewById(R.id.imageButton2);
        submit_button = (Button) findViewById(R.id.submitButton);


        // When a click event happens, onClick method is called (implements the interface)
        img_button.setOnClickListener(this);
        submit_button.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View v) {
        if (v == img_button) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
        else if (v == submit_button) {
            if (!boxEmpty() && !invalidAddress(address.getText().toString().trim())){
                try {
                    businessCreateAuth();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //store image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            uri = data.getData();
            try {
                img_button.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean invalidAddress(String address){
        //converting addresses into Lat/Long
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address , 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses.size() > 0) {
            latitude = String.valueOf(addresses.get(0).getLatitude());
            longitude = String.valueOf(addresses.get(0).getLongitude());
            return false;
        }
        else{
            Toast.makeText(this, R.string.invalid_address, Toast.LENGTH_LONG).show();
            return true;
        }

    }

    public boolean boxEmpty(){
        String owner = fullname.getText().toString().trim();
        String user = email.getText().toString().trim();
        String business_name = business.getText().toString().trim();
        String business_address = address.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String pass1 = confirm.getText().toString().trim();

        //Error Check for empty boxes
        if (TextUtils.isEmpty(owner) || TextUtils.isEmpty(user) || TextUtils.isEmpty(business_name) ||
                TextUtils.isEmpty(business_address) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(pass1)) {
            Toast.makeText(BusinessCreate.this, R.string.empty_field, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (!pass.equals(pass1)) {
            Toast.makeText(BusinessCreate.this, R.string.pw_mismatch, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void businessCreateAuth() throws IOException{
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(BusinessCreate.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("EmailPassword", "createUserWithEmail:failed:" + task.getException());
                            Toast.makeText(BusinessCreate.this, R.string.create_user_failed, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("EmailPassword", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            try {
                                businessCreateDB(task.getResult().getUser());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void businessCreateDB(FirebaseUser user)throws IOException{
        // Database
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        Business_CLASS newBusiness = new Business_CLASS();

        All_GENERIC newAccount = new All_GENERIC();
        newAccount.account_type = "Business";
        newBusiness.owner = fullname.getText().toString().trim();

        newBusiness.emailaddress = email.getText().toString().trim();
        newAccount.emailaddress = email.getText().toString().trim();

        newBusiness.business_name = business.getText().toString().trim();
        static_variable_CLASS.bname = business.getText().toString().trim();  //store business name in static field

        newBusiness.business_address = address.getText().toString();
        static_variable_CLASS.baddress = address.getText().toString().trim();  //store business address in static field

        newBusiness.password = password.getText().toString().trim();

        newBusiness.longitude = longitude;
        newBusiness.latitude = latitude;

        //Test activity list

        Map<String, Object> business_info = new HashMap<String, Object>();
        business_info.put(user.getUid(), newBusiness);

        Map<String, Object> account_info = new HashMap<String, Object>();
        account_info.put(user.getUid(), newAccount);

        //storing the User_ID in a static variable so it can be accessed from any page
        static_variable_CLASS.User_ID = user.getUid();

        root.child("Business_Accounts").child("User_ID").updateChildren(business_info);
        root.child("Business_Accounts").child("User_ID").child(user.getUid()).child("activity_List").setValue("No activities");

        root.child("All_Accounts_Basic_Info").child("User_ID").updateChildren(account_info);

        startActivity(new Intent(BusinessCreate.this, BusinessMenu.class));
    }

}





