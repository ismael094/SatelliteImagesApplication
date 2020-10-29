package model.events;

public enum ComponentEventType {
    LIST_UPDATED("listUpdated"),
    LIST_CREATED("listCreated"),
    LIST_DELETED("listDeleted"),
    DOWNLOAD_COMPLETED("downloadCompleted"),
    DOWNLOAD_STARTED("started"),
    DOWNLOAD_ERROR("error"),
    PROCESSING_COMPLETED("completed"),
    PROCESSING_STARTED("started"),
    PROCESSING_PREVIEW("preview"),
    PROCESSING_ERROR("error");


    private final String name;

    ComponentEventType(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }
}