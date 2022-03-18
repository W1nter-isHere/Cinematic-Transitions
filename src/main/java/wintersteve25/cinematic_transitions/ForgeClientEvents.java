package wintersteve25.cinematic_transitions;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.Transition;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void renderOverlayEventPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Minecraft.getInstance().player == null) return;

        Transition transition = new Transition(event.getMatrixStack());

        if (Transition.shouldPlay()) {
            if (transition.render()) {
                Transition.setCurrentClosingPosition(0);
                Transition.setCurrentPosition(0);
                Transition.finishPlay();
                return;
            }
            if(PopUp.shouldHideInv()) {
                event.setCanceled(true);
            }
        }

        if (PopUp.shouldPlay()) {
            PopUp.getPlay().setMatrixStack(event.getMatrixStack());
            if (PopUp.getPlay().render()) {
                PopUp.setCurrentClosingPosition(0);
                PopUp.setCurrentPosition(0);
                PopUp.finishPlay();
                return;
            }
            if(PopUp.shouldHideInv()) {
                event.setCanceled(true);
            }
        }
    }
}

