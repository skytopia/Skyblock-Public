package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.island.minigames.task.TimeCountdownMinigameTask;
import solar.rpg.skyblock.minigames.extra.maze.MazeGenerator;

import java.util.List;
import java.util.UUID;

public class MazeRunner extends Minigame implements NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MemoryRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Maze Runner";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ACTIVATOR_RAIL);
    }

    public String[] getDescription() {
        return new String[]{"Nothing better than getting lost!",
                ChatColor.ITALIC + "You've been put in a glass maze!",
                ChatColor.ITALIC + "Solve it as quickly as possible!",
                ChatColor.ITALIC + "You're being timed, be quick!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Escape the glass maze!";
    }

    public String getObjectiveWord() {
        return "seconds remaining";
    }

    public int getDuration() {
        return 180;
    }

    public int getGold() {
        return 150;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getMaxReward() {
        return 2750;
    }

    private class MemoryRun extends TimeCountdownMinigameTask implements Listener {

        private boolean win;
        private MazeGenerator mazeGen;
        private Location gen;

        MemoryRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("breaking", false);
            rules.put("placing", false);
            rules.put("modules", false);
        }

        public void onStart() {
            win = false;
            mazeGen = new MazeGenerator(10, 14);


            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x <= mazeGen.gridDimensionX + 8; x++)
                for (int y = 0; y <= 5; y++)
                    for (int z = 0; z <= mazeGen.gridDimensionY + 1; z++)
                        if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z).getType() != Material.AIR) {
                            error();
                            return;
                        }

            // Generate glass cube
            for (int x = 0; x <= mazeGen.gridDimensionX + 8; x++)
                for (int y = 0; y <= 5; y++)
                    for (int z = 0; z <= mazeGen.gridDimensionY + 1; z++) {
                        Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY() + y, gen.getBlockZ() + z);
                        if ((y == 0 || y == 5) || (x == 0 || x == mazeGen.gridDimensionX + 8 || z == 0 || z == mazeGen.gridDimensionY + 1)) {
                            bl.setType(Material.STAINED_GLASS);
                            placed.add(bl);
                        }
                    }

            // Generate maze
            for (int x = 0; x < mazeGen.gridDimensionX; x++)
                for (int y = 0; y <= 5; y++)
                    for (int z = 0; z < mazeGen.gridDimensionY; z++) {
                        if (mazeGen.grid[x][z] == 'X') {
                            Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x + 4, gen.getBlockY() + y, gen.getBlockZ() + z + 1);
                            bl.setType(Material.GLASS);
                            placed.add(bl);
                        }
                    }

            // Generate finish area
            for (int x = mazeGen.gridDimensionX + 4; x < mazeGen.gridDimensionX + 8; x++)
                for (int z = 1; z < mazeGen.gridDimensionY; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z + 1);
                    bl.setType(Material.BEDROCK);
                    placed.add(bl);
                }

            // Generate entrance
            boolean found = false;
            while (!found) {
                int pos = main.main().rng().nextInt(mazeGen.gridDimensionY);
                if (gen.getWorld().getBlockAt(gen.getBlockX() + 5, gen.getBlockY() + 1, gen.getBlockZ() + pos).getType() == Material.AIR) {
                    found = true;
                    gen.getWorld().getBlockAt(gen.getBlockX() + 4, gen.getBlockY(), gen.getBlockZ() + pos).setType(Material.GLOWSTONE);
                    for (int y = 1; y <= 3; y++)
                        gen.getWorld().getBlockAt(gen.getBlockX() + 4, gen.getBlockY() + y, gen.getBlockZ() + pos).setType(Material.AIR);
                }
            }

            // Generate exit
            found = false;
            while (!found) {
                int pos = main.main().rng().nextInt(mazeGen.gridDimensionY);
                if (gen.getWorld().getBlockAt(gen.getBlockX() + mazeGen.gridDimensionX + 2, gen.getBlockY() + 1, gen.getBlockZ() + pos).getType() == Material.AIR) {
                    found = true;
                    gen.getWorld().getBlockAt(gen.getBlockX() + mazeGen.gridDimensionX + 3, gen.getBlockY(), gen.getBlockZ() + pos).setType(Material.GLOWSTONE);
                    for (int y = 1; y <= 3; y++)
                        gen.getWorld().getBlockAt(gen.getBlockX() + mazeGen.gridDimensionX + 3, gen.getBlockY() + y, gen.getBlockZ() + pos).setType(Material.AIR);
                }
            }

            for (UUID in : getParticipants()) {
                Bukkit.getPlayer(in).teleport(gen.clone().add(2, 1, mazeGen.gridDimensionY / 2));
                Bukkit.getPlayer(in).getLocation().setPitch(270F);
            }
        }

        public void onFinish() {
            returnParticipants();
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (win) return;
            if (!isValidParticipant(event.getPlayer().getUniqueId())) return;
            if (event.getPlayer().getLocation().getX() > gen.getX() + mazeGen.gridDimensionX + 4) {
                win = true;
                titleParticipants(ChatColor.GOLD + "Winner!", event.getPlayer().getDisplayName() + ChatColor.GOLD + " solved the maze!");
                clock += 3;
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 60L);
            }
        }
    }
}
