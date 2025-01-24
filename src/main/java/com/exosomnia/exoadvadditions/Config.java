package com.exosomnia.exoadvadditions;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue NEW_WORLDS_INTRO = BUILDER
            .comment("Enables the intro period when creating new worlds. Time is paused, blocks cannot be broken, and no random ticks happen.")
            .define("newWorldsIntro", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean newWorldsIntro;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        newWorldsIntro = NEW_WORLDS_INTRO.get();
    }
}
