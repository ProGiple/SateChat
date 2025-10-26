package org.satellite.dev.progiple.satechat.users.component;

import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.UUID;

@FunctionalInterface
public interface ICreationComponent {
    IChatUser create(UUID uuid);
}
