package me.zeroseven.trial.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;


@AllArgsConstructor
@Getter
@Setter
public class PunishEvent extends CustomEvent{
    private Player player;
    private String reason;
    private long duration;
}
