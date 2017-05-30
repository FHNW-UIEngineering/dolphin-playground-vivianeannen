package myapp.controller;

import org.opendolphin.core.server.EventBus;

/**
 * @author Dieter Holz
 */
public enum Bus {
    PERSON;

    private final EventBus eventBus = new EventBus();

    public EventBus getEventBus() {
        return eventBus;
    }
}
