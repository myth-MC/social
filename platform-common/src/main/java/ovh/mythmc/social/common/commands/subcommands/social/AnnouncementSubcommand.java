package ovh.mythmc.social.common.commands.subcommands.social;

import org.bukkit.command.CommandSender;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.announcement")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length < 1) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Integer integer = tryParse(args[0]);
        if (integer == null || integer < 0 || integer >= Social.get().getAnnouncementManager().getAnnouncements().size()) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getInvalidNumber(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialAnnouncement announcement = Social.get().getAnnouncementManager().getAnnouncements().get(integer);

        if (Social.get().getConfig().getSettings().getAnnouncements().isUseActionBar()) {
            Social.get().getPlayerManager().get().forEach(s -> Social.get().getTextProcessor().parseAndSend(s, s.getMainChannel(), announcement.message(), ChannelType.ACTION_BAR));
        } else {
            for (ChatChannel channel : announcement.channels()) {
                channel.getMembers().forEach(uuid -> {
                    SocialPlayer s = Social.get().getPlayerManager().get(uuid);
                    Social.get().getTextProcessor().parseAndSend(s, s.getMainChannel(), announcement.message(), channel.getType());
                });
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        List<String> integers = new ArrayList<>();
        for (int i = 0; i < Social.get().getAnnouncementManager().getAnnouncements().size(); i++) {
            integers.add(String.valueOf(i));
        }

        return integers;
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
