public interface HuffmanTree extends Comparable<HuffmanTree> {
    /**
     * Returns true if the tree rooted at node a comes before the tree
     * rooted at node b, false otherwise.
     *
     * A node "comes before" another if its frequency count is smaller.
     * Ties are broken using the ASCII value of the character: the node
     * with the smaller ASCII value comes first.
     */
    public boolean comesBefore(HuffmanTree other);

    /**
     * Creates and returns a new internal HuffmanNode with a and b as its
     * two children.
     *
     * The child for which comesBefore() is true goes on the left, the
     * other goes on the right. The new node's frequency is the sum of a's
     * and b's frequencies. The new node's charAscii value is the smaller
     * of a's and b's charAscii values (used to break ties later on).
     */
    public HuffmanTree combine(HuffmanTree other);

    /**
     * Natural ordering consistent with comesBefore(), so that HuffmanNode
     * objects can be stored directly in a sorted structure (e.g. a
     * PriorityQueue or a sorted List) while building the tree.
     */
    @Override
    public int compareTo(HuffmanTree other);

    int freq();

    int charAscii();
}
