package solar.rpg.skyblock.challenges.chapter5.part1;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

class WarmongerCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public WarmongerCompleteEvent(Player player) {
        super();
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
