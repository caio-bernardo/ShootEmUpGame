package org.shootemup.utils;

import java.util.Optional;
import org.shootemup.entities.Projectile;

public interface Shooter {
    public Optional<Projectile> shot(long currentTime);
}
