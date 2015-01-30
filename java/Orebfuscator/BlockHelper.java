package Orebfuscator;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class BlockHelper 
{
	private static int getBlockExtId(final ExtendedBlockStorage extendedblockstorage, final int x, final int y, final int z)
	{
        if (extendedblockstorage != null)
        {
            int l = extendedblockstorage.getBlockLSBArray()[y << 8 | z << 4 | x] & 255;

            if (extendedblockstorage.getBlockMSBArray() != null)
            {
                l |= extendedblockstorage.getBlockMSBArray().get(x, y, z) << 8;
            }

            return l;
        }
        return 0;
	}
	
	public static int getBlockID(final Chunk chunk, final int x, final int y, final int z)
	{
        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();	                
        if (y >> 4 < storageArrays.length)
        {
            return getBlockExtId(storageArrays[y >> 4], x, y & 15, z);
        }
        return 0;
	}
	
	public static int getBlockID(final World world, final int x, final int y, final int z)
	{
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256)
        {
            Chunk chunk = null;

            try
            {
                chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                return getBlockID(chunk, x & 15, y, z & 15);
            }
            catch (Throwable throwable)
            {
            }
        }
        return 0;
	}
}
