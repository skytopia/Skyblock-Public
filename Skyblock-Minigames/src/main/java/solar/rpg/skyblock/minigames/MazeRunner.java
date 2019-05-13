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
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.minigames.extra.maze.MazeGenerator;
import solar.rpg.skyblock.minigames.tasks.TimeCountdownMinigameTask;

import java.util.List;
import java.util.UUID;

public class MazeRunner extends Minigame implements NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MazeRunnerTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Maze Runner";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ACTIVATOR_RAIL);
    }


    @Override
    public String[] getDescription() {
        return new String[]{"Nothing better than getting lost!",
                ChatColor.ITALIC + "You've been put in a glass maze!",
                ChatColor.ITALIC + "Solve it as quickly as possible!",
                ChatColor.ITALIC + "You're being timed, be quick!"};
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Escape the glass maze!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds remaining";
    }

    @Override
    public int getDuration() {
        return 180;
    }

    @Override
    public int getGold() {
        return 150;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getMaxReward() {
        return 2750;
    }

    private class MazeRunnerTask extends TimeCountdownMinigameTask {

        /* True when a player has solved the maze. */
        private boolean win;

        /* Maze generation helper. */
        private MazeGenerator mazeGen;

        MazeRunnerTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("breaking", false);
            rules.put("placing", false);
            rules.put("modules", false);
        }

        @Override
        public void onStart() {
            win = false;

            // Normal: Regular 10x14 dimension maze.
            // Harder: Double-sized 20x28 dimension maze.
            if (difficulty == Difficulty.NORMAL)
                mazeGen = new MazeGenerator(10, 14);
            else mazeGen = new MazeGenerator(20, 28);

            gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, mazeGen.gridDimensionX + 8, 5, mazeGen.gridDimensionY + 8)) {
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

            // Teleports players into the platform.
            for (UUID in : getParticipants()) {
                Bukkit.getPlayer(in).teleport(gen.clone().add(2, 1, mazeGen.gridDimensionY / 2));
                Bukkit.getPlayer(in).getLocation().setPitch(270F);
            }
        }

        @Override
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
