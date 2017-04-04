package com.example.radog.patm_agenda;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListEvents extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.lvEvents)
    ListView lvEvents;

    private ArrayList<itemEvent> arrayItem;
    private ListViewAdapter adapter = null;

    DBHelper objDBH;
    SQLiteDatabase BD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        ButterKnife.bind(this);

        objDBH = new DBHelper(this, "CURSO", null, 1);
        BD = objDBH.getWritableDatabase(); //open

        arrayItem = new ArrayList<>();
        cargarLista();
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
                break;
            case R.id.itmAbout:
        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarLista() {
        Cursor c;
        c = BD.rawQuery("SELECT * FROM event ORDER BY nameE", null);

        //CHECAR COMO HACER CICLO!!!
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
