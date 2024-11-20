package test;

import nl.appelgebakje22.eventbus.Subscribe;

public class InvalidObjectSubscriber {

	@Subscribe
	public void listen(String s) {
	}
}
