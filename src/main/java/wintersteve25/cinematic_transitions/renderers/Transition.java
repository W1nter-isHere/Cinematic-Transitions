package wintersteve25.cinematic_transitions.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import wintersteve25.cinematic_transitions.CinematicTransitions;

public class Transition extends AbstractGui {
    public static final ResourceLocation BLACK_BARS = new ResourceLocation(CinematicTransitions.MODID, "textures/black.png");

    private int round;
    private final float pixelPerTick = getBarsHeight() / transitionLength;

    private final MatrixStack matrixStack;
    private final int width;
    private final int height;

    private static int maxWait = 100;
    private static int wait = maxWait;
    private static float barsHeight = 0.15F;
    private static float transitionLength = 120;
    private static int currentPosition = 0;
    private static int currentClosingPosition = 0;

    private static boolean play = false;
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean hasChild = false;

    public Transition(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
        this.width = minecraft.getMainWindow().getScaledWidth();
        this.height = minecraft.getMainWindow().getScaledHeight();
    }

    public boolean render() {
        if (currentPosition >= transitionLength) {
            return renderClosing();
        }

        currentPosition++;
        round = Math.round(pixelPerTick*currentPosition);

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

        if (currentClosingPosition >= transitionLength) {
            setWait(getMaxWait());
            currentClosingPosition = 0;
            return true;
        }

        currentClosingPosition++;
        round = Math.round(pixelPerTick*currentClosingPosition);

        RenderSystem.pushMatrix();
        RenderSystem.color3f(0, 0, 0);

        minecraft.getTextureManager().bindTexture(BLACK_BARS);

        topClose();
        bottomClose();

        RenderSystem.popMatrix();

        return false;
    }

    public void renderBars() {
        RenderSystem.pushMatrix();
        RenderSystem.color3f(0, 0, 0);

        minecraft.getTextureManager().bindTexture(BLACK_BARS);

        blit(matrixStack, 0, 0, 0, 0, width, getBarsHeight());
        blit(matrixStack, 0, height-getBarsHeight(), 0, 0, width, getBarsHeight());

        RenderSystem.popMatrix();
    }

    private void topOpen() {
        blit(matrixStack, 0, 0, 0, 0, width, round);
    }

    private void topClose() {
        blit(matrixStack, 0, 0, 0, 0, width, getBarsHeight()-round);
    }

    private void bottomOpen() {
        blit(matrixStack, 0, height - round, 0, 0, width, round);
    }

    private void bottomClose() {
        blit(matrixStack, 0, (height-getBarsHeight()) + round, 0, 0, width, getBarsHeight() - round);
    }

    public static float getTransitionLength() {
        return transitionLength;
    }

    public static void setTransitionLength(float transitionLength) {
        Transition.transitionLength = transitionLength;
    }

    public static int getBarsHeight() {
        return Math.round(barsHeight*minecraft.getMainWindow().getScaledHeight());
    }

    /**
     * @param barsHeight should be a percentage eg 25% = 0.25; the percentage of how much the bars gonna take on the screen
     */
    public static void setBarsHeight(float barsHeight) {
        Transition.barsHeight = barsHeight;
    }

    public static int getMaxWait() {
        return maxWait;
    }

    public static void setMaxWait(int maxWait) {
        Transition.maxWait = maxWait;
        Transition.wait = maxWait;
    }

    public static int getWait() {
        return wait;
    }

    public static void setWait(int wait) {
        Transition.wait = wait;
    }

    public static int getCurrentPosition() {
        return currentPosition;
    }

    public static void setCurrentPosition(int currentPosition) {
        Transition.currentPosition = currentPosition;
    }

    public static int getCurrentClosingPosition() {
        return currentClosingPosition;
    }

    public static void setCurrentClosingPosition(int currentClosingPosition) {
        Transition.currentClosingPosition = currentClosingPosition;
    }

    public static void play(boolean hasChild) {
        if (hasChild) {
            Transition.hasChild = true;
        }
        play = true;
    }

    public static void finishPlay() {
        play = false;
    }

    public static boolean shouldPlay() {
        return hasChild ? play && PopUp.shouldPlay() : play;
    }
}
