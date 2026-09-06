package com.katbot;

import com.katbot.eventListeners.ButtonInteractionListener;
import com.katbot.eventListeners.GuildMessageListener;
import com.katbot.eventListeners.ReactionInteractionListener;
import com.katbot.eventListeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.EnumSet;

@Configuration
@ComponentScan(basePackages = "com.katbot")
@PropertySource("classpath:application.properties")
public class KatBotConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(System.getenv("DB_URL"));
        dataSource.setUsername(System.getenv("DB_USERNAME"));
        dataSource.setPassword(System.getenv("DB_PASSWORD"));
        dataSource.setSchema(System.getenv("DB_SCHEMA"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JDA jda (
            GuildMessageListener guildMessageListener,
            ButtonInteractionListener buttonInteractionListener,
            ReactionInteractionListener reactionInteractionListener,
            SlashCommandListener slashCommandListener
    ) throws Exception {
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS
        );

        String token = System.getenv("token");
        if (token == null) {
            throw new IllegalStateException("Discord bot token not found. Make sure the 'token' environment variable is set.");
        }

        JDABuilder builder = JDABuilder.createDefault(token, intents);
        builder.setActivity(Activity.watching("Netflix"));
        builder.setMemberCachePolicy(MemberCachePolicy.lru(20));

        // Add event listeners
        builder.addEventListeners(guildMessageListener, buttonInteractionListener, reactionInteractionListener, slashCommandListener);

        // Build and return JDA instance
        return builder.build();
    }
}
