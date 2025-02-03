package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SuspiciousMatterItem extends Item {
    public SuspiciousMatterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && level.random.nextInt(320) == 0 && entity instanceof ServerPlayer player) {
            player.playNotifySound(SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 2.0F);
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.suspicious_matter.info"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}
