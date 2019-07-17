package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.Playstyle;
import solar.rpg.skyblock.minigames.tasks.DefaultMinigameTask;
import solar.rpg.skyblock.util.Utility;

import java.util.List;
import java.util.UUID;

public class TheStairwell extends Minigame implements FlawlessEnabled {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TheStairwellTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "The Stairwell";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BRICK_STAIRS);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "You're at the bottom of a stairwell!",
                ChatColor.ITALIC + "Parkour your way back up, don't fall!",
                ChatColor.ITALIC + "The higher you get, the more points.",
                "\"Higher they get, harder they fall.\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Parkour up to the top!";
    }

    @Override
    public String getObjectiveWord() {
        return "blocks scaled";
    }

    @Override
    public int getMinimumPlayers() {
        return 1;
    }

    @Override
    public boolean enforceMinimum() {
        return false;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getGold() {
        return 175;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 250;
    }

    @Override
    public int getFlawlessPlayerMinimum() {
        return 1;
    }

    @Override
    public int getMaxReward() {
        return 8000;
    }

    @Override
    public Playstyle getPlaystyle() {
        return Playstyle.COMPETITIVE;
    }

    private class TheStairwellTask extends DefaultMinigameTask {

        /* The Y value of the next slice of blocks that will be removed. */
        private int yRemove;

        TheStairwellTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("placing", false);
            rules.put("breaking", false);
            rules.put("flying", false);
            rules.put("gliding", false);
            rules.put("commands", false);
        }

        @Override
        public boolean ascendingTimer() {
            return true;
        }

        @Override
        public void onStart() {
            yRemove = -15;

            gen = generateLocation(100, 70, 140, false, false);
            gen.setY(0);

            // Generate walls and floor.
            for (int x = 0; x < 16; x++)
                for (int y = 0; y <= 250; y++)
                    for (int z = 0; z < 16; z++)
                        if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z).getType() != Material.AIR) {
                            error();
                            return;
                        } else {
                            Block at = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z);
                            if (y == 0) at.setType(Material.BEDROCK);
                            else if (x == 0 && z == 0 || x == 0 && z == 15 || x == 15 && z == 0 || x == 15 && z == 15) {
                                at.setType(Material.WHITE_STAINED_GLASS);
                                //TODO: Add random color
                            }
                            placed.add(at);
                        }

            // Randomly select and place the predefined parkour schematics.
            for (int f = 0; f < 25; f++) {
                gen.setY(f * 10);
                String schematic = "schematics/";
                if (f < 5)
                    schematic += "easy";
                else if (f < 13)
                    schematic += "medium";
                else schematic += "hard";
                schematic += (main.main().rng().nextInt(6) + 1) + ".schematic";
                if (f % 2 == 0)
                    Utility.pasteSchematic(main.main(), schematic, gen.clone().add(1, 0, 1), true);
                else Utility.pasteSchematic(main.main(), schematic, gen.clone().add(14, 0, 14), true);

            }

            gen.setY(0);

            for (UUID part : getParticipants())
                Bukkit.getPlayer(part).teleport(gen.clone().add(8.5, 1, 8.5));
        }

        @Override
        public void onFinish() {
            returnParticipants();
        }

        @Override
        public void onTick() {
            /* Remove slices of the tower at a rate of 1 block per second. */
            yRemove++;
            if (yRemove >= 0 && yRemove <= 255) {
                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++) {
                        Block at = gen.getWorld().getBlockAt(gen.getBlockX() + x, yRemove, gen.getBlockZ() + z);
                        Material type = at.getType();
                        if (type != Material.AIR) {
                            at.setType(Material.AIR);
                            at.getWorld().spawnFallingBlock(at.getLocation(), at.getBlockData());
                        }
                    }
            }
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (finished.contains(event.getPlayer().getUniqueId())) return;
            if (isValidParticipant(event.getPlayer().getUniqueId())) {
                int currentY = event.getTo().getBlockY();
                int highestY = getActualResult(event.getPlayer().getUniqueId());
                if (currentY > highestY && highestY < 250) {
                    if (currentY >= 250) {
                        currentY = 250;
                        titleParticipants(ChatColor.GOLD + "Finished!", event.getPlayer().getDisplayName() + ChatColor.RED + " made it to the top!");
                        finished.add(event.getPlayer().getUniqueId());

                        // Stop minigame once all participants have either finished or been disqualified.
                        if (finished.size() + disqualified.size() == participants.size())
                            Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 60L);
                    }
                    scorePoints(event.getPlayer().getUniqueId(), currentY - highestY);
                }
            }
        }
    }
}