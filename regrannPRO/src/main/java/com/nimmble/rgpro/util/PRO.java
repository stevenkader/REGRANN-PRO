package com.nimmble.rgpro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.nimmble.rgpro.activity.RegrannApp;

import java.util.List;

public final class PRO {

    static Context _ctx;
    private final BillingClient billingClient;

    public PRO(Context ctx) {
        _ctx = ctx;

        billingClient = BillingClient.newBuilder(_ctx)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }


    //set to false to allow compiler to identify and eliminate
    //unreachable code
    public static final boolean VER = true;

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
        }
    };


    public void checkIsActiveSub() {


        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {


            }

        };
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_ctx);


        RegrannApp.sendEvent("PRO_check_if_active");
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(BillingResult billingResult, List purchases) {
                                    // check billingResult
                                    // process returned purchase list, e.g. display the plans user owns

                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {


                                        for (int i = 0; i < purchases.size(); i++) {
                                            Purchase p = (Purchase) purchases.get(i);

                                            if (p.isAcknowledged() == false) {
                                                AcknowledgePurchaseParams acknowledgePurchaseParams =
                                                        AcknowledgePurchaseParams.newBuilder()
                                                                .setPurchaseToken(p.getPurchaseToken())
                                                                .build();
                                                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

                                            }
                                            boolean isPremiumActive = p.getPurchaseState() == Purchase.PurchaseState.PURCHASED;

                                            if (isPremiumActive) {
                                                Log.d("app5", "subscription active");
                                                RegrannApp.sendEvent("PRO_sub_active");
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putBoolean("subscribed", true);


                                                editor.putBoolean("really_subscribed", true);

                                                editor.apply();
                                                return;

                                            }

                                        }
                                        Log.d("app5", "subscription NOT active");
                                        RegrannApp.sendEvent("PRO_sub_not_active");
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("subscribed", false);


                                        editor.putBoolean("really_subscribed", false);

                                        editor.apply();
                                    } else {
                                        Log.d("app5", "subscription NONE");
                                        RegrannApp.sendEvent("PRO_sub_no_purchases");
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("subscribed", false);


                                        editor.putBoolean("really_subscribed", false);

                                        editor.apply();
                                    }


                                }
                            }
                    );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }

}
