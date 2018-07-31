package com.kvapps.codecompiler;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilingActivity extends Fragment{
    //private TextView resultText;
    //private ImageButton compileBtn;
    //private ScrollView mScrollView;

    private EditText codeText;
    private EditText stdinText;
    private EditText currentEditText;
    private FloatingActionButton execBtn;
    private FloatingActionButton pasteLinkBtn;

    String testScript = "#include <iostream>\n" +
            "\n" +
            "using namespace std;\n" +
            "\n" +
            "int main() {\n" +
            "\tint x=0;\n cin>>x;\n" +
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
        //resultText = view.findViewById(R.id.resultText);
        //compileBtn = view.findViewById(R.id.compileBtn);
        codeText = currentEditText = view.findViewById(R.id.codeText);
        stdinText = view.findViewById(R.id.stdinText);

        codeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentEditText = codeText;
                    codeText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,9.0f));
                    stdinText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1.0f));
                }
            }
        });

        stdinText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentEditText = stdinText;
                    stdinText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,9.0f));
                    codeText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1.0f));
                }
            }
        });
        // Initialize view methods and properties

        //mScrollView = view.findViewById(R.id.resultScrollView);

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(testScript);
        SyntaxHighlight highlighter = new SyntaxHighlight();
        codeText.addTextChangedListener(highlighter);

        execBtn = view.findViewById(R.id.menu_item_execute);
        execBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData(codeText.getText().toString(),stdinText.getText().toString());
            }
        });

        //Edit button
        EditButtonListenerManager editButtonListenerManager = new EditButtonListenerManager(currentEditText, getActivity());
        Button tabBtn = view.findViewById(R.id.tabButton);
        Button semicolonBtn = view.findViewById(R.id.semicolonButton);
        Button forBtn = view.findViewById(R.id.forButton);
        Button bracketBtn = view.findViewById(R.id.bracketButton);
        editButtonListenerManager.setButtonOnClickListener(tabBtn, semicolonBtn, forBtn, bracketBtn);
        editButtonListenerManager.setButtonOnLongClickListener(forBtn, bracketBtn);

        //Paste link  button

        pasteLinkBtn = view.findViewById(R.id.menu_item_pastelink);
        //https://stackoverflow.com/questions/4134117/edittext-on-a-popup-window
        pasteLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Popup an alert with Edittext in it
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Input from link");
                alert.setMessage("Please paste your RAW link here");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint("https://pastebin.com/raw/asdfgh");
                //TODO remove this link later on
                input.setText("https://pastebin.com/raw/0GiN8ySH");
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // Do something with value!
                        GetRawFromLink getRawFromLink = new GetRawFromLink(new GetRawFromLink.AsyncResponse() {
                            @Override
                            public void onDataArrive(String output) {
                                currentEditText.setText(output);
                            }
                        });
                        //TODO set some message when getting data
                        getRawFromLink.execute(input.getText().toString());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

    }
    SendMessage sendMessage;


    interface SendMessage {
        void sendData(String code,String stdin);
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
