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

public class MagicalRunesItem extends Item {

    public enum Effect {
        SOUND_CREEPER,
        SOUND_ENDERMAN,
        POTION_BLINDNESS,
        POTION_LEVITATION,
        TELEPORT,
        NOTHING,
        DAMAGE,
        HEAL
    }

    public MagicalRunesItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && level.random.nextInt(200) == 0 && entity instanceof ServerPlayer player) {
            switch(Effect.values()[level.random.nextInt(8)]) {
                case SOUND_CREEPER -> player.playNotifySound(SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.0F, 1.0F);
                case SOUND_ENDERMAN -> player.playNotifySound(SoundEvents.ENDERMAN_SCREAM, SoundSource.HOSTILE, 1.0F, 1.0F);
                case POTION_BLINDNESS -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0));
                case POTION_LEVITATION -> player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 120, 0));
                case TELEPORT -> Items.CHORUS_FRUIT.finishUsingItem(new ItemStack(Items.AIR), level, player);
                case DAMAGE -> {
                    player.playNotifySound(SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 0.5F);
                    player.hurt(level.damageSources().wither(), 3);
                }
                case HEAL -> {
                    player.playNotifySound(SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 2.0F);
                    player.heal(2.0F);
                }
                default -> {}
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.magical_runes.info"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}
