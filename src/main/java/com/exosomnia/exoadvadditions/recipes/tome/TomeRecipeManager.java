package com.exosomnia.exoadvadditions.recipes.tome;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TomeRecipeManager {

    public ArrayList<ShapedTomeRecipe> recipes = new ArrayList<>();
    public ArrayList<AdvancedShapedTomeRecipe> advancedRecipes = new ArrayList<>();

    public TomeRecipe getRecipe(@Nullable ArrayList<ItemStack> itemMappings, List<BlockState[][]> layers) {
        if (layers.size() == 3) {
            for (ShapedTomeRecipe recipe : recipes) {
                if (recipe.isRecipe(itemMappings, layers.get(2), layers.get(1), layers.get(0))) {
                    return recipe;
                }
            }
        }
        else {
            for (AdvancedShapedTomeRecipe recipe : advancedRecipes) {
                if (recipe.isRecipe(itemMappings, layers)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public void registerRecipe(ShapedTomeRecipe recipe) {
        recipes.add(recipe);
    }

    public void registerAdvancedRecipe(AdvancedShapedTomeRecipe recipe) {
        advancedRecipes.add(recipe);
    }
}
