package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TomeOfFlight extends TomeItem {

    public TomeOfFlight(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_flight.info.1"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_flight.info.2"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_flight.info.3"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        if (eternal) {
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_flight.info.extra.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_flight.info.extra.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (player.level().isClientSide) { return InteractionResult.PASS; }

        ItemCooldowns cooldowns = player.getCooldowns();
        if (cooldowns.isOnCooldown(this)) { return InteractionResult.PASS; }

        if (!Registry.FLYING_MOUNT_CLASS.isInstance(livingEntity)) {
            player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_flight.not_valid").withStyle(ChatFormatting.RED), false);
            return InteractionResult.FAIL;
        }

        if (livingEntity.hasEffect(Registry.EFFECT_GROUNDED.get())) {
            player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_flight.is_grounded").withStyle(ChatFormatting.RED), false);
            return InteractionResult.FAIL;
        }

        livingEntity.addEffect(new MobEffectInstance(Registry.EFFECT_FLIGHT_READY.get(), eternal ? 4200 : 3600, 0));
        player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_flight.activated").withStyle(ChatFormatting.GOLD), false);
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.75F, 0.75F);
        cooldowns.addCooldown(Registry.ITEM_TOME_OF_FLIGHT.get(), 7200);
        cooldowns.addCooldown(Registry.ITEM_ASCENDED_ITEM_TOME_OF_FLIGHT.get(), 7200);

        if (!eternal) { itemStack.shrink(1); }

        return InteractionResult.SUCCESS;
    }
}
