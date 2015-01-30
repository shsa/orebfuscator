package Orebfuscator;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerHandler 
{
	public Map<EntityPlayer, BlockBreakInfo> list = new HashMap<EntityPlayer, BlockBreakInfo>();
	
	public static class BlockBreakInfo
	{
		public int x;
		public int y;
		public int z;
	}
	
	public void updateBlock(World world, int x, int y, int z)
	{
		if (Options.isObfuscated(world.getBlock(x, y, z)))
			world.markBlockForUpdate(x, y, z);
	}
	
	public void update(EntityPlayer player, int x, int y, int z)
	{
    	BlockBreakInfo info = list.get(player);
    	if (info == null)
    	{
    		info = new BlockBreakInfo();
    		list.put(player, info);
    	}

    	if (info.x != x || info.y != y || info.z != z)
    	{
	    	info.x = x;
			info.y = y;
			info.z = z;
			
			updateBlock(player.worldObj, x-1, y, z);
			updateBlock(player.worldObj, x+1, y, z);
			updateBlock(player.worldObj, x, y-1, z);
			updateBlock(player.worldObj, x, y+1, z);
			updateBlock(player.worldObj, x, y, z-1);
			updateBlock(player.worldObj, x, y, z+1);
    	}
    	//Log.msg("updated: %d, %d, %d", x, y, z);
	}
	
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event)
    {
    	update(event.entityPlayer, event.x, event.y, event.z);
    }
    
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
    	update(event.getPlayer(), event.x, event.y, event.z);
    }

}
