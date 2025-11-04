package org.satellite.dev.progiple.satechat.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Translit {
    SHIFT_ONE("!", "i"),
    SHIFT_TWO("@", "a"),
    SHIFT_THREE("$", "s"),

    ZERO(0, "o"),
    ONE(1, "l"),
    TWO(2, "s"),
    FIVE(5, "s"),
    SEVEN(7, "j"),
    NINE(9, "g"),
    THREE(3, "з"),
    FOUR(4, "ч"),
    SIX(6, "б"),

    Y("у"),
    E("е"),
    H("н"),
    X("х"),
    B("в"),
    A("а"),
    P("р"),
    O("о"),
    C("с"),
    M("м"),
    N("и"),
    U("и"),
    K("к"),
    T("т");

    private final String target;
    private String shiftNumber = null;
    private int number = -1;

    Translit(int numberForReplace, String target) {
        this.number = numberForReplace;
        this.target = target;
    }

    Translit(String shiftNumber, String target) {
        this.shiftNumber = shiftNumber;
        this.target = target;
    }

    public static String process(String originalMessage) {
        for (Translit value : Translit.values()) {
            if (value.number >= 0) {
                originalMessage = originalMessage.replace(String.valueOf(value.number), value.target);
                continue;
            }

            if (value.shiftNumber != null) {
                originalMessage = originalMessage.replace(value.shiftNumber, value.target);
                continue;
            }

            originalMessage = originalMessage
                    .replace(value.name().toLowerCase(), value.target)
                    .replace(value.name(), value.target.toUpperCase());
        }
        return originalMessage;
    }
}