package com.example.myapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * SMSRespondService stub
 */

public class SMSRespondService extends Service {
    public SMSRespondService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
