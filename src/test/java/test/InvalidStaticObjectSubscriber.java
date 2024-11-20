package test;

import nl.appelgebakje22.eventbus.Subscribe;

public class InvalidStaticObjectSubscriber {

	@Subscribe
	public static void listen(String s) {
	}
}
