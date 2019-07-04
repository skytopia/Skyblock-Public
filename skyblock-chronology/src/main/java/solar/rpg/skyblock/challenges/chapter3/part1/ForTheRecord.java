package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class ForTheRecord extends Chronicle {

    public String getName() {
        return "For The Record";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new Criteria() {
            @Override
            public boolean has(Island island, Player toCheck) {
                Inventory inv = toCheck.getInventory();
                return inv.contains(Material.MUSIC_DISC_CAT)
                        && inv.contains(Material.MUSIC_DISC_13)
                        && inv.contains(Material.MUSIC_DISC_BLOCKS)
                        && inv.contains(Material.MUSIC_DISC_CHIRP)
                        && inv.contains(Material.MUSIC_DISC_FAR)
                        && inv.contains(Material.MUSIC_DISC_MALL)
                        && inv.contains(Material.MUSIC_DISC_MELLOHI)
                        && inv.contains(Material.MUSIC_DISC_STAL)
                        && inv.contains(Material.MUSIC_DISC_STRAD)
                        && inv.contains(Material.MUSIC_DISC_WARD)
                        && inv.contains(Material.MUSIC_DISC_WAIT);
            }

            @Override
            public String[] getDescription() {
                return new String[]{"Collect every Music Disc", "(except Disc 11)"};
            }

            @Override
            public void success(Island island, Player toCheck) {
                Inventory inv = toCheck.getInventory();
                inv.remove(Material.MUSIC_DISC_CAT);
                inv.remove(Material.MUSIC_DISC_13);
                inv.remove(Material.MUSIC_DISC_BLOCKS);
                inv.remove(Material.MUSIC_DISC_CHIRP);
                inv.remove(Material.MUSIC_DISC_FAR);
                inv.remove(Material.MUSIC_DISC_MALL);
                inv.remove(Material.MUSIC_DISC_MELLOHI);
                inv.remove(Material.MUSIC_DISC_STAL);
                inv.remove(Material.MUSIC_DISC_STRAD);
                inv.remove(Material.MUSIC_DISC_WARD);
                inv.remove(Material.MUSIC_DISC_WAIT);
            }
        }};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Creeper Parry")
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.MENDING, 1, Enchantment.PROTECTION_EXPLOSIONS, 10, Enchantment.VANISHING_CURSE, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MUSIC_DISC_CAT);
    }

    public boolean isRepeatable() {
        return true;
    }
}
