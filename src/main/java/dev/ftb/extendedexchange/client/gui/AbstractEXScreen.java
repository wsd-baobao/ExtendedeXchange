package dev.ftb.extendedexchange.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.menu.AbstractEXMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEXScreen<C extends AbstractEXMenu<T>, T extends AbstractEMCBlockEntity> extends AbstractContainerScreen<C> {
    public AbstractEXScreen(C menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public static void bindTexture(ResourceLocation guiTexture) {
       RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, guiTexture);
//        RenderSystem.enableTexture();
    }

    final void bindGuiTexture() {
        ResourceLocation guiTexture = getGuiTexture();
        if (guiTexture != null) {
            bindTexture(guiTexture);
        }
    }

    protected abstract ResourceLocation getGuiTexture();

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        List<Component> tooltip = new ArrayList<>();
        renderables.stream()
                .filter(w -> w instanceof ITooltipProvider p && p.shouldProvide())
                .forEach(w -> ((ITooltipProvider) w).addTooltip(mouseX, mouseY, tooltip, Screen.hasShiftDown()));
        if (!tooltip.isEmpty()) {
            guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }


    }

//    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
//
//    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        bindGuiTexture();

        int xStart = (width - imageWidth) / 2;
        int yStart = (height - imageHeight) / 2;
        guiGraphics.blit(getGuiTexture(), xStart, yStart, 0, 0, imageWidth, imageHeight);
//        blit(poseStack, xStart, yStart, 0, 0, imageWidth, imageHeight);
    }

//    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
//
//    }
}
