package dev.ftb.extendedexchange.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.client.gui.AbstractEXScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ArrowButton extends EXButton {
    private final int x, y;
    public ArrowButton(int x, int y, OnPress onPress) {
        super(x, y, 18, 18, onPress);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHovered) {
            AbstractEXScreen.bindTexture(texture);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            guiGraphics.blit(texture, x, y,textureX, textureY, width, height);
//            blit(poseStack, x, y, textureX, textureY, width, height);
            RenderSystem.disableBlend();
        }
    }

}
