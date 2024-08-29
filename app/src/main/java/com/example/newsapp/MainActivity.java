package com.example.newsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.Models.NewsApiResponse;
import com.example.newsapp.Models.NewsHeadlines;

import java.util.List;

import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements SelectListener, View.OnClickListener {
    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    Button b1, b2, b3, b4, b5, b6, b7;
    SearchView searchView;
    String currentSource = "abc-news"; // Default source
    Button currentButton; // Variable to keep track of the currently selected button
    int defaultColor = Color.parseColor("#00897B"); // Default button color

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);

        // Make SearchView not focusable to prevent the keyboard from appearing
        searchView.setFocusable(false);
        searchView.setFocusableInTouchMode(false);

        // Ensure the keyboard doesn't pop up automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.setTitle("Fetching news articles of " + query + " from " + currentSource);
                dialog.show();

                RequestManager manager = new RequestManager(MainActivity.this);
                manager.getNewsHeadlines(listener, currentSource, query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching News");
        dialog.show();

        b1 = findViewById(R.id.btn_1);
        b2 = findViewById(R.id.btn_2);
        b3 = findViewById(R.id.btn_3);
        b4 = findViewById(R.id.btn_4);
        b5 = findViewById(R.id.btn_5);
        b6 = findViewById(R.id.btn_6);
        b7 = findViewById(R.id.btn_7);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);

        // Set the initial selected button color
        currentButton = b1; // Initially select b1
        currentButton.setBackgroundColor(Color.BLUE); // Set selected button color

        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, currentSource, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear focus from SearchView to prevent the keyboard from appearing when returning to MainActivity
        searchView.clearFocus();
    }

    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if (list.isEmpty()) {
                Toast.makeText(MainActivity.this, "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                showNews(list);
                dialog.dismiss();
            }
        }

        @Override
        public void onError(String message) {
            Toast.makeText(MainActivity.this, "An Error Occurred!!!", Toast.LENGTH_SHORT).show();
        }
    };

    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(MainActivity.this, DetailsActivity.class)
                .putExtra("data", headlines));
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        currentSource = clickedButton.getText().toString(); // Update the current source

        // Reset the color of the previously selected button to its default color
        currentButton.setBackgroundColor(defaultColor); // Reset to default button color

        // Set the color of the currently selected button
        clickedButton.setBackgroundColor(Color.BLUE); // Set selected button color

        // Update the currentButton reference
        currentButton = clickedButton;

        dialog.setTitle("Fetching news articles of " + currentSource);
        dialog.show();

        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, currentSource, null);
    }
}
