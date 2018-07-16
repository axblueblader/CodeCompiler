package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetRawFromLink extends AsyncTask<URL, Void, String> {
    @Override
    protected String doInBackground(URL... urls) {
        String string = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urls[0].openStream()));
            String content = "";
            while ((content = bufferedReader.readLine()) != null){
                string += content;
                string += '\n';
            }
            Log.d("kiet", string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }
}
