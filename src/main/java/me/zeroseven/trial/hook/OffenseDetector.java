package me.zeroseven.trial.hook;

import lombok.AllArgsConstructor;
import me.zeroseven.trial.Punishment;
import me.zeroseven.trial.config.BanWordsConfig;
import me.zeroseven.trial.events.PunishEvent;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor

public class OffenseDetector implements Listener {
    private Punishment punishment;
    private BanWordsConfig wordsConfig;


    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){
        if(matchWord(event.getMessage())){
            Player player = event.getPlayer();
            Bukkit.getPluginManager().callEvent(new PunishEvent(player, "Banned word!", 100));
        }
    }

    public boolean matchWord(String word){
        ArrayList<String> words = new ArrayList<>();
        for(String s: punishment.getConfig().getStringList("messages")){
            if(s.equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }


}
