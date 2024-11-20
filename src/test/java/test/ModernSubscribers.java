package test;

import nl.appelgebakje22.eventbus.Event;

public class ModernSubscribers {

	public static void parentEvent(Event e) {
	}

	public static void childEvent(TestEvent e) {
	}

	public static void invalidNoParams() {
	}

	public static void invalidParam(String s) {
	}
}
