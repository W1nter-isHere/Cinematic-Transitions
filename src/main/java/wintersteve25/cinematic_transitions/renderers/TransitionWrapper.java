package wintersteve25.cinematic_transitions.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TransitionWrapper {
    private int currentPosition = 0;
    private int currentClosingPosition = 0;

    private int barsHeight = 50;
    private float transitionLength = 120;

    public TransitionWrapper() {
    }

    public boolean render(MatrixStack matrix) {
        Transition transition = new Transition(barsHeight, transitionLength, currentPosition, currentClosingPosition, matrix);

        if (transition.render()) {
            currentPosition = 0;
            currentClosingPosition = 0;
            return true;
        } else {
            currentPosition++;
            return false;
        }
    }

    public boolean render(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return true;
        if (Minecraft.getInstance().player == null) return true;
        return render(event.getMatrixStack());
    }

    public int getBarsHeight() {
        return barsHeight;
    }

    public float getTransitionLength() {
        return transitionLength;
    }

    public void setBarsHeight(int barsHeight) {
        this.barsHeight = barsHeight;
    }

    public void setTransitionLength(float transitionLength) {
        this.transitionLength = transitionLength;
    }
}
