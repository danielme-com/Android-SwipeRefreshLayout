package com.danielme.android.swiperefreshlayout;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author danielme.com
 */
public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private List<Color> colors;
    private List<Color> allColors;
    private int lastColorPosition;
    private AsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncTask = new BackgroundTask();
                        Void[] params = null;
                        asyncTask.execute(params);
                    }
                });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);

        allColors = new ArrayList<Color>();
        colors = new ArrayList<Color>();
        initColors();
        loadMoreColors();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new MaterialPaletteAdapter(colors, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast toast = Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT);
                int color = android.graphics.Color.parseColor(colors.get(position).getHex());
                toast.getView().setBackgroundColor(color);
                toast.show();
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int sizePrev = colors.size();
            MainActivity.this.loadMoreColors();
            recyclerView.getAdapter().notifyItemRangeInserted(0, colors.size() - sizePrev);
            //hides the refresh indicator
            MainActivity.this.swipeRefreshLayout.setRefreshing(false);
            if (colors.size() > sizePrev) {
                recyclerView.scrollToPosition(0);
            }
        }

        @Override
        protected void onCancelled() {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
                lastColorPosition = 0;
                colors.clear();
                loadMoreColors();
                recyclerView.getAdapter().notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initColors() {

        allColors.add(new Color(getString(R.string.blue), getColorString(R.color.blue)));
        allColors.add(new Color(getString(R.string.indigo), getColorString(R.color.indigo)));
        allColors.add(new Color(getString(R.string.red), getColorString(R.color.red)));
        allColors.add(new Color(getString(R.string.green), getColorString(R.color.green)));
        allColors.add(new Color(getString(R.string.orange), getColorString(R.color.orange)));
        allColors.add(new Color(getString(R.string.grey), getColorString(R.color.bluegrey)));
        allColors.add(new Color(getString(R.string.amber), getColorString(R.color.teal)));
        allColors.add(new Color(getString(R.string.deeppurple), getColorString(R.color.deeppurple)));
        allColors.add(new Color(getString(R.string.bluegrey), getColorString(R.color.bluegrey)));
        allColors.add(new Color(getString(R.string.yellow), getColorString(R.color.yellow)));
        allColors.add(new Color(getString(R.string.cyan), getColorString(R.color.cyan)));
        allColors.add(new Color(getString(R.string.brown), getColorString(R.color.brown)));
        allColors.add(new Color(getString(R.string.teal), getColorString(R.color.teal)));

    }

    // adds 3 more colors to the displayed list
    private void loadMoreColors() {
        for (int i = lastColorPosition; i < lastColorPosition + 5 && i < allColors.size(); i++) {
            colors.add(0, allColors.get(i));
        }
        lastColorPosition += 5;
    }

    //returns the string hex value of a color in colors.xml
    private String getColorString(int color) {
        return "#" + Integer.toHexString(getResources().getColor(color)).toUpperCase().substring(2);
    }

}
