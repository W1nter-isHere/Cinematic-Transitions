package wintersteve25.cinematic_transitions.integration.ct;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;
import wintersteve25.cinematic_transitions.renderers.PopUp;

@ZenRegister
@ZenCodeType.Name("mods.cinematic_transitions.PopUpManager")
public class CTPopUpManager {
    @ZenCodeType.Method
    public static void setPopUpLength(float length) {
        PopUp.setTransitionLength(length);
    }

    @ZenCodeType.Method
    public static void setPopUpWaitDuration(int maxWait) {
        PopUp.setMaxWait(maxWait);
    }

    @ZenCodeType.Method
    public static void setPreRenderModification(Runnable modification) {
        PopUp.setPreMod(modification);
    }

    @ZenCodeType.Method
    public static void setPostRenderModification(Runnable modification) {
        PopUp.setPostMod(modification);
    }

    @ZenCodeType.Method
    public static void setTranslateModifier(float x, float y, float z) {
        PopUp.setTranslateModifier(x, y, z);
    }

    @ZenCodeType.Method
    public static void setColorModifier(float red, float green, float blue, float alpha) {
        PopUp.setColorModifier(red, green, blue, alpha);
    }

    @ZenCodeType.Method
    public static void setScaleModifier(float x, float y, float z) {
        PopUp.setScaleModifier(x, y, z);
    }

    @ZenCodeType.Method
    public static void disableTranslateModifier() {
        PopUp.disableTranslateModifier();
    }

    @ZenCodeType.Method
    public static void disableColorModifier() {
        PopUp.disableColorModifier();
    }

    @ZenCodeType.Method
    public static void disableScaleModifier() {
        PopUp.disableScaleModifier();
    }
}
