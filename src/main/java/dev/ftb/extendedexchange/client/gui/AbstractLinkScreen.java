package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.entity.AbstractLinkInvBlockEntity;
import dev.ftb.extendedexchange.client.EXClientEventHandler;
import dev.ftb.extendedexchange.inventory.FilterSlot;
import dev.ftb.extendedexchange.menu.AbstractLinkMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractLinkScreen<C extends AbstractLinkMenu<T>, T extends AbstractLinkInvBlockEntity> extends AbstractEXScreen<C,T> {
    public static final ResourceLocation TEXTURE_MK1 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/personal_link.png");
    public static final ResourceLocation TEXTURE_MK2 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/refined_link.png");
    public static final ResourceLocation TEXTURE_MK3 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/compressed_refined_link.png");

    public AbstractLinkScreen(C menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(font, menu.getBlockEntity().getOwnerName(), 8f, 6f, 0x404040, false);
        guiGraphics.drawString(font,EXClientEventHandler.getEMCRateString(), 8, getEMCLabelYPos(), 0x404040, false);
//        font.draw(poseStack, menu.getBlockEntity().getOwnerName(), 8f, 6f, 0x404040);
//        font.draw(poseStack, EXClientEventHandler.getEMCRateString(), 8, getEMCLabelYPos(), 0x404040);
    }

//    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
//        font.draw(poseStack, menu.getBlockEntity().getOwnerName(), 8f, 6f, 0x404040);
//        font.draw(poseStack, EXClientEventHandler.getEMCRateString(), 8, getEMCLabelYPos(), 0x404040);
//    }

    protected int getEMCLabelYPos() {
        return 73;
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> l = super.getTooltipFromContainerItem(stack);
        if (!stack.isEmpty() && getSlotUnderMouse() instanceof FilterSlot) {
            for (int i = 1; i <= 3; i++) {
                l.add(Component.translatable("gui.extendedexchange.link.tooltip." + i).withStyle(ChatFormatting.GRAY));
            }
        }
        return l;
    }

//    public List<Component> getTooltipFromItem(ItemStack itemStack) {
//        List<Component> l = super.getTooltipFromItem(itemStack);
//        if (!itemStack.isEmpty() && getSlotUnderMouse() instanceof FilterSlot) {
//            for (int i = 1; i <= 3; i++) {
//                l.add(Component.translatable("gui.extendedexchange.link.tooltip." + i).withStyle(ChatFormatting.GRAY));
//            }
//        }
//        return l;
//    }
}
