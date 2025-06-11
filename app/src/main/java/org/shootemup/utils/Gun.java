package org.shootemup.utils;

import java.util.Optional;
import org.shootemup.entities.Projectile;

public interface Gun<T extends Projectile> {
    public Optional<T> shot(long now);
}
