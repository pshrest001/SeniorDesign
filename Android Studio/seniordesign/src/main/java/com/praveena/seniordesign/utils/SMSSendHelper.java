
package com.praveena.seniordesign.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import com.praveena.seniordesign.receivers.InternalEventBroadcast;
import com.praveena.seniordesign.receivers.SMSSendResultBroadcastReceiver;

import java.util.ArrayList;

import static com.praveena.seniordesign.receivers.SMSSendResultBroadcastReceiver.SMS_DELIVERY;
import static com.praveena.seniordesign.receivers.SMSSendResultBroadcastReceiver.SMS_SENT;

/**
 * Sends SMS and processes the results of sending.
 * Requires android.permission.READ_PHONE_STATE and android.permission.SEND_SMS.
 */

public class SMSSendHelper {
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String MESSAGE_ID = "MESSAGE_ID";
    public static final String MESSAGE_PART = "MESSAGE_PART";
    public static final String MESSAGE_PART_ID = "MESSAGE_PART_ID";
    public static final String MESSAGE_PARTS = "MESSAGE_PARTS";
    public static final String DELIVERY = "DELIVERY";

    // Sends SMS
    public boolean sendSMS(Context context, @NonNull String phoneNumber, @NonNull String message) {
        if (phoneNumber.isEmpty() || message.isEmpty()) {
            return false;
        }

        // get application context
        context = context.getApplicationContext();

        // get flag of delivery waiting
        boolean delivery = false;
        if (Settings.getBooleanValue(context, Settings.DELIVERY_SMS_NOTIFICATION)) {
            delivery = true;
        }

        // write the sent message to the Outbox
        long messageId = writeSMSMessageToOutbox(context, phoneNumber, message);

        // divide message into parts
        SmsManager smsManager = getSmsManager(context);
        ArrayList<String> messageParts = smsManager.divideMessage(message);

        // create pending intents for each part of message
        ArrayList<PendingIntent> sentIntents = new ArrayList<>(messageParts.size());
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>(messageParts.size());
        for (int i = 0; i < messageParts.size(); i++) {
            String messagePart = messageParts.get(i);
            int messagePartId = i + 1;

            // pending intent for getting result of SMS sending
            PendingIntent pendingIntent = createPendingIntent(context, SMS_SENT, messageId,
                    phoneNumber, messagePart, messagePartId, messageParts.size(), delivery);
            sentIntents.add(pendingIntent);

            if (delivery) {
                // pending intent for getting result of SMS delivery
                pendingIntent = createPendingIntent(context, SMS_DELIVERY, messageId,
                        phoneNumber, messagePart, messagePartId, messageParts.size(), true);
                deliveryIntents.add(pendingIntent);
            }
        }

        // send message
        smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, sentIntents, deliveryIntents);

        // send internal event message
        InternalEventBroadcast.sendSMSWasWritten(context, phoneNumber);

        return true;
    }

    // Writes the sending SMS to the Outbox
    private long writeSMSMessageToOutbox(Context context, String phoneNumber, String message) {
        long id = -1;

        // if older than KITKAT or if app is default
        if (!DefaultSMSAppHelper.isAvailable() ||
                DefaultSMSAppHelper.isDefault(context)) {
            // write SMS to the outbox
            ContactsAccessHelper db = ContactsAccessHelper.getInstance(context);
            id = db.writeSMSMessageToOutbox(context, phoneNumber, message);
        }
        // else SMS will be written by the system

        return id;
    }

    // Creates pending intent for getting result of SMS sending/delivery
    private PendingIntent createPendingIntent(Context context, String action, long messageId,
                                              String phoneNumber, String messagePart,
                                              int messagePartId, int messageParts, boolean delivery) {
        // attaches static broadcast receiver for processing the results
        Intent intent = new Intent(context, SMSSendResultBroadcastReceiver.class);
        intent.setAction(action);
        intent.putExtra(MESSAGE_ID, messageId);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        intent.putExtra(MESSAGE_PART, messagePart);
        intent.putExtra(MESSAGE_PART_ID, messagePartId);
        intent.putExtra(MESSAGE_PARTS, messageParts);
        intent.putExtra(DELIVERY, delivery);

        return PendingIntent.getBroadcast(context, intent.hashCode(), intent, 0);
    }

    // Returns current SmsManager
    private SmsManager getSmsManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Integer subscriptionId = SubscriptionHelper.getCurrentSubscriptionId(context);
            if (subscriptionId != null) {
                return SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
            }
        }

        return SmsManager.getDefault();
    }

}
