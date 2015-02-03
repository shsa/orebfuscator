package Orebfuscator;

import java.net.SocketAddress;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ProxyChannel implements Channel 
{
	public Channel channel;
	public EntityPlayerMP player;
	
	public ProxyChannel(Channel channel, EntityPlayerMP player)
	{
		this.channel = channel;
		this.player = player;
	}

	public Object updateMsg(Object msg)
	{
		if (msg instanceof S26PacketMapChunkBulk) 
		{
			S26PacketMapChunkBulk packet = (S26PacketMapChunkBulk)msg;
			return MapChunkBulkObfuscator.obfuscate(player.worldObj, packet);
		}
		return msg;
	}
	
	@Override
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return this.channel.attr(key);
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress) {
		return this.channel.bind(localAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress) {
		return this.channel.connect(remoteAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		return this.channel.connect(remoteAddress, localAddress);
	}

	@Override
	public ChannelFuture disconnect() {
		return this.channel.disconnect();
	}

	@Override
	public ChannelFuture close() {
		return this.channel.close();
	}

	@Override
	public ChannelFuture deregister() {
		return this.channel.deregister();
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return this.channel.bind(localAddress, promise);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return this.channel.connect(remoteAddress, promise);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		return this.channel.connect(remoteAddress, localAddress, promise);
	}

	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return this.channel.disconnect(promise);
	}

	@Override
	public ChannelFuture close(ChannelPromise promise) {
		return this.channel.close(promise);
	}

	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return this.channel.deregister(promise);
	}

	@Override
	public ChannelFuture write(Object msg) {
		return this.channel.write(msg);
	}

	@Override
	public ChannelFuture write(Object msg, ChannelPromise promise) {
		return this.channel.write(msg, promise);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		//this.updateMsg(msg);
		return this.channel.writeAndFlush(msg, promise);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		msg = this.updateMsg(msg);
		return this.channel.writeAndFlush(msg);
	}

	@Override
	public ChannelPipeline pipeline() {
		return this.channel.pipeline();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.channel.alloc();
	}

	@Override
	public ChannelPromise newPromise() {
		return this.channel.newPromise();
	}

	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return this.channel.newProgressivePromise();
	}

	@Override
	public ChannelFuture newSucceededFuture() {
		return this.channel.newSucceededFuture();
	}

	@Override
	public ChannelFuture newFailedFuture(Throwable cause) {
		return this.channel.newFailedFuture(cause);
	}

	@Override
	public ChannelPromise voidPromise() {
		return this.channel.voidPromise();
	}

	@Override
	public int compareTo(Channel arg0) {
		return this.channel.compareTo(arg0);
	}

	@Override
	public EventLoop eventLoop() {
		return this.channel.eventLoop();
	}

	@Override
	public Channel parent() {
		return this.channel.parent();
	}

	@Override
	public ChannelConfig config() {
		return this.channel.config();
	}

	@Override
	public boolean isOpen() {
		return this.channel.isOpen();
	}

	@Override
	public boolean isRegistered() {
		return this.channel.isRegistered();
	}

	@Override
	public boolean isActive() {
		return this.channel.isActive();
	}

	@Override
	public ChannelMetadata metadata() {
		return this.channel.metadata();
	}

	@Override
	public SocketAddress localAddress() {
		return this.channel.localAddress();
	}

	@Override
	public SocketAddress remoteAddress() {
		return this.channel.remoteAddress();
	}

	@Override
	public ChannelFuture closeFuture() {
		return this.channel.closeFuture();
	}

	@Override
	public boolean isWritable() {
		return this.channel.isWritable();
	}

	@Override
	public Channel flush() {
		return this.channel.flush();
	}

	@Override
	public Channel read() {
		return this.channel.read();
	}

	@Override
	public Unsafe unsafe() {
		return this.channel.unsafe();
	}
}
