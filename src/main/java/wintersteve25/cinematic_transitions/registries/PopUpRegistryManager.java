package wintersteve25.cinematic_transitions.registries;

import net.minecraft.util.ResourceLocation;
import wintersteve25.cinematic_transitions.renderers.PopUp;

import java.util.HashMap;
import java.util.Map;

public class PopUpRegistryManager {
    private static final Map<ResourceLocation, PopUp> REGISTRY = new HashMap<>();

    public static void registerPopUp(PopUp popUp) {
        REGISTRY.remove(popUp.getRegistryName());
        REGISTRY.put(popUp.getRegistryName(), popUp);
    }

    public static PopUp getPopUpWithRegistryName(ResourceLocation registryName) {
        return REGISTRY.get(registryName);
    }
}
