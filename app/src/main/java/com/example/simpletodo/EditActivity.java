package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    EditText editItem;
    Button ButtonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editItem = findViewById(R.id.editItem);
        ButtonSave = findViewById(R.id.ButtonSave);

        getSupportActionBar().setTitle("Edit item");

        //setting the text to be what the item text currently is
        editItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        //setting a button click listener for when user is done editing item
        ButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating an intent which will contain the results
                Intent intent = new Intent();
                //pass the data
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, editItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                //set the results of the intent
                setResult(RESULT_OK, intent);
                //finish the activity
                finish();
            }
        });
    }
}