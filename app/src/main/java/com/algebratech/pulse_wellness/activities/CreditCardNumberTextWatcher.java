package com.algebratech.pulse_wellness.activities;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.algebratech.pulse_wellness.ResourceUtils;


public class CreditCardNumberTextWatcher implements TextWatcher {

    private static final String LOG_TAG = "AndroidExample";
    public static final char SEPARATOR = '-';

    private EditText editText;

    private int after;
    private String beforeString;

    public CreditCardNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        this.after = after;
        this.beforeString = s.toString();
        Log.e(LOG_TAG, "@@beforeTextChanged s=" + s
                + " . start="+ start+" . after=" + after+" . count="+ count);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e(LOG_TAG, "@@onTextChanged s=" + s
                + " . start="+ start+" . before=" + before+" . count="+ count);
        String newText = s.toString();

        String textPrefix = newText.substring(0, start);
        String textInserted = newText.substring(start, start + this.after);
        String textSuffix = newText.substring(start + this.after);
        String textBeforeCursor = textPrefix + textInserted;

        // User delete the SEPARATOR.
        if(this.after == 0 && count == 0 && beforeString.charAt(start) == SEPARATOR)  {
            if(start > 0)  {
                textPrefix = textPrefix.substring(0, textPrefix.length() -1);
            }
        }

        // Non-digit
        String regex = "[^\\d]";

        String textPrefixClean = textPrefix.replaceAll(regex, "");
        String textInsertedClean = textInserted.replaceAll(regex, "");
        String textSuffixClean = textSuffix.replaceAll(regex, "");
        String textBeforeCursorClean = textPrefixClean + textInsertedClean;

        // creditCardNumber
        String newTextClean = textPrefixClean + textInsertedClean + textSuffixClean;

        CreditCardType creditCardType = this.showDetectedCreditCardImage(newTextClean);

        int[] blockLengths = CreditCardType.DEFAULT_BLOCK_LENGTHS; // {4,4,4,4,4}
        int minLength = 0;
        int maxLength = CreditCardType.DEFAULT_MAX_LENGTH; // 4*5

        if(creditCardType != null)  {
            blockLengths = creditCardType.getBlockLengths();
            minLength = creditCardType.getMinLength();
            maxLength = creditCardType.getMaxLength();
        }
        Log.i(LOG_TAG, "newTextClean= " + newTextClean);


        int[] separatorIndexs = new int[blockLengths.length];
        for(int i=0; i < separatorIndexs.length; i++) {
            if(i==0)  {
                separatorIndexs[i] = blockLengths[i];
            } else {
                separatorIndexs[i] = blockLengths[i] + separatorIndexs[i-1];
            }
        }
        Log.i(LOG_TAG, "blockLengths= " + this.toString(blockLengths));
        Log.i(LOG_TAG, "separatorIndexs= " +  this.toString(separatorIndexs));

        int cursorPosition = start + this.after - textBeforeCursor.length() + textBeforeCursorClean.length();

        StringBuilder sb = new StringBuilder();
        int separatorCount = 0;
        int cursorPositionDelta = 0;
        int LOOP_MAX = Math.min(newTextClean.length(), maxLength);

        for(int i = 0; i < LOOP_MAX; i++) {
            sb.append(newTextClean.charAt(i));

            if(this.contains(separatorIndexs,i + 1) && i < LOOP_MAX - 1) {
                sb.append(SEPARATOR);
                separatorCount++;
                if(i < cursorPosition) {
                    cursorPositionDelta++;
                }
            }
        }
        cursorPosition= cursorPosition + cursorPositionDelta;

        String textFormatted = sb.toString();
        if(cursorPosition > textFormatted.length()) {
            cursorPosition =  textFormatted.length();
        }

        this.editText.removeTextChangedListener(this);
        this.editText.setText(textFormatted);
        this.editText.addTextChangedListener(this);
        this.editText.setSelection(cursorPosition);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private String toString(int[] array)  {
        StringBuilder sb= new StringBuilder();
        for(int i=0;i< array.length;i++) {
            if(i == 0) {
                sb.append("[").append(array[i]);
            } else {
                sb.append(", ").append(array[i]);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean contains(int[] values, int value)  {
        for(int i=0;i<values.length;i++) {
            if(values[i] == value) {
                return true;
            }
        }
        return false;
    }


    private CreditCardType showDetectedCreditCardImage(String creditCardNumber)  {
        CreditCardType type = CreditCardType.detect(creditCardNumber);

        if(type != null)  {
            Drawable icon = ResourceUtils.getDrawableByName(this.editText.getContext(), type.getImageResourceName());
            this.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        } else {
            Drawable icon = ResourceUtils.getDrawableByName(this.editText.getContext(), "icon_none");
            this.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }
        return type;
    }

}
