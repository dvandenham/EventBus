package nl.appelgebakje22.eventbus;

import java.util.function.Consumer;

final class ModernEventHandler extends EventHandler {

	private final Consumer<Event> consumer;

	ModernEventHandler(final Class<?> filteredEventType, final int priority, final Consumer<Event> consumer) {
		super(filteredEventType, null, priority);
		this.consumer = consumer;
	}

	@Override
	public void postEvent(Event event) {
		consumer.accept(event);
	}
}
