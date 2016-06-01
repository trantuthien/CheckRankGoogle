package thientt.app.android.checkrankgoogle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thientt.app.android.checkrankgoogle.adapter.WebAdapter;
import thientt.app.android.checkrankgoogle.model.Website;

public class CheckRank extends AppCompatActivity {

    private static final String LINKDEFAULT = "translateviet.com";
    private static int REQUESTCODE = 123;
    private Spinner spinner;
    private AutoCompleteTextView autoTextView;
    private ListView listview;
    private Toolbar toolbar;
    private MaterialDialog dialog;

    private ArrayList<Website> websites;
    private ArrayList<Website> ggseo;
    private WebAdapter adapter;
    private ArrayAdapter<Website> webadapter;
    private String strWebRank = "translateviet.com";
    private int rank = -1;
    private long startIndex = 1L;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 111) {
                if (rank == -1 && ggseo.size() < 50) {
                    startIndex += 10;
                    callTuongTacSeo();
                } else {
                    toolbar.setTitle(strWebRank + " -> " + rank);
                    adapter.notifyDataSetChanged();
                    showSnackbar(getString(R.string.yourrank) + " " + rank);
                }
                dialog.dismiss();
            } else if (msg.what == 112) {
                toolbar.setTitle(strWebRank + " -> " + rank);
                adapter.notifyDataSetChanged();
                showSnackbar(getString(R.string.missing_data));
                dialog.dismiss();
            } else if (msg.what == 113) {
                showSnackbar(getString(R.string.missing_input));
                dialog.dismiss();
            }
            return true;
        }
    });

    private void showSnackbar(String text) {
        Snackbar.make(autoTextView, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rank);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.listview);
        spinner = (Spinner) findViewById(R.id.spinner);
        autoTextView = (AutoCompleteTextView) findViewById(R.id.autoTextView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSpinner();
        toolbar.setTitle(strWebRank + " -> " + rank);
        ///google seo
        ggseo = new ArrayList<>();
        adapter = new WebAdapter(this, R.layout.adapter_web, ggseo);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openLinkOfSEO(position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                openMainActivity();
            }
        });
    }

    private void openLinkOfSEO(int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ggseo.get(position).getWebLink()));
        startActivity(browserIntent);
    }

    private void openMainActivity() {
        Intent intent = new Intent(CheckRank.this, MainActivity.class);
//                startActivity(intent);
        startActivityForResult(intent, CheckRank.REQUESTCODE);
    }

    private void initSpinner() {
        //getDataFromPreference(); if (websites == null)
        websites = new ArrayList<>();
        webadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, websites);
        webadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(webadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strWebRank = websites.get(position).getWebName();
                toolbar.setTitle(strWebRank + " -> " + rank);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strWebRank = CheckRank.LINKDEFAULT;
            }
        });

    }

    private void getDataFromPreference() {
        String str_websites = getSharedPreferences(MainActivity.CONTANTS, MODE_PRIVATE).getString(MainActivity.LINKWEB, "");
        if (str_websites.length() > 0) {
            websites.clear();
            websites.addAll((ArrayList<Website>) new Gson().fromJson(str_websites, new TypeToken<ArrayList<Website>>() {
            }.getType()));
            strWebRank = websites.get(0).getWebName();
        } else {
            strWebRank = CheckRank.LINKDEFAULT;
            openMainActivity();
        }
    }

    @Override
    protected void onResume() {
//        Log.d("ThienTT", "vao resume" + websites.size());
        getDataFromPreference();
//        Log.d("ThienTT", "vao sau resume" + websites.size());
        webadapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CheckRank.REQUESTCODE) {
            getDataFromPreference();
            webadapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkRank(View view) {
        rank = -1;
        ggseo.clear();
        if (dialog == null) {
            dialog = new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog)
                    .content(R.string.please_wait)
                    .canceledOnTouchOutside(false)
                    .progress(true, 0).build();
        }
        if (dialog != null)
            dialog.show();

        callTuongTacSeo();

//        Handler mainHandler = new Handler(getMainLooper());
//
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                tuongTacGooglePlayService();
//            } // This is your code
//        };
//        mainHandler.post(myRunnable);
    }


    private void callTuongTacSeo() {
        final Messenger messenger = new Messenger(handler);
        Thread thread = new Thread() {
            @Override
            public void run() {
                tuongTacGooglePlayService(messenger);
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        if(dialog != null)
            dialog.dismiss();
        super.onPause();
    }

    public void tuongTacGooglePlayService(Messenger messenger) {
        String key = "your_key";
        String cx = "your_cx";
        String qry = autoTextView.getText().toString();// search key word
        if (qry.length() > 0) {
            try {
                HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                    }
                };

                JsonFactory jsonFactory = new JacksonFactory();
                HttpTransport httpTransport = new NetHttpTransport();

                Customsearch customsearch = new Customsearch.Builder(httpTransport, jsonFactory, httpRequestInitializer)
                        .setApplicationName("TTT")
                        .build();

                Customsearch.Cse.List list = customsearch.cse().list(qry);
                list.setKey(key);
                list.setCx(cx);
                list.setStart(startIndex);
                //list.set("startIndex", startIndex);
                Search results = list.execute();
                List<Result> items = results.getItems();
                Message message = Message.obtain();
                if (items !=null && items.size() > 0) {
                    for (Result item : items) {
                        //Log.d("Response", item.toString());
                        ggseo.add(new Website(item.getDisplayLink(), item.getLink()));
                        if (item.getDisplayLink().contains(strWebRank) && rank == -1) {
                            rank = ggseo.size();
                        }
                    }
                    message.what = 111;
                } else {
                    message.what = 112;
                }
                messenger.send(message);
            } catch (IOException | RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Message message = Message.obtain();
            message.what = 113;
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rank, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_donate:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://translateviet.com/chia-se/tai-lieu/danh-sach-tu-vung-cho-ung-dung-android"));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
