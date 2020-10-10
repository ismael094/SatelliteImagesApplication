package model.events;

import java.util.EventObject;

public class DownloadEvent<T> extends EventObject {
    private T event;
    public DownloadEvent(Object source, T event) {
        super(source);
        this.event = event;
    }
    public T getEvent() { return event; }
}
