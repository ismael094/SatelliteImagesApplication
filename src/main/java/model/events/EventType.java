package model.events;

public class EventType {
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

    public enum DownloadEventType {
        COMPLETED("completed"),
        ERROR("error");
        private final String name;

        DownloadEventType(String name) {
            this.name = name;
        }

        public String getParameterName() {
            return name;
        }
    }
}


