package com.cherepanov.testtaskapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;

import android.support.annotation.NonNull;
import android.Manifest;
import android.widget.FrameLayout;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements Session.SessionListener,
        PublisherKit.PublisherListener {

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private static String API_KEY = "45979652";
    private static String SESSION_ID = "2_MX40NTk3OTY1Mn5-MTUwNzk3MzM4NjAwNn51dkYwTlpqNDNEQkRmNUZ3NGhxMHlmL0l-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NTk3OTY1MiZzaWc9OWU0YzNjMjI2ODc4MTBjYTQzYTIxOGRjZjBlMjM5NGJlYzUyMTQ1ZTpzZXNzaW9uX2lkPTJfTVg0ME5UazNPVFkxTW41LU1UVXdOemszTXpNNE5qQXdObjUxZGtZd1RscHFORE5FUWtSbU5VWjNOR2h4TUhsbUwwbC1mZyZjcmVhdGVfdGltZT0xNTA3OTczNDA1Jm5vbmNlPTAuNjIyNzU0MDczNDM5MDExNiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTA4NTc4MjA3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();
    }

    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());
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
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
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
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);

            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }
}