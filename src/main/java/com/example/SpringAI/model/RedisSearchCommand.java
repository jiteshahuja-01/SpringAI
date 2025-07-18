package com.example.SpringAI.model;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public enum RedisSearchCommand implements ProtocolCommand {
    FT_CREATE("FT.CREATE"),
    FT_INFO("FT.INFO"),
    FT_SEARCH("FT.SEARCH");

    private final byte[] raw;

    RedisSearchCommand(String command) {
        this.raw = SafeEncoder.encode(command);
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
