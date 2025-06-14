package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TomeOfBallisticsItem extends TomeItem {

    private static final UUID[] EXP_MOD_UUIDS = new UUID[]{
            UUID.fromString("682304c4-978c-4ae7-a2d8-481e8f1db635")
    };

    public TomeOfBallisticsItem(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_ballistics.info.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_ballistics.info.2"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_ballistics.info.3"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player) || level.isClientSide) { return itemStack; }

        player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            CompoundTag tag = playerData.get();
            CompoundTag modTag = tag.getCompound("exoadventure");

            boolean currentUse = modTag.getBoolean("ballistics");
            if (currentUse) {
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll.max_used").withStyle(ChatFormatting.RED));
            }
            else {
                modTag.putBoolean("ballistics", true);
                tag.put("exoadventure", modTag);
                playerData.set(tag);

                itemStack.shrink(1);
                player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.75F);
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_ballistics.used").withStyle(ChatFormatting.GOLD));
            }
        });

        return itemStack;
    }

    public static void applyAttributes(AttributeInstance ballisticAttribute, int rank) {
        ballisticAttribute.addPermanentModifier(new AttributeModifier(EXP_MOD_UUIDS[rank], "Ballistic Scroll", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
