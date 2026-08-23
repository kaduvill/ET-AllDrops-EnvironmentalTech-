package com.kaduvill.etvoidminerall.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public final class AggregateRecipeCategory implements IRecipeCategory<AggregateRecipeWrapper> {
    static final int COLUMNS = 9;
    static final int SLOT_SIZE = 18;
    static final int HEADER_HEIGHT = 13;
    static final int FOOTER_HEIGHT = 13;
    static final int MAX_ROWS = 9;
    static final int WIDTH = 160;
    static final int GRID_WIDTH = COLUMNS * SLOT_SIZE;
    static final int GRID_X = (WIDTH - GRID_WIDTH) / 2;
    static final int GRID_Y = HEADER_HEIGHT;
    private final VoidMinerType type;
    private final IDrawable background;
    private final IDrawable slot;
    private final int pageSize;
    private final int footerY;

    public AggregateRecipeCategory(IGuiHelper guiHelper, VoidMinerType type, int maxDrops) {
        int rows = Math.max(1, Math.min(MAX_ROWS, (maxDrops + COLUMNS - 1) / COLUMNS));
        this.type = type;
        this.pageSize = rows * COLUMNS;
        this.footerY = maxDrops > pageSize ? GRID_Y + rows * SLOT_SIZE : -1;
        this.background = guiHelper.createBlankDrawable(WIDTH,
                GRID_Y + rows * SLOT_SIZE + (footerY >= 0 ? FOOTER_HEIGHT : 0));
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public String getUid() {
        return type.getUid();
    }

    @Override
    public String getTitle() {
        return type.getTitle();
    }

    @Override
    public String getModName() {
        return I18n.format("jei.etvoidminerall.mod_name");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        for (int index = 0; index < pageSize; index++) {
            slot.draw(minecraft, GRID_X + index % COLUMNS * SLOT_SIZE, GRID_Y + index / COLUMNS * SLOT_SIZE);
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AggregateRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        recipeWrapper.bindLayout(itemStacks, pageSize, footerY);
    }
}
