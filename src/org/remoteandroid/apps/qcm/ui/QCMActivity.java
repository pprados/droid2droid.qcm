package org.remoteandroid.apps.qcm.ui;


import org.remoteandroid.apps.qcm.R;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class QCMActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}