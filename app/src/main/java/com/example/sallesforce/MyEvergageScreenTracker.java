package com.example.sallesforce;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evergage.android.Campaign;
import com.evergage.android.CampaignHandler;
import com.evergage.android.Evergage;
import com.evergage.android.Screen;

public class MyEvergageScreenTracker extends Activity {

    private Campaign activeCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Substitua com o layout correto
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Screen screen = Evergage.getInstance().getScreenForActivity(this);
        if (screen != null) {
            CampaignHandler handler = new CampaignHandler() {
                @Override
                public void handleCampaign(@NonNull Campaign campaign) {
                    // Validate the campaign data since it's dynamic JSON. Avoid processing if fails.
                    String featuredProductName = campaign.getData().optString("featuredProductName");
                    if (featuredProductName == null || featuredProductName.isEmpty()) {
                        return;
                    }

                    // Check if the same content is already visible/active (see Usage Details above).
                    if (activeCampaign != null && activeCampaign.equals(campaign)) {
                        Log.d("MyEvergageScreenTracker", "Ignoring campaign name " + campaign.getCampaignName() +
                                " since equivalent content is already active");
                        return;
                    }

                    // Track the impression for statistics even if the user is in the control group.
                    screen.trackImpression(campaign);

                    // Only display the campaign if the user is not in the control group.
                    if (!campaign.isControlGroup()) {
                        // Keep active campaign as long as needed for (re)display and comparison
                        activeCampaign = campaign;
                        Log.d("MyEvergageScreenTracker", "New active campaign name " + campaign.getCampaignName() +
                                " for target " + campaign.getTarget() + " with data " + campaign.getData());

                        // Display campaign content
                        TextView featuredProductTextView = findViewById(R.id.featured_product_text);
                        if (featuredProductTextView != null) {
                            featuredProductTextView.setText("Our featured product is " + featuredProductName + "!");
                        }
                    }
                }
            };

            // The target string uniquely identifies the expected data schema - here, a featured product:
            screen.setCampaignHandler(handler, "featuredProduct");
        }
    }
}