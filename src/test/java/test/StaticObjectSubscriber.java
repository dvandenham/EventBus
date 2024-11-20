package test;

import nl.appelgebakje22.eventbus.Event;
import nl.appelgebakje22.eventbus.Subscribe;

public class StaticObjectSubscriber {

	@Subscribe
	public static void listen(Event e) {
	}

	@Subscribe
	public static void listen(TestEvent e) {
	}
}
