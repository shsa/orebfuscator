package Orebfuscator;

import java.lang.reflect.Field;

import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class MapChunkBulkObfuscator 
{
    public static Field fieldChunkX;
    public static Field fieldChunkZ;
	
	
	// 1110011 - набор блоков (ExtendedBlockStorage.blockLSBArray) по высоте (1 - есть блок, 0 - нет блока)
	public static Field fieldStatusLSB = null;
	
	// 0001110 - набор блоков (ExtendedBlockStorage.blockMSBArray) по высоте (1 - есть блок, 0 - нет блока)
	public static Field fieldStatusMSB;
	
	// -- массив данных
	public static Field fieldData;
	
	public static ChunkObfuscator info;
	
	public static void obfuscate(World world, S26PacketMapChunkBulk packet)
	{
		if (fieldStatusLSB == null)
		{
            fieldChunkX = Fields.getField(packet, "field_149266_a");
            fieldChunkZ = Fields.getField(packet, "field_149264_b");

			fieldStatusLSB = Fields.getField(packet, "field_149265_c");
			fieldStatusMSB = Fields.getField(packet, "field_149262_d");
			fieldData = Fields.getField(packet, "field_149260_f");
			
			info = new ChunkObfuscator();
		}
		

		int[] chunkX = (int[]) Fields.getValue(packet, fieldChunkX);
		int[] chunkZ = (int[]) Fields.getValue(packet, fieldChunkZ);
        int[] statusLSB = (int[]) Fields.getValue(packet, fieldStatusLSB);
        int[] statusMSB = (int[]) Fields.getValue(packet, fieldStatusMSB);
        byte[][] dataArray = (byte[][]) Fields.getValue(packet, fieldData);

        for (int i = 0; i < statusLSB.length; i++)
        {
        	info.obfuscate(world, chunkX[i], chunkZ[i], statusLSB[i], statusMSB[i], dataArray[i]);
        }
	}
}
