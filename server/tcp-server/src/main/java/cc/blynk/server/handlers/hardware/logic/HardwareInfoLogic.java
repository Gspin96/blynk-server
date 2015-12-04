package cc.blynk.server.handlers.hardware.logic;

import cc.blynk.common.model.messages.StringMessage;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.common.utils.StringUtils;
import cc.blynk.server.handlers.hardware.auth.HardwareProfile;
import cc.blynk.server.handlers.hardware.auth.HardwareStateHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import static cc.blynk.common.enums.Response.*;
import static cc.blynk.common.model.messages.MessageFactory.*;

/**
 *
 * Simple handler that accepts info command from hardware.
 * At the moment only 1 param is used "h-beat".
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareInfoLogic {

    private final int hardwareIdleTimeout;

    public HardwareInfoLogic(ServerProperties props) {
        this.hardwareIdleTimeout = props.getIntProperty("hard.socket.idle.timeout", 0);
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        HardwareProfile hardwareProfile = new HardwareProfile(messageParts);
        int newHardwareInterval = hardwareProfile.getHeartBeatInterval();

        if (hardwareIdleTimeout != 0 && newHardwareInterval > 0) {
            ctx.pipeline().remove(ReadTimeoutHandler.class);
            ctx.pipeline().addFirst(new ReadTimeoutHandler(newHardwareInterval));
        }

        ctx.writeAndFlush(produce(message.id, OK));
    }

}