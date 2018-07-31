package com.kvapps.codecompiler;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;

public class CompilingActivity extends Fragment {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compiling, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize view variables
        codeText = currentEditText = view.findViewById(R.id.codeText);
        stdinText = view.findViewById(R.id.stdinText);

        codeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentEditText = codeText;

                    animateViewWeight(stdinText,1.0f);
                    animateViewWeight(codeText,8.0f);

                    //stdinText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
                    //codeText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 8.0f));
                }
            }
        });

        stdinText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentEditText = stdinText;

                    animateViewWeight(codeText,1.0f);
                    animateViewWeight(stdinText,8.0f);

                    //codeText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
                    //stdinText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 8.0f));
                }
            }
        });
        // Initialize view methods and properties

        codeText.setMovementMethod(new ScrollingMovementMethod());
        codeText.setText(testScript);
        SyntaxHighlight highlighter = new SyntaxHighlight();
        codeText.addTextChangedListener(highlighter);

        execBtn = view.findViewById(R.id.menu_item_execute);
        execBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage.sendData(codeText.getText().toString(), stdinText.getText().toString());
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

    public void animateViewWeight(View view, float endWeight){
        ViewWeightAnimationWrapper animationWrapper = new ViewWeightAnimationWrapper(view);
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                animationWrapper,
                "weight",
                animationWrapper.getWeight(),
                endWeight);
        anim.setDuration(200);
        anim.start();
    }

    SendMessage sendMessage;
    interface SendMessage {
        void sendData(String code, String stdin);
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
}
