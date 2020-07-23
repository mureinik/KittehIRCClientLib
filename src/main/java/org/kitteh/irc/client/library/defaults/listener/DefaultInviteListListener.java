/*
 * * Copyright (C) 2013-2020 Matt Baxter https://kitteh.org
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
package org.kitteh.irc.client.library.defaults.listener;

import net.engio.mbassy.listener.Handler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.ServerMessage;
import org.kitteh.irc.client.library.element.mode.ModeInfo;
import org.kitteh.irc.client.library.event.client.ClientReceiveNumericEvent;
import org.kitteh.irc.client.library.feature.filter.NumericFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Default invite list listener, producing events using default classes.
 */
public class DefaultInviteListListener extends AbstractModeInfoListenerBase {
    private final List<ServerMessage> inviteMessages = new ArrayList<>();
    private final List<ModeInfo> invites = new ArrayList<>();

    /**
     * Constructs the listener.
     *
     * @param client client
     */
    public DefaultInviteListListener(Client.@NonNull WithManagement client) {
        super(client);
    }

    @NumericFilter(346) // INVITELIST
    @Handler(priority = Integer.MAX_VALUE - 1)
    public void inviteList(ClientReceiveNumericEvent event) {
        this.modeInfoList(event, "INVITELIST", 'I', this.inviteMessages, this.invites);
    }

    @NumericFilter(347) // End of invite list
    @Handler(priority = Integer.MAX_VALUE - 1)
    public void inviteListEnd(ClientReceiveNumericEvent event) {
        this.endModeInfoList(event, "INVITELIST", 'I', this.inviteMessages, this.invites);
    }
}
