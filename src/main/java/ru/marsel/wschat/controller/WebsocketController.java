package ru.marsel.wschat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.RestController;
import ru.marsel.wschat.config.oauth.CustomPrincipal;
import ru.marsel.wschat.dto.MetaDto;
import ru.marsel.wschat.model.Message;
import ru.marsel.wschat.service.ChatService;
import ru.marsel.wschat.service.MessageService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {
    private final ChatService chatService;
    private final MessageService messageService;
    private final ResourceServerTokenServices tokenService;

    private static String URL_PATTERN = "https?:\\/\\/?[\\w\\d\\._\\-%\\/\\?=&#]+";
    private static String IMAGE_PATTERN = "\\.(jpeg|jpg|gif|png)$";

    private static Pattern URL_REGEX = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE);
    private static Pattern IMG_REGEX = Pattern.compile(IMAGE_PATTERN, Pattern.CASE_INSENSITIVE);

    @MessageMapping("/send/{id}")
    @SendTo("/topic/chat/{id}")
    public Message sendMessage(@DestinationVariable Long id, Message message, @Header(name="Authorization") String token) {
        CustomPrincipal principal = (CustomPrincipal) tokenService.loadAuthentication(token).getUserAuthentication().getPrincipal();
        message.setOwnerId(principal.getId());
        message.setOwnerName(principal.getName());
        message.setChat(chatService.getChat(id));
        messageService.save(message);
        fillMeta(message);
        return message;
    }


    public static void fillMeta(Message message) {
        String text = message.getMessage();
        Matcher matcher = URL_REGEX.matcher(text);

        if (matcher.find()) {
            String url = text.substring(matcher.start(), matcher.end());
            message.setLink(url);

            matcher = IMG_REGEX.matcher(url);

            if (matcher.find()) {
                message.setLink(url);
            } else if (!url.contains("youtu")) {
                MetaDto metaDto = getMeta(url);

                if (metaDto != null) {
                    message.setLinkTitle(metaDto.getTitle());
                    message.setLinkCover(metaDto.getCover());
                    message.setLinkDescription(metaDto.getDescription());
                }
            }
        }
    }

    private static MetaDto getMeta(String url) {
        try {
            Document document = Jsoup.connect(url).get();

            Elements title = document.select("meta[name$=title],meta[property$=title]");
            Elements description = document.select("meta[name$=description],meta[property$=description]");
            Elements cover = document.select("meta[name$=image],meta[property$=image]");

            return new MetaDto(
                    getContent(title.first()),
                    getContent(description.first()),
                    getContent(cover.first())
            );
        } catch (IOException e) {
            log.error("Error get meta", e);
            return null;
        }

    }

    private static String getContent(Element element) {
        return element == null ? "" : element.attr("content");
    }
}
