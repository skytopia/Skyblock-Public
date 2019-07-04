package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Camouflage extends Chronicle implements Live {

    public String getName() {
        return "Camouflage";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill any hostile mob whilst", "wearing their respective head")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("The Extrapolator")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.VINE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity)
            if (event.getDamage() >= ((LivingEntity) event.getEntity()).getHealth())
                if (event.getDamager() instanceof Player)
                    if (((Player) event.getDamager()).getInventory().getHelmet() != null)
                            if (main().islands().getIsland(event.getDamager().getUniqueId()) != null)
                                switch (((Player) event.getDamager()).getInventory().getHelmet().getType()) {
                                    case SKELETON_SKULL:
                                        if (event.getEntity() instanceof Skeleton)
                                            main.challenges().complete((Player) event.getDamager(), this);
                                        break;
                                    case WITHER_SKELETON_SKULL:
                                        if (event.getEntity() instanceof WitherSkeleton)
                                            main.challenges().complete((Player) event.getDamager(), this);
                                        break;
                                    case ZOMBIE_HEAD:
                                        if (event.getEntity() instanceof Zombie)
                                            main.challenges().complete((Player) event.getDamager(), this);
                                        break;
                                    case CREEPER_HEAD:
                                        if (event.getEntity() instanceof Creeper)
                                            main.challenges().complete((Player) event.getDamager(), this);
                                        break;
                                    case DRAGON_HEAD:
                                        if (event.getEntity() instanceof EnderDragon)
                                            main.challenges().complete((Player) event.getDamager(), this);
                                        break;
                                }
    }
}
