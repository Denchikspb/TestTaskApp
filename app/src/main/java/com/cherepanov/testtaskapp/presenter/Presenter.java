package com.cherepanov.testtaskapp.presenter;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.cherepanov.testtaskapp.contract.Contract;
import com.cherepanov.testtaskapp.model.service.IServiceModel;
import com.cherepanov.testtaskapp.model.service.ServiceImpl;
import com.cherepanov.testtaskapp.model.utils.ApiConfig;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Presenter implements Contract.Presenter {

    private static final String LOG_TAG = Presenter.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;

    private Contract.View view;
    private IServiceModel mServiceModel;

    public Presenter(Contract.View view) {
        this.view = view;
        mServiceModel = new ServiceImpl(this);
    }


    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    @Override
    public void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(view.getViewActivity(), perms)) {
            view.initView();
            mServiceModel.createSession(view.getViewActivity());
        } else {
            EasyPermissions.requestPermissions(view.getViewActivity(),
                    "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public Activity getActivity() {
        return view.getViewActivity();
    }

    @Override
    public void addPublisher(View view) {
        this.view.addPublisherView(view);
    }

    @Override
    public void addSubscribe(View view) {
        this.view.addSubscribeView(view);
    }

    @Override
    public void removeSubscribe() {
        this.view.removeSubscribeView();
    }

}
