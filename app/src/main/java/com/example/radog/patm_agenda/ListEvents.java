package com.example.radog.patm_agenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListEvents extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.lvEvents)
    ListView lvEvents;

    private ArrayList<itemEvent> arrayItem;
    private ListViewAdapter adapter = null;
    private ListView lvOptions;
    private String[] options = {};
    private AdapterView.AdapterContextMenuInfo info;

    DBHelper objDBH;
    SQLiteDatabase BD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        ButterKnife.bind(this);

        lvOptions = new ListView(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //para que se refresque la vista cuando volvemos a esta Activity
        objDBH = new DBHelper(this, "CURSO", null, 1);
        BD = objDBH.getWritableDatabase(); //open

        arrayItem = new ArrayList<>();
        cargarLista();

        registerForContextMenu(lvEvents);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmAddEvent:
                Intent intNewEvent = new Intent(this, NewEvent.class);
                startActivity(intNewEvent);
                break;
            case R.id.itmAbout:
                Intent intAbout = new Intent(this, About.class);
                startActivity(intAbout);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int id = v.getId();
        MenuInflater inflater = getMenuInflater();

        switch (id) {
            case R.id.lvEvents:
                inflater.inflate(R.menu.menu_events, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        itemEvent objItem;

        switch (item.getItemId()) {
            case R.id.itmActUpd:
                objItem = arrayItem.get(info.position);
                Intent intNewEvent = new Intent(this, UpdateEvent.class);

                Bundle data = new Bundle();
                data.putString("NAME", objItem.getNameE());
                data.putString("DESC", objItem.getDescE());
                data.putString("DATE", objItem.getDateE());
                intNewEvent.putExtras(data);

                startActivity(intNewEvent);
                break;

            case R.id.itmActDel:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete this event?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long result;
                                itemEvent objItem;
                                List<String> event;
                                objDBH = new DBHelper(ListEvents.this, "CURSO", null, 1);
                                objDBH.openDB();

                                objItem = arrayItem.get(info.position);
                                event = objDBH.selectEvent(objItem.getNameE(), objItem.getDescE(), objItem.getDateE());
                                if (event != null) {
                                    //DELETE PEOPLE
                                    result = objDBH.deletePeople(Integer.parseInt(event.get(0)));
                                    if (result != -1) {
                                        //DELETE EVENT
                                        result = objDBH.deleteEvent(Integer.parseInt(event.get(0)));
                                        if (result != -1) {
                                            objDBH.closeDB();
                                            Toast.makeText(ListEvents.this, "Event successfuly deleted", Toast.LENGTH_SHORT).show();
                                            onStart();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("ALERT!!!");
                alert.show();
                break;

            case R.id.itmActCall:
                break;
            case R.id.itmActSMS:
        }

        return super.onContextItemSelected(item);
    }

    private void cargarLista() {
        Cursor c = BD.rawQuery("SELECT * FROM event ORDER BY nameE", null);
        if (c.moveToFirst()) {
            do {
                arrayItem.add(new itemEvent(R.mipmap.ic_launcher, c.getString(1), c.getString(2), c.getString(3)));
            } while (c.moveToNext());
            adapter = new ListViewAdapter(this, arrayItem);
            lvEvents.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No existen registros", Toast.LENGTH_SHORT).show();
            //this.finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }
}
