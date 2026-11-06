public interface HQueue {
   /**
     * return true if the queue is empty
     */
    boolean isEmpty();

    /**
     * return a count of the number of elements in the queue
     */
    int count();

    /**
     * enqueue a tree
     */
    void enqueue(HuffmanTree tree);

    /**
     * dequeue and return the highest-priority tree
     */
    HuffmanTree dequeue();

}
