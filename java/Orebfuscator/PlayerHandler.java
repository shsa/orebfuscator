package Orebfuscator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import Orebfuscator.Options.WorldOptions;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
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
		
		public void update(final int x, final int y, final int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public boolean isTransparent(final World world, final int x, final int y, final int z)
	{
		return Options.isBlockTransparent(BlockHelper.getBlockID(world, x, y, z));
	}

	public void updateBlock(final World world, final int x, final int y, final int z)
	{
		if (isTransparent(world, x, y, z))
			return;
		
		if (Options.isObfuscated(BlockHelper.getBlockID(world, x, y, z)))
		{
			if (isTransparent(world, x - 1, y, z) || isTransparent(world, x + 1, y, z) || 
				isTransparent(world, x, y - 1, z) || isTransparent(world, x, y + 1, z) ||
				isTransparent(world, x, y, z - 1) || isTransparent(world, x, y, z + 1))
				return;
			
			world.markBlockForUpdate(x, y, z);
		}
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
    		info.update(x, y, z);
    		
    		for (int i = 0; i < Options.updateOffsets.size(); i++)
    		{
    			Options.Offset offset = Options.updateOffsets.get(i);
				updateBlock(player.worldObj, x + offset.x, y + offset.y, z + offset.z);
    		}
    	}
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
