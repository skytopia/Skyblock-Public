package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.MinigameTask;

import java.util.List;
import java.util.UUID;

/**
 * Basic proof-of-concept implementation of MinigameTask.
 * <ul>
 * <li>Timer: counts down to zero.</li>
 * <li>Score: increased linearly through actions.</li>
 * <li>Medals: achieved through scoring points.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 3.0
 * @since 3.0
 */
public class DefaultMinigameTask extends MinigameTask {

    public DefaultMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty) {
        super(owner, minigame, participants, main, difficulty);
    }

    @Override
    public boolean ascendingTimer() {
        return false;
    }

    @Override
    public boolean isMedalAchieved() {
        return true;
    }

    @Override
    public int getResult() {
        return points;
    }

    @Override
    public boolean isHighscoreCheckable() {
        return true;
    }

    @Override
    public boolean isNoScoreIfOutOfTime() {
        return false;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onTick() {
    }
}
