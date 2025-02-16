package com.exosomnia.exoadvadditions.recipes;

import com.exosomnia.exoadvadditions.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

    private final PackagedItem[] contains;
    private final ItemStack result;

    public record PackagedItem(String id, int count) {}

    public MysteriousPackageRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, PackagedItem[] contains) {
        super(resourceLocation, "", CraftingBookCategory.MISC, 3, 3, ingredients,
                new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get()), false);
        this.contains = contains;

        ItemStack result = new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get());
        CompoundTag tag = result.getOrCreateTag();
        ListTag contents = new ListTag();
        for (PackagedItem item : this.contains) {
            CompoundTag contentTag = new CompoundTag();
            contentTag.putString("id", item.id);
            contentTag.putInt("count", item.count);
            contents.add(contentTag);
        }
        tag.put("contents", contents);
        this.result = result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return this.result.copy(); }

    public static class Serializer implements RecipeSerializer<MysteriousPackageRecipe> {
        public MysteriousPackageRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            for (int i = 0; i < 9; ++i) {
                JsonElement element = array.get(i);
                if (element.isJsonNull()) { continue; }
                ingredients.set(i, Ingredient.fromJson(element));
            }

            JsonArray jsonContents = GsonHelper.getAsJsonArray(jsonObject, "contents");
            int amount = jsonContents.size();

            int index = 0;
            PackagedItem[] contents = new PackagedItem[amount];
            for (JsonElement element : jsonContents) {
                JsonObject object = element.getAsJsonObject();
                contents[index++] = new PackagedItem(object.get("id").getAsString(), object.get("count").getAsInt());
            }

            return new MysteriousPackageRecipe(resourceLocation, ingredients, contents);
        }

        public MysteriousPackageRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for(int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            int count = buffer.readInt();
            PackagedItem[] contents = new PackagedItem[count];
            int index = 0;
            for (var i = 0; i < count; i++) {
                contents[index++] = new PackagedItem(buffer.readUtf(), buffer.readInt());
            }

            return new MysteriousPackageRecipe(resourceLocation, ingredients, contents);
        }

        public void toNetwork(FriendlyByteBuf buffer, MysteriousPackageRecipe recipe) {
            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            int count = recipe.contains.length;
            buffer.writeInt(count);
            for (var i = 0; i < count; i++) {
                PackagedItem item = recipe.contains[i];
                buffer.writeUtf(item.id);
                buffer.writeInt(item.count);
            }
        }
    }
}
