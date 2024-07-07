package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sharebiteapp.Utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class MenuBarActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bar);
        sessionManager = SessionManager.getInstance(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bell:
                Toast.makeText(this, "Notification show", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_dashboard:
                Intent intent = new Intent(MenuBarActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_donate:
                Intent intent1 = new Intent(MenuBarActivity.this, DonateFoodActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.action_profile:
                Intent intent2 = new Intent(MenuBarActivity.this, ProfileActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.action_logout:
                sessionManager.logoutUser();
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(MenuBarActivity.this, SignInActivity.class);
                startActivity(intent3);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}