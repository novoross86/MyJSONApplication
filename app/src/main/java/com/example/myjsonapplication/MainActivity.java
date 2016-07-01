package com.example.myjsonapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import models.Channel;


public class MainActivity extends AppCompatActivity {

    private ListView lvChannel;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loadiong. Please wait...");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start


        Button btnHit = (Button)findViewById(R.id.btnHit);
        lvChannel = (ListView)findViewById(R.id.lvChannel);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("http://192.168.1.33:8280/channels");
            }
        });

    }

    public  class JSONTask extends AsyncTask<String, String, List<Channel> >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Channel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);

                StringBuffer finalBufferData = new StringBuffer();

                List<Channel> channelList = new ArrayList<>();

                Gson gson = new Gson();

                for(int i=0; i<parentArray.length(); i++){

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    Channel channel = gson.fromJson(finalObject.toString(), Channel.class);
                    channelList.add(channel);
                }
                return channelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;

        }
        @Override
        protected void onPostExecute(List<Channel> result){
            super.onPostExecute(result);
            dialog.dismiss();
            ChannelAdaptr adapter = new ChannelAdaptr(getApplicationContext(), R.layout.row, result);
            lvChannel.setAdapter(adapter);
        }
    }



    public class ChannelAdaptr extends ArrayAdapter {

        private List<Channel> channelList;
        private int resource;
        private LayoutInflater inflater;

        public ChannelAdaptr(Context context, int resource, List<Channel> objects){
            super(context, resource, objects);
            channelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);

                holder.ivChImage = (ImageView)convertView.findViewById(R.id.ivChImage);
                holder.tvChName = (TextView)convertView.findViewById(R.id.tvChName);
                holder.tvChDescription = (TextView)convertView.findViewById(R.id.tvChDescription);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(channelList.get(position).getChImage(), holder.ivChImage);
            holder.tvChName.setText(channelList.get(position).getChName());
            holder.tvChDescription.setText(channelList.get(position).getChDescription());

            return convertView;
        }

        class ViewHolder{
            private ImageView ivChImage;
            private  TextView tvChName;
            private  TextView tvChDescription;
        }
    }



}

