package com.example.newsfeedreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<NewsItem> newsItems;
    private RecyclearViewAdapter recyclearViewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsItems = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclearView);
        recyclearViewAdapter = new RecyclearViewAdapter(this);
        recyclerView.setAdapter(recyclearViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetDataAsyncTask getDataAsyncTask = new GetDataAsyncTask();
        getDataAsyncTask.execute();

        //WORK WITH RSS FEED

    }
    private class GetDataAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream();
            try {
                initXMLPullParser(inputStream);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclearViewAdapter.setNewsItems(newsItems);
            super.onPostExecute(aVoid);
        }
    }

    private InputStream getInputStream(){
        Log.d(TAG, "instance initializer: started");
        try {
            URL url = new URL("https://www.autosport.com/rss/feed/f1");
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
        Log.d(TAG, "initXMLPullParser: started");
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlPullParser.setInput(inputStream, null);
        xmlPullParser.nextTag();
        xmlPullParser.require(XmlPullParser.START_TAG, null, "rss");
        while(xmlPullParser.next() != XmlPullParser.END_TAG){
            if(xmlPullParser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            xmlPullParser.require(XmlPullParser.START_TAG, null, "channel");
            while(xmlPullParser.next() != XmlPullParser.END_TAG){
                if(xmlPullParser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }
                if(xmlPullParser.getName().equals("item")){
                    xmlPullParser.require(XmlPullParser.START_TAG, null, "item");

                    String title ="";
                    String desc = "";
                    String link = "";
                    String date = "";

                    while(xmlPullParser.next() != XmlPullParser.END_TAG){
                        if(xmlPullParser.getEventType() != XmlPullParser.START_TAG){
                            continue;
                        }
                        String tagName = xmlPullParser.getName();
                        if(tagName.equals("title")){
                            title = getContent(xmlPullParser, "title");
                        }else if(tagName.equals("description")){
                            desc = getContent(xmlPullParser, "description");
                        }else if(tagName.equals("link")){
                            link = getContent(xmlPullParser, "link");
                        }else if(tagName.equals("pubDate")){
                            date = getContent(xmlPullParser, "pubDate");
                        }else{
                            skipTag(xmlPullParser);
                        }
                    }
                    NewsItem newsItem = new NewsItem(title, desc, link, date);
                    newsItems.add(newsItem);

                } else{
                    //TODO: SKIP
                    skipTag(xmlPullParser);
                }
            }
        }
    }

    private void skipTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Log.d(TAG, "skipTag: skipping " + xmlPullParser.getName());
        if(xmlPullParser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int number = 1;
        while(number != 0){
            switch (xmlPullParser.next()){
                case XmlPullParser.START_TAG:
                    ++number;
                    break;
                case XmlPullParser.END_TAG:
                    --number;
                    break;
                default:
                    break;
            }
        }
    }

    private String getContent(XmlPullParser xmlPullParser, String tagName){
        Log.d(TAG, "getContent: started for tag: " + tagName);
        try {
            xmlPullParser.require(XmlPullParser.START_TAG, null, tagName);
            String content = "";
            if(xmlPullParser.next() == XmlPullParser.TEXT){
                content = xmlPullParser.getText();
                xmlPullParser.next();
            }
            return content;
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }
}
