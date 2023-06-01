package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.algebratech.pulse_wellness.R;

public class AddCardActivity extends AppCompatActivity {
    EditText number,expiry,cvv;
    private EditText editTextCreditCardNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        number = findViewById(R.id.card_number);
        expiry = findViewById(R.id.expiry);
        cvv = findViewById(R.id.cvv);

        this.editTextCreditCardNumber = this.findViewById(R.id.card_number);

        TextWatcher textWatcher = new CreditCardNumberTextWatcher(this.editTextCreditCardNumber);
        this.editTextCreditCardNumber.addTextChangedListener(textWatcher);

    }    }
