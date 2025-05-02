package dev.ftb.extendedexchange.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.client.gui.EMCFormat;
import dev.ftb.extendedexchange.item.ModItems;
import dev.ftb.extendedexchange.recipes.AlchemyTableRecipe;
import dev.ftb.extendedexchange.util.EXUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public  class AlchemyTableCategory extends AbstractEXCategory<AlchemyTableRecipe> {
    protected AlchemyTableCategory() {
        super(RecipeTypes.ALCHEMY_TABLE,
                Component.translatable("block.extendedexchange.alchemy_table"),
                guiHelper().drawableBuilder(EXUtils.rl("textures/gui/alchemy_table_jei.png"), 0, 0, 128, 18)
                        .setTextureSize(128, 64).build(),
                guiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.ALCHEMY_TABLE.get()))
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 1)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(0).getDisplayedIngredient(VanillaTypes.ITEM_STACK).ifPresent(stack -> {
            long emc = recipe.getTotalCost(stack);
            String s = EMCFormat.INSTANCE.format(emc) + " EMC";
            guiGraphics.drawString(font,s, (128 - font.width(s)) / 2f, 5f, 0xFF404040,  false);
//            font.draw(poseStack, s, (128 - font.width(s)) / 2f, 5f, 0xFF404040);
        });
    }

//    @Override
//    public void draw(AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
//        Font font = Minecraft.getInstance().font;
//        recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(0).getDisplayedIngredient(VanillaTypes.ITEM_STACK).ifPresent(stack -> {
//            long emc = recipe.getTotalCost(stack);
//            String s = EMCFormat.INSTANCE.format(emc) + " EMC";
//            font.draw(poseStack, s, (128 - font.width(s)) / 2f, 5f, 0xFF404040);
//        });
//    }


}
