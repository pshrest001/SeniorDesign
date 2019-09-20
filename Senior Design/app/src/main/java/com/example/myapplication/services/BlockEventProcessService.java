package com.example.myapplication.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.myapplication.receivers.InternalEventBroadcast;
import com.example.myapplication.utils.ContactsAccessHelper;
import com.example.myapplication.utils.DatabaseAccessHelper;
import com.example.myapplication.utils.DatabaseAccessHelper.Contact;
import com.example.myapplication.utils.Notifications;
import com.example.myapplication.utils.Settings;

/**
 * SMS/Call blocking events processing service
 */
public class BlockEventProcessService extends IntentService {
    private static final String TAG = BlockEventProcessService.class.getName();

    private static final String NUMBER = "NUMBER";
    private static final String NAME = "NAME";
    private static final String BODY = "BODY";

    public BlockEventProcessService() {
        super(BlockEventProcessService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String number = intent.getStringExtra(NUMBER);
            String name = intent.getStringExtra(NAME);
            String body = intent.getStringExtra(BODY);
            processEvent(this, number, name, body);
        }
    }

    // Processes the event
    private void processEvent(Context context, String number, String name, String body) {
        // everything can't be null
        if (name == null && number == null) {
            Log.w(TAG, "number and name can't be null");
            return;
        }

        if (name == null) {
            // get name from the contacts
            ContactsAccessHelper db = ContactsAccessHelper.getInstance(context);
            Contact contact = db.getContact(context, number);
            name = (contact != null ? contact.name : number);
        }

        // write to the journal
        writeToJournal(context, number, name, body);

        // if there is no body - there was a call
        if (body == null) {
            // notify the user
            Notifications.onCallBlocked(context, name);
            // remove the last call from the log
            if (Settings.getBooleanValue(context, Settings.REMOVE_FROM_CALL_LOG)) {
                removeFromCallLog(context, number);
            }
        } else {
            // notify the user
            Notifications.onSmsBlocked(context, name);
        }
    }

    // Writes record to the journal
    private void writeToJournal(Context context, String number, @NonNull String name, String body) {
        if (ContactsAccessHelper.isPrivatePhoneNumber(number)) {
            number = null;
        }
        long time = System.currentTimeMillis();
        DatabaseAccessHelper db = DatabaseAccessHelper.getInstance(context);
        if (db != null && db.addJournalRecord(time, name, number, body) >= 0) {
            // send broadcast message
            InternalEventBroadcast.send(context, InternalEventBroadcast.JOURNAL_WAS_WRITTEN);
        }
    }

    // Removes passed number from the Call log
    private void removeFromCallLog(Context context, String number) {
        // wait for the call be written to the Call log
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        // and then remove it
        ContactsAccessHelper db = ContactsAccessHelper.getInstance(context);
        db.deleteLastRecordFromCallLog(context, number, 10000);
    }

    // Starts the service
    public static void start(Context context, String number, String name, String body) {
        Intent intent = new Intent(context, BlockEventProcessService.class);
        intent.putExtra(NUMBER, number);
        intent.putExtra(NAME, name);
        intent.putExtra(BODY, body);
        context.startService(intent);
    }
}
