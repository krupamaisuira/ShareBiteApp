package com.example.sharebiteapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sharebiteapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {


    public static boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLetter(password.charAt(i))) {
                hasLetter = true;
            } else if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }
    public static String getCurrentDatetime() {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return datetimeFormat.format(new Date());
    }
    public static void togglePasswordVisibility(EditText editText, ImageView imageView) {
        if (editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show Password
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.hidden);
        } else {
            // Hide Password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.eye);
        }
        // Move the cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }
    public static void saveCredentials(Context context, String email, boolean rememberMe) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }
    public static void setStatusColor(Context context,FoodStatus status, TextView statusTextView) {
        int textColor;
        int backgroundColor;

        switch (status) {
            case Available:
                textColor = ContextCompat.getColor(context, R.color.white);
                backgroundColor = ContextCompat.getColor(context, R.color.holo_blue);
                break;
            case Expired:
                textColor = ContextCompat.getColor(context, R.color.red);
                backgroundColor = ContextCompat.getColor(context, R.color.light_red);
                break;
            case Donated:
                textColor = ContextCompat.getColor(context, R.color.green);
                backgroundColor = ContextCompat.getColor(context, R.color.light_green);
                break;
            case Requested:
                textColor = ContextCompat.getColor(context, R.color.dark_orange);
                backgroundColor = ContextCompat.getColor(context, R.color.light_yellow);
                break;
            default:
                textColor = ContextCompat.getColor(context, android.R.color.white);
                backgroundColor = ContextCompat.getColor(context, R.color.holo_blue);
                break;
        }

        statusTextView.setTextColor(textColor);
        statusTextView.setBackgroundColor(backgroundColor);
    }
    public static Uri[] getImageUrisFromImageViews(ImageView[] imageViews) {
        List<Uri> uriList = new ArrayList<>();

        for (ImageView imageView : imageViews) {
            Object tag = imageView.getTag();
            if (tag instanceof Uri) {
                Uri uri = (Uri) tag;
                uriList.add(uri);
                Log.d("DonateFoodActivity", "Retrieved URI: " + uri.toString());
            } else {
                Log.w("DonateFoodActivity", "Tag is not a URI: " + tag);
            }
        }

        // Convert List to Array
        Uri[] uris = uriList.toArray(new Uri[0]);

        return uris;
    }
    public static int isFoodExpired(String bestBeforeDateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date bestBeforeDate = sdf.parse(bestBeforeDateStr);
            Calendar currentDate = Calendar.getInstance();

            if (bestBeforeDate.before(currentDate.getTime())) {
                // The food is expired, return 0
                return 0;
            } else {
                // The food is not expired, return 1
                return 1;
            }
        } catch (ParseException e) {

            e.printStackTrace();
            return -1;
        }
    }
   public static boolean isFirebaseStorageUrl(String url) {
        return url != null && url.startsWith("https://firebasestorage.googleapis.com/");
    }
}
