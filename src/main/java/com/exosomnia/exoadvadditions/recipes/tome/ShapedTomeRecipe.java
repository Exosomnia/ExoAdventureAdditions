package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ShapedTomeRecipe {

    private BlockState[][] topLayer;
    private BlockState[][] midLayer;
    private BlockState[][] lowerLayer;

    private ImmutableList<ItemLike> itemMappings;
    private boolean hasItems = false;
    private ItemStack result;

    private BiConsumer<ServerPlayer, BlockPos> execute;
    private boolean hasExecution = false;

    public ShapedTomeRecipe(@NotNull ImmutableMap<Character, BlockState> blockMappings, @Nullable ImmutableList<ItemLike> itemMappings, String[] topLayer, String[] midLayer, String[] lowerLayer, @Nullable ItemStack result, BiConsumer<ServerPlayer, @Nullable BlockPos> execute) {
        int i, ii; //Used for loops

        this.lowerLayer = new BlockState[3][3]; //LOWER LAYER IS MINIMUM, THERE CANNOT BE ANY FEWER LAYERS
        for (i = 0; i < 3; i++) {
            String row = lowerLayer[i]; //"XXX", "XXX", "XXX" (As an example)
            for (ii = 0; ii < 3; ii++) {
                Character chara = row.charAt(ii);
                BlockState state = blockMappings.get(chara);
                this.lowerLayer[i][ii] = state == null ? Blocks.AIR.defaultBlockState() : state;
            }
        }

        if(midLayer != null) {
            this.midLayer = new BlockState[3][3];
            for (i = 0; i < 3; i++) {
                String row = midLayer[i]; //"XXX", "X X", "XXX" (As an example)
                for (ii = 0; ii < 3; ii++) {
                    Character chara = row.charAt(ii);
                    BlockState state = blockMappings.get(chara);
                    this.midLayer[i][ii] = state == null ? Blocks.AIR.defaultBlockState() : state;
                }
            }
        }

        if(topLayer != null) {
            this.topLayer = new BlockState[3][3];
            for (i = 0; i < 3; i++) {
                String row = topLayer[i]; //"XXX", "XXX", "XXX" (As an example)
                for (ii = 0; ii < 3; ii++) {
                    Character chara = row.charAt(ii);
                    BlockState state = blockMappings.get(chara);
                    this.topLayer[i][ii] = state == null ? Blocks.AIR.defaultBlockState() : state;
                }
            }
        }

        if (itemMappings != null) {
            this.itemMappings = itemMappings;
            this.hasItems = true;
        }
        if (execute != null) {
            this.execute = execute;
            this.hasExecution = true;
        }

        this.result = result;
    }

    public boolean isRecipe(@Nullable ImmutableList<ItemLike> itemMappings, BlockState[][] topLayer, BlockState[][] midLayer, BlockState[][] lowerLayer) {
        if ((hasItems && !this.itemMappings.equals(itemMappings)) || (!hasItems && itemMappings != null)) { return false; }

        //Start verification
        int i, ii;
        for (i = 0; i < 3; i++) {
            for (ii = 0; ii < 3; ii++) {
                if (!this.lowerLayer[i][ii].equals(lowerLayer[i][ii])) { return false; }
                if (!this.midLayer[i][ii].equals(midLayer[i][ii])) { return false; }
                if (!this.topLayer[i][ii].equals(topLayer[i][ii])) { return false; }
            }
        }

        return true;
    }

    public ItemStack getResult() {
        return result == null ? null : result.copy();
    }

    public BiConsumer<ServerPlayer, BlockPos> getExecute() {
        return execute;
    }


    public static class Builder {

        private String[] topLayer;
        private String[] midLayer;
        private String[] lowerLayer;

        private ImmutableMap<Character, BlockState> blockMappings;
        private ImmutableList<ItemLike> itemMappings;

        private ItemStack result;
        private BiConsumer<ServerPlayer, BlockPos> execute;

        public Builder topLayer(String[] topLayer) {
            this.topLayer = topLayer;
            return this;
        }

        public Builder midLayer(String[] midLayer) {
            this.midLayer = midLayer;
            return this;
        }

        public Builder lowLayer(String[] lowerLayer) {
            this.lowerLayer = lowerLayer;
            return this;
        }

        public Builder blockMappings(ImmutableMap<Character, BlockState> blockMappings) {
            this.blockMappings = blockMappings;
            return this;
        }

        public Builder itemMappings(ImmutableList<ItemLike> itemMappings) {
            this.itemMappings = itemMappings;
            return this;
        }

        public Builder result(ItemStack result) {
            this.result = result;
            return this;
        }

        public Builder execute(BiConsumer<ServerPlayer, BlockPos> execute) {
            this.execute = execute;
            return this;
        }

        public ShapedTomeRecipe build() { return new ShapedTomeRecipe(blockMappings, itemMappings, topLayer, midLayer, lowerLayer, result, execute); }
    }
}
