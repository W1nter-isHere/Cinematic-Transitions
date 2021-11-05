package wintersteve25.cinematic_transitions.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wintersteve25.cinematic_transitions.renderers.TransitionWrapper;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ForgeClientEvents {

    private static final TransitionWrapper wrapper = new TransitionWrapper();

    @SubscribeEvent
    public static void renderOverlayEventPre(RenderGameOverlayEvent.Pre event) {
        if (wrapper.render(event)) {
            return;
        }

        event.setCanceled(true);
    }
}
