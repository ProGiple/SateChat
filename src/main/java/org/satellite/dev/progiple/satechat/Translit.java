package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Translit {
    Y('У'),
    E('Е'),
    H('Н'),
    X('Х'),
    B('В'),
    A('А'),
    P('Р'),
    O('О'),
    C('С'),
    M('М'),
    N('И'),
    K('К'),
    T('Т');

    private final char ruChar;

    public static String process(String originalMessage) {
        String normalized = originalMessage.toUpperCase();
        for (Translit value : Translit.values())
            normalized = normalized.replace(value.name(), String.valueOf(value.getRuChar()));
        return normalized;
    }
}