package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoarmory.ExoArmory;
import net.minecraft.util.datafix.fixes.EntityTippedArrowFix;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModdedEventTweaks {

    /*
        The Depths spawn rule tweaks, makes creepers have a chance to breach
        and zombies a chance to be a miner
    */
    @SubscribeEvent
    public static void eventEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel().isClientSide || !event.getLevel().dimension().equals(Registry.DEPTHS_DIMENSION)) {
            return;
        }
        //Maybe move to like a map lookup? IDK, or actually, IDC
        if (entity instanceof Creeper creeper) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                creeper.getPersistentData().putBoolean("enhancedai:breach", true);
            }
        }
        else if (entity instanceof Zombie zombie) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                zombie.getPersistentData().putBoolean("enhancedai:miner", true);
            }
        }
        else if (entity instanceof Skeleton skeleton) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                MobEffectInstance newEffect = switch (event.getLevel().getRandom().nextInt(3)) {
                    case 0 -> new MobEffectInstance(MobEffects.BLINDNESS, 300, 0);
                    case 1 -> new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 300, 0);
                    default -> new MobEffectInstance(MobEffects.WITHER, 300, 0);
                };
                skeleton.setItemInHand(InteractionHand.OFF_HAND, PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW), List.of(newEffect)));
            }
        }
        else if (entity instanceof Spider spider) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0));
            }
        }
        else if (entity instanceof EnderMan enderman) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                enderman.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 0));
            }
        }
    }

    /*
        Gives players blighted while in the depths.
    */
    @SubscribeEvent
    public static void eventEntityJoinWorld(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide || level.getGameTime() % 30 != 0 || !level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
            return;
        }
        player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 40, 1, true, true));
    }

    @SubscribeEvent
    public static void eventEntityJoinWorld(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) { return; }
    }
}
