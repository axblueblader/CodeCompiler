package com.kvapps.codecompiler;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class CompilingActivity extends AppCompatActivity {
    private TextView resultText;
    private EditText codeText;
    private Button compileBtn;
    private ScrollView mScrollView;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compiling);
        // Initialize view variables
        resultText = findViewById(R.id.resultText);
        codeText = findViewById(R.id.codeText);
        compileBtn = findViewById(R.id.compileBtn);


        // Initialize view methods and properties

        mScrollView = findViewById(R.id.resultScrollView);

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(testScript);
        SyntaxHighlight highlighter = new SyntaxHighlight();
        codeText.addTextChangedListener(highlighter);

        compileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAPI();
            }
        });
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

    public void retrieveAPI(){
        new AsyncTaskAPI().execute(codeText.getText().toString());
        Toast.makeText(this, "Compiling code", Toast.LENGTH_SHORT).show();
        compileBtn.setText("EXECUTING CODE");
        compileBtn.setEnabled(false);
    }

    public void showResults(String result) throws JSONException {
        Toast.makeText(this, "Compiled successfully", Toast.LENGTH_LONG).show();

        // Parsing result string into json
        JSONObject json = new JSONObject(result);

        // Retrieve data by key
        String output = json.getString("output");
        String memory = json.getString("memory");
        String cpuTime = json.getString("cpuTime");

        resultText.setText(resultText.getText() +  "\nOUTPUT:\n" + output + "\nMEMORY: " + memory + "\nCPU TIME: " + cpuTime + "\n");
        scrollToBottom();
        compileBtn.setText("EXECUTE");
        compileBtn.setEnabled(true);
    }

    private void showProgress(String progress) {
        resultText.setText(resultText.getText() + "\n" + progress);
    }

    private class AsyncTaskAPI extends CallCompilerAPI {
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

    // Snippet credit to Jared Rummler from his answer in this post
    // https://stackoverflow.com/questions/42786493/syntax-highlighting-on-android-edittext-using-span?rq=1
    // Regular expression is truly magical
    public class SyntaxHighlight implements TextWatcher {
        ColorScheme keywords = new ColorScheme(
                Pattern.compile(
                        "\\b(package|transient|strictfp|void|char|short|int|long|double|float|const|static|volatile|byte|boolean|bool|class|" +
                                "interface|native|private|protected|public|final|abstract|synchronized|enum|instanceof|assert|if|else|switch|" +
                                "case|default|break|goto|return|for|while|do|continue|new|throw|throws|try|catch|finally|this|super|extends|" +
                                "implements|import|true|false|null|using|namespace|cout|cin|printf|inherit|friend|signed|sizeof|unsigned|struct|const)\\b"),
                Color.BLUE
        );

        ColorScheme numbers = new ColorScheme(
                Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)"),
                Color.MAGENTA
        );

        final ColorScheme[] schemes = { keywords, numbers };

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override public void afterTextChanged(Editable s) {
            removeSpans(s, ForegroundColorSpan.class);
            for (ColorScheme scheme : schemes) {
                for(Matcher m = scheme.pattern.matcher(s); m.find();) {
                    s.setSpan(new ForegroundColorSpan(scheme.color),
                            m.start(),
                            m.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        void removeSpans(Editable e, Class<? extends CharacterStyle> type) {
            CharacterStyle[] spans = e.getSpans(0, e.length(), type);
            for (CharacterStyle span : spans) {
                e.removeSpan(span);
            }
        }

        class ColorScheme {
            final Pattern pattern;
            final int color;

            ColorScheme(Pattern pattern, int color) {
                this.pattern = pattern;
                this.color = color;
            }
        }

    }
}
