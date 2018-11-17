package vn.edu.poly.demoxmlparser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vn.edu.poly.demoxmlparser.model.News;

public class MainActivity extends AppCompatActivity {

    // khai bao duong dan~ url de request.
    private String url = "http://docbao.vn/rss/export/home.rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // hien thi loading
                findViewById(R.id.loadingBar).setVisibility(View.VISIBLE);

                // khoi tao thread moi bang AsyncTask de request du lieu
                GetDataTask getDataTask = new GetDataTask();
                // truyen tham so url vao thread va run thread
                getDataTask.execute(url);

            }
        });
    }

    class GetDataTask extends AsyncTask<String, Long, List<News>> {

        @Override
        protected List<News> doInBackground(String... strings) {
            // boc tach xml tai day

            // khai bao 1 list rong~ de chua du lieu
            List<News> newsList = new ArrayList<>();

            // khai bao try catch de bat loi~
            try {

                URL url = new URL(strings[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();


                // khoi tao doi tuong xmlpullparser
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);

                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

                // truyen du lieu vao xmlpullparser tien hanh boc tach xml
                xmlPullParser.setInput(inputStream, "utf-8");

                int eventType = xmlPullParser.getEventType();
                News news = null;
                String text = "";
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equals("item")) {
                                news = new News();
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = xmlPullParser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (news != null && name.equalsIgnoreCase("title")) {
                                news.title = text;
                            } else if (news != null && name.equalsIgnoreCase("description")) {
                                news.description = text;
                            } else if (news != null && name.equalsIgnoreCase("pubdate")) {
                                news.pubDate = text;
                            } else if (news != null && name.equalsIgnoreCase("link")) {
                                news.link = text;
                            } else if (news != null && name.equalsIgnoreCase("guiid")) {
                                news.guiid = text;
                            } else if (name.equalsIgnoreCase("item")) {
                                newsList.add(news);
                            }
                            break;

                    }
                    // di chuyen toi tag ke tiep 
                    eventType = xmlPullParser.next(); //move to next element
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }

            return newsList;
        }


        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);

            // tuong tac voi layout, view tai day

            // tat loading
            findViewById(R.id.loadingBar).setVisibility(View.GONE);

            Toast.makeText(MainActivity.this,"Size :" + news.size(),
                    Toast.LENGTH_SHORT).show();






        }
    }


}
