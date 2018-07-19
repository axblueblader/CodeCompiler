package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetRawFromLink extends AsyncTask<URL, Void, String> {
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse response;

    public GetRawFromLink(AsyncResponse response){
        this.response = response;
    }
    @Override
    protected String doInBackground(URL... urls) {
        StringBuilder string = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urls[0].openStream()));
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
        response.processFinish(s);
    }
}
