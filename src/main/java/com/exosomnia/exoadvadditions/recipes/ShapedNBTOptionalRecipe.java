package com.exosomnia.exoadvadditions.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

public class ShapedNBTOptionalRecipe extends ShapedRecipe {

    private final ItemStack result;

    public ShapedNBTOptionalRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(resourceLocation, "", CraftingBookCategory.MISC, 3, 3, ingredients, result, false);
        this.result = result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return this.result.copy(); }

    public static class Serializer implements RecipeSerializer<ShapedNBTOptionalRecipe> {
        public ShapedNBTOptionalRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            for (int i = 0; i < 9; ++i) {
                JsonElement element = array.get(i);
                if (element.isJsonNull() || !element.isJsonObject()) continue;
                JsonObject object = element.getAsJsonObject();
                if (object.has("nbt")) { ingredients.set(i, PartialNBTIngredient.Serializer.INSTANCE.parse(object)); }
                else { ingredients.set(i, Ingredient.fromJson(object)); }
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));

            return new ShapedNBTOptionalRecipe(resourceLocation, ingredients, result);
        }

        public ShapedNBTOptionalRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for(int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            return new ShapedNBTOptionalRecipe(resourceLocation, ingredients, buffer.readItem());
        }

        public void toNetwork(FriendlyByteBuf buffer, ShapedNBTOptionalRecipe recipe) {
            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
        }
    }
}
