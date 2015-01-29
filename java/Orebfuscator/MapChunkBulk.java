package Orebfuscator;

import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class MapChunkBulk 
{
	public int offsetLSB;
	public int offsetMSB;
	public int offsetMetadata;
	public int offsetBlocklight;
	public int len;
	public int lenLSB;
	public int lenMSB;
	public int[] bufLSB;
	public int[] bufMSB;
	public byte[][] dataArray;
	public byte[] data;
	
	public MapChunkBulk(S26PacketMapChunkBulk packet, World world)
	{
        int k = 0;
        int l;

        bufLSB = (int[]) Fields.getValue(packet, "field_149265_c"); // 1110011 - набор блоков (ExtendedBlockStorage.blockLSBArray) по высоте (1 - есть блок, 0 - нет блока)
        bufMSB = (int[]) Fields.getValue(packet, "field_149262_d"); // 0001110 - набор блоков (ExtendedBlockStorage.blockMSBArray) по высоте (1 - есть блок, 0 - нет блока)
        dataArray = (byte[][]) Fields.getValue(packet, "field_149260_f"); // -- массив данных

        ChunkInfo info = new ChunkInfo();
        
        int lsb;
        int msb;
        int pos;
        int len;
        
        for (int i = 0; i < bufLSB.length; i++)
        {
        	info.parse(world, dataArray[i], bufLSB[i], bufMSB[i]);
        }
	}
}
