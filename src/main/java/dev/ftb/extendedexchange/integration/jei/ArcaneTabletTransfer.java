package dev.ftb.extendedexchange.integration.jei;

import dev.ftb.extendedexchange.menu.ArcaneTabletMenu;
import dev.ftb.extendedexchange.menu.ModMenuTypes;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketArcaneTabletRecipeTransfer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArcaneTabletTransfer implements IRecipeTransferHandler<ArcaneTabletMenu, CraftingRecipe> {
    private final IRecipeTransferHandlerHelper transferHelper;

    public ArcaneTabletTransfer(IRecipeTransferHandlerHelper transferHelper) {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<ArcaneTabletMenu> getContainerClass() {
        return ArcaneTabletMenu.class;
    }

    @Override
    public Optional<MenuType<ArcaneTabletMenu>> getMenuType() {
        return Optional.of(ModMenuTypes.ARCANE_TABLET.get());
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }


    @Override
    public @Nullable IRecipeTransferError transferRecipe(ArcaneTabletMenu container, CraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            List<IRecipeSlotView> views = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            System.out.println("==========================================" + views.toString());
            List<Slot> slots = container.slots.stream().filter(s -> s.container instanceof CraftingContainer).toList();
            System.out.println(slots);
            System.out.println(slots.size());
            System.out.println(views.size());

            if (views.size() != slots.size()) {
                return transferHelper.createInternalError();
            }
            Int2ObjectMap<List<ItemStack>> map = new Int2ObjectOpenHashMap<>();
            for (int i = 0; i < views.size(); i++) {
                map.put(i, views.get(i).getIngredients(VanillaTypes.ITEM_STACK).collect(Collectors.toList()));
            }
            System.out.println(map);
            NetworkHandler.sendToServer(new PacketArcaneTabletRecipeTransfer(map, maxTransfer));
        }
        else {
            IKnowledgeProvider knowledgeProvider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID());
//            boolean b = knowledgeProviderFor.hasKnowledge();
            List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            List<Integer> missingSlots = new ArrayList<>(); // 存储缺少物品的槽位索引

            // 遍历所有输入槽位
            for (int slotIndex = 0; slotIndex < inputSlots.size(); slotIndex++) {
                IRecipeSlotView slot = inputSlots.get(slotIndex);

                // 获取槽位中的物品
                Optional<ITypedIngredient<?>> optionalIngredient = slot.getDisplayedIngredient();
                if (optionalIngredient.isPresent()) {
                    ITypedIngredient<?> ingredient = optionalIngredient.get();

                    // 只处理物品类型的材料
                    if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
                        List<ItemStack> items = ingredient.getIngredient(VanillaTypes.ITEM_STACK).stream().toList();

                        // 检查玩家是否有槽位中任意一个物品
                        for (ItemStack requiredStack : items) {
//
//                            System.out.println(requiredStack);
                            // 检查玩家是否有知识和足够的物品
                            if (knowledgeProvider.hasKnowledge(requiredStack) || player.getInventory().countItem(requiredStack.getItem()) >= requiredStack.getCount()) {
                                break;
                            }
                            missingSlots.add(slotIndex);
                        }
                    }
                }
            }
            // 输出缺少物品的槽位信息
            if (!missingSlots.isEmpty()) {
                return new ErrorRender(missingSlots);
            }
        }

        return null;

    }

    private record ErrorRender(List<Integer> missingSlots) implements IRecipeTransferError {

        @Override
        public Type getType() {
            return Type.COSMETIC;
        }

        @Override
        public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX, int recipeY) {
            var poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(recipeX, recipeY, 0);

            // 1) draw slot highlights
            var slotViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
            for (int i = 0; i < slotViews.size(); i++) {
                var slotView = slotViews.get(i);
//                boolean missing = indices.missingSlots().contains(i);
//                boolean craftable = indices.craftableSlots().contains(i);
                if (missingSlots.contains(i)) {
                    slotView.drawHighlight(guiGraphics, 0x66ff0000);
                }
            }

            poseStack.popPose();
        }
    }
}
