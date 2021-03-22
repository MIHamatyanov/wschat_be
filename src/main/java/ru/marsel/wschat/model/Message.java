package ru.marsel.wschat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chat;
    private String ownerId;
    private String ownerName;

    private String message;

    @Transient
    private String link;
    @Transient
    private String linkTitle;
    @Transient
    private String linkDescription;
    @Transient
    private String linkCover;
}
