package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TomeRecipeManager {

    ArrayList<ShapedTomeRecipe> recipes = new ArrayList<>();

    public ShapedTomeRecipe getRecipe(@Nullable ImmutableList<ItemLike> itemMappings, @Nullable BlockState[][] topLayer, @Nullable BlockState[][] midLayer, BlockState[][] lowerLayer) {
        for (ShapedTomeRecipe recipe : recipes) {
            if (recipe.isRecipe(itemMappings, topLayer, midLayer, lowerLayer)) {
                return recipe;
            }
        }
        return null;
    }

    public void registerRecipe(ShapedTomeRecipe recipe) {
        recipes.add(recipe);
    }
}
