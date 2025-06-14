package com.exosomnia.exoadvadditions.integration;


import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoarmory.ExoArmory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    protected static final ResourceLocation INFO_ICON = ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "textures/gui/icon/info.png");

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IIngredientSubtypeInterpreter<ItemStack> featherInterp = (ingredient, context) -> {
            CompoundTag featherTag = ingredient.getTag();
            if (featherTag != null && featherTag.contains("LocateData") && featherTag.get("LocateData") instanceof CompoundTag locateTag) {
                return locateTag.getString("name");
            }
            return "";
        };

        registration.useNbtForSubtypes(Registry.ITEM_UNLOCATED_MAP.get());
        registration.registerSubtypeInterpreter(Registry.ITEM_MAGICKED_FEATHER.get(), featherInterp);
        registration.registerSubtypeInterpreter(Registry.ITEM_INFERNAL_FEATHER.get(), featherInterp);
        registration.registerSubtypeInterpreter(Registry.ITEM_ENDER_FEATHER.get(), featherInterp);
        registration.registerSubtypeInterpreter(Registry.ITEM_ANCIENT_FEATHER.get(), featherInterp);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ShapedTomeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AdvancedShapedTomeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);

        registration.addRecipes(ShapedTomeRecipeCategory.TOME_RECIPE, Registry.TOME_RECIPE_MANAGER.recipes);
        registration.addRecipes(AdvancedShapedTomeRecipeCategory.TOME_RECIPE, Registry.TOME_RECIPE_MANAGER.advancedRecipes);

        registration.addIngredientInfo(Registry.ITEM_CHARGED_CIRCUIT_BOARD.get(), Component.translatable("item.exoadvadditions.charged_circuit_board.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_MYSTERIOUS_TOME.get(), Component.translatable("item.exoadvadditions.mysterious_tome_dormant.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get(), Component.translatable("item.exoadvadditions.mysterious_tome_active.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get(), Component.translatable("item.exoadvadditions.mysterious_tome_unleashed.jei.info"));

        registration.addIngredientInfo(Registry.ITEM_MAGICKED_FEATHER.get(), Component.translatable("item.exoadvadditions.magicked_feather.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_INFERNAL_FEATHER.get(), Component.translatable("item.exoadvadditions.magicked_feather.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_ENDER_FEATHER.get(), Component.translatable("item.exoadvadditions.magicked_feather.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_ANCIENT_FEATHER.get(), Component.translatable("item.exoadvadditions.magicked_feather.jei.info"));

        registration.addIngredientInfo(Registry.ITEM_OLD_MANUSCRIPT.get(), Component.translatable("item.exoadvadditions.old_manuscript.jei.info"));

        registration.addIngredientInfo(Registry.ITEM_FLAWLESS_ONYX.get(), Component.translatable("item.exoadvadditions.flawless_onyx.jei.info"));

        registration.addIngredientInfo(Registry.ITEM_STARCALLER, Component.translatable("item.exoadvadditions.star_sword.jei.info"));
    }
}
