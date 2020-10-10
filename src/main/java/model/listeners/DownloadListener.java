package model.listeners;

import model.events.DownloadEvent;

public interface DownloadListener {
    void onComponentChange(DownloadEvent<?> event);
}
