package com.example.matheus.volleyinsertdata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityUserProfile extends AppCompatActivity {
    private TextView textView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_user_profile);
       // FacebookSdk.sdkInitialize(getApplicationContext());

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();
        String token = inBundle.get("token").toString();


        new DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(imageUrl);
        TextView nameView = (TextView)findViewById(R.id.nameAndSurname);

        TextView tokenView = (TextView) findViewById(R.id.token);

        nameView.setText(name + " " + surname);

        tokenView.setText(token);
    }



    public void logout(){
        LoginManager.getInstance().logOut();
        Intent login = new Intent(ActivityUserProfile.this, MainActivity.class);
        startActivity(login);
        finish();
    }


}
