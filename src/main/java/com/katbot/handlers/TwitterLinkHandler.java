package com.katbot.handlers;

public class TwitterLinkHandler {
    public static String convertTwitterLink(String message) {
        return message.replace("https://x.com/", "https://vxtwitter.com/");
    }
}
