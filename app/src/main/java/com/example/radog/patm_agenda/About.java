package com.example.radog.patm_agenda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class About extends AppCompatActivity {

    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.tvPhone) TextView tvPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Linkify.addLinks(tvEmail, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(tvPhone, Linkify.PHONE_NUMBERS);
    }
}
