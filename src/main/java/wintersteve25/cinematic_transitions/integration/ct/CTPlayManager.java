package wintersteve25.cinematic_transitions.integration.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.entity.CTEntityIngredient;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import wintersteve25.cinematic_transitions.registries.Listener;
import wintersteve25.cinematic_transitions.registries.ListenerRegistry;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.Transition;

@ZenRegister
@ZenCodeType.Name("mods.cinematic_transitions.PlayManager")
public class CTPlayManager {

    /**
     * @param registryName take the registry name of a PopUp registered using Json, plays the popup provided.
     */
    @ZenCodeType.Method
    public static void playPopUp(String registryName) {
        PopUp popup = PopUp.play(new ResourceLocation(registryName));
        if (popup == null) {
            CraftTweakerAPI.logError("PopUp with name: %s is not found", registryName);
        }
    }

    /**
     * Plays transition (black bars on the top and bottom of screen) with no children (popups)
     */
    @ZenCodeType.Method
    public static void playTransition() {
        Transition.play(false);
    }

    /**
     * @param childName Plays transition with child. Syncs up the child wait duration and transition length to parent's
     */
    @ZenCodeType.Method
    public static void playTransition(String childName) {
        Transition.play(true);
        playPopUp(childName);
    }

    /**
     * @param hideInventory whether if the hotbar, health, etc should be hidden during popup/transition
     */
    @ZenCodeType.Method
    public static void setHideInventory(boolean hideInventory) {
        PopUp.setShouldHideInv(hideInventory);
    }

    /**
     * Registers a listener that triggers given popup when player enters given structure
     * @param structureName registry name of structure
     * @param popUp registry name of popup
     * @param hasTransition whether if transition should be played
     */
    @ZenCodeType.Method
    public static void registerStructurePopUpListener(String structureName, String popUp, boolean hasTransition) {
        ListenerRegistry.registerListener(new Listener(structureName, popUp, Listener.Type.STRUCTURE, hasTransition));
    }

    /**
     * Registers a listener that triggers given popup when player enters given dimension
     * @param dimensionName registry name of dimension
     * @param popUp registry name of popup
     * @param hasTransition whether if transition should be played
     */
    @ZenCodeType.Method
    public static void registerDimensionPopUpListener(String dimensionName, String popUp, boolean hasTransition) {
        ListenerRegistry.registerListener(new Listener(dimensionName, popUp, Listener.Type.DIMENSION, hasTransition));
    }

    /**
     * Registers a listener that triggers given popup when player enters given biome
     * @param biomeName registry name of biome
     * @param popUp registry name of popup
     * @param hasTransition whether if transition should be played
     */
    @ZenCodeType.Method
    public static void registerBiomePopUpListener(String biomeName, String popUp, boolean hasTransition) {
        ListenerRegistry.registerListener(new Listener(biomeName, popUp, Listener.Type.BIOME, hasTransition));
    }

    /**
     * Registers a listener that triggers given popup when given entity spawns
     * @param entityName entity type
     * @param popUp registry name of popup
     * @param hasTransition whether if transition should be played
     */
    @ZenCodeType.Method
    public static void registerEntityPopUpListener(String entityName, String popUp, boolean hasTransition) {
        ListenerRegistry.registerListener(new Listener(entityName, popUp, Listener.Type.ENTITY, hasTransition));
    }
}
