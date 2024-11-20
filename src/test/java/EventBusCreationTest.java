import nl.appelgebakje22.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class EventBusCreationTest {

	@Test
	void testCreateWithNull() {
		assertDoesNotThrow(() -> EventBus.getOrCreateBus(null));
		assertEquals("default", EventBus.getOrCreateBus(null).getBusName(), "The default EventBus name should be 'default'");
	}

	@Test
	void testCreateWithEmpty() {
		assertDoesNotThrow(() -> EventBus.getOrCreateBus(""));
		assertEquals("default", EventBus.getOrCreateBus("").getBusName(), "An empty String as name should default to the null-name");
	}

	@Test
	void testCreateWithWhitespace() {
		assertDoesNotThrow(() -> EventBus.getOrCreateBus("    "));
		assertEquals("default", EventBus.getOrCreateBus("    ").getBusName(), "The name of an EventBus should be trimmed");
		assertEquals("test", EventBus.getOrCreateBus(" test   ").getBusName(), "The name of an EventBus should be trimmed");
	}

	@Test
	void testCreateWithNotNull() {
		assertDoesNotThrow(() -> EventBus.getOrCreateBus("testeventbus"));
		assertEquals("testeventbus", EventBus.getOrCreateBus("testeventbus").getBusName());
	}

	@Test
	void testCreateSameNameTwice() {
		EventBus bus1 = EventBus.getOrCreateBus("bus1");
		EventBus bus2 = EventBus.getOrCreateBus("bus1");
		assertSame(bus1, bus2);
		assertEquals(bus1, bus2);
	}

	@Test
	void testCreateDifferentName() {
		EventBus bus1 = EventBus.getOrCreateBus("bus1");
		EventBus bus2 = EventBus.getOrCreateBus("bus2");
		assertNotSame(bus1, bus2);
		assertNotEquals(bus1, bus2);
	}
}
