/**
 * A single node in a Huffman tree, implemented as a record.
 *
 * A HuffmanNode represents either a leaf (an actual character together
 * with its occurrence count) or an internal node created by combining two
 * subtrees during tree construction.
 *
 * Records are immutable and give you charAscii(), freq(), left(), and
 * right() accessor methods automatically, along with
 * equals()/hashCode()/toString(). Do not rename the components below.
 */
public record HuffmanNode(int charAscii, int freq, HuffmanTree left, HuffmanTree right)
        implements HuffmanTree{

    public boolean comesBefore(HuffmanTree other) {
        if (this.freq < other.freq()) {
            return true;
        }
        if (this.freq > other.freq()) {
            return false;
        }
        return this.charAscii < other.charAscii();
    }

    public int freq(){
        return freq;
    }

    public int charAscii(){
        return charAscii;
    }

    public HuffmanTree combine(HuffmanTree other) {
        // TODO: implement
        return null;
    }

    /**
     * Natural ordering consistent with comesBefore(), so that HuffmanNode
     * objects can be stored directly in a sorted structure (e.g. a
     * PriorityQueue or a sorted List) while building the tree.
     */
    @Override
    public int compareTo(HuffmanTree other) {
        if (this.comesBefore(other)) return -1;
        if (other.comesBefore(this)) return 1;
        return 0;
    }
}
