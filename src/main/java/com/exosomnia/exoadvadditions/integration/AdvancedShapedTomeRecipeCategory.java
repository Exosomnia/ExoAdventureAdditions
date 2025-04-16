package com.exosomnia.exoadvadditions.integration;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.recipes.tome.AdvancedShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipe;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AdvancedShapedTomeRecipeCategory implements IRecipeCategory<AdvancedShapedTomeRecipe> {

    public final static RecipeType<AdvancedShapedTomeRecipe> TOME_RECIPE = new RecipeType<>(ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "advanced_tome_crafting"),
            AdvancedShapedTomeRecipe.class);

    private final static Component TITLE = Component.translatable("recipe_category.exoadvadditions.tome_crafting.alt");
    private final IDrawable icon;
    private final IDrawable iconHelp;

    private final static int INDEX_SIZE = 4;

    public AdvancedShapedTomeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemLike(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get());
        iconHelp = guiHelper.drawableBuilder(JEIIntegration.INFO_ICON, 0, 0, 16, 16).setTextureSize(16,16).build();
    }

    public int getWidth() { return (int)(81 * getTargetScale()) + 53; }
    public int getHeight() { return (int)(230 * getTargetScale()); }

    private float getTargetScale() {
        Window window = Minecraft.getInstance().getWindow();
        int currentScale = window.calculateScale((int)window.getGuiScale(), false);
        return currentScale > 2 ? (2.0F / (float)currentScale) : 1.0F;
    }

    @Override
    public RecipeType<AdvancedShapedTomeRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, AdvancedShapedTomeRecipe tomeRecipe, IFocusGroup iFocusGroup) {
        float targetScale = getTargetScale();

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
        int itemXOffset = (int)Math.ceil(81 * targetScale);
        if (items != null && !items.isEmpty()) {
            int itemCount = items.size();
//            for (TomeRecipe.ItemMapping itemMapping : items) {
//                itemCount += itemMapping.count;
//            }
            int itemIndex = 0;
            int itemYOffset = ((getHeight()/2) - 9) - ((itemCount - 1) * 9);
            for (TomeRecipe.ItemMapping itemMapping : items) {
                builder.addSlot(RecipeIngredientRole.INPUT, itemXOffset, itemYOffset + (itemIndex++ * 18)).addIngredients(itemMapping.ingredient);
//                if (itemMapping.count > 1) {
//                    for (var ii = 1; ii < itemMapping.count; ii++) {
//                        builder.addSlot(RecipeIngredientRole.INPUT, itemXOffset, itemYOffset + (itemIndex++ * 18)).addIngredients(itemMapping.ingredient);
//                    }
//                }
            }
        }
        ItemStack result = tomeRecipe.getResult();
        builder.addSlot(RecipeIngredientRole.OUTPUT, itemXOffset + 36, (getHeight()/2) - 9).addItemStack(result == null ? ItemStack.EMPTY : result);

        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addItemLike(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get());
    }

    @Override
    public void draw(AdvancedShapedTomeRecipe tomeRecipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<TomeRecipe.BlockMapping[][]> layers = tomeRecipe.getRecipeShape();
        int size = layers.size();
        float targetScale = getTargetScale();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.last().pose().scale(targetScale);

        for (var lay = 0; lay < size; lay ++) {
            int totalIndex = 16;
            TomeRecipe.BlockMapping[][] layer = layers.get(lay);
            int drawYOffset = 184 - (46 * lay);
            for (var i = 0; i < size; i++) {
                Vector2i iStartPos = new Vector2i(32 - (8 * i), (i * 4));
                for (var ii = 0; ii < size; ii++) {
                    Block currentBlock;
                    Set<Block> validBlocks = layer[INDEX_SIZE - i][INDEX_SIZE - ii].validBlocks();
                    if (!validBlocks.isEmpty()) {
                        int validCount = validBlocks.size();
                        if (validCount == 1) { currentBlock = validBlocks.stream().findFirst().get(); }
                        else { currentBlock = validBlocks.toArray(new Block[0])[(int)(ExoArmory.RENDERING_MANAGER.getTotalTicks() * .05) % validCount]; }
                        if (!Screen.hasShiftDown() /*&& ((i % (size - 1) == 0) || (ii % (size - 1) == 0))*/) currentBlock = currentBlock.equals(Blocks.AIR) ? Registry.BLOCK_BLANK_OUTLIINE.get() : currentBlock;
                        guiGraphics.renderItem(new ItemStack(currentBlock.asItem()), iStartPos.x + (8 * ii), iStartPos.y + drawYOffset + (4 * ii), 0, totalIndex += 16);
                    }
                }
            }
        }

        poseStack.popPose();

        Font drawFont = Minecraft.getInstance().font;
        ImmutableList<TomeRecipe.ItemMapping> items = tomeRecipe.getRecipeItems();
        if (items != null && !items.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 320);
            int itemXOffset = (int)Math.ceil(81 * targetScale) + 17;
            int itemCount = items.size();
            int itemIndex = 0;
            int itemYOffset = (getHeight()/2) - ((itemCount - 1) * 9);
            for (TomeRecipe.ItemMapping itemMapping : items) {
                if (itemMapping.count > 1) {
                    String drawString = String.valueOf(itemMapping.count);
                    guiGraphics.drawString(drawFont, drawString, itemXOffset - drawFont.width(drawString), itemYOffset + (itemIndex++ * 18), 0xFFFFFF);
                }
                else { itemIndex++; }
            }
            guiGraphics.pose().popPose();
        }

        icon.draw(guiGraphics, (int)Math.ceil(81 * targetScale) + 18, (getHeight()/2) - 9);
        Rect2i hoverAreaTome = new Rect2i((int)Math.ceil(81 * targetScale) + 18, (getHeight()/2) - 9, 16, 16);

        if (tomeRecipe.hasRecipeHelp()) {
            iconHelp.draw(guiGraphics, (int)Math.ceil(81 * targetScale) + 18, (getHeight()/2) - 26);
            Rect2i hoverAreaHelp = new Rect2i((int)Math.ceil(81 * targetScale) + 18, (getHeight()/2) - 27, 16, 16);

            if (hoverAreaHelp.contains((int)mouseX, (int)mouseY)) {
                ImmutableList.Builder<Component> tooltipLines = new ImmutableList.Builder<>();
                tomeRecipe.getRecipeHelp().forEach(help -> tooltipLines.add(ComponentUtils.formatLine(I18n.get(help), ComponentUtils.Styles.DEFAULT_DESC.getStyle(),
                        ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle())));
                guiGraphics.renderTooltip(Minecraft.getInstance().font,
                        tooltipLines.build(),
                        Optional.empty(), (int)mouseX, (int)mouseY);
                return; //Return to prevent checking for tome hover
            }
        }

        if (hoverAreaTome.contains((int)mouseX, (int)mouseY)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font,
                    List.of(ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.1.alt"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.2"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.3"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.4"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.5"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine("", ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.6"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.7"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle()),
                            ComponentUtils.formatLine(I18n.get("recipe_category.exoadvadditions.tome_crafting.help.8"),
                                    ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle())),
                    Optional.empty(), (int)mouseX, (int)mouseY);
        }
    }
}
