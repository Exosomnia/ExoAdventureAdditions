package com.exosomnia.exoadvadditions.integration;


import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExoAdventureAdditions.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ShapedTomeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);

        registration.addRecipes(ShapedTomeRecipeCategory.TOME_RECIPE, Registry.TOME_RECIPE_MANAGER.recipes);
        registration.addRecipes(RecipeTypes.BREWING, List.of(
                new IJeiBrewingRecipe() {
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of();
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of();
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return null;
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 0;
                    }
                }
        ));

        registration.addIngredientInfo(Registry.ITEM_CHARGED_CIRCUIT_BOARD.get(), Component.translatable("item.exoadvadditions.charged_circuit_board.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get(), Component.translatable("item.exoadvadditions.mysterious_tome_active.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get(), Component.translatable("item.exoadvadditions.mysterious_tome_unleashed.jei.info"));
    }
}
