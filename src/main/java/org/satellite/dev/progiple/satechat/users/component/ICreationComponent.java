package org.satellite.dev.progiple.satechat.users.component;

import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.UUID;

public interface ICreationComponent {
    IChatUser create(UUID uuid);
}
