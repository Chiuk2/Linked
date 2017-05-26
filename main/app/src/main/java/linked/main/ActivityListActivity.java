package linked.main;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.security.AccessController.getContext;

/**
 * Created by Kyle on 4/9/2017.
 */

public class ActivityListActivity extends AppCompatActivity{
   // private List<String> activities = new ArrayList<>();                         ***List variable for the list of activities from location
    private String BusinessName;
    public static List<String> people = new ArrayList<>();          //List of people under activity
    public static List<String> people_key = new ArrayList<>();          //List of people under activity
    private static final String TAG = "CardFragment";
    //***Assign variable to value retreived from Database
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Business_Accounts").child("User_ID");
    private String BusinessKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        Bundle extra = getIntent().getExtras();
        BusinessName = extra.getString("NAME");
        BusinessKey = extra.getString(BusinessName);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activities offered by " + BusinessName);



        View recyclerView = findViewById(R.id.person_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(CardFragment.activity));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<String> mValues;

        public SimpleItemRecyclerViewAdapter(List<String> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {

            holder.mContentView.setText(mValues.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                        pass info of people in the activities here to create holder for person
                     */
                    root.child(BusinessKey).child("activity_List").addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            people.clear();
                            people_key.clear();

                            for (DataSnapshot snapshot : dataSnapshot.child(CardFragment.activity.get(position)).getChildren()) {
                                if ( !Objects.equals(snapshot.getKey(),"Anonymous")) {
                                    Log.d(TAG, "Key is " + snapshot.getKey());
                                    people.add((String) snapshot.getKey());  //add list of people under the activity atm
                                    people_key.add((String)snapshot.getValue());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent i = new Intent(v.getContext() ,PersonListActivity.class);
                    i.putExtra("activity", CardFragment.activity.get(position));
                    i.putExtra("key", BusinessKey);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
