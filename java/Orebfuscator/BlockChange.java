package Orebfuscator;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;

public class BlockChange 
{
	public static Field fieldPositionX = null;
	public static Field fieldPositionY = null;
	public static Field fieldPositionZ = null;
	public static HashSet<Integer> list = new HashSet();
	
	public static int baseX;
	public static int baseY;
	public static int baseZ;
	
	public static int updateRadius = 2;
	public static int startPos;
	
	public static void parse(World world, Channel channel, S23PacketBlockChange packet)
	{
		if (fieldPositionX == null)
		{
			fieldPositionX = Fields.getField(packet, "field_148887_a");
			fieldPositionY = Fields.getField(packet, "field_148885_b");
			fieldPositionZ = Fields.getField(packet, "field_148886_c");
		}
		
		int x = (Integer) Fields.getValue(packet, fieldPositionX);
		int y = (Integer) Fields.getValue(packet, fieldPositionY);
		int z = (Integer) Fields.getValue(packet, fieldPositionZ);
		
		// используем базовую точку, чтобы в дальнейшем высчитывать смещения обновляемых блоков
		// смещения будем сводить в один int и хранить в HashSet
		baseX = x - updateRadius;
		baseY = y - updateRadius;
		baseZ = z - updateRadius;
		
		x = updateRadius;
		y = updateRadius;
		z = updateRadius;
		
		startPos = (updateRadius << 16) | (updateRadius << 8) | updateRadius;
		
		list.clear();
		updateAjacentBlocks(world, updateRadius, updateRadius, updateRadius, updateRadius + 1);
		for (int pos : list)
		{
			x = baseX + (pos >> 16 & 255);
			y = baseY + (pos >> 8 & 255);
			z = baseZ + (pos & 255);
		
			world.markBlockForUpdate(x, y, z);
		}
	}
	
	public static boolean isTransparent(World world, int x, int y, int z)
	{
		int pos = (x << 16) | (y << 8) | z;
		if (pos == startPos)
			return false;
		
		Block block = world.getBlock(baseX + x, baseY + y, baseZ + z);
		return Options.isBlockTransparent(Block.getIdFromBlock(block));
	}
	
	public static boolean needUpdate(World world, int x, int y, int z)
	{
		int pos = (x << 16) | (y << 8) | z;
		if (pos == startPos)
			return false;
		
		return !(
			isTransparent(world, x - 1, y, z) || isTransparent(world, x + 1, y, z) ||
			isTransparent(world, x, y - 1, z) || isTransparent(world, x, y + 1, z) ||
			isTransparent(world, x, y, z - 1) || isTransparent(world, x, y, z + 1)
			);
	}
	
	public static boolean updateAjacentBlocks(World world, int x, int y, int z, int step)
	{
		if (step == 0)
			return false;
		
		int pos = (x << 16) | (y << 8) | z; 
		if (list.contains(pos))
			return false;
		
		if (needUpdate(world, x, y, z))
			list.add(pos);
		
		step--;
		updateAjacentBlocks(world, x - 1, y, z, step);
		updateAjacentBlocks(world, x + 1, y, z, step);
		updateAjacentBlocks(world, x, y - 1, z, step);
		updateAjacentBlocks(world, x, y + 1, z, step);
		updateAjacentBlocks(world, x, y, z - 1, step);
		updateAjacentBlocks(world, x, y, z + 1, step);
		
		return true;
	}
}
