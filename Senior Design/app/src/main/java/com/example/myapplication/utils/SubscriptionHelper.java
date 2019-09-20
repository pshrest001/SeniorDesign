package com.example.myapplication.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.List;

/**
 * Subscriptions helper (see SubscriptionManager).
 * Requires android.permission.READ_PHONE_STATE.
 */

public class SubscriptionHelper {

    public static boolean isAvailable() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Nullable
    public static List<SubscriptionInfo> getSubscriptions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sm = SubscriptionManager.from(context);
            return sm.getActiveSubscriptionInfoList();
        }

        return null;
    }

    /**
     * @return id of the current subscription (id of SIM)
     */
    @Nullable
    public static Integer getCurrentSubscriptionId(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionInfo info = getCurrentSubscription(context);
            if (info != null) return info.getSubscriptionId();
        }

        return null;
    }

    @Nullable
    public static String getCurrentSubscriptionName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionInfo info = getCurrentSubscription(context);
            if (info != null) return info.getDisplayName().toString();
        }

        return null;
    }

    @Nullable
    public static SubscriptionInfo getCurrentSubscription(Context context) {
        Integer subscriptionId = Settings.getIntegerValue(context, Settings.SIM_SUBSCRIPTION_ID);
        if (subscriptionId != null && subscriptionId >= 0) {
            return getSubscriptionById(context, subscriptionId);
        }

        return null;
    }

    @Nullable
    public static SubscriptionInfo getSubscriptionById(Context context, int subscriptionId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<SubscriptionInfo> list = getSubscriptions(context);
            if (list != null) {
                for (SubscriptionInfo info : list) {
                    if (info.getSubscriptionId() == subscriptionId) {
                        return info;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static String getName(SubscriptionInfo info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && info != null) {
            return info.getDisplayName().toString();
        }
        return null;
    }

    @Nullable
    public static Integer getId(SubscriptionInfo info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && info != null) {
            return info.getSubscriptionId();
        }
        return null;
    }
}
