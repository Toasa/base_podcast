package com.example.tohyama.base_podcast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Episode_list extends AppCompatActivity {

    ListView lvRss;
    ArrayList<String> links;

    ArrayList<HashMap<String, String>> list_data;
    HashMap<String, String> hashTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        lvRss = (ListView) findViewById(R.id.lvRss);
        links = new ArrayList<String>();

        list_data = new ArrayList<HashMap<String, String>>();
        hashTmp = new HashMap<String, String>();

        // エピソードをクリックした時の挙動
        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(Episode_list.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try {
                Intent intent = getIntent();
                String rss_url = intent.getStringExtra("RSS_URL");

                URL url = new URL(rss_url);
                //URL url = new URL("http://feeds.rebuild.fm/rebuildfm");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                boolean fill_title = false;
                boolean fill_subtitle = false;
                boolean fill_duration = false;

                // tag の種類
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        // item tag
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            // itemタグの中にtitleタグがある場合
                            if (insideItem) {
                                hashTmp.put("title", xpp.nextText());
                                fill_title = true;
                            }
                        }  else if (xpp.getName().equalsIgnoreCase("itunes:subtitle")) {
                            if (insideItem) {
                                hashTmp.put("subtitle", xpp.nextText());
                                fill_subtitle = true;
                            }
                        } else if (xpp.getName().equalsIgnoreCase("itunes:duration")) {
                            if (insideItem) {
                                hashTmp.put("duration", xpp.nextText());
                                fill_duration = true;
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    if (fill_title && fill_subtitle && fill_duration) {
                        list_data.add(new HashMap<String, String>(hashTmp));
                        hashTmp.clear();

                        fill_title = false;
                        fill_subtitle = false;
                        fill_duration = false;
                    }

                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.two_line_list_item,
                    new String[]{"title", "subtitle", "duration"}, new int[]{R.id.item_main, R.id.item_sub, R.id.item_right});

            lvRss.setAdapter(simp);

            progressDialog.dismiss();
        }
    }

}
