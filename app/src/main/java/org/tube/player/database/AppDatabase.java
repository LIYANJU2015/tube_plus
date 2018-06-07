package org.tube.player.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import org.tube.player.database.history.Converters;
import org.tube.player.database.history.dao.SearchHistoryDAO;
import org.tube.player.database.history.dao.WatchHistoryDAO;
import org.tube.player.database.history.model.SearchHistoryEntry;
import org.tube.player.database.history.model.WatchHistoryEntry;
import org.tube.player.database.subscription.SubscriptionDAO;
import org.tube.player.database.subscription.SubscriptionEntity;

@TypeConverters({Converters.class})
@Database(entities = {SubscriptionEntity.class, WatchHistoryEntry.class, SearchHistoryEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "playtube.db";

    public abstract SubscriptionDAO subscriptionDAO();

    public abstract WatchHistoryDAO watchHistoryDAO();

    public abstract SearchHistoryDAO searchHistoryDAO();
}
