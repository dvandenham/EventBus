import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import nl.appelgebakje22.eventbus.EventBus;
import nl.appelgebakje22.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TestEvent;
import test.TestEvent2;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventTest {

	private static final AtomicInteger BUS_ID = new AtomicInteger();
	private static EventBus bus;
	private static List<Object> list;

	@BeforeEach
	void makeBus() {
		bus = EventBus.getOrCreateBus(String.valueOf(BUS_ID.getAndIncrement()));
		list = new ArrayList<>();
	}

	@Test
	void testPostNull() {
		assertThrows(NullPointerException.class, () -> bus.post(null));
	}

	@Test
	void testPostClassic() {
		bus.register(this);
		TestEvent event = new TestEvent();
		bus.post(event);
		assertSame(event, list.getFirst());
	}

	@Test
	void testPostOnceClassic() {
		bus.register(this);
		TestEvent event = new TestEvent();
		bus.post(event);
		assertSame(1, list.size(), "Events should only arrive once");
	}

	@Test
	void testPostPriorityClassic() {
		bus.register(this);
		bus.post(new TestEvent2());
		assertSame(3, list.get(0));
		assertSame(2, list.get(1));
	}

	@Test
	void testPostModern() {
		bus.register(this::listener1);
		TestEvent event = new TestEvent();
		bus.post(event);
		assertSame(event, list.getFirst());
	}

	@Test
	void testPostOnceModern() {
		bus.register(this::listener1);
		TestEvent event = new TestEvent();
		bus.post(event);
		assertSame(1, list.size(), "Events should only arrive once");
	}

	@Test
	void testPostPriorityModern() {
		bus.register(this::listener2);
		bus.register(this::listener3, 10);
		bus.post(new TestEvent2());
		assertSame(3, list.get(0));
		assertSame(2, list.get(1));
	}

	@Subscribe
	public void listener1(TestEvent e) {
		list.add(e);
	}

	@Subscribe
	public void listener2(TestEvent2 e) {
		list.add(2);
	}

	@Subscribe(priority = 10)
	public void listener3(TestEvent2 e) {
		list.add(3);
	}
}
