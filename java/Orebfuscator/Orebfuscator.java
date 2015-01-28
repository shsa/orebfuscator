package Orebfuscator;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = Orebfuscator.MODID, version = Orebfuscator.VERSION, acceptableRemoteVersions = "*")
public class Orebfuscator
{
    public static final String MODID = "Orebfuscator";
    public static final String VERSION = "0.1";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onPlayerLogged(PlayerEvent.PlayerLoggedInEvent event)
    {
    	PlayerInjector.hookPlayer((EntityPlayerMP)event.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
    	PlayerInjector.cleanupPlayer((EntityPlayerMP) event.player);
    }
    
    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    {
    	/*
    	NetHandlerPlayServer handler = (NetHandlerPlayServer)event.handler;
    	PlayerInjector.hookPlayer(handler.playerEntity, handler.netManager);
    	*/
    }
    
	private static HashSet<Integer> forcedTransparentBlocks = new HashSet<Integer>();
	private static boolean[] TransparentBlocks = new boolean[4096];
	private static boolean TransparentCached = false;
	public static boolean isBlockTransparent(int id) 
	{
		if (id < 0)
			return true;
		if (!TransparentCached) 
		{
			// Generate TransparentBlocks by reading them from Minecraft
			for (int i = 0; i < TransparentBlocks.length; i++) {
				if (forcedTransparentBlocks.contains(i)) 
				{
					TransparentBlocks[i] = true;
				}
				else
				{
					Block block = Block.getBlockById(i);
					if (block == null)
					{
						TransparentBlocks[i] = true;
					}
					else
					{
						TransparentBlocks[i] = !block.isNormalCube();
					}
				}
			}
			TransparentCached = true;
		}
		return TransparentBlocks[id];
	}
}
