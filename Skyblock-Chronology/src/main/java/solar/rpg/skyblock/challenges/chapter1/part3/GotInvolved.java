package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerForumCheckStatsEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class GotInvolved extends Chronicle implements Live {

    public String getName() {
        return "Got Involved!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Post 10 messages on the forums", "(/register confirm, once done)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), ChatColor.RED + "Magmatic Leggings", "A piece of a mystic armor set.", "It is very cool to the touch."), Enchantment.DURABILITY, 999, Enchantment.MENDING, 1)),
                new ItemReward(new ItemStack(Material.ELYTRA, 1)),
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ITEM_FRAME);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onRegister(PlayerForumCheckStatsEvent event) {
        if (event.getMessageCount() >= 10)
            main().challenges().award(event.getPlayer(), this);
    }
}
