package org.shootemup.utils;

import java.util.Optional;
import org.shootemup.entities.Projectile;

/// Classes que implementam essa interface são capazes de disparar projeteis
public interface Shooter {
    public Optional<Projectile> shot(long currentTime);
}
