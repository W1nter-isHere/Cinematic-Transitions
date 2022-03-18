package wintersteve25.cinematic_transitions.integration.ct;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;
import wintersteve25.cinematic_transitions.renderers.Transition;

@ZenRegister
@ZenCodeType.Name("mods.cinematic_transitions.TransitionManager")
public class CTTransitionManager {
    @ZenCodeType.Method
    public static void setTransitionWaitDuration(int duration) {
        Transition.setMaxWait(duration);
    }

    @ZenCodeType.Method
    public static void setTransitionHeight(float height) {
        Transition.setBarsHeight(height);
    }

    @ZenCodeType.Method
    public static void setTransitionLength(float length) {
        Transition.setTransitionLength(length);
    }
}
