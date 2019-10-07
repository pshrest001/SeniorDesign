package com.example.myapplication.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Counts the length of the SMS-message being edited.
 * Shows the count in the TextView.
 */
public class MessageLengthCounter implements TextWatcher {
    private static final int SMS_LENGTH = 160;
    private static final int SMS_LENGTH2 = 153;
    private static final int SMS_LENGTH_UNICODE = 70;
    private static final int SMS_LENGTH2_UNICODE = 67;

    private TextView counterTextView;

    public MessageLengthCounter(TextView counterTextView) {
        this.counterTextView = counterTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        update(s);
    }

    private void update(Editable messageText) {
        int messageLength = messageText.length();

        // is there unicode character in the message?
        boolean unicode = false;
        for (int i = 0; i < messageLength; i++) {
            char c = messageText.charAt(i);
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                unicode = true;
                break;
            }
        }

        // get max length of sms part depending on encoding and full length
        int length1 = (unicode ? SMS_LENGTH_UNICODE : SMS_LENGTH);
        int length2 = (unicode ? SMS_LENGTH2_UNICODE : SMS_LENGTH2);
        int partMaxLength = (messageLength > length1 ? length2 : length1);
        // create current length status info
        int partsNumber = messageLength / partMaxLength + 1;
        int partLength = partMaxLength - messageLength % partMaxLength;
        // correct length info for second part
        if (partsNumber == 2 && partLength == partMaxLength) {
            partLength = length1 - (length1 - length2) * 2;
        }

        // show current length status info
        String counterText = "" + partLength + "/" + partsNumber;
        counterTextView.setText(counterText);
    }
}
