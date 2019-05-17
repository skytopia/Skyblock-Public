package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.MinigameTask;

import java.util.List;
import java.util.UUID;

/**
 * Supports "survive-the-longest" minigames.
 * <ul>
 * <li>Timer: counts up from zero.</li>
 * <li>Score: start at zero, increases with timer.</li>
 * <li>Medals: earnt over time as score increases.</li>
 * <li>Gameover occurs when all players are disqualified.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 3.0
 * @since 3.0
 */
public class TimeCountupMinigameTask extends MinigameTask {

    public TimeCountupMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty) {
        super(owner, minigame, participants, main, difficulty);
    }

    @Override
    public boolean ascendingTimer() {
        return true;
    }

    @Override
    public boolean isMedalAchieved() {
        return true;
    }

    @Override
    public int getResult() {
        return clock;
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
