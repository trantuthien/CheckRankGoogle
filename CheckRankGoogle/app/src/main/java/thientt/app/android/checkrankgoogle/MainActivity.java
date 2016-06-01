package thientt.app.android.checkrankgoogle;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import thientt.app.android.checkrankgoogle.adapter.WebAdapter;
import thientt.app.android.checkrankgoogle.model.Website;

public class MainActivity extends AppCompatActivity {

    public static final String CONTANTS = "WEBSITE";
    public static String LINKWEB = "LINKWEB";
    private ArrayList<Website> websites;
    //private EditText editText;
    private TextInputEditText editText;
    private WebAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find view
        ListView listview = (ListView) findViewById(R.id.listview);
        editText = (TextInputEditText) findViewById(R.id.editText);

        String str_websites = getSharedPreferences(MainActivity.CONTANTS, MODE_PRIVATE).getString(MainActivity.LINKWEB, "");
        if (str_websites.length() > 0)
            websites = new Gson().fromJson(str_websites, new TypeToken<ArrayList<Website>>() {
            }.getType());
        else
            websites = new ArrayList<>();

        adapter = new WebAdapter(this, R.layout.adapter_web, websites);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                websites.remove(position);
                adapter.notifyDataSetChanged();
                saveData();
                return true;
            }
        });
    }

    public void saveWebLink(View view) {
        String web = editText.getText().toString();
        if (web.length() > 0) {
            if (websites == null)
                websites = new ArrayList<>();
            websites.add(new Website(web, web));
            saveData();
            adapter.notifyDataSetChanged();
        }
        editText.setText("");
    }

    private void saveData() {
        getSharedPreferences(MainActivity.CONTANTS, MODE_PRIVATE).edit().putString(MainActivity.LINKWEB, new Gson().toJson(websites)).commit();
    }
}
