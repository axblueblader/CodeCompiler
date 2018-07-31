package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CallCompilerAPI extends AsyncTask<String,String,String> {
    private String script;
    private String clientId = "c8807bf538e0e9db198229a3efad101d"; //Replace with your client ID
    private String clientSecret = "3bd3383373412f109935801fd3aba476619704ff9b0309f7f2ea7c58067c5f06"; //Replace with your client Secret

    private String language = "cpp";
    private int versionIndex = 0;
    private String input;

    @Override
    protected String doInBackground(String... strings) {
        this.input = "{\"clientId\": \"" + clientId + "\",\"clientSecret\":\"" + clientSecret + "\",\"script\":\"" + strings[0] +
                "\",\"language\":\"" + language + "\",\"versionIndex\":\"" + versionIndex + "\"} ";
        try {
            URL url = new URL("https://api.jdoodle.com/v1/execute");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");


            Log.d("TAG",input);
            publishProgress("Making request to API");
            // Create JSONObject Request
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("clientId", clientId);
            jsonRequest.put("clientSecret", clientSecret);
            jsonRequest.put("script",strings[0]);
            if (!strings[1].isEmpty())
            {
                jsonRequest.put("stdin",strings[1]);
            }
            jsonRequest.put("language", language);
            // API wants versionIndex to be integer type for some reason
            jsonRequest.put("versionIndex", versionIndex);


            // Write Request to output stream to server.
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonRequest.toString());
            out.close();
            publishProgress("Retrieving results");
            // Connection failed
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Please check your inputs : HTTP error code : "+ connection.getResponseCode());
            }

            // Retrieving results

            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            // Build into string
            StringBuilder stringBuilder = new StringBuilder("");
            String tmp;
            Log.d("TAG","Output from JDoodle .... \n");
            while ((tmp = bufferedReader.readLine()) != null) {
                stringBuilder.append(tmp + "\n");
            }
            String output = stringBuilder.toString();
            Log.d("API",output);

            connection.disconnect();
            return output;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
