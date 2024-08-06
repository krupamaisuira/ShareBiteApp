package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class DonationSuccessActivity extends BottomMenuActivity {

    Button btnshare,btndonateanother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_donation_success);
        getLayoutInflater().inflate(R.layout.activity_donation_success, findViewById(R.id.container));
        btnshare = findViewById(R.id.btnshare);
        btndonateanother = findViewById(R.id.btndonateanother);

        SpannableString content = new SpannableString("SHARING OPTIONS");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        btnshare.setText(content);
        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("image/*");

                int drawableResId = getResources().getIdentifier("sharebite_logo", "drawable", getPackageName());

                Uri imageUri = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(getResources().getResourcePackageName(drawableResId))
                        .appendPath(getResources().getResourceTypeName(drawableResId))
                        .appendPath(getResources().getResourceEntryName(drawableResId))
                        .build();

                ArrayList<Uri> imageUris = new ArrayList<>();
                imageUris.add(imageUri);

                ArrayList<CharSequence> textItems = new ArrayList<>();
                textItems.add("Join the Fight Against Hunger");
                textItems.add("Our amazing donors have just uploaded nutritious food items to our app! ");
                textItems.add("Every contribution counts towards feeding those in need and building a stronger community. Join us in making a positive impact today!");
                textItems.add("#FoodDonation #CommunitySupport #ShareBiteApp");

                shareIntent.putCharSequenceArrayListExtra(Intent.EXTRA_TEXT, textItems);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);

                startActivity(Intent.createChooser(shareIntent, "Share via ShareBite"));
            }
        });

        btndonateanother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonationSuccessActivity.this, DonateFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}