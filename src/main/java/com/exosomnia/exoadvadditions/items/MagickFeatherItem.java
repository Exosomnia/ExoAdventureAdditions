package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.List;

public class MagickFeatherItem extends Item {
    public MagickFeatherItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        String name = getAttunedName(itemStack);
        if (name == null) { return Component.translatable(this.getDescriptionId()); }
        return Component.translatable(this.getDescriptionId()).append(" (").append(Component.translatable(name)).append(")");
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        String name = getAttunedName(itemStack);
        if (name == null) { return; }

        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.magicked_feather.info.1", I18n.get(name)),
                        ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level contextLevel = context.getLevel();
        if (contextLevel.isClientSide) return InteractionResult.sidedSuccess(true);

        ServerLevel level = (ServerLevel)contextLevel;
        BlockPos position = context.getClickedPos();
        ServerPlayer player = (ServerPlayer)context.getPlayer();
        ItemStack usedItemStack = context.getItemInHand();

        if (usedItemStack.hasTag() && usedItemStack.getTag().contains("LocateData") && level.getBlockState(position).is(Blocks.GRINDSTONE) && player.isCrouching()) {
            usedItemStack.shrink(1);
            if (!player.getInventory().add(new ItemStack(this))) {
                player.drop(new ItemStack(this), false);
            }
            level.playSound(null, position, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            ExperienceOrb.award(level, position.getCenter().add(0.0, 0.5, 0.0), 3 + level.random.nextInt(5));
        }

        return InteractionResult.sidedSuccess(true);
    }

    private String getAttunedName(ItemStack itemStack) {
        if (!itemStack.hasTag() || !itemStack.getTag().contains("LocateData")) {
            return null;
        }

        Tag tag = itemStack.getTag().get("LocateData");
        if (!(tag instanceof CompoundTag locateData)) {
            return null;
        }

        if (!locateData.contains("name")) {
            return "item.exoadvadditions.magicked_feather.info.fallback";
        }

        return locateData.getString("name");
    }
}
