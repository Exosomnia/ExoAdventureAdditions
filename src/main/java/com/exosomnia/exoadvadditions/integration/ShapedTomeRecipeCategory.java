package com.exosomnia.exoadvadditions.integration;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipe;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ShapedTomeRecipeCategory implements IRecipeCategory<ShapedTomeRecipe> {

    public final static RecipeType<ShapedTomeRecipe> TOME_RECIPE = new RecipeType<>(ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "tome_crafting"),
            ShapedTomeRecipe.class);

    private final static Component TITLE = Component.translatable("recipe_category.exoadvadditions.tome_crafting");
    private final IDrawable icon;

    private final static int INDEX_SIZE = 2;

    public ShapedTomeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemLike(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get());
    }

    public int getWidth() { return 102; }
    public int getHeight() { return 96; }

    @Override
    public RecipeType<ShapedTomeRecipe> getRecipeType() {
        return TOME_RECIPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShapedTomeRecipe tomeRecipe, IFocusGroup iFocusGroup) {
        List<TomeRecipe.BlockMapping[][]> layers = tomeRecipe.getRecipeShape();
        int size = layers.size();
        for (TomeRecipe.BlockMapping[][] layer : layers) {
            for (var i = 0; i < size; i++) {
                for (var ii = 0; ii < size; ii++) {
                    Set<Block> validBlocks = layer[i][ii].validBlocks();
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(Ingredient.of(validBlocks.toArray(new Block[0])));
                }
            }
        }
        ImmutableList<TomeRecipe.ItemMapping> items = tomeRecipe.getRecipeItems();
        if (items != null && !items.isEmpty()) {
            int itemCount = items.size();
//            for (TomeRecipe.ItemMapping itemMapping : items) {
//                itemCount += itemMapping.count;
//            }
            int itemIndex = 0;
            int itemYOffset = 40 - ((itemCount - 1) * 9);
            for (TomeRecipe.ItemMapping itemMapping : items) {
                builder.addSlot(RecipeIngredientRole.INPUT, 49, itemYOffset + (itemIndex++ * 18)).addIngredients(itemMapping.ingredient);
//                if (itemMapping.count > 1) {
//                    for (var ii = 1; ii < itemMapping.count; ii++) {
//                        builder.addSlot(RecipeIngredientRole.INPUT, 49, itemYOffset + (itemIndex++ * 18)).addIngredients(itemMapping.ingredient);
//                    }
//                }
            }
        }
        ItemStack result = tomeRecipe.getResult();
        if (result != null) { builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 40).addItemStack(result); }

        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addItemLike(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get());
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addItemLike(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get());
    }

    @Override
    public void draw(ShapedTomeRecipe tomeRecipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<TomeRecipe.BlockMapping[][]> layers = tomeRecipe.getRecipeShape();
        int size = layers.size();
        int totalIndex = 16;

        for (var lay = 0; lay < size; lay ++) {
            TomeRecipe.BlockMapping[][] layer = layers.get(lay);
            int drawYOffset = 64 - (32 * lay);
            for (var i = 0; i < size; i++) {
                Vector2i iStartPos = new Vector2i(16 - (8 * i), (i * 4));
                for (var ii = 0; ii < size; ii++) {
                    Block currentBlock;
                    Set<Block> validBlocks = layer[INDEX_SIZE - i][INDEX_SIZE - ii].validBlocks();
                    if (!validBlocks.isEmpty()) {
                        int validCount = validBlocks.size();
                        if (validCount == 1) { currentBlock = validBlocks.stream().findFirst().get(); }
                        else { currentBlock = validBlocks.toArray(new Block[0])[(int)(ExoArmory.RENDERING_MANAGER.getTotalTicks() * .05) % validCount]; }
                        guiGraphics.renderItem(new ItemStack(currentBlock.asItem()), iStartPos.x + (8 * ii), iStartPos.y + drawYOffset + (4 * ii), 0, totalIndex += 16);
                    }
                }
            }
        }

        Font drawFont = Minecraft.getInstance().font;
        ImmutableList<TomeRecipe.ItemMapping> items = tomeRecipe.getRecipeItems();
        if (items != null && !items.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 1600);
            int itemCount = items.size();
            int itemIndex = 0;
            int itemYOffset = 49 - ((itemCount - 1) * 9);
            for (TomeRecipe.ItemMapping itemMapping : items) {
                if (itemMapping.count > 1) {
                    String drawString = String.valueOf(itemMapping.count);
                    guiGraphics.drawString(drawFont, drawString, 66 - drawFont.width(drawString), itemYOffset + (itemIndex++ * 18), 0xFFFFFF);
                }
                else { itemIndex++; }
            }
            guiGraphics.pose().popPose();
        }

        icon.draw(guiGraphics, 67, 40);
        Rect2i hoverArea = new Rect2i(67, 40, 17, 17);

        if (hoverArea.contains((int)mouseX, (int)mouseY)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font,
                    List.of(ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.1"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.2"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.3"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.4"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.5"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle())),
                    Optional.empty(), (int)mouseX, (int)mouseY);
        }
    }
}
