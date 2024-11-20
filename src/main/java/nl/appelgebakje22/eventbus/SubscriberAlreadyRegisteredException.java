package nl.appelgebakje22.eventbus;

public final class SubscriberAlreadyRegisteredException extends RuntimeException {

	SubscriberAlreadyRegisteredException(String handler) {
		super(handler);
	}
}
