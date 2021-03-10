package ru.marsel.wschat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class Message {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    private Long id;
    private String username;
    private String message;

    private String link;
    private String linkTitle;
    private String linkDescription;
    private String linkCover;

    public Message() {
        this.id = ID_COUNTER.incrementAndGet();
    }
}
