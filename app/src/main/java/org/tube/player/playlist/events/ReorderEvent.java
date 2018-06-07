package org.tube.player.playlist.events;

public class ReorderEvent implements PlayQueueEvent {
    @Override
    public PlayQueueEventType type() {
        return PlayQueueEventType.REORDER;
    }

    public ReorderEvent() {

    }
}
