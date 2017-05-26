package linked.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener {

    Button user_account, business_account, user_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // User Account button from Create Account screen to User Create screen
        user_account = (Button) findViewById(R.id.userButton);
        user_account.setOnClickListener(this);

        business_account = (Button) findViewById(R.id.businessButton);
        business_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == user_account)
            startActivity(new Intent(CreateAccount.this, UserCreate.class));
        else if (v == business_account)
            startActivity(new Intent(CreateAccount.this, BusinessCreate.class));
        }
}
