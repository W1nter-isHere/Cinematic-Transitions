package wintersteve25.cinematic_transitions.registries;

public class Listener {
    private final String registryName;
    private final String popUpName;
    private final Type type;
    private final boolean transition;

    public Listener(String registryName, String popUpName, Type type, boolean transition) {
        this.registryName = registryName;
        this.popUpName = popUpName;
        this.type = type;
        this.transition = transition;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getPopUpName() {
        return popUpName;
    }

    public Type getType() {
        return type;
    }

    public boolean hasTransition() {
        return transition;
    }

    public enum Type {
        STRUCTURE,
        BIOME,
        DIMENSION,
        ENTITY,
    }
}
