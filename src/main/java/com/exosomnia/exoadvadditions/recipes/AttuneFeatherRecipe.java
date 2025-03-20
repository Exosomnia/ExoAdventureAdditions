package com.exosomnia.exoadvadditions.recipes;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttuneFeatherRecipe extends ShapelessRecipe {

    private final static Set<Item> MAGICKED_FEATHER_ITEMS = Set.of(Registry.ITEM_MAGICKED_FEATHER.get(), Registry.ITEM_INFERNAL_FEATHER.get(),
            Registry.ITEM_ENDER_FEATHER.get(), Registry.ITEM_ANCIENT_FEATHER.get());

    private final static CompoundTag BASE_FEATHER_TAG = new CompoundTag();
    private final ItemStack resultFeather;
    static {
        BASE_FEATHER_TAG.put("LocateData", new CompoundTag());
    }

    public AttuneFeatherRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> ingredients, ItemStack resultFeather) {
        super(resourceLocation, "", CraftingBookCategory.MISC, resultFeather, ingredients);
        this.resultFeather = resultFeather;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        List<ItemStack> itemStacks = container.getItems();
        ItemStack fromFeather = ItemStack.EMPTY;

        for(ItemStack itemStack : itemStacks) {
            if (getIngredients().get(0).test(itemStack)) {
                fromFeather = itemStack;
                break;
            }
        }

        if (!fromFeather.isEmpty()) {
            ItemStack toFeather = resultFeather.copy();
            toFeather.setTag(fromFeather.getTag());
            return toFeather.copy();
        }
        return resultFeather.copy();
    }

    public static class Serializer implements RecipeSerializer<AttuneFeatherRecipe> {
        public AttuneFeatherRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack toFeather = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "feather"));
            Set<Item> validItems = new HashSet<>(MAGICKED_FEATHER_ITEMS);
            validItems.remove(toFeather.getItem());

            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(PartialNBTIngredient.of(BASE_FEATHER_TAG, validItems.toArray(new ItemLike[3])));
            ingredients.add(StrictNBTIngredient.of(toFeather));

            toFeather.setTag(BASE_FEATHER_TAG);
            return new AttuneFeatherRecipe(resourceLocation, ingredients, toFeather);
        }

        public AttuneFeatherRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            ItemStack toFeather = buffer.readItem();
            Set<Item> validItems = new HashSet<>(MAGICKED_FEATHER_ITEMS);
            validItems.remove(toFeather.getItem());

            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(PartialNBTIngredient.of(BASE_FEATHER_TAG, validItems.toArray(new ItemLike[3])));
            ingredients.add(StrictNBTIngredient.of(toFeather));

            toFeather.setTag(BASE_FEATHER_TAG);
            return new AttuneFeatherRecipe(resourceLocation, ingredients, toFeather);
        }

        public void toNetwork(FriendlyByteBuf buffer, AttuneFeatherRecipe recipe) {
            buffer.writeItem(recipe.resultFeather);
        }
    }
}
