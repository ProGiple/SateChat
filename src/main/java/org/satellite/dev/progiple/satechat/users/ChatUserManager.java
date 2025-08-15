package org.satellite.dev.progiple.satechat.users;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class ChatUserManager {
    private final List<IChatUser> users = new ArrayList<>();

    public void register(IChatUser chatUser) {
        users.add(chatUser);
    }

    public void unregister(IChatUser chatUser) {
        users.remove(chatUser);
    }

    public ChatUser create(UUID uuid) {
        ChatUser chatUser = new ChatUser(uuid);
        register(chatUser);
        return chatUser;
    }

    public @NonNull IChatUser get(UUID uuid) {
        return Utils.find(users, u -> u.getUUID().equals(uuid)).orElse(create(uuid));
    }

    public boolean contains(IChatUser chatUser) {
        return users.contains(chatUser);
    }

    public boolean contains(UUID uuid) {
        return users.stream().anyMatch(u -> u.getUUID().equals(uuid));
    }
}
