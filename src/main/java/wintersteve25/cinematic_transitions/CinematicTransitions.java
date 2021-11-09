package wintersteve25.cinematic_transitions;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.PopUpType;
import wintersteve25.cinematic_transitions.renderers.Transition;

@Mod(CinematicTransitions.MODID)
public class CinematicTransitions {
    public static final String MODID = "cinematic_transitions";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public CinematicTransitions() {
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::attack);
    }

    public static void attack(AttackEntityEvent event) {
        Transition.play(true, new PopUp(null, 256, 256, new ResourceLocation(CinematicTransitions.MODID, "textures/test.png"), PopUpType.FADE, true));
    }
}
