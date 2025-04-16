package com.exosomnia.exoadvadditions.managers;

import com.exosomnia.exoadvadditions.Config;
import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.events.ModdedEventTweaks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdventureManager {

    public final static GameRules.Key<GameRules.BooleanValue> RULE_CAN_BREAK = GameRules.register("canBreak", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public final static GameRules.Key<GameRules.BooleanValue> RULE_CAN_HURT = GameRules.register("canHurt", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public final static GameRules.Key<GameRules.BooleanValue> RULE_CAN_HUNGER_DRAIN = GameRules.register("canHungerDrain", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public final static GameRules.Key<GameRules.BooleanValue> RULE_ADVENTURE_STARTED = GameRules.register("adventureStarted", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    @SubscribeEvent
    public static void serverStartEvent(ServerAboutToStartEvent event) {
        ModdedEventTweaks.setupAboutToStartTweaks();
    }

    @SubscribeEvent
    public static void serverStartEvent(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        ModdedEventTweaks.setupStartedTweaks(level);

        GameRules rules = level.getGameRules();
        if (level.getGameTime() == 0 && Config.newWorldsIntro) {
            CommandSourceStack stack = server.createCommandSourceStack().withPermission(4).withSource(server).withSuppressedOutput();
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.antidoteVessel.maxEffectDuration 15");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.feralClaws.attackSpeedBonus 10");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.powerGlove.attackDamageBonus 2");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.crystalHeart.healthBonus 5");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.runningShoes.speedBonus 20");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.vampiricGlove.maxHealingPerHit 1");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.vampiricGlove.absorptionRatio 10");
            server.getCommands().performPrefixedCommand(stack, "gamerule artifacts.vampiricGlove.absorptionChance 100");
            rules.getRule(RULE_CAN_BREAK).set(false, event.getServer());
            rules.getRule(RULE_CAN_HURT).set(false, event.getServer());
            rules.getRule(RULE_CAN_HUNGER_DRAIN).set(false, event.getServer());
            rules.getRule(RULE_ADVENTURE_STARTED).set(false, event.getServer());
            rules.getRule(GameRules.RULE_DAYLIGHT).set(false, event.getServer());
            rules.getRule(GameRules.RULE_RANDOMTICKING).set(0, event.getServer());
            rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, event.getServer());

            BlockPos spawn = level.getSharedSpawnPos();
            WorldBorder border = level.getWorldBorder();
            border.setCenter(spawn.getX(), spawn.getZ()); // defaults to 0,0
            border.setWarningBlocks(1); // defaults to 5
            border.setSize(32); // defaults to 30000000 million
        }
    }

    @SubscribeEvent
    public static void playerBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level && !level.getGameRules().getRule(RULE_CAN_BREAK).get()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingHurtEvent(LivingHurtEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level && !level.getGameRules().getRule(RULE_CAN_HURT).get()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && !event.player.level().getGameRules().getRule(RULE_CAN_HUNGER_DRAIN).get()) {
            event.player.getFoodData().setExhaustion(0);
        }
    }
}
