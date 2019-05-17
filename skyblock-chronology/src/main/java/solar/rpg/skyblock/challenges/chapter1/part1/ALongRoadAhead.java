package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class ALongRoadAhead extends Chronicle implements Live {

    public String getName() {
        return "A Long Road Ahead!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("View all 10 pages of the Chronology")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.SPEED, 20 * 600, 1)),
                new ItemReward(new ItemStack(Material.ENDER_PEARL, 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BOOK_AND_QUILL, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals("Chronology " + ChatColor.UNDERLINE + "(Chapter IX)"))
            if (event.getCurrentItem() != null)
                if (event.getCurrentItem().getType().equals(Material.EMERALD_BLOCK))
                    main.challenges().complete((Player) event.getWhoClicked(), this);
    }
}
