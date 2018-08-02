package com.kvapps.codecompiler;

import android.app.Activity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

public class EditButtonListenerManager implements View.OnLongClickListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener{
    private EditText currentEditText;
    private Activity currentActivity;

    EditButtonListenerManager(EditText currentEditText, Activity currentActivity) {
        this.currentEditText = currentEditText;
        this.currentActivity = currentActivity;
    }

    public void setButtonOnClickListener(Button... buttons){
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    public void setButtonOnLongClickListener(Button... buttons){
        for (Button button : buttons) {
            button.setOnLongClickListener(this);
        }
    }

    public void setCurrentEditText(EditText currentEditText){
        this.currentEditText=currentEditText;
    }

    @Override
    public void onClick(View v) {
        int start= Math.max(currentEditText.getSelectionStart(), 0);
        int end = Math.max(currentEditText.getSelectionEnd(), 0);
        String replaceStr = null;
        switch (v.getId()){
            case R.id.tabButton:
                replaceStr="\t";
                break;
            case R.id.semicolonButton:
                replaceStr=";";
                break;
            case R.id.forButton:
                replaceStr = "for(int  =  ;  ;  ){ }";
                break;
            case R.id.bracketButton:
                replaceStr = "( )";
                break;
        }
        currentEditText.getText().replace(Math.min(start, end), Math.max(start, end), replaceStr, 0, replaceStr.length());
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popupMenu = new PopupMenu(currentActivity, v);
        MenuInflater menuInflater = new MenuInflater(currentActivity);
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
        int start= Math.max(currentEditText.getSelectionStart(), 0);
        int end = Math.max(currentEditText.getSelectionEnd(), 0);
        String tmp = null;
        switch (item.getItemId()){
            case R.id.ifOption:
                tmp = "if( ) { }";
                break;
            case R.id.whileOption:
                tmp = "while( ) { }";
                break;
            case R.id.squareBracketOption:
                tmp = "[ ]";
                break;
            case R.id.curlyBracesOption:
                tmp = "{ }";
                break;
            case R.id.cinOption:
                tmp = ">>";
                break;
            case R.id.coutOption:
                tmp = "<<";
                break;
        }
        currentEditText.getText().replace(Math.min(start, end), Math.max(start, end), tmp, 0, tmp.length());
        return false;
    }
}
