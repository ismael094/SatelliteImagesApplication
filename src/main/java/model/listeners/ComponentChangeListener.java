package model.listeners;

import model.events.ToolbarComponentEvent;

public interface ComponentChangeListener {
    void onComponentChange(ToolbarComponentEvent event);
}
