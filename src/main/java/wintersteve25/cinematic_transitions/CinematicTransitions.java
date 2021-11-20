package wintersteve25.cinematic_transitions;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.PopUpType;

@Mod(CinematicTransitions.MODID)
public class CinematicTransitions {
    public static final String MODID = "cinematic_transitions";

    public CinematicTransitions() {
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::wow);
    }

    public static void wow(PlayerInteractEvent.RightClickBlock event) {
        PopUp.play(new PopUp(256, 256, new ResourceLocation(CinematicTransitions.MODID, "textures/test"), PopUpType.FADE, PopUpType.FADE, true, false));
    }
}
