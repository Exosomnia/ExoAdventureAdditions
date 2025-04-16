package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GrowthScrollItem extends Item {

    private static final UUID[] EXP_MOD_UUIDS = new UUID[]{
            UUID.fromString("49db8278-d20d-405f-b97b-c1919f78cb60"),
            UUID.fromString("2bf4c8bb-fb6f-4fcc-ba60-edffb210dd3f"),
            UUID.fromString("cb7d23d4-67f0-4cdf-8082-db5003abcf80"),
            UUID.fromString("208a403e-0fb3-4bd4-b1a3-4b8dc390e454"),
    };

    private final int rank;
    private final double xpRate;

    public GrowthScrollItem(int rank) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        this.rank = rank;
        xpRate = 1.0 + ((rank + 1.0) * .125);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_growth.tier", rank + 1, xpRate),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.level().isClientSide) { return InteractionResultHolder.consume(itemStack); }

        AttributeInstance expAttribute = player.getAttribute(Registry.ATTRIBUTE_SKILL_EXP_BONUS.get());
        if (expAttribute.getModifier(EXP_MOD_UUIDS[rank]) != null) {
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_growth.already_used").withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(itemStack);
        }
        else if (rank > 0 && expAttribute.getModifier(EXP_MOD_UUIDS[rank - 1]) == null) {
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_growth.skipped_tier").withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(itemStack);
        }
        itemStack.shrink(1);
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.75F);
        player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_growth.used").withStyle(ChatFormatting.GOLD));
        applyAttributes(expAttribute, rank);
        player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            CompoundTag tag = playerData.get();
            CompoundTag modTag = tag.getCompound("exoadventure");
            modTag.putInt("growthScrolls", rank + 1);
            tag.put("exoadventure", modTag);
            playerData.set(tag);
        });

        return InteractionResultHolder.consume(itemStack);
    }

    public static void applyAttributes(AttributeInstance expAttribute, int rank) {
        expAttribute.addPermanentModifier(new AttributeModifier(EXP_MOD_UUIDS[rank], "Growth Scroll", 0.125, AttributeModifier.Operation.ADDITION));
    }
}
