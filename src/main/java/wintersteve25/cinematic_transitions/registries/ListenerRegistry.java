package wintersteve25.cinematic_transitions.registries;

import com.google.common.base.Stopwatch;
import wintersteve25.cinematic_transitions.CinematicTransitions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ListenerRegistry {
    private static final Map<String, Listener> LISTENERS = new HashMap<>();

    public static void registerListener(Listener listener) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        LISTENERS.remove(listener.getRegistryName());
        LISTENERS.put(listener.getRegistryName(), listener);
        stopwatch.stop();
        CinematicTransitions.LOGGER.info("Registering listener took: {}nsec", stopwatch.elapsed(TimeUnit.NANOSECONDS));
    }

    public static Collection<Listener> getListeners() {
        return LISTENERS.values();
    }
}
