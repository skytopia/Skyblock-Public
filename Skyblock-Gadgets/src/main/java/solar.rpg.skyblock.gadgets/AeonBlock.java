package solar.rpg.skyblock.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.ArrayList;

/**
 * It is a block that can be placed infinitely.
 * Its type can be changed by left clicking.
 * New types must be unlocked by completing challenges.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class AeonBlock extends Gadget {

    /* Gray background tile. */
    private final ItemStack GRAY = ItemUtility.changeItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), "", "");

    /* Locked Aeon block type. */
    private final ItemStack LOCKED = ItemUtility.changeItem(new ItemStack(Material.IRON_FENCE, 1, (short) 7), ChatColor.RED + "Not Unlocked", "");

    /* Available Aeon block types. */
    private final ItemStack[] AEON = {new ItemStack(Material.DIRT, 1, (short) 1), new ItemStack(Material.SAND, 1, (short) 0), new ItemStack(Material.STONE, 1, (short) 0), new ItemStack(Material.PRISMARINE, 1, (short) 0), new ItemStack(Material.ENDER_STONE, 1, (short) 0), new ItemStack(Material.MYCEL, 1, (short) 0), new ItemStack(Material.PACKED_ICE, 1, (short) 0), new ItemStack(Material.WOOL, 1, (short) 0), new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA, 1, (short) 0), new ItemStack(Material.NETHERRACK, 1), new ItemStack(Material.RED_MUSHROOM, 1), new ItemStack(Material.BROWN_MUSHROOM, 1), new ItemStack(Material.MAGMA, 1), new ItemStack(Material.PURPUR_SLAB, 1),};

    @Override
    public String getName() {
        return ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Aeon Block";
    }

    @Override
    public int getPrice() {
        return 125000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Why won't this block disappear?", "Left click to change block type"};
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIRT, 1, (short) 1);
    }

    @EventHandler
    public void onClick(BlockPlaceEvent event) {
        ItemStack placed = getItemInHand(event.getPlayer(), event.getHand());
        if (usable(event.getPlayer(), placed)) {
            placed.setAmount(2);
            Bukkit.getScheduler().runTaskLater(main().plugin(), () -> placed.setAmount(1), 1L);
            event.getPlayer().getWorld().spawnParticle(Particle.FLAME, event.getBlock().getLocation().clone().add(0.5, 1.5, 0.5), 1);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (usable(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand()))
            event.getPlayer().openInventory(generateAeonMenu(event.getPlayer()));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getName() == null) return;
        if (event.getInventory().getName().equals("Choose your Aeon Block")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            int slot = event.getSlot();
            if (slot > AEON.length) return;
            Island found = main().islands().getIsland(event.getWhoClicked().getUniqueId());
            if (found == null || (!found.aeon().has(slot) && slot != 0)) return;

            // Re-create gadget item meta based on original item.
            ItemStack aeonT = AEON[slot];
            ArrayList<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "GADGET");
            for (String purpose : getPurpose())
                lore.add(ChatColor.GOLD + purpose);
            lore.add("");
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "UNLOCKED" + ChatColor.RED + " (" + getPrice() + "ƒ)");
            event.getWhoClicked().getInventory().setItemInMainHand(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(aeonT.getType(), aeonT.getAmount(), aeonT.getData().getData()), getName(), lore), Enchantment.ARROW_INFINITE, 1, Enchantment.VANISHING_CURSE, 1));
            event.getWhoClicked().closeInventory();
        }
    }

    /**
     * Generates a Aeon block selection menu for a player.
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

        // Create and fill inventory.
        Inventory inv = Bukkit.createInventory(null, size, "Choose your Aeon Block");
        for (int i = 0; i < AEON.length; i++)
            if (island.aeon().has(i) || i == 0)
                inv.setItem(i, AEON[i]);
            else
                inv.setItem(i, LOCKED);

        // Fill any remaining empty space.
        for (int i = 0; i < inv.getSize(); i++)
            if (inv.getItem(i) == null)
                inv.setItem(i, GRAY);
        return inv;
    }

}
