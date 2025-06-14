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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TomeOfLuckItem extends TomeItem {

    public TomeOfLuckItem(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_luck.info.1"), ComponentUtils.Styles.DEFAULT_DESC.getStyle(),
                ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_luck.info.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) { return itemStack; }

        if (!level.isClientSide) {
            player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
                CompoundTag tag = playerData.get();
                CompoundTag modTag = tag.getCompound("exoadventure");
                if (!modTag.getBoolean("luckTome")) {
                    modTag.putBoolean("luckTome", true);
                    tag.put("exoadventure", modTag);
                    playerData.set(tag);
                    applyAttributes(player.getAttribute(Attributes.LUCK));
                    if (!eternal) { itemStack.shrink(1); }
                    player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_luck.activated").withStyle(ChatFormatting.GOLD));
                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.75F, 0.75F);
                }
                else {
                    player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_luck.already_used").withStyle(ChatFormatting.RED));
                }
            });
        }
        return itemStack;
    }

    public static void applyAttributes(AttributeInstance expAttribute) {
        expAttribute.addPermanentModifier(new AttributeModifier(UUID.fromString("34ab9e11-0adb-4fad-b306-ce7c1141d1e5"), "Luck Tome", 1.0, AttributeModifier.Operation.ADDITION));
    }
}
