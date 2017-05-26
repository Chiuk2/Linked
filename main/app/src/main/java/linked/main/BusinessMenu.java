package linked.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * Created by Kyle on 3/27/2017.
 */

public class BusinessMenu extends AppCompatActivity implements View.OnClickListener{

    /* We need to get a reference to the User who logged in, in order to get the correct information
       from the Database.
      ******* Requires interactions with the Database.
     */

    /*
    In reply above,
        //An example to access current user's information
        FirebaseUser current_user;
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null){
            String name = current_user.getDisplayName();
            String email = current_user.getEmail();
            Uri photoUrl = current_user.getPhotoUrl;
            String uid = current_user.getUid();           // The user's ID, unique to the Firebase project.
     }
     */

    //protected Button selectActivitiesBtn;
    protected TextView businessName;
    protected TextView businessAddress;
    protected String[] activity = {};                           //******Retrieved Activities from DataBase in String Array GOES HERE
    protected ImageView selectActivitiesBtn, editBusinessBtn, infoButton, signout;

    protected ArrayList<String> selectedActivities = new ArrayList<>();
    private String nameText = static_variable_CLASS.bname;                              //******Variable to hold Business Name
    private String addressText = static_variable_CLASS.baddress;                           //******Variable to hold Address Name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_menu);


        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pathTObusiness = root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID).child("activity_List");
        pathTObusiness.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("No activities")){
                    Log.v("activities", "NONE");
                }
                else {
                    List<String> current_activites = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot c : children) {
                        current_activites.add(c.getKey().toString());
                        selectedActivities.add(c.getKey().toString());
                    }
                    activity = current_activites.toArray(new String[current_activites.size()]);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nameText =  static_variable_CLASS.bname;                    //******Retrieve Business Name from Database HERE
        addressText = static_variable_CLASS.baddress;               //******Retrieve Business Address from Database HERE


        /*businessName = (TextView) findViewById(R.id.businessName);
        businessAddress = (TextView) findViewById(R.id.businessAddress);

        businessName.setText(nameText);
        businessAddress.setText(addressText);*/

        editBusinessBtn = (ImageView) findViewById(R.id.editImageView);
        editBusinessBtn.setOnClickListener(this);

        selectActivitiesBtn = (ImageView) findViewById(R.id.activitiesImageView);
        selectActivitiesBtn.setOnClickListener(this);

        infoButton = (ImageView) findViewById(R.id.infoImageView);
        infoButton.setOnClickListener(this);

        signout = (ImageView) findViewById(R.id.signoutImageView);
        signout.setOnClickListener(this);


        for (int i = 0; i < activity.length; i++) {
            selectedActivities.add(activity[i]);
        }

    }


    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.activitiesImageView:
                showSelectActivitiesDialog();
                break;
            case R.id.infoImageView:
                showViewInfoDialog();
                break;
            case R.id.editImageView:
                try {
                    showEditBusinessDialog();
                    break;
                }
                catch(IOException e){
                    e.printStackTrace();
                }

            case R.id.signoutImageView:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(BusinessMenu.this, Start.class));
                break;
            default:
                break;

        }
    }

    // For the dialogs, we need to change the CheckBoxes to buttons because right now
    // it works backwards (when we uncheck box, it deletes it)

    //Popup Window for Editing Business Name and Address
    protected void showEditBusinessDialog() throws IOException {
        AlertDialog.Builder editBusiness = new AlertDialog.Builder(this);


        final EditText nameInput = new EditText(this);
        final EditText addressInput = new EditText(this);
        nameInput.setHint("Enter new Business Name here...");
        addressInput.setHint("Enter new Address here...");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameInput);
        layout.addView(addressInput);
        editBusiness
                .setView(layout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tName="", tAddress="";
                        tName = nameInput.getText().toString().trim();
                        tAddress = addressInput.getText().toString().trim();
                        if(!tName.isEmpty()){
                            nameText = tName;                   //******Update Business Name in Database HERE
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                            root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID).child("business_name").setValue(nameText);
                            businessName.setText(nameText);
                        }
                        if(!tAddress.isEmpty()){
                            addressText = tAddress;             //******Update Business Address in Database HERE
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                            root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID).child("business_address").setValue(addressText);

                            //update lat/long
                            Geocoder geocoder = new Geocoder(BusinessMenu.this);
                            try {
                                List<Address> addresses = geocoder.getFromLocationName(addressText,1);
                                if(addresses.size() > 0)
                                {
                                    String latitude = String.valueOf(addresses.get(0).getLatitude());
                                    String longitude =String.valueOf(addresses.get(0).getLongitude());
                                    root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID).child("latitude").setValue(latitude);
                                    root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID).child("longitude").setValue(longitude);
                                }
                                else{
                                    Toast.makeText(BusinessMenu.this, R.string.invalid_address, Toast.LENGTH_LONG).show();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            businessAddress.setText(addressText);
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = editBusiness.create();
        dialog.show();
    }


    //Popup Window for View/Edit Activities
    protected void showSelectActivitiesDialog() {

        boolean[] checkedActivities = new boolean[activity.length];
        int count = activity.length;

        for(int i = 0; i < count; i++)
            checkedActivities[i] = !selectedActivities.contains(activity[i]);

        final DialogInterface.OnMultiChoiceClickListener activityDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (!isChecked)
                    selectedActivities.add(activity[which]);
                else {
                    selectedActivities.remove(activity[which]);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Select Activities for Deletion");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter new Activity here...");
        builder
                .setMultiChoiceItems(activity, checkedActivities, activityDialogListener)
                .setView(input)
                .setNeutralButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tAct = input.getText().toString().trim();
                        if(!tAct.isEmpty()) {
                            selectedActivities = new ArrayList<>();
                            for (int i = 0; i < activity.length; i++) {
                                selectedActivities.add(activity[i]);
                            }
                            selectedActivities.add(tAct);
                            activity = new String[selectedActivities.size()];
                            activity = selectedActivities.toArray(activity);
                            //******Store Updated Array of Activities to go to Database GOES HERE
                            List<String> db_activity_list = new ArrayList<String>(Arrays.asList(activity));
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference pathTObusiness = root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID);

                            if(db_activity_list.size() == 0)
                                pathTObusiness.child("activity_List").setValue("No activities");
                            else
                                for(int i =0; i < db_activity_list.size(); i++) {
                                    pathTObusiness.child("activity_List").child(db_activity_list.get(i)).child("Anonymous").setValue("Null");
                                }
                        }
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        activity = new String[selectedActivities.size()];
                        activity = selectedActivities.toArray(activity);
                        //******Store Updated Array of Activities into Database GOES HERE
                        List<String> db_activity_list = new ArrayList<String>(Arrays.asList(activity));
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference pathTObusiness = root.child("Business_Accounts").child("User_ID").child(static_variable_CLASS.User_ID);
                        if(db_activity_list.size() == 0)
                            pathTObusiness.child("activity_List").setValue("No activities");
                        else {
                            pathTObusiness.child("activity_List").removeValue();
                            for(int i =0; i < db_activity_list.size(); i++) {
                                pathTObusiness.child("activity_List").child(db_activity_list.get(i)).child("Anonymous").setValue("Null");
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedActivities = new ArrayList<>();
                        for (int i = 0; i < activity.length; i++) {
                            selectedActivities.add(activity[i]);
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void showViewInfoDialog() {
        AlertDialog.Builder viewInfo = new AlertDialog.Builder(this);
        viewInfo.setTitle("My Business");
        viewInfo
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })

                .setMessage(nameText + "\n\n" + addressText);

        AlertDialog dialog = viewInfo.create();
        dialog.show();
    }
}