package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.client.gui.buttons.ArrowButton;
import dev.ftb.extendedexchange.client.gui.buttons.ExtractItemButton;
import dev.ftb.extendedexchange.client.gui.buttons.HighlightButton;
import dev.ftb.extendedexchange.config.ConfigHelper;
import dev.ftb.extendedexchange.menu.StoneTableMenu;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StoneTableScreen extends AbstractTableScreen<StoneTableMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/stone_table.png");

    public StoneTableScreen(StoneTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ArrowButton(leftPos + 7, topPos + 20, b -> changePage(false))
                .withTexture(TEXTURE, 196, 0));
        addRenderableWidget(new ArrowButton(leftPos + 151, topPos + 20, b -> changePage(true))
                .withTexture(TEXTURE, 215, 0));

        addRenderableWidget(new HighlightButton(leftPos + 9, topPos + 116)
                .withTag("learn").withTooltip(Component.translatable("block.extendedexchange.stone_table.learn")));
        addRenderableWidget(new HighlightButton(leftPos + 153, topPos + 116)
                .withTag("unlearn").withTooltip(Component.translatable("block.extendedexchange.stone_table.unlearn")));

        addRenderableWidget(new HighlightButton(leftPos + 80, topPos + 68).withTag("burn"));

        extractButtons.clear();
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 28, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 110, topPos + 38, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 50, topPos + 38, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 120, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 40, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 110, topPos + 98, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 50, topPos + 98, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 108, menu.getProvider()));

        updateValidItemList();
    }

    public Rect2i searchFieldPos() {
        return new Rect2i(leftPos + 8, topPos + 7, 160, 11);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> list = super.getTooltipFromContainerItem(itemStack);
        if (!ConfigHelper.isStoneTableWhitelisted(itemStack) && IEMCProxy.INSTANCE.hasValue(itemStack)) {
            list.add(Component.translatable("gui.extendedexchange.stone_table.cant_use").withStyle(ChatFormatting.RED));
        }
        return list;
    }
//    @Override
//    public List<Component> getTooltipFromItem(ItemStack itemStack) {
//        List<Component> list = super.getTooltipFromItem(itemStack);
//        if (!ConfigHelper.isStoneTableWhitelisted(itemStack) && ProjectEAPI.getEMCProxy().hasValue(itemStack)) {
//            list.add(Component.translatable("gui.extendedexchange.stone_table.cant_use").withStyle(ChatFormatting.RED));
//        }
//        return list;
//    }
}
