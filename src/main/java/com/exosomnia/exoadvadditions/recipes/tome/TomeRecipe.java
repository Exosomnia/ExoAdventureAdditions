package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class TomeRecipe {

    public abstract ItemStack getResult();
    public abstract Integer getScore();
    public abstract BiConsumer<ServerPlayer, BlockPos> getExecute();

    public abstract List<BlockMapping[][]> getRecipeShape();
    public abstract ImmutableList<ItemMapping> getRecipeItems();

    public static class ItemMapping {

        public Ingredient ingredient;
        public int count;

        ItemMapping(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }

        public boolean matches(ItemStack itemStack) {
            return (this.count == itemStack.getCount() && this.ingredient.test(itemStack));
        }

        public static ItemMapping of(Ingredient ingredient, int count) {
            return new ItemMapping(ingredient, count);
        }
    }

    public static class BlockMapping {

        public Block block;
        public TagKey<Block> blockTag;
        public Predicate<BlockState> predicate;
        private boolean cachedValid = false;
        public Set<Block> validBlocks;

        BlockMapping(Block block) {
            this.block = block;
            this.predicate = this::matchesBlock;
        }

        BlockMapping(TagKey<Block> blockTag) {
            this.blockTag = blockTag;
            this.predicate = this::matchesTag;
        }

        @OnlyIn(Dist.CLIENT)
        public Set<Block> validBlocks() {
            if (!cachedValid) {
                Set<Block> blocks = new HashSet<>();
                if (block != null) blocks.add(block);
                else {
                    Registry<Block> blockRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BLOCK);
                    for (Holder<Block> blockHolder : blockRegistry.getTagOrEmpty(blockTag)) {
                        blocks.add(blockHolder.get());
                    }
                }
                this.validBlocks = blocks;
                return this.validBlocks;
            }
            return validBlocks;
        }

        public boolean matches(BlockState blockState) {
            return this.predicate.test(blockState);
        }

        private boolean matchesBlock(BlockState blockState) {
            return blockState.getBlock().equals(this.block);
        }

        private boolean matchesTag(BlockState blockState) {
            return blockState.is(blockTag);
        }

        public static BlockMapping of(Block block) {
            return new BlockMapping(block);
        }

        public static BlockMapping of(TagKey<Block> blockTag) {
            return new BlockMapping(blockTag);
        }
    }
}
