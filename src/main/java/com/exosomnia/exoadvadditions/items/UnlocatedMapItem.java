package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exolib.utils.ComponentUtils.Styles;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.List;

public class UnlocatedMapItem extends Item {
    public UnlocatedMapItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.success(itemStack); }

        level.playSound(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1.0F, 1.0F);

        CompoundTag mapTag = itemStack.getTag();
        if(mapTag == null) { return InteractionResultHolder.fail(itemStack); }

        String mapStruct = mapTag.getString("structure");
        String mapName = mapTag.getString("name");
        if(mapStruct.isEmpty()) { return InteractionResultHolder.fail(itemStack); }
        ServerLevel mapLevel = (ServerLevel)level;

        BlockPos blockpos;
        if (mapStruct.startsWith("#")) {
            TagKey<Structure> structure = TagKey.create(Registries.STRUCTURE, ResourceLocation.of(mapStruct.substring(1), ':'));
            blockpos = mapLevel.findNearestMapStructure(structure, player.blockPosition(), 50, !player.isCrouching());
        }
        else {
            Registry<Structure> structureRegistry = mapLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
            ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.of(mapStruct, ':'));
            Holder<Structure> structureHolder = structureRegistry.getHolderOrThrow(structureKey);
            HolderSet.Direct<Structure> structureSet = HolderSet.direct(structureHolder);
            Pair<BlockPos, Holder<Structure>> structure = mapLevel.getChunkSource().getGenerator().findNearestMapStructure(mapLevel, structureSet, player.blockPosition(), 50, !player.isCrouching());
            blockpos = structure == null ? null : structure.getFirst();
        }

        if (blockpos != null) {
            if (!player.getAbilities().instabuild) { itemStack.shrink(1); }
            player.awardStat(Stats.ITEM_USED.get(this));

            ItemStack newMap = MapItem.create(mapLevel, blockpos.getX(), blockpos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(mapLevel, newMap);
            MapItemSavedData.addTargetDecoration(newMap, blockpos, "+", MapDecoration.Type.TARGET_X);
            newMap.setHoverName(Component.translatable("item.exoadvadditions.map.prefix").append(Component.translatable(mapName)));

            if (itemStack.isEmpty()) { return InteractionResultHolder.consume(newMap); }
            if (!player.getInventory().add(newMap.copy())) {
                player.drop(newMap, false);
            }
            return InteractionResultHolder.consume(itemStack);
        }

        player.displayClientMessage(Component.translatable("item.exoadvadditions.unlocated_map.not_found").withStyle(ChatFormatting.RED), false);
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("item.exoadvadditions.unlocated_map.help.1").withStyle(Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.unlocated_map.help.2"),
                Styles.DEFAULT_DESC.getStyle(), Styles.HIGHLIGHT_DESC.getStyle()));
        components.add(Component.translatable("item.exoadvadditions.unlocated_map.help.3").withStyle(Styles.DEFAULT_DESC.getStyle()));
    }
}
