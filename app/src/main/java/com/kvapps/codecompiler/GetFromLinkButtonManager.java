package com.kvapps.codecompiler;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class GetFromLinkButtonManager implements Button.OnClickListener{
    private Activity currentActivity;
    private EditText currentEditText;


    public GetFromLinkButtonManager(Activity currentActivity, EditText currentEditText) {
        this.currentActivity = currentActivity;
        this.currentEditText = currentEditText;
    }

    @Override
    public void onClick(View v) {
        //Popup an alert with Edittext in it
        AlertDialog.Builder alert = new AlertDialog.Builder(currentActivity);

        alert.setTitle("Input from link");
        alert.setMessage("Please paste your RAW link here");
        // Set an EditText view to get user input
        //TODO remove this link later on
       // final EditText link = new EditText(currentActivity);
        LinearLayout linearLayout = (LinearLayout)currentActivity.getLayoutInflater().inflate(R.layout.getfromlinklayout, null);
        final EditText link = linearLayout.findViewById(R.id.inputLink);
        final TextView raw = linearLayout.findViewById(R.id.rawPreview);

        final Button getRaw = linearLayout.findViewById(R.id.getRawButton);
        final TextView status = linearLayout.findViewById(R.id.getlinkstatus);
        getRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRaw.setClickable(false);
                GetRawFromLink getRawFromLink = new GetRawFromLink(status, new GetRawFromLink.AsyncResponse() {
                    @Override
                    public void onDataArrive(String output) {
                        raw.setText(output);
                    }
                });
                //TODO set some message when getting data
                getRawFromLink.execute(link.getText().toString());
            }
        });
        //link.setText("https://pastebin.com/raw/0GiN8ySH");
        link.setText("https://pastebin.com/raw/eC19Ep1R");
        alert.setView(linearLayout);


        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something with value!
                currentEditText.setText(raw.getText());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    public void setCurrentEditText(EditText currentEditText){
        this.currentEditText = currentEditText;
    }
}
