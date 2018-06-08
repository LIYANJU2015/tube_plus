package org.tube.player.mission;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.facebook.ads.Ad;

import org.tube.player.settings.SettingsActivity;
import org.tube.player.util.Constants;
import org.tube.player.util.FBAdUtils;
import org.tube.player.util.Utils;

import com.leiting.mission.service.MissionManagerService;
import com.leiting.mission.ui.fragment.AllMissionsFragment;
import com.leiting.mission.ui.fragment.MissionsFragment;

public class MissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Service
        Intent i = new Intent();
        i.setClass(this, MissionManagerService.class);
        startService(i);

        super.onCreate(savedInstanceState);
        setContentView(org.tube.player.R.layout.activity_downloader);
        Utils.compat(this, ContextCompat.getColor(this, org.tube.player.R.color.color_cccccc));

        Toolbar toolbar = findViewById(org.tube.player.R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(org.tube.player.R.string.downloads_title);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        // Fragment
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateFragments();
                getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        FBAdUtils.get().interstitialLoad(Constants.FB_CHAPING_AD, new FBAdUtils.FBInterstitialAdListener(){
            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                FBAdUtils.get().destoryInterstitial();
            }
        });
    }

    public static void goToGP(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            Intent launchIntent = new Intent();
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + appPackageName));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FBAdUtils.get().isInterstitialLoaded()) {
            FBAdUtils.get().showInterstitial();
        }
        FBAdUtils.get().destoryInterstitial();
    }

    private void updateFragments() {

        MissionsFragment fragment = new AllMissionsFragment();
        getFragmentManager().beginTransaction()
                .replace(org.tube.player.R.id.frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(org.tube.player.R.menu.download_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case org.tube.player.R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
