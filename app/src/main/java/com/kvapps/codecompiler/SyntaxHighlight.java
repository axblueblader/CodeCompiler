package com.kvapps.codecompiler;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Regex taken from
    // https://stackoverflow.com/questions/171480/regex-grabbing-values-between-quotation-marks
    ColorScheme inStrings = new ColorScheme(
            Pattern.compile("([\"'])(.*?[^\\\\])\\1"),
            Color.parseColor("#da7c00")
    );

    // Regex taken from
    // https://stackoverflow.com/questions/5989315/regex-for-match-replacing-javascript-comments-both-multiline-and-inline
    // Multi line: /(\/\*(?:(?!\*\/).|[\n\r])*\*\/)/
    // Single line: /(\/\/[^\n\r]*[\n\r]+)/
    ColorScheme comments = new ColorScheme(
            Pattern.compile("(\\/\\*(?:(?!\\*\\/).|[\\n\\r])*\\*\\/)|(\\/\\/[^\\n\\r]*[\\n\\r]+)",Pattern.MULTILINE|Pattern.DOTALL),
            Color.GRAY
    );

    final ColorScheme[] schemes = { keywords, numbers ,inStrings, comments};

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

