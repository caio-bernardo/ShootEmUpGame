package org.shootemup.engine;

public interface GameEval {
    void update(GameData data, long delta, long currentTime);
    void read_input(GameData data, long delta, long currentTime);
    void render(GameData data, long delta, long currentTime);
}
