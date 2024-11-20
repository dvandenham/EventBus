package nl.appelgebakje22.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.jodah.typetools.TypeResolver;

public final class EventBus {

	private static final Map<String, EventBus> EVENT_BUS_CACHE = new ConcurrentHashMap<>();
	private final Map<Object, List<EventHandler>> handlerMapping = new ConcurrentHashMap<>();
	private final Set<EventHandler> sortedHandlers = new TreeSet<>((o1, o2) -> o1.getPriority() == o2.getPriority() ? -1 : Integer.compare(o2.getPriority(), o1.getPriority()));
	private final Map<Object, SubscriberToken> tokens = new ConcurrentHashMap<>();
	private final String busName;

	private EventBus(final String busName) {
		this.busName = busName;
	}

	public String getBusName() {
		return busName;
	}

	public SubscriberToken register(final Object object) {
		Objects.requireNonNull(object);
		if (this.handlerMapping.containsKey(object)) {
			throw new SubscriberAlreadyRegisteredException(object instanceof final Class<?> cls ? cls.getName() : object.getClass().getName());
		}
		final Method[] methods = EventBus.findListenerMethods(object);
		final List<ClassicEventHandler> eventHandlers = new ArrayList<>();
		for (final Method method : methods) {
			eventHandlers.add(new ClassicEventHandler(method, object instanceof Class ? null : object));
		}
		this.handlerMapping.put(object, Collections.unmodifiableList(eventHandlers));
		this.sortedHandlers.addAll(eventHandlers);
		this.tokens.put(object, new SubscriberToken(object, obj -> {
			final List<EventHandler> handlers = this.handlerMapping.get(obj);
			if (handlers != null) {
				handlers.forEach(h -> this.sortedHandlers.removeIf(h2 -> h2.id == h.id));
			}
			this.handlerMapping.remove(obj);
			this.tokens.remove(obj);
		}));
		return this.tokens.get(object);
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> SubscriberToken register(final Consumer<T> consumer, final int priority) {
		Objects.requireNonNull(consumer);
		if (this.handlerMapping.containsKey(consumer)) {
			throw new SubscriberAlreadyRegisteredException(Objects.toString(consumer));
		}
		final Class<T> eventType = (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
		final ModernEventHandler handler = new ModernEventHandler(eventType, priority, (Consumer<Event>) consumer);
		this.handlerMapping.put(consumer, List.of(handler));
		this.sortedHandlers.add(handler);
		this.tokens.put(consumer, new SubscriberToken(consumer, obj -> {
			final List<EventHandler> handlers = this.handlerMapping.get(obj);
			if (handlers != null) {
				handlers.forEach(h -> this.sortedHandlers.removeIf(h2 -> h2.id == h.id));
			}
			this.handlerMapping.remove(obj);
			this.tokens.remove(obj);
		}));
		return this.tokens.get(consumer);
	}

	public <T extends Event> SubscriberToken register(final Consumer<T> consumer) {
		return this.register(consumer, 0);
	}

	public void post(final Event event) {
		Objects.requireNonNull(event);
		for (final EventHandler handler : this.sortedHandlers) {
			if (handler.canPost(event)) {
				handler.postEvent(event);
			}
		}
	}

	private static Method[] findListenerMethods(final Object object) {
		final Class<?> clazz = object instanceof final Class<?> cls ? cls : object.getClass();
		final List<Method> result = new ArrayList<>();
		for (final Method method : clazz.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(Subscribe.class)) {
				continue;
			}
			if (clazz != object && Modifier.isStatic(method.getModifiers())) {
				throw new IllegalArgumentException("Static event subscriber %s cannot be registered, only non-static methods are allowed when registering instanced objects".formatted(method.toString()));
			}
			if (clazz == object && !Modifier.isStatic(method.getModifiers())) {
				throw new IllegalArgumentException("Non-static event subscriber %s cannot be registered, only static methods are allowed when registering classes".formatted(method.toString()));
			}
			if (method.getReturnType() != Void.TYPE) {
				throw new IllegalArgumentException("Event subscriber %s must return void".formatted(method.toString()));
			}
			if (method.getParameterCount() != 1) {
				throw new IllegalArgumentException("Event subscriber %s must have exactly 1 parameter".formatted(method.toString()));
			}
			if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				throw new IllegalArgumentException("Parameter of event subscriber %s must extend %s".formatted(method.toString(), Event.class.getName()));
			}
			result.add(method);
		}
		return result.toArray(Method[]::new);
	}

	public static EventBus getOrCreateBus(String busName) {
		if (busName == null || busName.trim().isBlank()) {
			busName = "default";
		}
		busName = busName.trim();
		return EventBus.EVENT_BUS_CACHE.computeIfAbsent(busName, EventBus::new);
	}
}
