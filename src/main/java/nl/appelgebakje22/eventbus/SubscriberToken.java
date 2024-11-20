package nl.appelgebakje22.eventbus;

import java.util.function.Consumer;

public final class SubscriberToken {

	private final Object handler;
	private final Consumer<Object> unsubscribeFunction;

	SubscriberToken(Object handler, Consumer<Object> unsubscribeFunction) {
		this.handler = handler;
		this.unsubscribeFunction = unsubscribeFunction;
	}

	public void unsubscribe() {
		this.unsubscribeFunction.accept(this.handler);
	}
}
