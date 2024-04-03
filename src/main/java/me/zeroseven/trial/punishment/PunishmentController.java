package me.zeroseven.trial.punishment;

import lombok.AllArgsConstructor;
import me.zeroseven.trial.Punishment;
import me.zeroseven.trial.data.MySQLConnector;
import me.zeroseven.trial.events.PunishEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class PunishmentController implements Listener {

    private MySQLConnector connector;
    private Punishment punishment;

    @EventHandler
    public void onPunish(PunishEvent event) {
        Player player = event.getPlayer();
        int punishments = getPunishments(player);
        String punishmentType = getPunishment(punishments);

        CommandSender console = Bukkit.getConsoleSender();
        String timeUnit = getTimeUnitFromConfig(punishment.getConfig().getString("temp_ban_time"));
        String command = String.format("/%s %s %s %s repeated offense!", punishmentType, player.getName(), timeUnit, punishments);

        Bukkit.getServer().dispatchCommand(console, command);
    }

    private int getPunishments(Player player) {
        String playerName = player.getDisplayName();
        try (Connection connection = connector.connection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM " + MySQLConnector.ACTIVE_TABLE +
                             " WHERE name= ? AND type='BAN' ORDER BY expire DESC LIMIT 1")) {
            statement.setString(1, playerName);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    return set.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private String getPunishment(int time) {
        FileConfiguration config = punishment.getConfig();

        String punishTimeKey = switch (time) {
            case 1 -> "first_time";
            case 2 -> "second_time";
            case 3 -> "third_time";
            default -> throw new IllegalStateException("Unexpected value: " + time);
        };

        PunishType punishType = PunishType.valueOf(config.getString(punishTimeKey));
        return switch (punishType) {
            case KICK -> "kick";
            case MUTE -> "mute";
            case TEMP_BAN -> "ban";
            default -> "ban";
        };
    }

    private String getTimeUnitFromConfig(String timeString) {
        String[] parts = timeString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        if (parts.length != 2) {
            return "INVALID";
        }
        int time = Integer.parseInt(parts[0]);
        TimeUnit unit = TimeUnit.valueOf(parts[1].toUpperCase());
        return String.format("%d%s", time, unit.toString().toLowerCase());
    }

    public enum PunishType {
        TEMP_BAN, MUTE, KICK
    }
}
