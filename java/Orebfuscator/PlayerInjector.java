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

	public static void hookPlayer(EntityPlayerMP player) 
	{
		NetworkManager nm = player.playerNetServerHandler.netManager;
		Channel channel = (Channel)Fields.getValue(nm, Fields.NetworkManager.getChannelIndex());
		channel = new ProxyChannel(channel, player);
		Fields.setValue(nm, Fields.NetworkManager.getChannelIndex(), channel);
	}

	public static void cleanupPlayer(EntityPlayerMP player) 
	{
	}
}
