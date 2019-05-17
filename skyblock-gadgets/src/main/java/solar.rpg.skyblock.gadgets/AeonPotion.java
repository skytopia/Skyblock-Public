package solar.rpg.skyblock.gadgets;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Gadget;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.ItemUtility;

/**
 * This potion can be drank an unlimited amount of times.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class AeonPotion extends Gadget {

    /* Gray background tile. */
    private final ItemStack GRAY = ItemUtility.changeItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), "", "");

    /* Locked Aeon potion type. */
    private final ItemStack LOCKED = ItemUtility.changeItem(new ItemStack(Material.IRON_FENCE, 1, (short) 7), ChatColor.RED + "Not Unlocked", "");

    /* Available Aeon potion types. */
    private final PotionEffect[] AEON = {new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 180, 0, true, true, Color.WHITE), new PotionEffect(PotionEffectType.LUCK, 20 * 180, 0, true, true, Color.WHITE)};

    @Override
    public String getName() {
        return ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Aeon Potion";
    }

    @Override
    public int getPrice() {
        return 125000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Can be consumed infinitely",
                "Left click to change potion"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.POTION, 1, (short) 0);
    }

    @EventHandler
    public void onClick(PlayerItemConsumeEvent event) {
        if (usable(event.getPlayer(), event.getItem())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            for (PotionEffect effect : ((PotionMeta) event.getItem().getItemMeta()).getCustomEffects())
                event.getPlayer().addPotionEffect(effect);
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_DRINK, 1F, 1F);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (usable(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand()))
            event.getPlayer().openInventory(generateAeonMenu(event.getPlayer()));
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getName() == null) return;
        if (event.getInventory().getName().equals("Choose your Aeon Potion")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            int slot = event.getSlot();
            if (slot > AEON.length) return;
            Island found = main().islands().getIsland(event.getWhoClicked().getUniqueId());
            if (found == null || !found.potion().has(slot)) return;

            // Get new potion effect, apply it.
            PotionEffect newEffect = AEON[slot];
            PotionMeta meta = (PotionMeta) event.getWhoClicked().getInventory().getItemInMainHand().getItemMeta();
            for (PotionEffect effect : meta.getCustomEffects())
                meta.removeCustomEffect(effect.getType());
            meta.addCustomEffect(newEffect, true);
            meta.setColor(newEffect.getColor());
            event.getWhoClicked().getInventory().getItemInMainHand().setItemMeta(meta);
            event.getWhoClicked().closeInventory();
        }
    }

    /**
     * Generates a Aeon potion selection menu for a player.
     *
     * @param pl The gadget holder.
     * @return Aeon menu inventory.
     */
    private Inventory generateAeonMenu(Player pl) {
        // We can safely get their island without a check,
        // Since the gadget is confiscated if they do not have an island.
        Island island = main().islands().getIsland(pl.getUniqueId());

        // Determine size of inventory.
        int size = 9;
        int tmp = AEON.length;
        while (tmp > 9) {
            size += 9;
            tmp -= 9;
        }
        Inventory inv = Bukkit.createInventory(null, size, "Choose your Aeon Potion");
        for (int i = 0; i < AEON.length; i++)
            if (island.potion().has(i)) {
                PotionEffect effect = AEON[i];
                inv.setItem(i, ItemUtility.createPotion(effect.getType(), effect.getDuration(), effect.getAmplifier()));
            } else
                inv.setItem(i, LOCKED);
        for (int i = 0; i < inv.getSize(); i++)
            if (inv.getItem(i) == null)
                inv.setItem(i, GRAY);
        return inv;
    }

}
