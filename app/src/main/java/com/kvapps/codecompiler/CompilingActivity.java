package com.kvapps.codecompiler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
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

public class CompilingActivity extends AppCompatActivity {
    private TextView resultText;
    private EditText codeText;
    private Button compileBtn;
    private ScrollView mScrollView;

    String clientId = "c8807bf538e0e9db198229a3efad101d"; //Replace with your client ID
    String clientSecret = "3bd3383373412f109935801fd3aba476619704ff9b0309f7f2ea7c58067c5f06"; //Replace with your client Secret
    String script = "#include <iostream>\n" +
            "\n" +
            "using namespace std;\n" +
            "\n" +
            "int main() {\n" +
            "\tint x=10;\n" +
            "\tint y=25;\n" +
            "\tint z=x+y;\n" +
            "\n" +
            "\tcout<<\"Sum of x+y = \" << z;\n" +
            "}";
    String language = "cpp";
    int versionIndex = 0;
    String input = "{\"clientId\": \"" + clientId + "\",\"clientSecret\":\"" + clientSecret + "\",\"script\":\"" + script +
            "\",\"language\":\"" + language + "\",\"versionIndex\":\"" + versionIndex + "\"} ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compiling);
        // Initialize view variables
        resultText = findViewById(R.id.resultText);
        codeText = findViewById(R.id.codeText);
        compileBtn = findViewById(R.id.compileBtn);


        // Initialize view methods and properties

        mScrollView = (ScrollView) findViewById(R.id.resultScrollView);

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(script);

        compileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAPI();
            }
        });
    }

    public void retrieveAPI(){
        CallCompilerAPI task = new CallCompilerAPI();
        task.execute();
        Toast.makeText(this, "Compiling code", Toast.LENGTH_SHORT).show();
        compileBtn.setText("EXECUTING CODE");
    }

    public void showResults(String result) throws JSONException {
        Toast.makeText(this, "Compiled successfully", Toast.LENGTH_LONG).show();

        // Parsing result string into json
        JSONObject json = new JSONObject(result);

        // Retrieve data by key
        String output = json.getString("output");
        String memory = json.getString("memory");
        String cpuTime = json.getString("cpuTime");

        resultText.setText("OUTPUT:\n" + output + "\nMEMORY: " + memory + "\nCPU TIME: " + cpuTime);
        compileBtn.setText("EXECUTE");
    }

    public class CallCompilerAPI extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.jdoodle.com/execute");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                Log.d("TAG",input);

                // Create JSONObject Request
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("clientId", clientId);
                jsonRequest.put("clientSecret", clientSecret);
                jsonRequest.put("script", codeText.getText().toString());
                jsonRequest.put("language", language);
                // API wants versionIndex to be integer type for some reason
                jsonRequest.put("versionIndex", versionIndex);


                // Write Request to output stream to server.
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonRequest.toString());
                out.close();

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
            return "Empty string";
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            showProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
            showResults(result);}
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showProgress(Integer progress) {
        compileBtn.setText("PROGRESS: " + progress +"%");
    }

    private void scrollToBottom()
    {
        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, codeText.getBottom());
            }
        });
    }
}
