package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.client.gui.buttons.ArrowButton;
import dev.ftb.extendedexchange.client.gui.buttons.ExtractItemButton;
import dev.ftb.extendedexchange.client.gui.buttons.HighlightButton;
import dev.ftb.extendedexchange.client.gui.buttons.SearchTypeButton;
import dev.ftb.extendedexchange.menu.ArcaneTabletMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ArcaneTabletScreen extends AbstractTableScreen<ArcaneTabletMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/arcane_tablet.png");

    public ArcaneTabletScreen(ArcaneTabletMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ArrowButton(leftPos + 7, topPos + 20, b -> changePage(false))
                .withTexture(TEXTURE, 196, 0));
        addRenderableWidget(new ArrowButton(leftPos + 151, topPos + 20, b -> changePage(true))
                .withTexture(TEXTURE, 215, 0));

        addRenderableWidget(new HighlightButton(leftPos + 80, topPos + 68).withTag("burn"));

        extractButtons.clear();
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 20, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 105, topPos + 26, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 55, topPos + 26, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 123, topPos + 44, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 37, topPos + 44, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 128, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 32, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 123, topPos + 92, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 37, topPos + 92, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 105, topPos + 110, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 55, topPos + 110, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 116, menu.getProvider()));

        addRenderableWidget(new HighlightButton(leftPos + 9, topPos + 116)
                .withTag("learn").withTooltip(Component.translatable("block.extendedexchange.stone_table.learn")));
        addRenderableWidget(new HighlightButton(leftPos + 153, topPos + 116)
                .withTag("unlearn").withTooltip(Component.translatable("block.extendedexchange.stone_table.unlearn")));

        addRenderableWidget(new HighlightButton(leftPos - 71, topPos + 16, 9, 9)
                .withTag("rotate").withTooltip(Component.translatable("gui.extendedexchange.arcane_tablet.rotate")));
        addRenderableWidget(new HighlightButton(leftPos - 71, topPos + 26, 9, 9)
                .withTag("balance").withTooltip(Component.translatable("gui.extendedexchange.arcane_tablet.balance")));
        addRenderableWidget(new HighlightButton(leftPos - 71, topPos + 61, 9, 9)
                .withTag("clear").withTooltip(Component.translatable("gui.extendedexchange.arcane_tablet.clear")));

        addRenderableWidget(new SearchTypeButton(leftPos - 71, topPos + 36));

        updateValidItemList();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        guiGraphics.blit(TEXTURE,leftPos - 75, topPos + 10, 180, 19, 76, 89);
    }

//    @Override
//    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
//        super.renderBg(poseStack, partialTick, mouseX, mouseY);
//        blit(poseStack, leftPos - 75, topPos + 10, 180, 19, 76, 89);
//    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    protected Rect2i searchFieldPos() {
        return new Rect2i(leftPos + 8, topPos + 7, 160, 11);
    }
}
