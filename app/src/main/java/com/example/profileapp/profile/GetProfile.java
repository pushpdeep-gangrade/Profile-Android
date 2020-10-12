package com.example.profileapp.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.profileapp.MainActivity;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetProfile extends AsyncTask<String, Void, Integer> {
     final Context context;
     final String token;

    public GetProfile(Context context, String token) {
        this.context = context;
        this.token = token;
    }

    protected Integer doInBackground(String... urls) {
        com.squareup.okhttp.Response response = null;
        try {
            URL url = new URL(urls[0]);
            OkHttpClient client = new OkHttpClient();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .addHeader("authorizationkey", token)
                    .url(url).build();

            try {
                Log.d("res",url.toString());
               response = client.newCall(request).execute();
               Log.d("res", String.valueOf(response));
            } catch (Exception e) {

                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if(response ==null)
        return null;
        return response.code();
    }

    @Override
    protected void onPostExecute(Integer code) {
        super.onPostExecute(code);
        if(code == null)
            Toast.makeText(context, "Server is Down", Toast.LENGTH_SHORT).show();
        else if(code == 200){
            Intent gotoMainActivity = new Intent(context.getApplicationContext(), MainActivity.class);
            gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(gotoMainActivity);
        }
        else {
            SharedPreferences settings = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
        }
    }

}
