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

public class MysteriousPackageRecipe extends ShapedRecipe {

    private final String contains;
    private int amount ;
    private final ItemStack result;

    public MysteriousPackageRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, String contains) {
        this(resourceLocation, ingredients, contains, 1);
    }

    public MysteriousPackageRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, String contains, int amount) {
        super(resourceLocation, "", CraftingBookCategory.MISC, 3, 3, ingredients,
                new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get()), false);
        this.contains = contains;
        this.amount = amount;

        ItemStack result = new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get(), amount);
        CompoundTag tag = result.getOrCreateTag();
        tag.putString("contains", contains);
        tag.putInt("amount", amount);

        this.result = result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return this.result; }

    public static class Serializer implements RecipeSerializer<MysteriousPackageRecipe> {
        public MysteriousPackageRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            for (int i = 0; i < 9; ++i) {
                JsonElement element = array.get(i);
                if (element.isJsonNull()) continue;
                ingredients.set(i, Ingredient.fromJson(element));
            }

            if (jsonObject.has("amount")) {
                return new MysteriousPackageRecipe(resourceLocation, ingredients,
                        GsonHelper.getAsString(jsonObject, "contains"),
                        GsonHelper.getAsInt(jsonObject, "amount"));
            }
            return new MysteriousPackageRecipe(resourceLocation, ingredients,
                    GsonHelper.getAsString(jsonObject, "contains"));
        }

        public MysteriousPackageRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for(int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            return new MysteriousPackageRecipe(resourceLocation, ingredients, buffer.readUtf(), buffer.readInt());
        }

        public void toNetwork(FriendlyByteBuf buffer, MysteriousPackageRecipe recipe) {
            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeUtf(recipe.contains);
            buffer.writeInt(recipe.amount);
        }
    }
}
