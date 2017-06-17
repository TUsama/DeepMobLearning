package xt9.deepmoblearning.common.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xt9.deepmoblearning.common.items.ItemDeepLearner;
import xt9.deepmoblearning.common.items.ItemMobChip;

/**
 * Created by xt9 on 2017-06-11.
 */
@Mod.EventBusSubscriber
public class EntityDeathHandler {

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        if(event.getSource().getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getEntity();

            // Update the chips in the players deep learner(s)
            updateDeepLearnerOnEntityDeath(player.inventory.mainInventory, event);
            updateDeepLearnerOnEntityDeath(player.inventory.offHandInventory, event);
        }
    }

    private static void updateDeepLearnerOnEntityDeath(NonNullList<ItemStack> inventory, LivingDeathEvent event) {
        for(ItemStack inventoryStack : inventory) {
            if (inventoryStack.getItem() instanceof ItemDeepLearner) {
                NonNullList<ItemStack> deepLearnerInternalInv = ItemDeepLearner.getContainedItems(inventoryStack);
                for (ItemStack stack : deepLearnerInternalInv) {
                    if (stack.getItem() instanceof ItemMobChip) {
                        if (ItemMobChip.entityLivingMatchesType(event.getEntityLiving(), stack)) {
                            ItemMobChip.increaseMobKillCount(stack);
                        }
                    }
                    ItemDeepLearner.setContainedItems(inventoryStack, deepLearnerInternalInv);
                }
            }
        }
    }
}