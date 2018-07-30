package com.kvapps.codecompiler;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

public class OutputFragment extends Fragment {
    private TextView resultText;
    private ScrollView mScrollView;

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

    public void retrieveAPI(String script,String stdin){
        new AsyncTaskAPI().execute(script, stdin);
        Toast.makeText(getActivity(), "Compiling code", Toast.LENGTH_SHORT).show();

    }

    public void showResults(String result) throws JSONException {
        Log.d("API RETURN:",result);
        if  (result.isEmpty()) {
            Toast.makeText(getActivity(), "Compiled unsuccessfully", Toast.LENGTH_LONG).show();
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
            spanBuilder.append(resultText.getText());
            int start = spanBuilder.length();
            spanBuilder.append("\nSomething went wrong, check your internet connection, inputs or restart the app and try again\n");
            spanBuilder.setSpan((new ForegroundColorSpan(Color.RED)), start, spanBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            resultText.setText(spanBuilder);
            scrollToBottom();
            return;
        }
        Toast.makeText(getActivity(), "Compiled successfully", Toast.LENGTH_LONG).show();

        // Parsing result string into json
        JSONObject json = new JSONObject(result);

        // Retrieve data by key
        String output = json.getString("output");
        String memory = json.getString("memory");
        String cpuTime = json.getString("cpuTime");

        //format the output string
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
        spanBuilder.append(resultText.getText());
        spanBuilder.append("\nOUTPUT:\n");
        int start = spanBuilder.length();
        spanBuilder.append(output);
        spanBuilder.setSpan((new ForegroundColorSpan(Color.BLUE)), start, spanBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanBuilder.append(MessageFormat.format("\nMEMORY: {0}\nCPU TIME: {1}\n", memory, cpuTime));

        resultText.setText(spanBuilder);
       // resultText.setText(MessageFormat.format("{0}\n" + outputSpan + ":\n{1}\nMEMORY: {2}\nCPU TIME: {3}\n",
        //resultText.getText(), output, memory, cpuTime));
        scrollToBottom();
    }

    private void showProgress(String progress) {
        resultText.setText(MessageFormat.format("{0}\n{1}", resultText.getText(), progress));
        scrollToBottom();
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
