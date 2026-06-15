package com.yaazyy.deathnote.core.support;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

/** Simple test double for {@link DNPlayer}. */
public final class FakePlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private boolean op;
    private boolean online = true;

    public FakePlayer(String name) {
        this(UUID.randomUUID(), name);
    }

    public FakePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public FakePlayer op(boolean value) {
        this.op = value;
        return this;
    }

    public FakePlayer online(boolean value) {
        this.online = value;
        return this;
    }

    @Override public UUID uuid() { return uuid; }
    @Override public String name() { return name; }
    @Override public boolean isOp() { return op; }
    @Override public boolean isOnline() { return online; }
}
