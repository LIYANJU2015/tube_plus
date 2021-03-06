package org.tube.player.player.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import org.tube.player.Downloader;

import java.io.File;

public class CacheFactory implements DataSource.Factory {
    private static final String TAG = "CacheFactory";
    private static final String CACHE_FOLDER_NAME = "exoplayer";
    private static final int CACHE_FLAGS = CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR;

    private final DefaultDataSourceFactory dataSourceFactory;
    private final File cacheDir;
    private final long maxFileSize;

    // Creating cache on every instance may cause problems with multiple players when
    // sources are not ExtractorMediaSource
    // see: https://stackoverflow.com/questions/28700391/using-cache-in-exoplayer
    // todo: make this a singleton?
    private static SimpleCache cache;

    public CacheFactory(@NonNull final Context context) {
        this(context, PlayerHelper.getPreferredCacheSize(context), PlayerHelper.getPreferredFileSize(context));
    }

    CacheFactory(@NonNull final Context context, final long maxCacheSize, final long maxFileSize) {
        super();
        this.maxFileSize = maxFileSize;

        final String userAgent = Downloader.USER_AGENT;
        final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(context, userAgent, bandwidthMeter);

        cacheDir = new File(context.getExternalCacheDir(), CACHE_FOLDER_NAME);
        if (!cacheDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdir();
        }

        if (cache == null) {
            final LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
            cache = new SimpleCache(cacheDir, evictor);
        }
    }

    @Override
    public DataSource createDataSource() {
        Log.d(TAG, "initExoPlayerCache: cacheDir = " + cacheDir.getAbsolutePath());

        final DefaultDataSource dataSource = dataSourceFactory.createDataSource();
        final FileDataSource fileSource = new FileDataSource();
        final CacheDataSink dataSink = new CacheDataSink(cache, maxFileSize);

        return new CacheDataSource(cache, dataSource, fileSource, dataSink, CACHE_FLAGS, null);
    }

    public void tryDeleteCacheFiles() {
        if (!cacheDir.exists() || !cacheDir.isDirectory()) return;

        try {
            for (File file : cacheDir.listFiles()) {
                final String filePath = file.getAbsolutePath();
                final boolean deleteSuccessful = file.delete();

                Log.d(TAG, "tryDeleteCacheFiles: " + filePath + " deleted = " + deleteSuccessful);
            }
        } catch (Exception ignored) {
            Log.e(TAG, "Failed to delete file.", ignored);
        }
    }
}