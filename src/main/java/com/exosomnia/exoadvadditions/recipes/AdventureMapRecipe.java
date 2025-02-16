package com.exosomnia.exoadvadditions.recipes;

import com.exosomnia.exoadvadditions.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class AdventureMapRecipe extends ShapedRecipe {

    private final String structure;
    private final String name;
    private final ItemStack result;

    public AdventureMapRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, String structure, String name) {
        super(resourceLocation, "", CraftingBookCategory.MISC, 3, 3, ingredients,
                new ItemStack(Registry.ITEM_UNLOCATED_MAP.get()), false);
        this.structure = structure;
        this.name = name;

        ItemStack result = new ItemStack(Registry.ITEM_UNLOCATED_MAP.get());
        CompoundTag tag = result.getOrCreateTag();
        tag.putString("structure", structure);
        tag.putString("name", name);

        this.result = result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return this.result.copy(); }

    public static class Serializer implements RecipeSerializer<AdventureMapRecipe> {
        public AdventureMapRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            for (int i = 0; i < 9; ++i) {
                JsonElement element = array.get(i);
                if (element.isJsonNull()) continue;
                ingredients.set(i, Ingredient.fromJson(element));
            }

            return new AdventureMapRecipe(resourceLocation, ingredients,
                    GsonHelper.getAsString(jsonObject, "structure"),
                    GsonHelper.getAsString(jsonObject, "name"));
        }

        public AdventureMapRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for(int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            return new AdventureMapRecipe(resourceLocation, ingredients, buffer.readUtf(), buffer.readUtf());
        }

        public void toNetwork(FriendlyByteBuf buffer, AdventureMapRecipe recipe) {
            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeUtf(recipe.structure);
            buffer.writeUtf(recipe.name);
        }
    }
}
