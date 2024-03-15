package com.simibubi.create.foundation.ponder;

import com.simibubi.create.foundation.ponder.element.PonderElement;

import java.util.UUID;

public class ElementLink<T extends PonderElement> {
    private final Class<T> elementClass;
    private final UUID id;

    public ElementLink(Class<T> elementClass) {
        this(elementClass, UUID.randomUUID());
    }

    public ElementLink(Class<T> elementClass, UUID id) {
        this.elementClass = elementClass;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public T cast(PonderElement e) {
        return elementClass.cast(e);
    }

}
