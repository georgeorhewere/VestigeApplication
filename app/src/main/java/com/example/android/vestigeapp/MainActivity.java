package com.example.android.vestigeapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
        getJournalEntries(getEntriesByDefault());
        //Display toast for delete instructions
        Toast.makeText(MainActivity.this, "Swipe to delete item!",Toast.LENGTH_LONG);

    }

    private void getJournalEntries(LiveData<List<VestigeEntry>> entryList){
        final LiveData<List<VestigeEntry>> entries = entryList;
        entries.observe(this, new Observer<List<VestigeEntry>>() {
            @Override
            public void onChanged(@Nullable List<VestigeEntry> vestigeEntries) {
                vestigeListAdapter.setDbEntries(vestigeEntries);
            }
        });
    }

    private LiveData<List<VestigeEntry>> getEntriesByPriority(){
        return vestigeDb.vestigeDAO().loadEntryByPriority();
    }
    private LiveData<List<VestigeEntry>> getEntriesByUpdated(){
        return vestigeDb.vestigeDAO().loadEntryByUpdate();
    }
    private LiveData<List<VestigeEntry>> getEntriesByDefault(){
        return vestigeDb.vestigeDAO().loadAllEntries();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        Context context = MainActivity.this;

        if(menuItemSelected == R.id.sortby_priority){
            //Sort by Priority
            getJournalEntries(getEntriesByPriority());

        }else if(menuItemSelected == R.id.sortby_update){
            //Sort by Updated On
            getJournalEntries(getEntriesByUpdated());


        }else{
            getJournalEntries(getEntriesByDefault());
        }
        return true;
    }
}
