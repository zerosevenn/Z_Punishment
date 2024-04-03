package me.zeroseven.trial;

import lombok.Getter;
import me.zeroseven.trial.commands.PunishCommands;
import me.zeroseven.trial.data.MySQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Punishment extends JavaPlugin {


    @Getter
    private MySQLConnector connector;

    @Override
    public void onEnable() {
        this.connector = new MySQLConnector(this);
        connector.init();
        new PunishCommands(this).scheduleCommand();
        Bukkit.getPluginManager().registerEvents(new PunishCommands(this), this);
    }

    @Override
    public void onDisable() {
        connector.stop();
    }

}
