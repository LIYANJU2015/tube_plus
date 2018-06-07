package org.tube.player.database.history.dao;

import org.tube.player.database.BasicDAO;

public interface HistoryDAO<T> extends BasicDAO<T> {
    T getLatestEntry();
}
