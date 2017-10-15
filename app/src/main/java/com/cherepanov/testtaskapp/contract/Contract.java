package com.cherepanov.testtaskapp.contract;

import android.app.Activity;

public interface Contract {

    interface View {

        void initView();

        Activity getViewActivity();

        void addPublisherView(android.view.View view);

        void addSubscribeView(android.view.View view);

        void removeSubscribeView();
    }

    interface Presenter {

        void requestPermissions();

        void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults);
    }
}
