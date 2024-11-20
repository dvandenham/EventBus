package test;

import nl.appelgebakje22.eventbus.Event;
import nl.appelgebakje22.eventbus.Subscribe;

public class ValidObjectSubscriber {

	@Subscribe
	public void listen(Event e) {
	}

	@Subscribe
	public void listen(TestEvent e) {
	}
}
