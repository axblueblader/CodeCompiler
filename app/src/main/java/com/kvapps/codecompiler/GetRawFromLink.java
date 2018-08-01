package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GetRawFromLink extends AsyncTask<String, String, String> {
    private TextView status;
    public interface AsyncResponse {
        void onDataArrive(String output);
    }

    private AsyncResponse response;

    GetRawFromLink(TextView status, AsyncResponse response){
        this.status = status;
        this.response = response;
    }
    @Override
    protected String doInBackground(String... strings) {
        StringBuilder string = new StringBuilder();
        try {
            publishProgress("Getting raw from URL");
            URL url = new URL(strings[0]);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String content;
            while ((content = bufferedReader.readLine()) != null){
                string.append(content);
                string.append('\n');
            }
            publishProgress("Finished");
            Log.d("kiet", string.toString());
        } catch (IOException e) {
            publishProgress(e.getMessage());
            e.printStackTrace();
        }
        return string.toString();
    }

    @Override
    protected void onPostExecute(String s){
        response.onDataArrive(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        status.setText(values[0]);
    }
}
