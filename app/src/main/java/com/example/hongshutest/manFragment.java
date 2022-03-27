package com.example.hongshutest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class manFragment extends Fragment {
    ImageView imageview1;
    ImageView imageview2;
    ImageView imageview3;
    TextView textView1;
    TextView textViewBook1;
    TextView textViewBook2;
    TextView textViewBook3;
    private Timer timer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_man, container,false);
        imageview1 = view.findViewById(R.id.imageView1);
        imageview2 = view.findViewById(R.id.imageView2);
        imageview3 = view.findViewById(R.id.imageView3);
        textView1=view.findViewById(R.id.time);
        textViewBook1=view.findViewById(R.id.book1);
        textViewBook2=view.findViewById(R.id.book2);
        textViewBook3=view.findViewById(R.id.book3);
        postSync();
        return view;
    }

    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void postSync(){
        OkHttpClient httpClient = buildHttpClient();

        HashMap<String,String> paramsMap=new HashMap<>();
        paramsMap.put("pageconfig","index");

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, Objects.requireNonNull(paramsMap.get(key)));
        }

        RequestBody formBody=builder.build();
        Request.Builder reqBuild = new Request.Builder().post(formBody);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("http://android.mcdn.hongshu.com/listmodules"))
                .newBuilder();
        urlBuilder.addQueryParameter("P31","nan")
                .addQueryParameter("P27","3.9.9")
                .addQueryParameter("P35","ali")
                .addQueryParameter("version","1.5.0");
        reqBuild.url(urlBuilder.build());
        Request request = reqBuild.build();
        Call call= httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String ResponseData=response.body().string();
                parseDiffJson(ResponseData);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void parseDiffJson(String json) {
        try {
            JSONObject jsonObject1 = new JSONObject(json);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("data");

            JSONObject jsonObject4=(JSONObject) jsonArray1.get(0);
            String banner=jsonObject4.getString("m_name");
            JSONArray jsonArray3 = jsonObject4.getJSONArray("content");
            for (int i = 0; i < jsonArray3.length(); i++) {
                JSONObject jsonObject5 = (JSONObject) jsonArray3.get(i);
                String url = jsonObject5.getString("imgurl");
                String NAME=jsonObject5.getString("landmine");
                Log.e("顶部banner", NAME);
                Log.e("url"+i, url);
            }
            String Cover1 = "";
            String Cover2 = "";
            String Cover3 = "";
            String BookName1 = "";
            String BookName2 = "";
            String BookName3 = "";
            String author1 = "";
            String author2 = "";
            String author3 = "";
            Log.e("Json: ",banner);
            JSONObject jsonObject2 = (JSONObject) jsonArray1.get(8);
            String time = jsonObject2.getString("remaintime");
            JSONArray jsonArray2 = jsonObject2.getJSONArray("content");
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonObject3 = (JSONObject) jsonArray2.get(i);
                if(i==0){
                    Cover1=jsonObject3.getString("cover");
                    BookName1=jsonObject3.getString("catename");
                    author1=jsonObject3.getString("author");
                }else if(i==1){
                    Cover2=jsonObject3.getString("cover");
                    BookName2=jsonObject3.getString("catename");
                    author2=jsonObject3.getString("author");
                }else if(i==2){
                    Cover3=jsonObject3.getString("cover");
                    BookName3=jsonObject3.getString("catename");
                    author3=jsonObject3.getString("author");
                }
            }
            StrictMode.setThreadPolicy(new
                    StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
            try {
                Bitmap bitmap1 = getBitmap(Cover1);imageview1.setImageBitmap(bitmap1);
                Bitmap bitmap2 = getBitmap(Cover2);imageview2.setImageBitmap(bitmap2);
                Bitmap bitmap3 = getBitmap(Cover3);imageview3.setImageBitmap(bitmap3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Long[] timeNum = {Long.valueOf(time)};
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    textView1.setText("剩余时间:"+secondToTime(timeNum[0]));
                    timeNum[0]--;
                }
            },0,1000);
            textViewBook1.setText(BookName1+"\n"+"作者:"+author1);
            textViewBook2.setText(BookName2+"\n"+"作者:"+author2);
            textViewBook3.setText(BookName3+"\n"+"作者:"+author3);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String secondToTime(long second){
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数
        if(days>0){
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        }else{
            return hours + "小时" + minutes + "分" + second + "秒";
        }
    }
}