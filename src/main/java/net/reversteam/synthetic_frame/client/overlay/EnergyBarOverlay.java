package net.reversteam.synthetic_frame.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.reversteam.synthetic_frame.SyntheticFrame;
import net.reversteam.synthetic_frame.client.config.SFClientConfig;
import net.reversteam.synthetic_frame.client.config.SFClientConfig.TextDisplayMode;
import net.reversteam.synthetic_frame.common.capabilities.FrameData;
import net.reversteam.synthetic_frame.common.capabilities.FrameDataProvider;

@Mod.EventBusSubscriber(modid = SyntheticFrame.MODID, value = Dist.CLIENT)
public class EnergyBarOverlay {

    private static final ResourceLocation POWER_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(SyntheticFrame.MODID, "textures/gui/power_bar.png");
    
    // Standard vanilla hunger bar width is 90 (10 shanks * 9px). 
    // If your texture is exactly 81px wide as specified, change this to 81 to prevent slight stretching.
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.FOOD_LEVEL.id())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            // Respect vanilla's visibility rules (hide in creative/spectator)
            if (player.isCreative() || player.isSpectator()) return;

            FrameData data = FrameDataProvider.get(player);
            if (data.isSynthetic()) {
                // Cancel vanilla hunger bar rendering
                event.setCanceled(true);
                renderEnergyBar(event.getGuiGraphics(), player, data, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }
        }
    }

    private static void renderEnergyBar(GuiGraphics guiGraphics, Player player, FrameData data, int width, int height) {
        long maxEnergy = data.getMaxEnergy();
        long currentEnergy = data.getEnergy(); // Use getEnergy() to get the precise long value
        float energyPercent = maxEnergy > 0 ? (float) currentEnergy / maxEnergy : 0.0f;

        // Standard hunger bar positioning in Minecraft
        int x = (width / 2) + 10;
        int y = height - 39;

        // 1. Draw the full unlit bar (background)
        // u=0, v=0 targets the top half of your stacked texture
        guiGraphics.blit(POWER_BAR_TEXTURE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT * 2);

        // 2. Draw the lit bar, filling from the RIGHT edge towards the LEFT (center)
        int fillWidth = (int) (BAR_WIDTH * energyPercent);
        if (fillWidth > 0) {
            // Start drawing from the right side of the filled portion
            int startX = x + BAR_WIDTH - fillWidth;
            
            // The UV u-offset must also start from the right side of the lit texture
            // so it aligns perfectly with the unlit bar's right edge.
            int uOffset = BAR_WIDTH - fillWidth;
            int vOffset = BAR_HEIGHT; // Bottom half of the texture (lit bar)
            
            guiGraphics.blit(POWER_BAR_TEXTURE, startX, y, uOffset, vOffset, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT * 2);
        }

        // 3. Draw formatted energy text based on client config
        TextDisplayMode mode = SFClientConfig.ENERGY_BAR_TEXT_MODE.get();
        if (mode != TextDisplayMode.NONE) {
            String text;
            if (mode == TextDisplayMode.VALUE_ONLY) {
                text = formatEnergy(currentEnergy) + " FE";
            } else {
                text = formatEnergy(currentEnergy) + " / " + formatEnergy(maxEnergy) + " FE";
            }
            
            Font font = Minecraft.getInstance().font;
            int textWidth = font.width(text);
            int textX = x + (BAR_WIDTH - textWidth) / 2;
            int textY = y + 1; // Centered vertically within the 9px height
            
            guiGraphics.drawString(font, text, textX, textY, 0xFFFFFF, true);
        }
    }

    private static String formatEnergy(long energy) {
        if (energy >= 1_000_000) {
            return (energy / 1_000_000) + "M";
        } else if (energy >= 1_000) {
            return (energy / 1_000) + "k";
        }
        return String.valueOf(energy);
    }
}