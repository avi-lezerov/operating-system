/**
 * A synchronized bounded-size queue for multithreaded producer-consumer
 * applications.
 * 
 * @param <T> Type of data items
 */
public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	private ArrayQueue<T> queue;
	private final Object lock = new Object();

	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * 
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[]) (new Object[capacity]);
		this.producers = 0;
		this.queue = new ArrayQueue<>(buffer);
	}

	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue,
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this
	 * queue (because no producers are registered), this method returns null.
	 * 
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public T dequeue() {
		synchronized (lock) {
			while (queue.isEmpty() && producers > 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			if (queue.isEmpty() && producers == 0) {
				return null;
			}
			T item = queue.dequeue();
			lock.notifyAll();
			return item;
		}
	}
	

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this
	 * method blocks until some space becomes available.
	 * 
	 * @param item Item to enqueue
	 */
	public void enqueue(T item) {
		synchronized (lock) {
			while (queue.isFull()) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			queue.enqueue(item);
			if (queue.size() == 1) {
				lock.notifyAll();
			}
		}
	}
	

	/**
	 * Returns the capacity of this queue
	 * 
	 * @return queue capacity
	 */
	public int getCapacity() {
		return buffer.length;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * 
	 * @return queue size
	 */
	public int getSize() {
		synchronized(lock){
			return queue.size();
		}
			
	}

	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see>
	 * when
	 * finishes to enqueue all items.
	 * 
	 * @see #dequeue().
	 * @see #unregisterProducer()
	 */
	public  void registerProducer() {
		synchronized(lock){
		this.producers++;
		}
	}

	/**
	 * Unregisters a producer from this queue. See
	 * <see>{@link #registerProducer()}</see>.
	 * 
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public void unregisterProducer() {
		synchronized (lock) {
			this.producers--;
			lock.notifyAll();
		}
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	public synchronized int getProducers() {
		return producers;
	}
}
