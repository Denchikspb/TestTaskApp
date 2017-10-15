package com.cherepanov.testtaskapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.cherepanov.testtaskapp.R;
import com.cherepanov.testtaskapp.contract.Contract;
import com.cherepanov.testtaskapp.presenter.Presenter;

public class MainActivity extends AppCompatActivity
        implements Contract.View {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private FrameLayout mSubscriberViewContainer;
    private FrameLayout mPublisherViewContainer;

    private Contract.Presenter mPresenter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new Presenter(this);
        mPresenter.requestPermissions();
    }

    @Override
    public void initView() {
        mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void addPublisherView(View view) {
        mPublisherViewContainer.addView(view);
    }

    @Override
    public void addSubscribeView(View view) {
        mSubscriberViewContainer.addView(view);
    }

    @Override
    public void removeSubscribeView() {
        mSubscriberViewContainer.removeAllViews();
    }
}