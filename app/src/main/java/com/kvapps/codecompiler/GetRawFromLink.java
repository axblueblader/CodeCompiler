package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetRawFromLink extends AsyncTask<String, Void, String> {
    public interface AsyncResponse {
        void onDataArrive(String output);
    }

    private AsyncResponse response;

    public GetRawFromLink(AsyncResponse response){
        this.response = response;
    }
    @Override
    protected String doInBackground(String... strings) {
        StringBuilder string = new StringBuilder();
        try {
            URL url = new URL(strings[0]);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String content;
            while ((content = bufferedReader.readLine()) != null){
                string.append(content);
                string.append('\n');
            }
            Log.d("kiet", string.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string.toString();
    }

    @Override
    protected void onPostExecute(String s) {
       response.onDataArrive(s);
    }
}
