package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ShapedTomeRecipe extends TomeRecipe {

    private BlockMapping[][] topLayer;
    private BlockMapping[][] midLayer;
    private BlockMapping[][] lowerLayer;

    private ImmutableList<ItemMapping> itemMappings;
    private boolean hasItems = false;
    private boolean hasRecipeHelp = false;
    private CraftResult result;

    private BiConsumer<ServerPlayer, BlockPos> execute;
    private List<String> recipeHelp;

    public ShapedTomeRecipe(@NotNull ImmutableMap<Character, BlockMapping> blockMappings, @Nullable ImmutableList<ItemMapping> itemMappings, String[] topLayer, String[] midLayer, String[] lowerLayer, @Nullable CraftResult result, BiConsumer<ServerPlayer, @Nullable BlockPos> execute) {
        this(blockMappings, itemMappings, topLayer, midLayer, lowerLayer, result, execute, null);
    }

    public ShapedTomeRecipe(@NotNull ImmutableMap<Character, BlockMapping> blockMappings, @Nullable ImmutableList<ItemMapping> itemMappings, String[] topLayer, String[] midLayer, String[] lowerLayer, @Nullable CraftResult result, BiConsumer<ServerPlayer, @Nullable BlockPos> execute, @Nullable List<String> recipeHelp) {
        int i, ii; //Used for loops

        this.lowerLayer = new BlockMapping[3][3]; //LOWER LAYER IS MINIMUM, THERE CANNOT BE ANY FEWER LAYERS
        for (i = 0; i < 3; i++) {
            String row = lowerLayer[i]; //"XXX", "XXX", "XXX" (As an example)
            for (ii = 0; ii < 3; ii++) {
                BlockMapping mapping = blockMappings.get(row.charAt(ii));
                this.lowerLayer[i][ii] = mapping == null ? BlockMapping.of(Blocks.AIR) : mapping;
            }
        }

        this.midLayer = new BlockMapping[3][3];
        for (i = 0; i < 3; i++) {
            String row = midLayer[i]; //"XXX", "X X", "XXX" (As an example)
            for (ii = 0; ii < 3; ii++) {
                BlockMapping mapping = blockMappings.get(row.charAt(ii));
                this.midLayer[i][ii] = mapping == null ? BlockMapping.of(Blocks.AIR) : mapping;
            }
        }

        this.topLayer = new BlockMapping[3][3];
        for (i = 0; i < 3; i++) {
            String row = topLayer[i]; //"XXX", "XXX", "XXX" (As an example)
            for (ii = 0; ii < 3; ii++) {
                BlockMapping mapping = blockMappings.get(row.charAt(ii));
                this.topLayer[i][ii] = mapping == null ? BlockMapping.of(Blocks.AIR) : mapping;
            }
        }

        if (itemMappings != null) {
            this.itemMappings = itemMappings;
            this.hasItems = true;
        }
        if (execute != null) {
            this.execute = execute;
        }

        this.result = result;
        this.recipeHelp = recipeHelp;
        if (recipeHelp != null) hasRecipeHelp = true;
    }

    public boolean isRecipe(@Nullable ArrayList<ItemStack> itemMappings, BlockState[][] topLayer, BlockState[][] midLayer, BlockState[][] lowerLayer) {
        if ((hasItems && !itemsMatch(itemMappings)) || (!hasItems && itemMappings != null)) { return false; }

        //Start verification
        int i, ii;
        for (i = 0; i < 3; i++) {
            for (ii = 0; ii < 3; ii++) {
                if (!this.lowerLayer[i][ii].matches(lowerLayer[i][ii])) { return false; }
                if (!this.midLayer[i][ii].matches(midLayer[i][ii])) { return false; }
                if (!this.topLayer[i][ii].matches(topLayer[i][ii])) { return false; }
            }
        }

        return true;
    }
    private boolean itemsMatch(ArrayList<ItemStack> itemMappings) {
        if (itemMappings == null) return false;
        ArrayList<ItemStack> itemMapCopy = new ArrayList<>(itemMappings);

        int mappingSize = this.itemMappings.size();
        if (mappingSize != itemMapCopy.size()) { return false; }
        for (var i = 0; i < mappingSize; i++) {
            ItemMapping check = this.itemMappings.get(i);
            boolean isValid = false;
            for (var ii = 0; ii < itemMapCopy.size(); ii++) {
                if (check.matches(itemMapCopy.get(ii))) {
                    itemMapCopy.remove(ii);
                    isValid = true;
                    break;
                }
            }
            if (!isValid) { return false; }
        }
        return true;
    }

    @Override
    public boolean shouldDropResult() {
        return (result != null) && result.shouldDrop();
    }

    public ItemStack getResult() {
        if (result == null) return null;
        if (result.itemStack() == null) return null;
        return result.itemStack().copy();
    }

    public Integer getScore() {
        return result == null ? 0 : result.score();
    }

    public BiConsumer<ServerPlayer, BlockPos> getExecute() {
        return execute;
    }

    @Override
    public List<BlockMapping[][]> getRecipeShape() {
        return List.of(lowerLayer, midLayer, topLayer);
    }

    @Override
    public ImmutableList<ItemMapping> getRecipeItems() {
        return itemMappings;
    }

    @Override
    public boolean hasRecipeHelp() {
        return hasRecipeHelp;
    }

    @Override
    public List<String> getRecipeHelp() {
        return recipeHelp;
    }

    public static class Builder {

        private String[] topLayer;
        private String[] midLayer;
        private String[] lowerLayer;

        private ImmutableMap<Character, BlockMapping> blockMappings;
        private ImmutableList<ItemMapping> itemMappings;

        private CraftResult result;
        private BiConsumer<ServerPlayer, BlockPos> execute;

        private boolean hasRecipeHelp = false;
        private List<String> recipeHelp;

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
            this.result = new CraftResult(result, score, true);
            return this;
        }

        public Builder result(ItemStack result, Integer score, boolean drops) {
            this.result = new CraftResult(result, score, drops);
            return this;
        }

        public Builder execute(BiConsumer<ServerPlayer, BlockPos> execute) {
            this.execute = execute;
            return this;
        }

        public Builder withHelp(List<String> recipeHelp) {
            this.recipeHelp = recipeHelp;
            if (recipeHelp != null) hasRecipeHelp = true;
            return this;
        }

        public ShapedTomeRecipe build() {
            if (!hasRecipeHelp) {
                return new ShapedTomeRecipe(blockMappings, itemMappings, topLayer, midLayer, lowerLayer, result, execute);
            }
            return new ShapedTomeRecipe(blockMappings, itemMappings, topLayer, midLayer, lowerLayer, result, execute, recipeHelp);
        }
    }
}
