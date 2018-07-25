package com.kvapps.codecompiler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class OutputFragment extends Fragment {
    private TextView resultText;
    private ScrollView mScrollView;
    //private EditText codeText;
    //private ImageButton compileBtn;

    String testScript = "#include <iostream>\n" +
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.output_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize view variables
        resultText = view.findViewById(R.id.resultText);


        // Initialize view methods and properties

        mScrollView = view.findViewById(R.id.resultScrollView);

    }



    private void scrollToBottom()
    {
        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, resultText.getBottom());
            }
        });
    }

    public void retrieveAPI(String script){
        new AsyncTaskAPI().execute(script);
        Toast.makeText(getActivity(), "Compiling code", Toast.LENGTH_SHORT).show();
        //compileBtn.setText("EXECUTING CODE");
    }

    public void showResults(String result) throws JSONException {
        Toast.makeText(getActivity(), "Compiled successfully", Toast.LENGTH_LONG).show();

        // Parsing result string into json
        JSONObject json = new JSONObject(result);

        // Retrieve data by key
        String output = json.getString("output");
        String memory = json.getString("memory");
        String cpuTime = json.getString("cpuTime");

        resultText.setText(resultText.getText() +  "\nOUTPUT:\n" + output + "\nMEMORY: " + memory + "\nCPU TIME: " + cpuTime + "\n");
        scrollToBottom();
        //compileBtn.setText("EXECUTE");
    }

    private void showProgress(String progress) {
        resultText.setText(resultText.getText() + "\n" + progress);
    }

    public class AsyncTaskAPI extends CallCompilerAPI {
        @Override
        protected void onProgressUpdate(String... progress) {
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
}
