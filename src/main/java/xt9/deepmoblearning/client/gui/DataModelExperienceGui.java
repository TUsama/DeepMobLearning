package xt9.deepmoblearning.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xt9.deepmoblearning.DeepConstants;
import xt9.deepmoblearning.common.config.Config;
import xt9.deepmoblearning.common.items.ItemDeepLearner;
import xt9.deepmoblearning.common.util.DataModel;
import xt9.deepmoblearning.common.util.PlayerHelper;

import java.text.DecimalFormat;

/**
 * Created by xt9 on 2017-06-14.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class DataModelExperienceGui extends GuiScreen {
    private final FontRenderer renderer;
    private Minecraft mc;
    private ItemStack deepLearner;
    private NonNullList<ItemStack> chipStackList;
    private PlayerHelper playerH;

    private static final ResourceLocation experienceBar = new ResourceLocation(DeepConstants.MODID, "textures/gui/experience_gui.png");

    public DataModelExperienceGui(Minecraft mc) {
        super();
        this.mc = mc;
        this.renderer = this.mc.fontRenderer;
        this.itemRender = this.mc.getRenderItem();
        setGuiSize(89, 12);
    }

    /* Needed on 1.12 to render tooltips */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        if(!mc.inGameHasFocus) {
            return;
        }

        this.playerH = new PlayerHelper(mc.player);
        if(!playerH.isHoldingDeepLearner()) {
            return;
        } else {
            this.deepLearner = playerH.getHeldDeepLearner();
            this.chipStackList = DataModel.getValidFromList(ItemDeepLearner.getContainedItems(deepLearner));
        }


        int x = getLeftCornerX() + 18;
        if(Config.guiOverlaySide.getString().equals("right")) {
            x = getRightCornerX();
        }
        int y = 5;

        for (int i = 0; i < chipStackList.size(); i++) {
            ItemStack stack = chipStackList.get(i);
            String tierName = DataModel.getTierName(stack, false);
            int tier = DataModel.getTier(stack);
            double k = DataModel.getKillsToNextTier(stack);
            double c = DataModel.getCurrentTierKillCountWithSims(stack);
            int roof = DataModel.getTierRoofAsKills(stack);
            drawExperienceBar(x, y, i, tierName, tier, k, c, roof, stack);
        }
    }

    private void drawExperienceBar(int x, int y, int index, String tierName, int tier, double killsToNextTier, double currenKillCount, int tierRoof, ItemStack stack) {
        int componentHeight = 26;
        int spacing = 12;
        DecimalFormat f = new DecimalFormat("0.#");

        drawItemStack(x - 18, y - 2 + spacing + (index * componentHeight), stack);
        drawString(renderer, tierName + " Model", x - 14, y + (index * componentHeight) + 2, 16777215);

        // Draw the bar
        mc.getTextureManager().bindTexture(experienceBar);
        drawTexturedModalRect(x, y + spacing + (index * componentHeight), 0, 0, 89, 12);

        if(tier == DeepConstants.MOB_CHIP_MAXIMUM_TIER) {
            drawTexturedModalRect(x + 1,  y + 1 + spacing + (index * componentHeight), 0, 12, 89, 11);
        } else {
            drawTexturedModalRect(x + 1,  y + 1 + spacing + (index * componentHeight), 0, 12,
                    (int) (((float) currenKillCount / tierRoof * 89)), 11);
            drawString(renderer, f.format(killsToNextTier) + " to go", x + 3, y + 2 + spacing + (index * componentHeight), 16777215);
        }
    }

    private int getLeftCornerX() {
        return 5;
    }

    private int getRightCornerX() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledWidth() - width - 5;
    }

    private void drawItemStack(int x, int y, ItemStack stack)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }
}