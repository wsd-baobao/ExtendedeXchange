package dev.ftb.extendedexchange.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class HighlightButton extends EXButton {

    private final int x, y;

    public HighlightButton(int x, int y) {
        super(x, y, 14, 14, b -> { });
        this.x = x;
        this.y = y;
    }

    public HighlightButton(int x, int y, int w, int h) {
        super(x, y, w, h, b -> { });
        this.x = x;
        this.y = y;
    }

    public HighlightButton(int x, int y, int w, int h, OnPress onPress) {
        super(x, y, w, h, onPress);
        this.x = x;
        this.y = y;
    }



    private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/screenshots/blank.png");

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHovered) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
//            RenderSystem.setShaderTexture(0, BLANK_TEXTURE); // 禁用纹理
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            guiGraphics.fill(x, y, x + width, y + height, 0x80FFFFFF);
            RenderSystem.disableBlend();
        }
    }
}
