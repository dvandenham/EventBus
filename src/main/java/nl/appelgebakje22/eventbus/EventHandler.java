package nl.appelgebakje22.eventbus;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicLong;

abstract class EventHandler implements EventDispatcher {

	private static final AtomicLong HANDLER_COUNTER = new AtomicLong(0);
	protected final long id = EventHandler.HANDLER_COUNTER.getAndIncrement();
	protected final Class<?> filteredEventType;
	private final Class<?>[] filteredEventGenerics;
	private final int priority;

	EventHandler(Class<?> filteredEventType, Class<?>[] filteredEventGenerics, int priority) {
		this.filteredEventType = filteredEventType;
		this.filteredEventGenerics = filteredEventGenerics;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	boolean canPost(final Event event) {
		if (!this.filteredEventType.isAssignableFrom(event.getClass())) {
			return false;
		}
		final java.lang.reflect.Type genericSuperClass = event.getClass().getGenericSuperclass();
		if (!(genericSuperClass instanceof final ParameterizedType parameterizedType)) {
			return this.filteredEventGenerics == null;
		}
		if (this.filteredEventGenerics == null) {
			return true;
		}
		if (this.filteredEventGenerics.length != parameterizedType.getActualTypeArguments().length) {
			return false;
		}
		for (int i = 0; i < this.filteredEventGenerics.length; ++i) {
			final Class<?> rawType = EventHandler.getRawType(parameterizedType.getActualTypeArguments()[i]);
			if ((this.filteredEventGenerics[i] == null) != (rawType == null)) {
				return false;
			}
			if (this.filteredEventGenerics[i] != null && !this.filteredEventGenerics[i].isAssignableFrom(rawType)) {
				return false;
			}
		}
		return true;
	}

	static Class<?> getRawType(final java.lang.reflect.Type type) {
		return switch (type) {
			case final Class<?> aClass -> aClass;
			case final GenericArrayType genericArrayType -> EventHandler.getRawType(genericArrayType.getGenericComponentType());
			case final ParameterizedType parameterizedType -> EventHandler.getRawType(parameterizedType.getRawType());
			case null, default -> null;
		};
	}

	@Override
	public int hashCode() {
		return (int) this.id;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof final EventHandler h && h.id == this.id;
	}
}
