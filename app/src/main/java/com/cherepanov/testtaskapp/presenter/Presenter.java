package com.cherepanov.testtaskapp.presenter;

import android.Manifest;
import android.util.Log;

import com.cherepanov.testtaskapp.contract.Contract;
import com.cherepanov.testtaskapp.model.utils.ApiConfig;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Presenter implements Contract.Presenter,
        Session.SessionListener,
        PublisherKit.PublisherListener {

    private static final String LOG_TAG = Presenter.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;

    private Contract.View view;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    public Presenter(Contract.View view) {
        this.view = view;
    }

    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(view.getViewActivity()).build();
        mPublisher.setPublisherListener(this);

        view.addPublisherView(mPublisher.getView());
        mSession.publish(mPublisher);
    }


    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(view.getViewActivity(), stream).build();
            mSession.subscribe(mSubscriber);
            view.addSubscribeView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            view.removeSubscribeView();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    @Override
    public void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(view.getViewActivity(), perms)) {
            view.initView();

            mSession = new Session.Builder(view.getViewActivity(), ApiConfig.API_KEY, ApiConfig.SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(ApiConfig.TOKEN);
        } else {
            EasyPermissions.requestPermissions(view.getViewActivity(),
                    "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
