package com.example.android.vestigeapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.android.vestigeapp.database.VestigeDAO;
import com.example.android.vestigeapp.database.VestigeDatabase;
import com.example.android.vestigeapp.database.VestigeEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity implements VestigeListAdapter.ItemClickListener {

private RecyclerView vestigeList;
private VestigeListAdapter vestigeListAdapter;
private VestigeDatabase vestigeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vestigeList = (RecyclerView) findViewById(R.id.rv_vestigeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        vestigeList.setLayoutManager(layoutManager);
        vestigeList.setHasFixedSize(true);

        vestigeListAdapter = new VestigeListAdapter(this,this);

        vestigeList.setAdapter(vestigeListAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        vestigeList.addItemDecoration(itemDecoration);

        /*Touchhelper for delete feature*/

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int itemPosition = viewHolder.getAdapterPosition();
                Log.d("Error" , String.valueOf(itemPosition));
                VestigeThreadExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //get swipe position
                        List<VestigeEntry>  _entries = vestigeListAdapter.getDbEntries();
                        VestigeEntry swipedItem = _entries.get(itemPosition);
                        vestigeDb.vestigeDAO().deleteEntry(swipedItem);
                    }
                });
            }
        }).attachToRecyclerView(vestigeList);
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addEntryIntent = new Intent(MainActivity.this, AddEntry.class);
                startActivity(addEntryIntent);
            }
        });

        vestigeDb= VestigeDatabase.getInstance(getApplicationContext());
        getJournalEntries();
    }

    private void getJournalEntries(){
        final LiveData<List<VestigeEntry>> entries = vestigeDb.vestigeDAO().loadAllEntries();
        entries.observe(this, new Observer<List<VestigeEntry>>() {
            @Override
            public void onChanged(@Nullable List<VestigeEntry> vestigeEntries) {
                vestigeListAdapter.setDbEntries(vestigeEntries);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddEntry.class);
        intent.putExtra(AddEntry.EXTRA_ENTRY_ID, itemId);
        startActivity(intent);
    }
}
