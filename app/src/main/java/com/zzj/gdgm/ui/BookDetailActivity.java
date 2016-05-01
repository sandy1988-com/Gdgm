package com.zzj.gdgm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.gdgm.R;
import com.zzj.gdgm.support.JsoupService;
import com.zzj.gdgm.support.OkHttpUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by J。 on 2016/5/1.
 */
public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";

    private Toolbar toolbar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_layout);
        initView();
        Intent intent = getIntent();
        String book_name = intent.getStringExtra("book_name");
        String url = OkHttpUtil.encodeUrl(OkHttpUtil.getLibrarySearchParentUrl() + intent.getStringExtra("url"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(book_name);
        Request request = OkHttpUtil.getRequest(url, OkHttpUtil.getLibraySearchHost(), OkHttpUtil.getREFERER());
        OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookDetailActivity.this, "获取失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.code() == 200) {
                        String content = new String(response.body().bytes(), "gb2312");
                        final Map<String, String> map = JsoupService.parseBookDetail(content);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                Iterator iterator = map.keySet().iterator();
                                while (iterator.hasNext()) {
                                    TextView textView = new TextView(BookDetailActivity.this);
                                    textView.setLayoutParams(layoutParams);
                                    textView.setTextSize(getResources().getDimension(R.dimen.book_detail_textSize));
                                    textView.setPadding(0, (int) getResources().getDimension(R.dimen.book_detail_margin_top_bottom), 0, (int) getResources().getDimension(R.dimen.book_detail_margin_top_bottom));
                                    String key = (String) iterator.next();
                                    textView.setText(key + ":" + map.get(key));
                                    linearLayout.addView(textView);
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BookDetailActivity.this, "获取失败,请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookDetailActivity.this, "获取失败,请检查网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                }
            }
        });
    }

    private void initView() {
        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.linearLayout = (LinearLayout) findViewById(R.id.book_detail_linearLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}