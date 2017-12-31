/*
 * * Copyright (C) 2013-2017 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.irc.client.library.defaults.element;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.command.ChannelModeCommand;
import org.kitteh.irc.client.library.command.KickCommand;
import org.kitteh.irc.client.library.command.TopicCommand;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.element.mode.ChannelMode;
import org.kitteh.irc.client.library.element.mode.ChannelUserMode;
import org.kitteh.irc.client.library.element.mode.ModeInfo;
import org.kitteh.irc.client.library.element.mode.ModeStatusList;
import org.kitteh.irc.client.library.util.Sanity;
import org.kitteh.irc.client.library.util.ToStringer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * Default Channel implementation.
 */
public class DefaultChannel extends DefaultStaleable implements Channel {
    /**
     * Default channel commands.
     */
    public static class DefaultChannelCommands implements Channel.Commands {
        private final String channel;
        private final Client client;

        /**
         * Constructs for the given channel.
         *
         * @param client client
         * @param channel channel name
         */
        public DefaultChannelCommands(@Nonnull Client client, @Nonnull String channel) {
            this.client = client;
            this.channel = channel;
        }

        @Nonnull
        @Override
        public ChannelModeCommand mode() {
            return new ChannelModeCommand(this.client, this.channel);
        }

        @Nonnull
        @Override
        public KickCommand kick() {
            return new KickCommand(this.client, this.channel);
        }

        @Nonnull
        @Override
        public TopicCommand topic() {
            return new TopicCommand(this.client, this.channel);
        }
    }

    private final ModeStatusList<ChannelMode> channelModes;
    private final Map<Character, List<ModeInfo>> modeInfoLists;
    private final Map<String, SortedSet<ChannelUserMode>> modes;
    private final List<String> names;
    private final Map<String, User> nickMap;
    private final List<User> users;
    private final boolean complete;
    private final Topic topic;
    private final DefaultChannelCommands commands;

    /**
     * Constructs the channel snapshot.
     *
     * @param client client
     * @param name channel name
     * @param topic topic
     * @param channelModes channel modes
     * @param modeInfoLists modeinfolists
     * @param modes modes
     * @param names who is in the channel
     * @param nickMap map of nicks to Users
     * @param users users
     * @param complete true if WHO completed
     * @param commands commands object
     */
    public DefaultChannel(@Nonnull Client.WithManagement client, @Nonnull String name, @Nonnull Topic topic,
                          @Nonnull ModeStatusList<ChannelMode> channelModes,
                          @Nonnull Map<Character, List<ModeInfo>> modeInfoLists,
                          @Nonnull Map<String, SortedSet<ChannelUserMode>> modes, @Nonnull List<String> names,
                          @Nonnull Map<String, User> nickMap, @Nonnull List<User> users,
                          boolean complete, @Nonnull DefaultChannelCommands commands) {
        super(client, name);
        this.complete = complete;
        this.channelModes = channelModes;
        this.topic = topic;
        this.commands = commands;
        this.modeInfoLists = modeInfoLists;
        this.modes = Collections.unmodifiableMap(modes);
        this.names = Collections.unmodifiableList(names);
        this.nickMap = Collections.unmodifiableMap(nickMap);
        this.users = Collections.unmodifiableList(users);
    }

    @Override
    public boolean equals(Object o) {
        // RFC 2812 section 1.3 'Channel names are case insensitive.'
        return (o instanceof DefaultChannel) && (((DefaultChannel) o).getClient() == this.getClient()) && ((Channel) o).getLowerCaseName().equals(this.getLowerCaseName());
    }

    @Nonnull
    @Override
    public String getMessagingName() {
        return this.getName();
    }

    @Nonnull
    @Override
    public Optional<List<ModeInfo>> getModeInfoList(@Nonnull ChannelMode mode) {
        Sanity.nullCheck(mode, "Mode cannot be null");
        Sanity.truthiness(mode.getType() == ChannelMode.Type.A_MASK, "Mode type must be A, found " + mode.getType());
        return Optional.ofNullable(this.modeInfoLists.get(mode.getChar()));
    }

    @Override
    @Nonnull
    public ModeStatusList<ChannelMode> getModes() {
        return this.channelModes;
    }

    @Nonnull
    @Override
    public List<String> getNicknames() {
        return this.names;
    }

    @Nonnull
    @Override
    public Topic getTopic() {
        return this.topic;
    }

    @Nonnull
    @Override
    public Optional<User> getUser(@Nonnull String nick) {
        Sanity.nullCheck(nick, "Nick cannot be null");
        return Optional.ofNullable(this.nickMap.get(nick));
    }

    @Nonnull
    @Override
    public Optional<SortedSet<ChannelUserMode>> getUserModes(@Nonnull String nick) {
        Sanity.nullCheck(nick, "Nick cannot be null");
        return Optional.ofNullable(this.modes.get(nick));
    }

    @Nonnull
    @Override
    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public boolean hasCompleteUserData() {
        return this.complete;
    }

    @Override
    public void setModeInfoTracking(@Nonnull ChannelMode mode, boolean track) {
        Sanity.nullCheck(mode, "Mode cannot be null");
        Sanity.truthiness(mode.getType() == ChannelMode.Type.A_MASK, "Mode type must be A, found " + mode.getType());
        Sanity.truthiness((mode.getChar() == 'b') || (mode.getChar() == 'e') || (mode.getChar() == 'I') || (mode.getChar() == 'q'), "Only modes b, e, I, and q supported");
        Optional<Channel> channel = this.getClient().getActorTracker().getTrackedChannel(this.getName());
        if (!channel.isPresent()) {
            throw new IllegalStateException("Not currently in channel " + this.getName());
        }
        this.getClient().getActorTracker().trackChannelMode(channel.get().getName(), mode, track);
    }

    @Nonnull
    @Override
    public Commands commands() {
        return this.commands;
    }

    @Override
    public int hashCode() {
        // RFC 2812 section 1.3 'Channel names are case insensitive.'
        return (this.getLowerCaseName().hashCode() * 2) + this.getClient().hashCode();
    }

    @Override
    @Nonnull
    public String toString() {
        return new ToStringer(this).add("client", this.getClient()).add("name", this.getName()).add("complete", this.complete).add("users", this.users.size()).toString();
    }
}
