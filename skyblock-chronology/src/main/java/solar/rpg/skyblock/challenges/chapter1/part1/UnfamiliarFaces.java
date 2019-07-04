package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerVisitIslandEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class UnfamiliarFaces extends Chronicle implements Live {

    public String getName() {
        return "Unfamiliar Faces";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Visit another Player's Island")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.LEAD, 2)),
                new ItemReward(new ItemStack(Material.NAME_TAG, 1)),
                new ItemReward(new ItemStack(Material.CAKE, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.OXEYE_DAISY, 1, (short) 8);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onVisit(PlayerVisitIslandEvent event) {
        main().challenges().complete(event.getVisitor(), this);
    }
}
