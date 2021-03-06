import audio.manager.GuildAudioManager;
import client.ClientProfile;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.application.audio.*;
import awaiter.SearchCommandResponseListener;
import commands.application.info.AvatarCommand;
import commands.application.info.HelpCommand;
import commands.application.info.PingCommand;
import exceptions.NullTokenException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws LoginException, FileNotFoundException, NullTokenException {
        final int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 1) {
            System.out.println("Available Cores \"" + cores + "\", setting Parallelism Flag");
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
        }

        ClientProfile clientProfile = ClientProfile.from("settings.txt");

        JDABuilder jdaBuilder = JDABuilder.createDefault(clientProfile.token());
        configureMemoryUsage(jdaBuilder);

        Command.Category audioCategory = new Command.Category("Audio Commands");
        Command.Category infoCategory = new Command.Category("Info Commands");

        GuildAudioManager audioManager = new GuildAudioManager();

        SearchCommandResponseListener searchWaiter = new SearchCommandResponseListener(audioManager);

        CommandClientBuilder clientBuilder = setupClientBuilderBasicInfo(new CommandClientBuilder(), clientProfile);
        clientBuilder.addSlashCommand(new JoinCommand(audioCategory));
        clientBuilder.addSlashCommand(new LoopCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new NowPlayCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new PauseCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new PlayCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new ShowQueueCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new ShuffleQueueCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new SkipCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new StopCommand(audioManager, audioCategory));
        clientBuilder.addSlashCommand(new YoutubeSearchCommand(audioManager, audioCategory, searchWaiter));

        HelpCommand helpCommand = new HelpCommand(infoCategory);
        clientBuilder.addSlashCommand(new AvatarCommand(infoCategory));
        clientBuilder.addSlashCommand(helpCommand);
        clientBuilder.addSlashCommand(new PingCommand(infoCategory));

        // classic message command
//        clientBuilder.addCommand(new commands.classic.audio.JoinCommand(audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.LoopCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.NowPlayCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.PauseCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.PlayCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.ShowQueueCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.ShuffleQueueCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.SkipCommand(audioManager, audioCategory));
//        clientBuilder.addCommand(new commands.classic.audio.StopCommand(audioManager, audioCategory));

        clientBuilder.setActivity(Activity.playing("n>help | /help"));

        CommandClient commandClient = clientBuilder.build();

        helpCommand.addCommands(commandClient.getSlashCommands());

        jdaBuilder.addEventListeners(commandClient);
        jdaBuilder.addEventListeners(searchWaiter);

        JDA jda = jdaBuilder.build();

        helpCommand.buildEmbed(jda);
    }

    public static CommandClientBuilder setupClientBuilderBasicInfo(CommandClientBuilder builder,
                                                                   ClientProfile profile) {
        builder.setOwnerId(profile.ownerId());
        builder.setPrefix(profile.prefix());
        builder.useHelpBuilder(false);

        return builder;
    }

    private static void configureMemoryUsage(JDABuilder builder) {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.disableCache(CacheFlag.CLIENT_STATUS);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.disableCache(CacheFlag.EMOTE);

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Disable presence updates and typing events and more Guild Events
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_MESSAGES);

        builder.disableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES);

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(50);
    }
}
