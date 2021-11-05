package wintersteve25.cinematic_transitions.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import wintersteve25.cinematic_transitions.CinematicTransitions;

public class Transition extends AbstractGui {
    public static ResourceLocation BLACK_BARS = new ResourceLocation(CinematicTransitions.MODID, "textures/black.png");

    private final int barsHeight;
    private final float transitionLength;
    private final int round;

    private final int currentPosition;
    private final int currentClosingPosition;
    private final MatrixStack matrixStack;

    private final int width;
    private final int height;
    private final Minecraft minecraft;

    private static int wait = 100;
    private static final int waitLength = 100;

    public Transition(int barsHeight, float transitionLength, int currentPosition, int currentClosingPosition, MatrixStack matrixStack) {
        this.barsHeight = barsHeight;
        this.transitionLength = transitionLength;
        this.currentPosition = currentPosition;
        this.currentClosingPosition = currentClosingPosition;
        this.matrixStack = matrixStack;
        this.minecraft = Minecraft.getInstance();
        this.width = minecraft.getMainWindow().getScaledWidth();
        this.height = minecraft.getMainWindow().getScaledHeight();

        float pixelPerTick = barsHeight / transitionLength;
        round = Math.round(pixelPerTick *currentPosition);
    }

    public boolean render() {
        if (currentPosition >= transitionLength) {
            return renderClosing();
        }

        RenderSystem.pushMatrix();
        RenderSystem.color3f(0, 0, 0);

        minecraft.getTextureManager().bindTexture(BLACK_BARS);

        topOpen();
        bottomOpen();

        RenderSystem.popMatrix();

        return false;
    }

    private boolean renderClosing() {
        wait--;
        if (wait > 0) {
            renderBars();
            return false;
        }

        if (currentClosingPosition >= transitionLength+waitLength) {
            wait = 100;
            return true;
        }

        RenderSystem.pushMatrix();
        RenderSystem.color3f(0, 0, 0);

        minecraft.getTextureManager().bindTexture(BLACK_BARS);

        topClose();
        bottomClose();

        RenderSystem.popMatrix();

        return false;
    }

    private void renderBars() {
        RenderSystem.pushMatrix();
        RenderSystem.color3f(0, 0, 0);

        minecraft.getTextureManager().bindTexture(BLACK_BARS);

        blit(matrixStack, 0, 0, 0, 0, width, barsHeight);
        blit(matrixStack, 0, height-barsHeight, 0, 0, width, barsHeight);

        RenderSystem.popMatrix();
    }

    private void topOpen() {
        blit(matrixStack, 0, 0, 0, 0, width, round);
    }

    private void topClose() {
        blit(matrixStack, 0, 0, 0, 0, width, barsHeight-round);
    }

    private void bottomOpen() {
        blit(matrixStack, 0, height - round, 0, 0, width, round);
    }

    private void bottomClose() {
        blit(matrixStack, 0, (height-barsHeight) + round, 0, 0, width, barsHeight - round);
    }

    public float getTransitionLength() {
        return transitionLength;
    }

    public int getBarsHeight() {
        return barsHeight;
    }
}
