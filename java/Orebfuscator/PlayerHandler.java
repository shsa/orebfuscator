package Orebfuscator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

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
		public boolean[][][] isTransparent = new boolean[5][5][5];
		
		public int x;
		public int y;
		public int z;
		
		public void updateBlocksTransparent(World world, int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			
			for (short i = -1; i < 2; i++)
			{
				for (short j = -1; j < 2; j++)
				{
					for (short k = -1; k < 2; k++)
					{
						this.isTransparent[2 + i][2 + j][2 + k] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x + i, y + j, z + k));
					}
				}
			}
			this.isTransparent[0][2][2] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x - 2, y, z));
			this.isTransparent[4][2][2] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x + 2, y, z));

			this.isTransparent[2][0][2] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x, y - 2, z));
			this.isTransparent[2][4][2] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x, y + 2, z));

			this.isTransparent[2][2][0] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x, y, z - 2));
			this.isTransparent[2][2][4] = Options.isBlockTransparent(BlockHelper.getBlockID(world, x, y, z + 2));
			
			this.isTransparent[2][2][2] = false;
		}
		
		public boolean isTransparent(final World world, final int x, final int y, final int z)
		{
			return this.isTransparent[2 + x - this.x][2 + y - this.y][2 + z - this.z];
		}

		public void updateBlock(World world, int x, int y, int z)
		{
			if (Options.isObfuscated(BlockHelper.getBlockID(world, x, y, z)))
			{
				if (isTransparent(world, x - 1, y, z) || isTransparent(world, x + 1, y, z) || 
					isTransparent(world, x, y - 1, z) || isTransparent(world, x, y + 1, z) ||
					isTransparent(world, x, y, z - 1) || isTransparent(world, x, y, z + 1))
					return;
				
				world.markBlockForUpdate(x, y, z);
			}
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
    		info.updateBlocksTransparent(player.worldObj, x, y, z);
			
			info.updateBlock(player.worldObj, x-1, y, z);
			info.updateBlock(player.worldObj, x+1, y, z);
			info.updateBlock(player.worldObj, x, y-1, z);
			info.updateBlock(player.worldObj, x, y+1, z);
			info.updateBlock(player.worldObj, x, y, z-1);
			info.updateBlock(player.worldObj, x, y, z+1);
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
