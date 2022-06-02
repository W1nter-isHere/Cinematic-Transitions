package wintersteve25.cinematic_transitions;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.google.common.base.Stopwatch;
import fictioncraft.wintersteve25.fclib.api.events.JsonConfigEvent;
import fictioncraft.wintersteve25.fclib.api.events.PlayerMovedEvent;
import fictioncraft.wintersteve25.fclib.api.json.base.IJsonConfig;
import fictioncraft.wintersteve25.fclib.api.json.base.JsonConfigBuilder;
import fictioncraft.wintersteve25.fclib.api.json.objects.SimpleConfigObject;
import fictioncraft.wintersteve25.fclib.api.json.objects.SimpleObjectMap;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.ObjProviderType;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wintersteve25.cinematic_transitions.integration.json.JsonPopUp;
import wintersteve25.cinematic_transitions.registries.Listener;
import wintersteve25.cinematic_transitions.registries.ListenerRegistry;
import wintersteve25.cinematic_transitions.registries.PopUpRegistryManager;
import wintersteve25.cinematic_transitions.renderers.PopUp;
import wintersteve25.cinematic_transitions.renderers.PopUpType;
import wintersteve25.cinematic_transitions.renderers.Transition;

@Mod(CinematicTransitions.MODID)
public class CinematicTransitions {
    public static final String MODID = "cinematic_transitions";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ObjProviderType POPUP;
    public static IJsonConfig JSON_REGISTRY;

    public CinematicTransitions() {
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::json);
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::readJson);
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::playerMoved);
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::changeDimension);
        MinecraftForge.EVENT_BUS.addListener(CinematicTransitions::onEntitySpawned);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CinematicTransitions::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        POPUP = new ObjProviderType("PopUp", null, null, null, JsonPopUp.class);
    }

    private static void json(JsonConfigEvent.Registration event) {
        JsonConfigBuilder<SimpleObjectMap> builder = new JsonConfigBuilder<>(SimpleObjectMap.class, new ResourceLocation(MODID, "popup_registry"));
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome.getRegistryName() == null) continue;
            builder.addConfigObjectToList("popups", new SimpleConfigObject(new JsonPopUp("cinematic_transition:biomes/" + biome.getRegistryName().toString().replace(':', '/'), 256, 256, "cinematic_transitions:textures/biomes/" + biome.getRegistryName().toString().replace(':', '/'), PopUpType.FADE, PopUpType.FADE, false, true)), false);
        }
        JSON_REGISTRY = builder.build();
    }

    private static void readJson(JsonConfigEvent.Post event) {
        IJsonConfig config = event.getConfig();
        if (config.UID().equals(new ResourceLocation(MODID, "popup_registry"))) {
            SimpleObjectMap obj = config.finishedConfig();
            if (obj == null) return;
            for (SimpleConfigObject configObject : obj.getConfigs().get("popups")) {
                PopUpRegistryManager.registerPopUp(((JsonPopUp) configObject.getTarget()).serialize());
            }
        }
    }

    private static ResourceLocation lastVisitedBiome;
    private static ResourceLocation lastVisitedStructure;
    private static int timer = 0;

    private static void onEntitySpawned(LivingSpawnEvent.SpecialSpawn event) {
        if (!event.getWorld().isRemote()) {
            for (Listener listener : ListenerRegistry.getListeners()) {
                if (listener.getType() == Listener.Type.ENTITY) {
                    ResourceLocation entityType = event.getEntity().getType().getRegistryName();
                    if (entityType == null) continue;
                    if (entityType.equals(new ResourceLocation(listener.getRegistryName()))) {
                        playListener(listener);
                    }
                }
            }
        }
    }

    private static void playerMoved(PlayerMovedEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.getEntityWorld().isRemote()) {
            if (event.getMovement() != PlayerMovedEvent.MovementTypes.SNEAK && event.getMovement() != PlayerMovedEvent.MovementTypes.JUMP) {
                for (Listener listener : ListenerRegistry.getListeners()) {
                    switch (listener.getType()) {
                        case BIOME:
                            Biome biome = player.getEntityWorld().getBiome(player.getPosition());
                            if (biome.getRegistryName() == null) return;
                            if ((lastVisitedBiome == null || !lastVisitedBiome.equals(biome.getRegistryName())) && biome.getRegistryName().toString().equals(listener.getRegistryName())) {
                                playListener(listener);
                            }
                            break;
                        case STRUCTURE:
                            Structure<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(listener.getRegistryName()));
                            if (structure == null) return;
                            LocationPredicate structurePredicate = LocationPredicate.forFeature(structure);
                            if (timer <= 0 && (lastVisitedStructure == null || !lastVisitedStructure.equals(structure.getRegistryName())) && structurePredicate.test((ServerWorld) player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ())) {
                                timer = 100;
                                playListener(listener);
                            }
                            Structure<?> strucPlayerIn = ((ServerWorld) player.getEntityWorld()).func_241112_a_().getStructureStart(player.getPosition(), true, structure).getStructure();
                            lastVisitedStructure = strucPlayerIn.getRegistryName();
                            timer--;
                            break;
                    }
                }

                Biome biome = player.getEntityWorld().getBiome(player.getPosition());
                if (biome.getRegistryName() == null) return;
                lastVisitedBiome = biome.getRegistryName();
            }
        }
    }

    private static void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.getEntityWorld().isRemote()) {
            for (Listener listener : ListenerRegistry.getListeners()) {
                if (listener.getType() == Listener.Type.DIMENSION) {
                    if (event.getTo().getLocation().equals(new ResourceLocation(listener.getRegistryName()))) {
                        playListener(listener);
                    }
                }
            }
        }
    }

    private static void playListener(Listener listener) {
        if (PopUpRegistryManager.getPopUpWithRegistryName(new ResourceLocation(listener.getPopUpName())) == null) {
            LOGGER.error("PopUp with name: {} is not found", listener.getPopUpName());
        }

        if (listener.hasTransition()) {
            Transition.play(true);
        }
        PopUp.play(new ResourceLocation(listener.getPopUpName()));
    }
}
