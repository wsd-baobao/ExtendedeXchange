package dev.ftb.extendedexchange.client.gui;

import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.client.gui.buttons.ExtractItemButton;
import dev.ftb.extendedexchange.config.ConfigHelper;
import dev.ftb.extendedexchange.integration.jei.JEIHooks;
import dev.ftb.extendedexchange.menu.AbstractTableMenu;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * Base functionality for the Stone Tablet and Arcane Transmutation Tablet GUIs
 * @param <C>
 */
public abstract class AbstractTableScreen<C extends AbstractTableMenu> extends AbstractEXScreen<C,AbstractEMCBlockEntity> implements KnowledgeUpdateListener {

    // static so they persist across GUI invocations
    private static int staticPage = 0;
    private static String staticSearch = "";
    
    private final List<ItemStack> validItems = new ArrayList<>();
    protected final List<ExtractItemButton> extractButtons = new ArrayList<>();
    private EditBox searchField;

    public AbstractTableScreen(C menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageHeight = 217;
    }

    @Override
    protected void init() {
        super.init();

        Rect2i tb = searchFieldPos();
        searchField = new EditBox(font, tb.getX(), tb.getY(), tb.getWidth(), tb.getHeight(),Component.empty());
        searchField.setTextColor(0xFFFFFFFF);
        searchField.setTextColorUneditable(0xFF808080);
        searchField.setBordered(false);
        searchField.setMaxLength(35);
        searchField.setValue(staticSearch);
        if (ConfigHelper.client().general.searchType.get().autoselected) {
            setInitialFocus(searchField);
        }
        addRenderableWidget(searchField);
    }

    protected abstract Rect2i searchFieldPos();

    protected void addExtractButton(ExtractItemButton extractItemButton) {
        extractButtons.add(extractItemButton);
        addRenderableWidget(extractItemButton);
    }


    @Override
    protected void containerTick() {
        if (!staticSearch.equals(searchField.getValue())) {
            staticSearch = searchField.getValue();
            staticPage = 0;
            updateValidItemList();
            if (ConfigHelper.client().general.searchType.get().jeiSync) JEIHooks.handleJEISync(staticSearch);
        }
    }



    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        changePage(delta < 0);  // scroll down = forward a page
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchFieldPos().contains((int) mouseX, (int) mouseY) && button == 1) {
            searchField.setValue("");
            return true;
        }
        //  如果点击的不是搜索框就取消焦点
        if (!searchFieldPos().contains((int) mouseX, (int) mouseY) && searchField.isFocused()) {
            searchField.setFocused(false);
        }
        System.out.println(button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(p -> {
            String s = EMCFormat.INSTANCE.format(p.getEmc());
//            font.draw(poseStack, s, ((imageWidth - font.width(s)) / 2f),  -9f, 0xFFB5B5B5);
            guiGraphics.drawString(font, s, ((imageWidth - font.width(s)) / 2f),  -9f, 0xFFB5B5B5,  false);
        });
    }

//    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
//        Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(p -> {
//            String s = EMCFormat.INSTANCE.format(p.getEmc());
//            font.draw(poseStack, s, ((imageWidth - font.width(s)) / 2f),  -9f, 0xFFB5B5B5);
//        });
//    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers) || this.searchField.canConsumeInput()) {
            if (keyCode == GLFW.GLFW_KEY_TAB) changeFocus(getCurrentFocusPath());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onKnowledgeUpdate() {
        staticPage = 0;
        staticSearch = "";
        searchField.setValue("");
        updateValidItemList();
    }

    protected void changePage(boolean forward) {
        if (forward) {
            if (staticPage < Math.ceil(validItems.size() / (float) extractButtons.size()) - 1) {
                staticPage++;
                updateDisplayedItems();
            }
        } else if (staticPage > 0) {
            staticPage--;
            updateDisplayedItems();
        }
    }

    protected void updateValidItemList() {
        validItems.clear();

        String srchStr = trim(staticSearch);
        boolean mod = srchStr.startsWith("@");
        if (mod) {
            srchStr = srchStr.substring(1);
        }

        for (ItemInfo itemInfo : menu.getProvider().getKnowledge()) {
            ItemStack stack = itemInfo.createStack();
            if (!stack.isEmpty() && menu.isItemValid(stack) && (srchStr.isEmpty() || mod ?
                    ForgeRegistries.ITEMS.getKey(itemInfo.getItem()).getNamespace().startsWith(srchStr) :
                    StringUtils.contains(trim(stack.getDisplayName().getString()), srchStr)))
            {
                validItems.add(IEMCProxy.INSTANCE.getPersistentInfo(itemInfo).createStack());
            }
        }

        validItems.sort(Comparator.comparingLong(o -> IEMCProxy.INSTANCE.getValue(o)));
//        Collections.reverse(validItems);
        updateDisplayedItems();
    }

    private String trim(String s) {
        return ChatFormatting.stripFormatting(s).trim().toLowerCase(Locale.ROOT);
    }

    public void updateDisplayedItems() {
        for (int i = 0; i < extractButtons.size(); i++) {
            int index = i + staticPage * extractButtons.size();
            if (index >= 0 && index < validItems.size()) {
                extractButtons.get(i).setItem(validItems.get(index));
                extractButtons.get(i).visible = true;
            } else {
                extractButtons.get(i).setItem(ItemStack.EMPTY);
                extractButtons.get(i).visible = false;
            }
        }
    }
}
