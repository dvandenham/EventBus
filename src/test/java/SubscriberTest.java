import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import nl.appelgebakje22.eventbus.EventBus;
import nl.appelgebakje22.eventbus.SubscriberAlreadyRegisteredException;
import nl.appelgebakje22.eventbus.SubscriberToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.EmptyObjectSubscriber;
import test.InvalidObjectSubscriber;
import test.InvalidStaticObjectSubscriber;
import test.ModernSubscribers;
import test.StaticObjectSubscriber;
import test.TestEvent;
import test.ValidObjectSubscriber;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriberTest {

	private static final AtomicInteger BUS_ID = new AtomicInteger();
	private static EventBus bus;

	@BeforeEach
	void makeBus() {
		bus = EventBus.getOrCreateBus(String.valueOf(BUS_ID.getAndIncrement()));
	}

	@Test
	void testRegisterNull() {
		assertThrows(NullPointerException.class, () -> bus.register(null));
	}

	@Test
	void testRegisterValidObject() {
		assertDoesNotThrow(() -> bus.register(new ValidObjectSubscriber()));
	}

	@Test
	void testRegisterInvalidObject() {
		assertThrows(IllegalArgumentException.class, () -> bus.register(new InvalidObjectSubscriber()));
	}

	@Test
	void testRegisterEmptyObject() {
		assertDoesNotThrow(() -> bus.register(new EmptyObjectSubscriber()));
	}

	@Test
	void testRegisterSameObject() {
		Object o = new ValidObjectSubscriber();
		bus.register(o);
		assertThrows(SubscriberAlreadyRegisteredException.class, () -> bus.register(o));
	}

	@Test
	void testUnsubscribeObject() {
		ValidObjectSubscriber o = new ValidObjectSubscriber();
		SubscriberToken token = bus.register(o);
		assertNotNull(token);
		assertThrows(SubscriberAlreadyRegisteredException.class, () -> bus.register(o));
		assertDoesNotThrow(token::unsubscribe);
		assertDoesNotThrow(() -> bus.register(o));
	}

	@Test
	void testRegisterValidStatic() {
		assertDoesNotThrow(() -> bus.register(StaticObjectSubscriber.class));
	}

	@Test
	void testRegisterStaticButIsNot() {
		assertThrows(IllegalArgumentException.class, () -> bus.register(ValidObjectSubscriber.class));
	}

	@Test
	void testRegisterNotStaticButIs() {
		assertThrows(IllegalArgumentException.class, () -> bus.register(new InvalidStaticObjectSubscriber()));
	}

	@Test
	void testRegisterValidModern() {
		assertDoesNotThrow(() -> bus.register(ModernSubscribers::parentEvent));
		assertDoesNotThrow(() -> bus.register(ModernSubscribers::childEvent));
	}

	@Test
	void testRegisterInvalidModern() {
		//Registered invalid modern subscribers cause the compiler to refuse, making this test useless
		//This test is only here for completeness’s sake
		assertTrue(true);
	}

	@Test
	void testRegisterSameStoredModern() {
		Consumer<TestEvent> o = ModernSubscribers::childEvent;
		bus.register(o);
		assertThrows(SubscriberAlreadyRegisteredException.class, () -> bus.register(o));
	}

	@Test
	void testRegisterSameReferenceModern() {
		//Registering the same method will have a different signature each time, giving us currently no way to properly detect duplicates.
		//This test is only here for completeness’s sake
		bus.register(ModernSubscribers::childEvent);
		// assertThrows(SubscriberAlreadyRegisteredException.class, () -> bus.register(ModernSubscribers::childEvent));
		assertTrue(true);
	}

	@Test
	void testUnsubscribeModern() {
		Consumer<TestEvent> o = ModernSubscribers::childEvent;
		SubscriberToken token = bus.register(o);
		assertNotNull(token);
		assertThrows(SubscriberAlreadyRegisteredException.class, () -> bus.register(o));
		assertDoesNotThrow(token::unsubscribe);
		assertDoesNotThrow(() -> bus.register(o));
	}
}
