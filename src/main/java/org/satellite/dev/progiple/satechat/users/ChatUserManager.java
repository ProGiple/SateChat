package org.satellite.dev.progiple.satechat.users;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.users.component.ICreationComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ChatUserManager {
    private final List<IChatUser> users = new ArrayList<>();
    @Setter private ICreationComponent creationComponent;
    static {
        if (creationComponent == null) creationComponent = ChatUser::new;
    }

    public void register(IChatUser chatUser) {
        users.add(chatUser);
    }

    public void unregister(IChatUser chatUser) {
        users.remove(chatUser);
    }

    public void unregister(UUID uuid) {
        Set<IChatUser> chatUsers = users.stream().filter(u -> u.getUUID().equals(uuid)).collect(Collectors.toSet());
        chatUsers.forEach(ChatUserManager::unregister);
    }

    public IChatUser create(UUID uuid) {
        IChatUser chatUser = creationComponent.create(uuid);
        register(chatUser);
        return chatUser;
    }

    public Stream<UUID> getUUIDs() {
        return users.stream().map(IChatUser::getUUID);
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
