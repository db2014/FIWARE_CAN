package com.example.okis.fiwareproba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrikazNaKartiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_na_karti);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrikazNaKartiActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
