package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TomeOfWakefulness extends TomeItem {

    private final int duration;

    public TomeOfWakefulness(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
        duration = Math.min((int)Math.pow(3, rank - 1) * 12000, 72000);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.1", duration/1200),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.2"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.3"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.4"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.5"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        if (eternal) { components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_wakefulness.info.extra"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle())); }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            if (entity instanceof ServerPlayer player) { player.getCooldowns().addCooldown(this, 20); }
            entity.addEffect(new MobEffectInstance(Registry.EFFECT_WAKEFULNESS.get(), duration, 0, true, false, true));
        }
        else if (entity instanceof LocalPlayer player) {
            player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_wakefulness.activated").withStyle(ChatFormatting.GOLD), false);
            player.playSound(SoundEvents.PLAYER_LEVELUP, 0.75F, 0.75F);
        }
        if (!eternal) { itemStack.shrink(1); }
        return itemStack;
    }
}
