package com.wkq.scrollviewenlargebanner;

import android.os.Binder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        banner = (Banner)findViewById(R.id.banner);
    }

    private void initData() {
        List<Integer> list = new ArrayList<>();

        list.add(R.drawable.card_cover3);
        list.add(R.drawable.card_cover4);
        list.add(R.drawable.card_cover5);
        list.add(R.drawable.card_cover6);


        banner.setImages(list)
                .setImageLoader(new GlideImageLoader())
                .start();
        banner.setDelayTime(3000);

    }
}
