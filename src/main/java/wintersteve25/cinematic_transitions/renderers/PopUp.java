package wintersteve25.cinematic_transitions.renderers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;
import wintersteve25.cinematic_transitions.registries.PopUpRegistryManager;

public class PopUp extends Screen {

    private MatrixStack matrixStack;
    private final int iWidth;
    private final int iHeight;
    private final ResourceLocation texture;
    private final ResourceLocation ogTexture;
    private final PopUpType entryType;
    private final PopUpType exitType;
    private final boolean hasParent;
    private final ResourceLocation registryName;
    private final Minecraft minecraft;

    private int wWidth;
    private int wHeight;

    private static PopUp play = null;
    private static int maxWait = 100;
    private static int wait = maxWait;
    private static int currentPosition = 0;
    private static int currentClosingPosition = 0;
    private static float transitionLength = 120;
    private static boolean shouldHideInv = false;

    private static Runnable preMod, postMod = null;

    private static float scaleModX, scaleModY, scaleModZ = 1;
    private static boolean enableScaleMod = false;
    private static float colorModRed, colorModGreen, colorModBlue, colorModAlpha = 1;
    private static boolean enableColorMod = false;
    private static float translateModX, translateModY = 1;
    private static float translateModZ = 0;
    private static boolean enableTranslateMod = false;

    public PopUp(ResourceLocation registryName, int iWidth, int iHeight, ResourceLocation texture, PopUpType entryType, PopUpType exitType, boolean hasParent, boolean langSupport) {
        super(StringTextComponent.EMPTY);
        this.registryName = registryName;
        this.iWidth = iWidth;
        this.iHeight = iHeight;
        this.entryType = entryType;
        this.exitType = exitType == null ? entryType : exitType;
        this.hasParent = hasParent;
        this.minecraft = Minecraft.getInstance();
        this.wWidth = minecraft.getMainWindow().getScaledWidth();
        this.wHeight = minecraft.getMainWindow().getScaledHeight();
        this.texture = langSupport ? new ResourceLocation(texture.getNamespace(), texture.getPath() + "_" + minecraft.getLanguageManager().getCurrentLanguage().getCode() + ".png") : new ResourceLocation(texture.getNamespace(), texture.getPath() + ".png");
        this.ogTexture = new ResourceLocation(texture.getNamespace(), texture.getPath() + ".png");
    }

    public boolean render() {
        // HACK: This has to be done this way since init is not called. so there is no other way I can think of to change the value when changing window size
        this.wWidth = minecraft.getMainWindow().getScaledWidth();
        this.wHeight = minecraft.getMainWindow().getScaledHeight();

        if (currentPosition >= transitionLength) {
            return renderClosing();
        }

        if (hasParent) {
            setWait(Transition.getWait());
            setMaxWait(Transition.getMaxWait());
            PopUp.transitionLength = Transition.getTransitionLength();
        }

        currentPosition++;

        float percentage = clamp(currentPosition / transitionLength, 0f, 1f);
        renderMain(percentage, entryType);

        return false;
    }

    public boolean renderClosing() {
        wait--;
        if (wait > 0) {
            renderTexture();
            return false;
        }

        if (currentClosingPosition >= transitionLength) {
            setWait(getMaxWait());
            currentClosingPosition = 0;
            return true;
        }

        currentClosingPosition++;

        float percentage = clamp(1 - (currentClosingPosition / transitionLength), 0f, 1f);
        renderMain(percentage, exitType);

        return false;
    }

    public void renderTexture() {
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        bindTexture();

        runModifier(true);
        if (!hasParent) {
            blit(matrixStack, (wWidth - iWidth) / 2, wHeight / 128, getBlitOffset(), 0, 0, iWidth, iHeight, iHeight, iWidth);
        } else {
            blit(matrixStack, (wWidth - iWidth) / 2, Transition.getBarsHeight() - wHeight / 12, getBlitOffset(),0, 0, iWidth, iHeight, iHeight, iWidth);
        }
        runModifier(false);

        RenderSystem.popMatrix();
    }

