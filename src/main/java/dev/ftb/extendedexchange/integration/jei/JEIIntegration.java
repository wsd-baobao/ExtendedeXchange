package dev.ftb.extendedexchange.integration.jei;

import com.google.common.collect.ImmutableList;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.ModBlocks;
import dev.ftb.extendedexchange.client.gui.AbstractEXScreen;
import dev.ftb.extendedexchange.client.gui.AlchemyTableScreen;
import dev.ftb.extendedexchange.client.gui.ArcaneTabletScreen;
import dev.ftb.extendedexchange.item.ModItems;
import dev.ftb.extendedexchange.recipes.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.logging.Logger;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(ExtendedExchange.MOD_ID, "default");

    static IJeiHelpers jeiHelpers;
    static IJeiRuntime runtime;

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new AlchemyTableCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        addRecipeType(registration, ModRecipeTypes.ALCHEMY_TABLE.get(), RecipeTypes.ALCHEMY_TABLE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALCHEMY_TABLE.get()), RecipeTypes.ALCHEMY_TABLE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.ARCANE_TABLET.get()), mezz.jei.api.constants.RecipeTypes.CRAFTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlchemyTableScreen.class, 78, 35, 23, 14, RecipeTypes.ALCHEMY_TABLE);

        registration.addGhostIngredientHandler(AbstractEXScreen.class, new EMCLinkJEI<>());

        registration.addGuiContainerHandler(ArcaneTabletScreen.class, new ArcaneTabletGuiArea());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new ArcaneTabletTransfer(registration.getTransferHelper()));
    }

    private <C extends Container, T extends Recipe<C>> void addRecipeType(IRecipeRegistration registration, RecipeType<T> type, mezz.jei.api.recipe.RecipeType<T> recipeType) {
        List<T> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type);
        registration.addRecipes(recipeType, ImmutableList.copyOf(recipes));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        System.out.println("JEI is available");
        runtime = jeiRuntime;
    }

    public static void setFilterText(String text) {
        runtime.getIngredientFilter().setFilterText(text);
    }
}
