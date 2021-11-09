package wintersteve25.cinematic_transitions.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PopUp extends AbstractGui {

    private MatrixStack matrixStack;
    private final int iWidth;
    private final int iHeight;
    private final ResourceLocation texture;
    private final PopUpType entryType;
    private final boolean hasParent;

    private final int wWidth;
    private final int wHeight;
    private final Minecraft minecraft;

    private static PopUp play = null;
    private static int wait = 100;
    private static int currentPosition = 0;
    private static int currentClosingPosition = 0;
    private static float transitionLength = 120;

    public PopUp(MatrixStack matrixStack, int iWidth, int iHeight, ResourceLocation texture, PopUpType entryType, boolean hasParent) {
        this.matrixStack = matrixStack;
        this.iWidth = iWidth;
        this.iHeight = iHeight;
        this.texture = texture;
        this.entryType = entryType;
        this.hasParent = hasParent;
        this.minecraft = Minecraft.getInstance();
        this.wWidth = minecraft.getMainWindow().getScaledWidth();
        this.wHeight = minecraft.getMainWindow().getScaledHeight();
    }

    public boolean render() {
        if (currentPosition >= transitionLength) {
            return renderClosing();
        }

        if (hasParent) {
            PopUp.wait = Transition.getWait();
            PopUp.transitionLength = Transition.getTransitionLength();
        }

        currentPosition++;

        switch (entryType) {
            case FADE:
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.color4f(1, 1, 1, clamp(currentPosition/transitionLength, 0f, 1f));
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                minecraft.getTextureManager().bindTexture(texture);
                if (!hasParent) {
                    blit(matrixStack, (wWidth-iWidth)/2, wHeight/128, 0, 0, iWidth, iHeight);
                } else {
                    blit(matrixStack, (wWidth-iWidth)/2, Transition.getBarsHeight()-wHeight/12, 0, 0, iWidth, iHeight);
                }
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
                break;
//            case ZOOM:

        }

        return false;
    }

    public boolean renderClosing() {
        wait--;
        if (wait > 0) {
            renderTexture();
            return false;
        }

        if (currentClosingPosition >= transitionLength) {
            wait = 100;
            currentClosingPosition = 0;
            return true;
        }

        currentClosingPosition++;
        minecraft.getTextureManager().bindTexture(texture);

        switch (entryType) {
            case FADE:
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.color4f(1, 1, 1, clamp(currentPosition/transitionLength, 0f, 1f));
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                minecraft.getTextureManager().bindTexture(texture);
                if (!hasParent) {
                    blit(matrixStack, (wWidth-iWidth)/2, wHeight/128, 0, 0, iWidth, iHeight);
                } else {
                    blit(matrixStack, (wWidth-iWidth)/2, Transition.getBarsHeight()-wHeight/12, 0, 0, iWidth, iHeight);
                }
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
                break;
//            case ZOOM:

        }

        return false;
    }

    public void renderTexture() {
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(texture);

        if (!hasParent) {
            blit(matrixStack, (wWidth-iWidth)/2, wHeight/128, 0, 0, iWidth, iHeight);
        } else {
            blit(matrixStack, (wWidth-iWidth)/2, Transition.getBarsHeight()-wHeight/12, 0, 0, iWidth, iHeight);
        }

        RenderSystem.popMatrix();
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public static int getWait() {
        return wait;
    }

    public static void setWait(int wait) {
        PopUp.wait = wait;
    }

    public static int getCurrentPosition() {
        return currentPosition;
    }

    public static void setCurrentPosition(int currentPosition) {
        PopUp.currentPosition = currentPosition;
    }

    public static int getCurrentClosingPosition() {
        return currentClosingPosition;
    }

    public static void setCurrentClosingPosition(int currentClosingPosition) {
        PopUp.currentClosingPosition = currentClosingPosition;
    }

    public static PopUp getPlay() {
        return play;
    }

    public static void play(PopUp popUp) {
        play = popUp;
    }

    public static void finishPlay() {
        play = null;
    }

    public static boolean shouldPlay() {
        return play != null;
    }

    private float clamp(float num, float min, float max) {
        if (num < min) return min;
        return Math.min(num, max);
    }
}