    private void renderMain(float percentage, PopUpType type) {
        float v = ((wWidth - iWidth) / 2f) + iWidth / 2f;
        float h = (wHeight / 128f) + iHeight / 2f;

        switch (type) {
            case FADE:
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.color4f(1, 1, 1, percentage);
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                bindTexture();
                runModifier(true);
                if (!hasParent) {
                    blit(matrixStack, (wWidth - iWidth) / 2, wHeight / 128, getBlitOffset(), 0, 0, iWidth, iHeight, iHeight, iWidth);
                } else {
                    blit(matrixStack, (wWidth - iWidth) / 2, Transition.getBarsHeight() - wHeight / 12, getBlitOffset(),0, 0, iWidth, iHeight, iHeight, iWidth);
                }
                runModifier(false);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
                break;
            case ZOOM_TO_BIG:
                RenderSystem.pushMatrix();
                RenderSystem.translatef(v, h, 0);
                RenderSystem.scalef(percentage, percentage, percentage);
                RenderSystem.translatef(-v, -h, 0);
                RenderSystem.color4f(1, 1, 1, 1);
                bindTexture();
                runModifier(true);
                if (!hasParent) {
                    blit(matrixStack, (wWidth - iWidth) / 2, wHeight / 128, getBlitOffset(), 0, 0, iWidth, iHeight, iHeight, iWidth);
                } else {
                    blit(matrixStack, (wWidth - iWidth) / 2, Transition.getBarsHeight() - wHeight / 12, getBlitOffset(), 0, 0, iWidth, iHeight, iHeight, iWidth);
                }
                runModifier(false);
                RenderSystem.popMatrix();
                break;
        }
    }

    private void bindTexture() {
        if (!minecraft.getResourceManager().hasResource(texture)) {
            minecraft.getTextureManager().bindTexture(ogTexture);
        } else {
            minecraft.getTextureManager().bindTexture(texture);
        }
    }

    private void runModifier(boolean pre) {
        if (pre && preMod != null) {
            preMod.run();
        }
        if (!pre && postMod != null) {
            postMod.run();
        }

        if (pre) {
            if (enableTranslateMod) RenderSystem.translatef(translateModX, translateModY, translateModZ);
            if (enableColorMod) RenderSystem.color4f(colorModRed, colorModGreen, colorModBlue, colorModAlpha);
            if (enableScaleMod) RenderSystem.scalef(scaleModX, scaleModY, scaleModZ);
        }
    }

    public static void setTranslateModifier(float x, float y, float z) {
        enableTranslateMod = true;
        translateModX = x;
        translateModY = y;
        translateModZ = z;
    }

    public static void setColorModifier(float red, float green, float blue, float alpha) {
        enableColorMod = true;
        colorModRed = red;
        colorModGreen = green;
        colorModBlue = blue;
        colorModAlpha = alpha;
    }

    public static void setScaleModifier(float x, float y, float z) {
        enableScaleMod = true;
        scaleModX = x;
        scaleModY = y;
        scaleModZ = z;
    }

    public static void disableTranslateModifier() {
        enableTranslateMod = false;
        translateModX = 1;
        translateModY = 1;
        translateModZ = 0;
    }

    public static void disableColorModifier() {
        enableColorMod = false;
        colorModRed = 1;
        colorModGreen = 1;
        colorModBlue = 1;
        colorModAlpha = 1;
    }

    public static void disableScaleModifier() {
        enableScaleMod = false;
        scaleModX = 1;
        scaleModY = 1;
        scaleModZ = 1;
    }

    public static void setPreMod(Runnable preMod) {
        PopUp.preMod = preMod;
    }

    public static void setPostMod(Runnable postMod) {
        PopUp.postMod = postMod;
    }

    public static boolean shouldHideInv() {
        return shouldHideInv;
    }

    public static void setShouldHideInv(boolean shouldHideInv) {
        PopUp.shouldHideInv = shouldHideInv;
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public static int getMaxWait() {
        return maxWait;
    }

    public static void setMaxWait(int maxWait) {
        PopUp.maxWait = maxWait;
        PopUp.wait = maxWait;
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

    public static float getTransitionLength() {
        return transitionLength;
    }

    public static void setTransitionLength(float transitionLength) {
        PopUp.transitionLength = transitionLength;
    }

    public static PopUp getPlay() {
        return play;
    }

    public static void play(PopUp popUp) {
        play = popUp;
    }

    public static PopUp play(ResourceLocation popUp) {
        play = PopUpRegistryManager.getPopUpWithRegistryName(popUp);
        return play;
    }

    public static void finishPlay() {
        play = null;
        disableScaleModifier();
        disableColorModifier();
        disableTranslateModifier();
    }

    public static boolean shouldPlay() {
        return play != null;
    }

    private float clamp(float num, float min, float max) {
        if (num < min) return min;
        return Math.min(num, max);
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
