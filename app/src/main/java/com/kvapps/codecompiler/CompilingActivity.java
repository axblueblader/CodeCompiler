package com.kvapps.codecompiler;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilingActivity extends Fragment {
    private TextView resultText;
    private EditText codeText;
    private ImageButton compileBtn;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compiling, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize view variables
        resultText = view.findViewById(R.id.resultText);
        codeText = view.findViewById(R.id.codeText);
        compileBtn = view.findViewById(R.id.compileBtn);


        // Initialize view methods and properties

        mScrollView = view.findViewById(R.id.resultScrollView);

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(testScript);
        SyntaxHighlight highlighter = new SyntaxHighlight();
        codeText.addTextChangedListener(highlighter);

        compileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData(codeText.getText().toString());
            }
        });
    }
    SendMessage sendMessage;

    interface SendMessage {
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sendMessage = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
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
        Toast.makeText(getActivity(), "Compiling code", Toast.LENGTH_SHORT).show();
        //compileBtn.setText("EXECUTING CODE");
        compileBtn.setEnabled(false);
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
        compileBtn.setEnabled(true);
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
