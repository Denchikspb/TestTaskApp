package com.cherepanov.testtaskapp.model.service;


import android.app.Activity;
import android.util.Log;

import com.cherepanov.testtaskapp.contract.Contract;
import com.cherepanov.testtaskapp.model.utils.ApiConfig;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

/**
 * Created by Денис on 15.10.2017.
 */

public class ServiceImpl implements IServiceModel,
        Session.SessionListener,
        PublisherKit.PublisherListener{

    private static final String LOG_TAG = ServiceImpl.class.getSimpleName();

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private Contract.Presenter mPresenter;

    public ServiceImpl(Contract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(mPresenter.getActivity()).build();
        mPublisher.setPublisherListener(this);

        mPresenter.addPublisher(mPublisher.getView());
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
            mSubscriber = new Subscriber.Builder(mPresenter.getActivity(), stream).build();
            mSession.subscribe(mSubscriber);
            mPresenter.addSubscribe(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mPresenter.removeSubscribe();
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

    @Override
    public void createSession(Activity activity) {
        mSession = new Session.Builder(activity, ApiConfig.API_KEY, ApiConfig.SESSION_ID).build();
        mSession.setSessionListener(this);
        mSession.connect(ApiConfig.TOKEN);
    }
}
