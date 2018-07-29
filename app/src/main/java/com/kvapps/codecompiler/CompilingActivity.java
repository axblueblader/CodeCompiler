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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.github.clans.fab.FloatingActionButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilingActivity extends Fragment implements
        View.OnClickListener,
        View.OnLongClickListener,
        PopupMenu.OnMenuItemClickListener{

    //private TextView resultText;
    private EditText codeText;
    //private ImageButton compileBtn;
    //private ScrollView mScrollView;
    private FloatingActionButton execBtn;

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
        //resultText = view.findViewById(R.id.resultText);
        //compileBtn = view.findViewById(R.id.compileBtn);
        codeText = view.findViewById(R.id.codeText);
        execBtn = view.findViewById(R.id.menu_item_execute);

        // Initialize view methods and properties

       // mScrollView = view.findViewById(R.id.resultScrollView);

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(testScript);
        SyntaxHighlight highlighter = new SyntaxHighlight();
        codeText.addTextChangedListener(highlighter);

        /*compileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData(codeText.getText().toString());
            }
        });*/

        execBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData(codeText.getText().toString());
            }
        });

        //Edit button
        Button tab = view.findViewById(R.id.tabButton);
        tab.setOnClickListener(this);
        Button semicolon = view.findViewById(R.id.semicolonButton);
        semicolon.setOnClickListener(this);
        Button forBtn = view.findViewById(R.id.forButton);
        forBtn.setOnClickListener(this);
        forBtn.setOnLongClickListener(this);
        Button bracketBtn = view.findViewById(R.id.bracketButton);
        bracketBtn.setOnClickListener(this);
        bracketBtn.setOnLongClickListener(this);

    }
    SendMessage sendMessage;

    @Override
    public void onClick(View v) {
        int start= Math.max(codeText.getSelectionStart(), 0);
        int end = Math.max(codeText.getSelectionEnd(), 0);
        String tmp;
        switch (v.getId()){
            case R.id.tabButton:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), "\t", 0, 1);
                break;
            case R.id.semicolonButton:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), ";", 0, 1);
                break;
            case R.id.forButton:
                tmp = "for(int  =  ;  ;  ){ }";
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), tmp, 0, tmp.length());
                break;
            case R.id.bracketButton:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), "( )", 0, 3);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        MenuInflater menuInflater = new MenuInflater(getActivity());
        popupMenu.setOnMenuItemClickListener(this);
        switch(v.getId()){
            case R.id.forButton:
                menuInflater.inflate(R.menu.forpopupmenu, popupMenu.getMenu());
                break;
            case R.id.bracketButton:
                menuInflater.inflate(R.menu.bracketpopupmenu, popupMenu.getMenu());
                break;
        }
        popupMenu.show();
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int start= Math.max(codeText.getSelectionStart(), 0);
        int end = Math.max(codeText.getSelectionEnd(), 0);
        String tmp;
        switch (item.getItemId()){
            case R.id.ifOption:
                tmp = "if( ) { }";
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), tmp, 0, tmp.length());
                break;
            case R.id.whileOption:
                tmp = "while( ) { }";
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), tmp, 0, tmp.length());
                break;
            case R.id.squareBracketOption:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), "[ ]", 0, 3);
                break;
            case R.id.curlyBracesOption:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), "{ }", 0, 3);
                break;
            case R.id.cinOption:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), ">>", 0, 2);
                break;
            case R.id.coutOption:
                codeText.getText().replace(Math.min(start, end), Math.max(start, end), "<<", 0, 2);
                break;
        }
        return false;
    }

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
