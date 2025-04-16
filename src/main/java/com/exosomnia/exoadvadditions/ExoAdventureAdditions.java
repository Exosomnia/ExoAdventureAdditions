package com.exosomnia.exoadvadditions;

import com.exosomnia.exoadvadditions.events.ModdedEventTweaks;
import com.exosomnia.exoadvadditions.managers.DepthsMusicManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExoAdventureAdditions.MODID)
public class ExoAdventureAdditions
{
    public static final String MODID = "exoadvadditions";
    private static DepthsMusicManager depthsMusicManager;

    public ExoAdventureAdditions() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Registry::buildCreative);

        Registry.registerCommon();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::clientTick) );
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient) );
    }

    public void setupCommon(FMLCommonSetupEvent event) {
        Registry.setupOres();
        Registry.MYSTERIOUS_RARITY = Rarity.create("mysterious", (style) -> style.withObfuscated(true).withColor(ChatFormatting.RED).withFont(ResourceLocation.fromNamespaceAndPath("minecraft", "alt")));
        Registry.fillInAfterRegistry();

        ModdedEventTweaks.initalizeTweaks();
    }

    @OnlyIn(Dist.CLIENT)
    public void setupClient(FMLClientSetupEvent event) {
        depthsMusicManager = new DepthsMusicManager();
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(Registry.BLOCK_BLANK_OUTLIINE.get(), RenderType.cutout()); // or translucent()
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.END)) {
            depthsMusicManager.tick();
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !player.hasEffect(Registry.EFFECT_GROUNDED.get()) || !player.getAbilities().flying) return;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
