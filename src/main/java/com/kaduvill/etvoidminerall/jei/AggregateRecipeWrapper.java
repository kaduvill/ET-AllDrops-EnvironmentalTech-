package com.kaduvill.etvoidminerall.jei;

import com.kaduvill.etvoidminerall.loot.VoidMinerDrop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public final class AggregateRecipeWrapper implements IRecipeWrapper {
    private static final int HIDDEN_POSITION = -10000;
    private static final int PAGE_BUTTON_WIDTH = 18;
    private static final int LEFT_PAGE_X = 0;
    private static final int RIGHT_PAGE_X = AggregateRecipeCategory.WIDTH - PAGE_BUTTON_WIDTH;
    private final VoidMinerType type;
    private final int tier;
    private final List<VoidMinerDrop> drops;
    private final List<ItemStack> outputs;
    private IGuiItemStackGroup itemStacks;
    private int page;
    private int pageSize;
    private int footerY = -1;

    public AggregateRecipeWrapper(VoidMinerType type, int tier, List<VoidMinerDrop> drops) {
        this.type = type;
        this.tier = tier;
        this.drops = Collections.unmodifiableList(new ArrayList<>(drops));
        List<ItemStack> stacks = new ArrayList<>(drops.size());
        for (VoidMinerDrop drop : drops) stacks.add(drop.getStack());
        this.outputs = Collections.unmodifiableList(stacks);
    }

    public VoidMinerType getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    public List<VoidMinerDrop> getDrops() {
        return drops;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // Outputs control JEI's recipe/usage lookup: R finds this page and U does not.
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        FontRenderer font = minecraft.fontRenderer;
        String text = I18n.format("jei.etvoidminerall.tier_drops", tier, drops.size());
        font.drawString(text, (recipeWidth - font.getStringWidth(text)) / 2, 1, 0x404040);

        int pageCount = getPageCount();
        if (footerY < 0 || pageCount < 2) return;
        int textY = footerY + (AggregateRecipeCategory.FOOTER_HEIGHT - font.FONT_HEIGHT) / 2;
        String pageText = I18n.format("jei.etvoidminerall.page", page + 1, pageCount);
        font.drawString(pageText, (recipeWidth - font.getStringWidth(pageText)) / 2, textY, 0x606060);
        font.drawString("<", LEFT_PAGE_X + (PAGE_BUTTON_WIDTH - font.getStringWidth("<")) / 2,
                textY, page > 0 ? 0x404040 : 0xA0A0A0);
        font.drawString(">", RIGHT_PAGE_X + (PAGE_BUTTON_WIDTH - font.getStringWidth(">")) / 2,
                textY, page + 1 < pageCount ? 0x404040 : 0xA0A0A0);
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        int pageCount = getPageCount();
        if (mouseButton != 0 || footerY < 0 || pageCount < 2
                || mouseY < footerY || mouseY >= footerY + AggregateRecipeCategory.FOOTER_HEIGHT) return false;
        if (mouseX >= LEFT_PAGE_X && mouseX < LEFT_PAGE_X + PAGE_BUTTON_WIDTH) {
            if (page > 0) setPage(page - 1);
            return true;
        }
        if (mouseX >= RIGHT_PAGE_X && mouseX < RIGHT_PAGE_X + PAGE_BUTTON_WIDTH) {
            if (page + 1 < pageCount) setPage(page + 1);
            return true;
        }
        return false;
    }

    void bindLayout(IGuiItemStackGroup itemStacks, int pageSize, int footerY) {
        this.itemStacks = itemStacks;
        this.pageSize = pageSize;
        this.footerY = footerY;
        this.page = Math.max(0, Math.min(page, getPageCount() - 1));
        int firstVisible = page * pageSize;
        int lastVisible = Math.min(firstVisible + pageSize, drops.size());
        for (int index = 0; index < drops.size(); index++) {
            initSlot(index, index >= firstVisible && index < lastVisible ? index - firstVisible : -1);
        }
    }

    private void setPage(int newPage) {
        if (newPage == page || newPage < 0 || newPage >= getPageCount()) return;
        int oldPage = page;
        page = newPage;
        positionPage(oldPage, false);
        positionPage(page, true);
    }

    private void positionPage(int targetPage, boolean visible) {
        int first = targetPage * pageSize;
        int last = Math.min(first + pageSize, drops.size());
        for (int index = first; index < last; index++) initSlot(index, visible ? index - first : -1);
    }

    private void initSlot(int index, int visibleIndex) {
        int x = HIDDEN_POSITION;
        int y = HIDDEN_POSITION;
        if (visibleIndex >= 0) {
            x = AggregateRecipeCategory.GRID_X
                    + visibleIndex % AggregateRecipeCategory.COLUMNS * AggregateRecipeCategory.SLOT_SIZE;
            y = AggregateRecipeCategory.GRID_Y
                    + visibleIndex / AggregateRecipeCategory.COLUMNS * AggregateRecipeCategory.SLOT_SIZE;
        }
        // Void Miner drops are products. Keep every concrete drop in its own output slot.
        itemStacks.init(index, false, x, y);
        itemStacks.set(index, drops.get(index).getStack());
    }

    private int getPageCount() {
        return pageSize > 0 ? Math.max(1, (drops.size() + pageSize - 1) / pageSize) : 1;
    }
}
