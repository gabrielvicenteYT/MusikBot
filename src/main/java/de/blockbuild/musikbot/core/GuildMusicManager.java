package de.blockbuild.musikbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.blockbuild.musikbot.Bot;
import de.blockbuild.musikbot.configuration.GuildConfiguration;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildMusicManager {
	private final AudioPlayer player;
	private final TrackScheduler trackScheduler;
	public final GuildConfiguration config;
	private final Bot bot;
	private final Guild guild;
	private Boolean autoPlay;

	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild, Bot bot) {
		this.bot = bot;
		this.guild = guild;
		this.player = playerManager.createPlayer();
		this.trackScheduler = new TrackScheduler(bot, this);
		this.player.addListener(trackScheduler);
		this.autoPlay = false;
		this.config = new GuildConfiguration(bot, this);

		if (config.isAutoConnectEnabled()) {
			if (config.getAutoConnectVoiceChannelId() == 0) {
				bot.joinDiscordVoiceChannel(guild);
			} else {
				if (!bot.joinDiscordVoiceChannel(guild, config.getAutoConnectVoiceChannelId())
						&& config.isDefaultTextChannelEnabled()) {
					TextChannel channel = bot.getTextChannelById(config.getDefaultTextChannel());
					StringBuilder builder = new StringBuilder();
					builder.append(" Missing permission or there is no channel called `")
							.append(config.getDefaultTextChannel()).append("`.");
					channel.sendMessage(builder.toString());

				}
			}
			if (!(config.getAutoConnectTrack() == null)) {
				playerManager.loadItemOrdered(playerManager, config.getAutoConnectTrack(),
						new BasicResultHandler(this.getAudioPlayer()));
			}
		}
	}

	public Guild getGuild() {
		return this.guild;
	}

	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player, bot);
	}

	public TrackScheduler getTrackScheduler() {
		return this.trackScheduler;
	}

	public AudioPlayer getAudioPlayer() {
		return this.player;
	}

	public void setVolume(int volume) {
		player.setVolume(volume);
		config.setVolume(volume);
	}

	public int getVolume() {
		return player.getVolume();
	}

	public Boolean isAutoPlay() {
		return this.autoPlay;
	}

	public void setIsAutoPlay(boolean bool) {
		this.autoPlay = bool;
	}
}
