package gui.components.listener;

public enum ComponentEventType {
    LIST_UPDATED("listUpdated"),
    LIST_CREATED("listCreated"),
    LIST_DELETED("listDeleted");
    private final String name;

    ComponentEventType(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }
}
