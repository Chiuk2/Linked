package linked.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.CheckBox;
import android.content.Intent;

public class BusinessActivities extends AppCompatActivity implements View.OnClickListener {

    Button art_button, food_button, sports_button, submit_button;
    TextView select_act, skip;
    CheckBox box1, box2, box3, box4, box5, box6, box7, box8, box9, box10, box11, box12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_activities);

        select_act = (TextView) findViewById(R.id.select_act);
        select_act.setVisibility(View.INVISIBLE);

        box1 = (CheckBox) findViewById(R.id.checkBox1);
        box2 = (CheckBox) findViewById(R.id.checkBox2);
        box3 = (CheckBox) findViewById(R.id.checkBox3);
        box4 = (CheckBox) findViewById(R.id.checkBox4);
        box5 = (CheckBox) findViewById(R.id.checkBox5);
        box6 = (CheckBox) findViewById(R.id.checkBox6);
        box7 = (CheckBox) findViewById(R.id.checkBox7);
        box8 = (CheckBox) findViewById(R.id.checkBox8);
        box9 = (CheckBox) findViewById(R.id.checkBox9);
        box10 = (CheckBox) findViewById(R.id.checkBox10);
        box11 = (CheckBox) findViewById(R.id.checkBox11);
        box12 = (CheckBox) findViewById(R.id.checkBox12);

        makeInvisible(box1,box2,box3,box4);
        makeInvisible(box5,box6,box7,box8);
        makeInvisible(box9,box10,box11,box12);

        art_button = (Button) findViewById(R.id.artButton);
        art_button.setOnClickListener(this);

        food_button = (Button) findViewById(R.id.foodButton);
        food_button.setOnClickListener(this);

        sports_button = (Button) findViewById(R.id.sportsButton);
        sports_button.setOnClickListener(this);

        submit_button = (Button) findViewById(R.id.submitButton5);
        submit_button.setOnClickListener(this);

        skip = (TextView) findViewById(R.id.skip_step);
        skip.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == skip)
            startActivity(new Intent(BusinessActivities.this, BusinessMenu.class));
        else if (v == submit_button)
            startActivity(new Intent(BusinessActivities.this, BusinessMenu.class));
        else if (v == art_button){
            makeInvisible(box5, box6, box7, box8);
            makeInvisible(box9,box10,box11,box12);
            select_act.setVisibility(View.VISIBLE);
            box1.setText("Film");
            box1.setVisibility(View.VISIBLE);
            box2.setText("Music");
            box2.setVisibility(View.VISIBLE);
            box3.setText("Pottery");
            box3.setVisibility(View.VISIBLE);
            box4.setText("Theatre");
            box4.setVisibility(View.VISIBLE);
        }
        else if (v == food_button){
            makeInvisible(box1, box2, box3, box4);
            makeInvisible(box9,box10,box11,box12);
            select_act.setVisibility(View.VISIBLE);
            box5.setText("Bars");
            box5.setVisibility(View.VISIBLE);
            box6.setText("Dinner");
            box6.setVisibility(View.VISIBLE);
            box7.setText("Eating Contests");
            box7.setVisibility(View.VISIBLE);
            box8.setText("Wine Tasting");
            box8.setVisibility(View.VISIBLE);
        }
        else if (v == sports_button) {
            makeInvisible(box1,box2,box3,box4);
            makeInvisible(box5,box6,box7,box8);
            select_act.setVisibility(View.VISIBLE);
            box9.setText("Basketball");
            box9.setVisibility(View.VISIBLE);
            box10.setText("Golf");
            box10.setVisibility(View.VISIBLE);
            box11.setText("Soccer");
            box11.setVisibility(View.VISIBLE);
            box12.setText("Yoga");
            box12.setVisibility(View.VISIBLE);
        }
    }

    public void makeInvisible(CheckBox b1, CheckBox b2, CheckBox b3, CheckBox b4){
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
        b4.setVisibility(View.INVISIBLE);
    }
}
