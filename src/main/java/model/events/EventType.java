package model.events;

public enum EventType {
    LIST("list"),
    DOWNLOAD("download"),
    PROCESSING("processing");


    private final String name;

    EventType(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }
}


