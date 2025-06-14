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

public class TomeOfStaminaItem extends TomeItem {

    private static final UUID[] EXP_MOD_UUIDS = new UUID[]{
            UUID.fromString("4b8979e9-52a5-48e2-983e-ef3b408b7ee3"),
            UUID.fromString("baf7f4a0-a650-44d2-a0c9-4bbf8eae3f9f")
    };

    public TomeOfStaminaItem(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_stamina.info.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_stamina.info.2"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_stamina.info.3"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_stamina.info.4"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
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

            int currentUse = modTag.getInt("stamina");
            if (currentUse >= 2) {
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll.max_used").withStyle(ChatFormatting.RED));
            }
            else {
                modTag.putInt("stamina", ++currentUse);
                tag.put("exoadventure", modTag);
                playerData.set(tag);

                itemStack.shrink(1);
                player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.75F);
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_stamina.used").withStyle(ChatFormatting.GOLD));
            }
        });

        return itemStack;
    }

    public static void applyAttributes(AttributeInstance staminaAttribute, int rank) {
        staminaAttribute.addPermanentModifier(new AttributeModifier(EXP_MOD_UUIDS[rank], "Stamina Scroll", 0.0625, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
