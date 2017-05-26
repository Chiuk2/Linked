package linked.main;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import linked.main.dummy.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An activity representing a list of People. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PersonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PersonListActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final String TAG = "CardFragment";
    public static List<String> info = new ArrayList<>();
    private boolean mTwoPane;
    private boolean isCheckIn = false;
    protected Button checkinBtn;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();


    private String selectedActivity;                         //***Name of Activity selected goes here
    private String key;                                      //Business Key
    private String UserName;                                 //userName of current user
                                                             //user's key store in the static_variable_CLASS.User_ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        Bundle extra = getIntent().getExtras();
        selectedActivity = extra.getString("activity");
        key = extra.getString("key");                       //business key

        //Get userName for register under the activity
       root.child("User_Accounts").child("User_ID").child(static_variable_CLASS.User_ID)
                .child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserName = (String)dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //toolbar layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Others interested in " + selectedActivity);


        //refresh button
         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.child("Business_Accounts").child("User_ID").child(key).child("activity_List")
                        .child(selectedActivity).addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ActivityListActivity.people.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!Objects.equals(snapshot.getKey(),"Anonymous")) {
                                ActivityListActivity.people.add((String) snapshot.getKey());  //add list of people under the activity atm
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                View recyclerView = findViewById(R.id.person_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });


        View recyclerView = findViewById(R.id.person_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.person_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        checkinBtn = (Button) findViewById(R.id.Checkin);
        checkinBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Checkin:
                if(isCheckIn == false) {
                    isCheckIn = true;
                    ((Button) findViewById(R.id.Checkin)).setText("Not Interested");
                    root.child("Business_Accounts").child("User_ID").child(key).child("activity_List")
                            .child(selectedActivity).child(UserName).setValue(static_variable_CLASS.User_ID);

                    //*** Put user in the Activity
                }else{
                    isCheckIn = false;
                    ((Button) findViewById(R.id.Checkin)).setText("Interested");
                    root.child("Business_Accounts").child("User_ID").child(key).child("activity_List")
                            .child(selectedActivity).child(UserName).removeValue();
                    //*** Drop user out the Activity
                }
                break;
            default:
                break;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ActivityListActivity.people));
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
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    /*if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PersonDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        PersonDetailFragment fragment = new PersonDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.person_detail_container, fragment)
                                .commit();
                    } else {
                    */
                    root.child("User_Accounts").child("User_ID").child(ActivityListActivity.people_key.get(position))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            info.clear();
                            info.add((String) dataSnapshot.child("username").getValue());
                            info.add((String) dataSnapshot.child("gender").getValue());
                            info.add((String) dataSnapshot.child("year").getValue());
                            info.add((String) dataSnapshot.child("month").getValue());
                            nextActivity(v, position);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    }
                });
        }
        public void nextActivity(View v, int pos){
            Intent intent = new Intent(v.getContext(), PersonDetailActivity.class);
            intent.putExtra("userName", ActivityListActivity.people.get(pos) );
            startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            //public Person.DummyItem mItem;

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
