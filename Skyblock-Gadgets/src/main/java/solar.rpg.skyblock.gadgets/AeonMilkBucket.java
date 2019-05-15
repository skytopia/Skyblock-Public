package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import solar.rpg.skyblock.island.Gadget;

/**
 * Drinking this bucket of milk will not empty it.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class AeonMilkBucket extends Gadget {

    @Override
    public String getName() {
        return ChatColor.WHITE + "Aeon Milk Bucket";
    }

    @Override
    public int getPrice() {
        return 750;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Can be consumed infinitely",
                "\"Take that, potion effects!\""
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MILK_BUCKET, 1);
    }

    @EventHandler
    public void onClick(PlayerItemConsumeEvent event) {
        if (usable(event.getPlayer(), event.getItem())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            for (PotionEffect active : event.getPlayer().getActivePotionEffects())
                event.getPlayer().removePotionEffect(active.getType());
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_DRINK, 1F, 1F);
        }
    }
}
