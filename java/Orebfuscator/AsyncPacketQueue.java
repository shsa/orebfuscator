package Orebfuscator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class AsyncPacketQueue<E> implements Iterable<E>, Collection<E>, Queue<E> 
{

	public Queue queue;
	public EntityPlayerMP player;
	
	public AsyncPacketQueue(EntityPlayerMP player, Queue queue)
	{
		this.player = player;
		this.queue = queue;
	}
	
	@Override
	public boolean add(E arg0) {
		//Packet packet = (Packet) Fields.getValue(arg0, Fields.InboundHandlerTuplePacketListener.getPacketIndex());
		//Log.msg("%s", packet.getClass().getName());
		Log.msg("%s", arg0.getClass().getName());
		return this.queue.add(arg0);
	}

	public void cleanup() {
		this.player = null;
	}

	@Override
	public E element() {
		return (E) this.queue.element();
	}

	@Override
	public boolean offer(E arg0) {
		return this.queue.offer(arg0);
	}

	@Override
	public E peek() {
		return (E) this.queue.peek();
	}

	@Override
	public E poll() {
		return (E) this.queue.poll();
	}

	@Override
	public E remove() {
		return (E) this.queue.remove();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		Log.error("Queue.addAll");
		return this.queue.addAll(arg0);
	}

	@Override
	public void clear() {
		this.queue.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return this.queue.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return this.queue.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public boolean remove(Object arg0) {
		return this.queue.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return this.queue.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return this.queue.removeAll(arg0);
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	@Override
	public Object[] toArray() {
		return this.queue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return (T[]) this.queue.toArray(arg0);
	}

	@Override
	public Iterator<E> iterator() {
		return this.queue.iterator();
	}
}
