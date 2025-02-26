package de.maxhenkel.voicechat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.net.CreateGroupPacket;
import de.maxhenkel.voicechat.net.NetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class CreateGroupScreen extends VoiceChatScreenBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_create_group.png");
    private static final Component TITLE = Component.translatable("gui.voicechat.create_group.title");
    private static final Component CREATE = Component.translatable("message.voicechat.create");
    private static final Component CREATE_GROUP = Component.translatable("message.voicechat.create_group");
    private static final Component GROUP_NAME = Component.translatable("message.voicechat.group_name");
    private static final Component OPTIONAL_PASSWORD = Component.translatable("message.voicechat.optional_password");

    private EditBox groupName;
    private EditBox password;
    private Button createGroup;

    public CreateGroupScreen() {
        super(TITLE, 195, 100);
    }

    @Override
    protected void init() {
        super.init();
        hoverAreas.clear();
        clearWidgets();

        groupName = new EditBox(font, guiLeft + 7, guiTop + 7 + (font.lineHeight + 5) * 2 - 5 + 2, xSize - 7 * 2, 10, Component.empty());
        groupName.setMaxLength(16);
        groupName.setFilter(s -> s.isEmpty() || Voicechat.GROUP_REGEX.matcher(s).matches());
        addRenderableWidget(groupName);

        password = new EditBox(font, guiLeft + 7, guiTop + 7 + (font.lineHeight + 5) * 3 - 5 + 10 + 2 * 2, xSize - 7 * 2, 10, Component.empty());
        password.setMaxLength(16);
        password.setFilter(s -> s.isEmpty() || Voicechat.GROUP_REGEX.matcher(s).matches());
        addRenderableWidget(password);

        createGroup = Button.builder(CREATE, button -> {
            if (!groupName.getValue().isEmpty()) {
                NetManager.sendToServer(new CreateGroupPacket(groupName.getValue(), password.getValue().isEmpty() ? null : password.getValue()));
            }
        }).bounds(guiLeft + 7, guiTop + ySize - 20 - 7, xSize - 7 * 2, 20).build();
        addRenderableWidget(createGroup);
    }

    @Override
    public void tick() {
        super.tick();
        groupName.tick();
        password.tick();
        createGroup.active = !groupName.getValue().isEmpty();
    }

    @Override
    public void renderBackground(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        font.draw(poseStack, CREATE_GROUP, guiLeft + xSize / 2 - font.width(CREATE_GROUP) / 2, guiTop + 7, FONT_COLOR);
        font.draw(poseStack, GROUP_NAME, guiLeft + 8, guiTop + 7 + font.lineHeight + 5, FONT_COLOR);
        font.draw(poseStack, OPTIONAL_PASSWORD, guiLeft + 8, guiTop + 7 + (font.lineHeight + 5) * 2 + 10 + 2, FONT_COLOR);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(null);
            return true;
        }
        return groupName.keyPressed(keyCode, scanCode, modifiers) | password.keyPressed(keyCode, scanCode, modifiers) | super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String groupNameText = groupName.getValue();
        String passwordText = password.getValue();
        init(client, width, height);
        groupName.setValue(groupNameText);
        password.setValue(passwordText);
    }

}
