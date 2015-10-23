package com.example.karlmosenbacher.eurotrip;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karlmosenbacher on 2015-10-20.
 */
public class TopScoreActivity extends ListActivity {
    private List<String[]> player_list = new ArrayList<String[]>();
    private ListView view;
    private int currentposition;
    private final int SHARE = 1;
    private SQLiteDatabase resultdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_score_list);

        view = getListView();
        // Register for context menu
        registerForContextMenu(view);

        initiateDB();

        Cursor c = resultdb.rawQuery("SELECT Name, Totalpoints, Date  FROM resultTable", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {

            String[] player = {c.getString(0), c.getString(1), c.getString(2)};
            player_list.add(player);
            c.moveToNext();
        }


        // player_list.add(player);
/*        player_list.add("Andree", 18);
        player_list.add("Filip", 20);
        player_list.add("Kajsa");
        player_list.add("Karl");*/

        ListAdapter adapter = new MultiAdapter(this);
        setListAdapter(adapter);

        // Make context menu with listeners
        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                currentposition = info.position;
                System.out.println(currentposition);
                menu.setHeaderTitle("What do you want to do?");
                menu.add(0, SHARE, 0, "Share Result");
            }
        });
    }

    private void initiateDB() {
        resultdb = openOrCreateDatabase("result", MODE_PRIVATE, null);

        // resultdb.delete("resultTable",null,null);
        //Creating Table if not exists
        resultdb.execSQL("CREATE TABLE IF NOT EXISTS resultTable (Trip1 INT(2), Trip2 INT(2), Trip3 INT(2), Trip4 INT(2), Trip5 INT(2), Name VARCHAR, Date DATE, Totalpoints INT(2))");
        // For testing!
        //resultdb.execSQL("INSERT INTO resultTable VALUES ('10','20','30','40','50','Anders','2015-11-28',70)");

    }

    // Adapter for setting up row
    class MultiAdapter extends ArrayAdapter {

        public MultiAdapter(Context context) {
            super(context, R.layout.top_score_row, player_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.top_score_row, parent, false);
            } else
                row = convertView;

            String[] summary = player_list.get(position);

            // Set players name on list.
            TextView playerView = (TextView) row.findViewById(R.id.player_name);
            playerView.setText(summary[0]);

            // Set players total score on list.
            TextView scoreView = (TextView) row.findViewById(R.id.total_points);
            scoreView.setText(summary[1]);

            // Set date on list.
            TextView dateView = (TextView) row.findViewById(R.id.date);
            dateView.setText(summary[2]);


            return row;
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuId = item.getItemId();

        switch (menuId) {

            case SHARE:
                shareResult(player_list.get(currentposition).toString());
                System.out.println("SHARE RESULT");
                return true;

            default:
                return false;
        }
    }

    // Lets user choose applicatio to sen SMS with
    public void shareResult(String share) {

/*        Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"
                + share));
        shareIntent.putExtra("sms_body", share);
        Intent chooser = Intent.createChooser(shareIntent, getResources()
                .getString(R.string.app_chooser));
        startActivity(chooser);*/

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MY EUROTRIP");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, share);
        startActivity(Intent.createChooser(shareIntent, "How do you want to share?"));
    }
}
