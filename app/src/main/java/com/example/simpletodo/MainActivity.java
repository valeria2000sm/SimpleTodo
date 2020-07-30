package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item-position";
    public static final int EDIT_TEXT_CODE = 20; //this is arbitrary since we only have one activity

    List<String> items;
    Button ButtonAdd;
    EditText editText;
    RecyclerView RVItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonAdd = findViewById(R.id.ButtonAdd);
        editText = findViewById(R.id.editText);
        RVItems = findViewById(R.id.RVItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                //informing the user that item has been removed at the top
                Toast toastDelete = Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT);
                toastDelete.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toastDelete.show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position " + position);
                //create new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the edit activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //constructing the adapter
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        RVItems.setAdapter(itemsAdapter);
        RVItems.setLayoutManager(new LinearLayoutManager(this));

        //to keep track of when add button is pressed
        ButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //grabbing content inside of the text field
                String todoItem = editText.getText().toString();
                //adding item to the model
                items.add(todoItem);
                //notifying adapter that item has been inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                editText.setText("");
                //to notify user that an item has been added
                Toast toastAdd = Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT);
                //making toast appear at top (I think it's a lot more user friendly especially because
                //if it shows at the bottom it can conflict with the keyboard)
                toastAdd.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toastAdd.show();
                saveItems();
            }
        });
    }

    //handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //updating model at right position with new item text
            items.set(position, itemText);
            //notify adapter of change
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            //indicate to user that an item has been edited
            Toast toastEdit = Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT);
            toastEdit.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toastEdit.show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    //This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}