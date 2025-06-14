package com.exosomnia.exoadvadditions.recipes;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.items.AttributeTemplateItem;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
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
import java.util.stream.Stream;

public class AttributeTemplateSmithingRecipe extends SmithingTrimRecipe {

    final Ingredient template;
    final Ingredient addition;

    public AttributeTemplateSmithingRecipe(ResourceLocation resourceLocation, Ingredient template, Ingredient addition) {
        super(resourceLocation, template, Ingredient.of(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("forge", "armors"))), addition);
        this.template = template;
        this.addition = addition;
    }

    @Override
    public boolean matches(Container container, Level level) {
        boolean validTemplate = false;
        ItemStack templateItemStack = container.getItem(0);
        if (this.template.test(templateItemStack) && templateItemStack.getItem() instanceof AttributeTemplateItem attributeTemplateItem &&
                attributeTemplateItem.validate(container.getItem(1))) {
            validTemplate = true;
        }
        return validTemplate && this.addition.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess level) {
        ItemStack itemstack = container.getItem(1).copy();
        CompoundTag compoundtag = container.getItem(1).getTag();
        if (compoundtag != null && container.getItem(0).getItem() instanceof AttributeTemplateItem attributeTemplateItem) {
            compoundtag.putString("AttributeTemplate", attributeTemplateItem.getId());
            itemstack.setTag(compoundtag.copy());
        }
        return itemstack;
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(Items.IRON_CHESTPLATE);
    }

    public RecipeSerializer<?> getSerializer() {
        return Registry.RECIPE_ATTRIBUTE_TEMPLATE_SMITHING.get();
    }

    public static class Serializer implements RecipeSerializer<AttributeTemplateSmithingRecipe> {
        public AttributeTemplateSmithingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "template"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "addition"));
            return new AttributeTemplateSmithingRecipe(resourceLocation, ingredient, ingredient1);
        }

        public AttributeTemplateSmithingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            return new AttributeTemplateSmithingRecipe(resourceLocation, ingredient, ingredient1);
        }

        public void toNetwork(FriendlyByteBuf buffer, AttributeTemplateSmithingRecipe recipe) {
            recipe.template.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
