package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class AdvancedShapedTomeRecipe extends TomeRecipe {

    private List<BlockMapping[][]> layers = new ArrayList<>();

    private ImmutableList<ItemMapping> itemMappings;
    private boolean hasItems = false;
    public record CraftResult(ItemStack itemStack, Integer score){}
    private CraftResult result;

    private BiConsumer<ServerPlayer, BlockPos> execute;

    public AdvancedShapedTomeRecipe(@NotNull ImmutableMap<Character, BlockMapping> blockMappings, @Nullable ImmutableList<ItemMapping> itemMappings, List<String[]> layerStrings, @Nullable CraftResult result, BiConsumer<ServerPlayer, @Nullable BlockPos> execute) {
        int lay, i, ii; //Used for loops

        for (lay = 0; lay < 5; lay++) {
            BlockMapping[][] thisLayer = new BlockMapping[5][5];
            for (i = 0; i < 5; i++) {
                String row = layerStrings.get(lay)[i]; //"XXX", "XXX", "XXX" (As an example)
                for (ii = 0; ii < 5; ii++) {
                    BlockMapping mapping = blockMappings.get(row.charAt(ii));
                    thisLayer[i][ii] = mapping == null ? BlockMapping.of(Blocks.AIR) : mapping;
                }
            }
            layers.add(thisLayer);
        }

        if (itemMappings != null) {
            this.itemMappings = itemMappings;
            this.hasItems = true;
        }
        if (execute != null) {
            this.execute = execute;
        }

        this.result = result;
    }

    public boolean isRecipe(@Nullable ArrayList<ItemStack> itemMappings, List<BlockState[][]> layers) {
        if ((hasItems && !itemsMatch(itemMappings)) || (!hasItems && itemMappings != null)) { return false; }

        //Start verification
        int i, ii;
        for (i = 0; i < 5; i++) {
            for (ii = 0; ii < 5; ii++) {
                if (!this.layers.get(0)[i][ii].matches(layers.get(0)[i][ii])) { return false; }
                if (!this.layers.get(1)[i][ii].matches(layers.get(1)[i][ii])) { return false; }
                if (!this.layers.get(2)[i][ii].matches(layers.get(2)[i][ii])) { return false; }
                if (!this.layers.get(3)[i][ii].matches(layers.get(3)[i][ii])) { return false; }
                if (!this.layers.get(4)[i][ii].matches(layers.get(4)[i][ii])) { return false; }
            }
        }

        return true;
    }
    private boolean itemsMatch(ArrayList<ItemStack> itemMappings) {
        if (itemMappings == null) return false;

        int mappingSize = this.itemMappings.size();
        if (mappingSize != itemMappings.size()) { return false; }
        for (var i = 0; i < mappingSize; i++) {
            ItemMapping check = this.itemMappings.get(i);
            boolean isValid = false;
            for (var ii = 0; ii < mappingSize; ii++) {
                if (check.matches(itemMappings.get(ii))) {
                    itemMappings.remove(ii);
                    isValid = true;
                    break;
                }
            }
            if (!isValid) { return false; }
        }
        return true;
    }

    public ItemStack getResult() {
        return result == null ? null : result.itemStack.copy();
    }

    public Integer getScore() {
        return result == null ? null : result.score;
    }

    public BiConsumer<ServerPlayer, BlockPos> getExecute() {
        return execute;
    }

    @Override
    public List<BlockMapping[][]> getRecipeShape() {
        return layers;
    }

    @Override
    public ImmutableList<ItemMapping> getRecipeItems() {
        return itemMappings;
    }

    public static class Builder {

        private List<String[]> layers = new ArrayList<>();

        private ImmutableMap<Character, BlockMapping> blockMappings;
        private ImmutableList<ItemMapping> itemMappings;

        private CraftResult result;
        private BiConsumer<ServerPlayer, BlockPos> execute;

        public Builder withLayer(String[] layerString) {
            this.layers.add(layerString);
            return this;
        }

        public Builder withLayer(int layer, String[] layerString) {
            this.layers.add(layer, layerString);
            return this;
        }

        public Builder blockMappings(ImmutableMap<Character, BlockMapping> blockMappings) {
            this.blockMappings = blockMappings;
            return this;
        }

        public Builder itemMappings(ImmutableList<ItemMapping> itemMappings) {
            this.itemMappings = itemMappings;
            return this;
        }

        public Builder result(ItemStack result) {
            return result(result, 0);
        }

        public Builder result(ItemStack result, Integer score) {
            this.result = new CraftResult(result, score);
            return this;
        }

        public Builder execute(BiConsumer<ServerPlayer, BlockPos> execute) {
            this.execute = execute;
            return this;
        }

        public AdvancedShapedTomeRecipe build() { return new AdvancedShapedTomeRecipe(blockMappings, itemMappings, layers, result, execute); }
    }
}
