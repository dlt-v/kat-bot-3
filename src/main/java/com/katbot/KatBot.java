package com.katbot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class KatBot {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(KatBotConfig.class);
    }
}
