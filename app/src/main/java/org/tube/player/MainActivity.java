/*
 * Created by Christian Schabesberger on 02.08.16.
 * <p>
 * Copyright (C) Christian Schabesberger 2016 <chris.schabesberger@mailbox.org>
 * DownloadActivity.java is part of NewPipe.
 * <p>
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.tube.player;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.tube.player.database.AppDatabase;
import org.tube.player.database.history.dao.HistoryDAO;
import org.tube.player.database.history.dao.SearchHistoryDAO;
import org.tube.player.database.history.dao.WatchHistoryDAO;
import org.tube.player.database.history.model.HistoryEntry;
import org.tube.player.database.history.model.SearchHistoryEntry;
import org.tube.player.database.history.model.WatchHistoryEntry;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.tube.player.fragments.BackPressable;
import org.tube.player.fragments.detail.VideoDetailFragment;
import org.tube.player.fragments.list.search.SearchFragment;
import org.tube.player.history.HistoryListener;
import org.tube.player.util.Constants;
import org.tube.player.util.FBAdUtils;
import org.tube.player.util.FacebookReport;
import org.tube.player.util.NavigationHelper;
import org.tube.player.util.StateSaver;
import org.tube.player.util.Utils;

import java.util.Date;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import com.leiting.mission.util.Utility;

public class MainActivity extends AppCompatActivity implements HistoryListener {
    private static final String TAG = "MainActivity";
    public static final boolean DEBUG = true;
    private SharedPreferences sharedPreferences;

    /*//////////////////////////////////////////////////////////////////////////
    // Activity's LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(org.tube.player.R.layout.activity_main);
        Utils.compat(this, ContextCompat.getColor(this, org.tube.player.R.color.color_cccccc));

        if (getSupportFragmentManager() != null && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            initFragments();
        }

        Toolbar toolbar = findViewById(org.tube.player.R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initHistory();

        FBAdUtils.get().showAdDialog(this, Constants.FB_NATIVE_DIALOG);

        FacebookReport.logSentMainPageShow();

        Utils.checkAndRequestPermissions(this);

        if (App.sPreferences.getBoolean("canRefer", true)) {
            Utility.runUIThread(new Runnable() {
                @Override
                public void run() {
                    App.sPreferences.edit().putBoolean("canRefer", false).apply();
                }
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            StateSaver.clearStateFiles();
        }

        disposeHistory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FBAdUtils.get().loadFBAds(Constants.FB_NATIVE_AD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(Constants.KEY_THEME_CHANGE, false)) {
            if (DEBUG) Log.d(TAG, "Theme has changed, recreating activity...");
            sharedPreferences.edit().putBoolean(Constants.KEY_THEME_CHANGE, false).apply();
            // https://stackoverflow.com/questions/10844112/runtimeexception-performing-pause-of-activity-that-is-not-resumed
            // Briefly, let the activity resume properly posting the recreate call to end of the message queue
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.recreate();
                }
            });
        }

        if(sharedPreferences.getBoolean(Constants.KEY_MAIN_PAGE_CHANGE, false)) {
            if (DEBUG) Log.d(TAG, "main page has changed, recreating main fragment...");
            sharedPreferences.edit().putBoolean(Constants.KEY_MAIN_PAGE_CHANGE, false).apply();
            NavigationHelper.openMainActivity(this);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (DEBUG) Log.d(TAG, "onNewIntent() called with: intent = [" + intent + "]");
        if (intent != null) {
            // Return if launched from a launcher (e.g. Nova Launcher, Pixel Launcher ...)
            // to not destroy the already created backstack
            String action = intent.getAction();
            if ((action != null && action.equals(Intent.ACTION_MAIN)) && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) return;
        }

        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (DEBUG) Log.d(TAG, "onBackPressed() called");

        Fragment fragment = getSupportFragmentManager().findFragmentById(org.tube.player.R.id.fragment_holder);
        // If current fragment implements BackPressable (i.e. can/wanna handle back press) delegate the back press to it
        if (fragment instanceof BackPressable) {
            if (((BackPressable) fragment).onBackPressed()) return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Menu
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DEBUG) Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "]");
        super.onCreateOptionsMenu(menu);

        Fragment fragment = getSupportFragmentManager().findFragmentById(org.tube.player.R.id.fragment_holder);
        if (!(fragment instanceof VideoDetailFragment)) {
            findViewById(org.tube.player.R.id.toolbar).findViewById(org.tube.player.R.id.toolbar_spinner).setVisibility(View.GONE);
        }

        if (!(fragment instanceof SearchFragment)) {
            findViewById(org.tube.player.R.id.toolbar).findViewById(org.tube.player.R.id.toolbar_search_container).setVisibility(View.GONE);

            MenuInflater inflater = getMenuInflater();
            if (App.isSuper()) {
                inflater.inflate(org.tube.player.R.menu.main_menu, menu);
            } else {
                inflater.inflate(org.tube.player.R.menu.main_menu2, menu);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DEBUG) Log.d(TAG, "onOptionsItemSelected() called with: item = [" + item + "]");
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavigationHelper.gotoMainFragment(getSupportFragmentManager());
                return true;
            case org.tube.player.R.id.action_settings:
                NavigationHelper.openSettings(this);
                return true;
            case org.tube.player.R.id.action_show_downloads:
                return NavigationHelper.openDownloads(this);
            case org.tube.player.R.id.action_history:
                NavigationHelper.openHistory(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Init
    //////////////////////////////////////////////////////////////////////////*/

    private void initFragments() {
        if (DEBUG) Log.d(TAG, "initFragments() called");
        StateSaver.clearStateFiles();
        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_LINK_TYPE)) {
            handleIntent(getIntent());
        } else NavigationHelper.gotoMainFragment(getSupportFragmentManager());
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private void handleIntent(Intent intent) {
        if (DEBUG) Log.d(TAG, "handleIntent() called with: intent = [" + intent + "]");

        if (intent.hasExtra(Constants.KEY_LINK_TYPE)) {
            String url = intent.getStringExtra(Constants.KEY_URL);
            int serviceId = intent.getIntExtra(Constants.KEY_SERVICE_ID, 0);
            String title = intent.getStringExtra(Constants.KEY_TITLE);
            switch (((StreamingService.LinkType) intent.getSerializableExtra(Constants.KEY_LINK_TYPE))) {
                case STREAM:
                    boolean autoPlay = intent.getBooleanExtra(VideoDetailFragment.AUTO_PLAY, false);
                    NavigationHelper.openVideoDetailFragment(getSupportFragmentManager(), serviceId, url, title, autoPlay);
                    break;
                case CHANNEL:
                    NavigationHelper.openChannelFragment(getSupportFragmentManager(), serviceId, url, title);
                    break;
                case PLAYLIST:
                    NavigationHelper.openPlaylistFragment(getSupportFragmentManager(), serviceId, url, title);
                    break;
            }
        } else if (intent.hasExtra(Constants.KEY_OPEN_SEARCH)) {
            String searchQuery = intent.getStringExtra(Constants.KEY_QUERY);
            if (searchQuery == null) searchQuery = "";
            int serviceId = intent.getIntExtra(Constants.KEY_SERVICE_ID, 0);
            NavigationHelper.openSearchFragment(getSupportFragmentManager(), serviceId, searchQuery);
        } else {
            NavigationHelper.gotoMainFragment(getSupportFragmentManager());
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // History
    //////////////////////////////////////////////////////////////////////////*/

    private WatchHistoryDAO watchHistoryDAO;
    private SearchHistoryDAO searchHistoryDAO;
    private PublishSubject<HistoryEntry> historyEntrySubject;
    private Disposable disposable;

    private void initHistory() {
        final AppDatabase database = NewPipeDatabase.getInstance();
        watchHistoryDAO = database.watchHistoryDAO();
        searchHistoryDAO = database.searchHistoryDAO();
        historyEntrySubject = PublishSubject.create();
        disposable = historyEntrySubject
                .observeOn(Schedulers.io())
                .subscribe(getHistoryEntryConsumer());
    }

    private void disposeHistory() {
        if (disposable != null) disposable.dispose();
        watchHistoryDAO = null;
        searchHistoryDAO = null;
    }

    @NonNull
    private Consumer<HistoryEntry> getHistoryEntryConsumer() {
        return new Consumer<HistoryEntry>() {
            @Override
            public void accept(HistoryEntry historyEntry) throws Exception {
                //noinspection unchecked
                HistoryDAO<HistoryEntry> historyDAO = (HistoryDAO<HistoryEntry>)
                        (historyEntry instanceof SearchHistoryEntry ? searchHistoryDAO : watchHistoryDAO);

                HistoryEntry latestEntry = historyDAO.getLatestEntry();
                if (historyEntry.hasEqualValues(latestEntry)) {
                    latestEntry.setCreationDate(historyEntry.getCreationDate());
                    historyDAO.update(latestEntry);
                } else {
                    historyDAO.insert(historyEntry);
                }
            }
        };
    }

    private void addWatchHistoryEntry(StreamInfo streamInfo) {
        if (sharedPreferences.getBoolean(getString(org.tube.player.R.string.enable_watch_history_key), true)) {
            WatchHistoryEntry entry = new WatchHistoryEntry(streamInfo);
            historyEntrySubject.onNext(entry);
        }
    }

    @Override
    public void onVideoPlayed(StreamInfo streamInfo, @Nullable VideoStream videoStream) {
        addWatchHistoryEntry(streamInfo);
    }

    @Override
    public void onAudioPlayed(StreamInfo streamInfo, AudioStream audioStream) {
        addWatchHistoryEntry(streamInfo);
    }

    @Override
    public void onSearch(int serviceId, String query) {
        // Add search history entry
        if (sharedPreferences.getBoolean(getString(org.tube.player.R.string.enable_search_history_key), true)) {
            SearchHistoryEntry searchHistoryEntry = new SearchHistoryEntry(new Date(), serviceId, query);
            historyEntrySubject.onNext(searchHistoryEntry);
        }
    }
}
