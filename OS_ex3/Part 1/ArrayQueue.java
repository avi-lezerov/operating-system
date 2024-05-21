import java.util.NoSuchElementException;
/**
 * The ArrayQueue class represents a generic queue implemented using an array.
 * It supports enqueueing, dequeueing, peeking, and checking if the queue is full or empty.
 *
 * @param <T> the type of elements in the queue
 */
public class ArrayQueue<T> {
    private T[] queue;
    private int size, head, tail;

    /**
     * Constructs an ArrayQueue with the specified array as the underlying storage.
     *
     * @param queue the array to be used as the storage for the queue
     */
    public ArrayQueue(T[] queue) {
        this.queue = queue;
        size = 0;
        head = 0;
        tail = -1;
    }

    /**
     * Adds an element to the end of the queue.
     *
     * @param t the element to be added to the queue
     * @throws IllegalStateException if the queue is full
     */
    public void enqueue(T t) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        tail = (tail + 1) % queue.length;
        queue[tail] = t;
        size++;
    }

    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return the element at the front of the queue
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T element = queue[head];
        head = (head + 1) % queue.length;
        size--;
        return element;
    }


    /**
     * Checks if the queue is full.
     *
     * @return true if the queue is full, false otherwise
     */
    public boolean isFull() {
        return size == queue.length;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return size;
    }
}