package solar.rpg.skyblock.gadgets;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import solar.rpg.skyblock.island.Gadget;

/**
 * When used on a player, it will drop their head as an item.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class TheExtrapolator extends Gadget {

    @Override
    public String getName() {
        return "The Extrapolator";
    }

    @Override
    public int getPrice() {
        return 7500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Right click a player to steal their head"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SHEARS, 1, (short) 1000);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        ItemStack inHand = getItemInHand(event.getPlayer(), event.getHand());
        if (usable(event.getPlayer(), inHand)) {
            inHand.setAmount(1);
            event.getPlayer().getInventory().removeItem(inHand);
            event.getRightClicked().getWorld().playEffect(((Player) event.getRightClicked()).getEyeLocation(), Effect.STEP_SOUND, 20);
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(event.getRightClicked().getName());
            skull.setItemMeta(meta);
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), skull).setPickupDelay(1);
        }
    }
}
