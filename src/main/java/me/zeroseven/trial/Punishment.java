package me.zeroseven.trial;

import lombok.Getter;
import me.zeroseven.trial.commands.PunishCommands;
import me.zeroseven.trial.config.BanWordsConfig;
import me.zeroseven.trial.data.MySQLConnector;
import me.zeroseven.trial.hook.OffenseDetector;
import me.zeroseven.trial.punishment.PunishmentController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Punishment extends JavaPlugin {


    @Getter
    private MySQLConnector connector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        BanWordsConfig banWordsConfig =  new BanWordsConfig(this);
        banWordsConfig.saveDefaultConfig();
        this.connector = new MySQLConnector(this);
        connector.init();
        new PunishCommands(this).scheduleCommand();
        Bukkit.getPluginManager().registerEvents(new PunishCommands(this), this);
        Bukkit.getPluginManager().registerEvents(new OffenseDetector(this, banWordsConfig), this);
        Bukkit.getPluginManager().registerEvents(new PunishmentController(connector, this), this);
    }

    @Override
    public void onDisable() {
        connector.stop();
    }

}
