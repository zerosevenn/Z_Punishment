package me.zeroseven.trial.config;

import me.zeroseven.trial.Punishment;

public class BanWordsConfig extends CustomConfig{
    public BanWordsConfig(Punishment instance) {
        super(instance, "bannedwords");
    }
}
