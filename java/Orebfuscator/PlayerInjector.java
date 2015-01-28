package Orebfuscator;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PlayerInjector {

	@SuppressWarnings("unchecked")
	public static void hookPlayer(EntityPlayerMP player) 
	{
		/*
		NetworkManager nm = player.playerNetServerHandler.netManager;
		Queue queue = (Queue)Fields.getValue(nm, Fields.NetworkManager.getReceivedPacketsQueueIndex());
		queue = new AsyncPacketQueue(player, queue);
		Fields.setValue(nm, Fields.NetworkManager.getReceivedPacketsQueueIndex(), queue);
		*/
		/*
		Queue queue = (Queue)Fields.getValue(nm, Fields.NetworkManager.getOutboundPacketsQueueIndex());
		queue = new AsyncPacketQueue(player, queue);
		Fields.setValue(nm, Fields.NetworkManager.getOutboundPacketsQueueIndex(), queue);
		*/
		
		NetworkManager nm = player.playerNetServerHandler.netManager;
		Channel channel = (Channel)Fields.getValue(nm, Fields.NetworkManager.getChannelIndex());
		channel = new ProxyChannel(channel, player);
		Fields.setValue(nm, Fields.NetworkManager.getChannelIndex(), channel);
	}

	public static void cleanupPlayer(EntityPlayerMP player) 
	{
		/*
		NetworkManager nm = player.playerNetServerHandler.netManager;
		((AsyncPacketQueue)Fields.getValue(nm, Fields.NetworkManager.getReceivedPacketsQueueIndex())).cleanup();
		*/
		/*
		NetworkManager nm = player.playerNetServerHandler.netManager;
		((AsyncPacketQueue)Fields.getValue(nm, Fields.NetworkManager.getOutboundPacketsQueueIndex())).cleanup();
		*/
	}
}
