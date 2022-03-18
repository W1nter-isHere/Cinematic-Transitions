package wintersteve25.cinematic_transitions.integration.json;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;
import net.minecraft.util.ResourceLocation;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.PopUpType;

public class JsonPopUp extends SimpleObjProvider {
    private final int imageWidth;
    private final int imageHeight;
    private final String textureLocation;
    private final PopUpType entryType;
    private final PopUpType exitType;
    private final boolean hasParent;
    private final boolean langSupport;

    public JsonPopUp(String name, int imageWidth, int imageHeight, String textureLocation, PopUpType entryType, PopUpType exitType, boolean hasParent, boolean langSupport) {
        super(name, false, "PopUp");
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.textureLocation = textureLocation;
        this.entryType = entryType;
        this.exitType = exitType;
        this.hasParent = hasParent;
        this.langSupport = langSupport;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public String getTextureLocation() {
        return textureLocation;
    }

    public PopUpType getEntryType() {
        return entryType;
    }

    public PopUpType getExitType() {
        return exitType;
    }

    public boolean isHasParent() {
        return hasParent;
    }

    public boolean isLangSupport() {
        return langSupport;
    }

    public PopUp serialize() {
        return new PopUp(new ResourceLocation(getName()), imageWidth, imageHeight, new ResourceLocation(textureLocation), entryType, exitType, hasParent, langSupport);
    }
}
