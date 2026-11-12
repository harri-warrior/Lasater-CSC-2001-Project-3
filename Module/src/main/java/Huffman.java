import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Project 3: Huffman Encoding / Decoding.
 *
 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.
 *
 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /** Number of possible 8-bit character values. */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     */
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];
        Path filePath = Path.of(filename);
        String content = Files.readString(filePath);
        int index = 0;
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            index = (int) ch;
            freq[index]++;
        }
        return freq;
    }

    public static HuffmanLeaf[] sortFrequencies(int[] freq) {
        HuffmanLeaf[] sorted = new HuffmanLeaf[256];
        int back = 0;
        for (int i = 0; i < freq.length; i++) {
            sorted[back] = new HuffmanLeaf(freq[i], (char) i);
            if (back != 0) {
                for (int c = back; c > 0; c--) {
                    if (sorted[c-1].freq() > sorted[c].freq()) {
                        HuffmanLeaf placeholder = sorted[c-1];
                        sorted[c-1] = sorted[c];
                        sorted[c] = placeholder;
                    }
                }
            }
            back++;
        }

        HuffmanLeaf[] trimmed = new HuffmanLeaf[back];
        for (int i = 0; i < back; i++) {
            trimmed[i] = sorted[i];
        }
        return trimmed;
    }

    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     *
     * Use an HQueue to manage the collection of
     * not-yet-combined nodes as you build the tree.
     *
     * Special cases:
     *  - If freq contains no non-zero counts (empty input file), return null
     *  - If freq contains exactly one non-zero count (input file has only
     *    one unique character), return a single leaf HuffmanNode for that
     *    character (no combine() call needed).
     */


    public static HuffmanTree createHuffTree(int[] freq) {
        ArrayList<HuffmanTree> tree = new ArrayList<>();
        HuffmanLeaf[] sortedFreqs = sortFrequencies(freq);

        for (HuffmanLeaf leaf : sortedFreqs) {
            if (leaf.freq() > 0) {
                tree.add(leaf);
            }
        }

        while (tree.size() > 1) {

            HuffmanTree smallest1 = tree.remove(0);
            HuffmanTree smallest2 = tree.remove(0);

            HuffmanNode parent = new HuffmanNode(
                    smallest2.charAscii(),
                    smallest1.freq() + smallest2.freq(),
                    smallest1,
                    smallest2
            );

            boolean isConditionTrue = true;
            int i = 0;

            while (i < tree.size() && tree.get(i).freq() < parent.freq()) {
                i++;
            }

            tree.add(i, parent);
        }

        if (tree.isEmpty()) {
            return null;
        }
        return tree.getFirst();
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     *
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     */
    public static String[] createCode(HuffmanTree root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");

        buildCodes(root, "", codes);

        return codes;
    }

    private static void buildCodes(HuffmanTree node, String code, String[] codes) {
        if (node == null) {
            return;
        }

        if (node instanceof HuffmanLeaf leaf) {
            codes[(int) leaf.charASCII()] = code;
        } else if (node instanceof HuffmanNode internal) {
            buildCodes(internal.left(), code + "0", codes);
            buildCodes(internal.right(), code + "1", codes);
        }
    }

//    private static void buildCodes(HuffmanTree node, String code, String[] codes) {
////        switch (node) {
////            case HuffmanNode(int charAscii, int freq, HuffmanNode left, HuffmanNode right):
////                buildCodes(left, code + "0", codes);
////                buildCodes(right, code + "1", codes);
////                break;
////
////            case HuffmanLeaf(int freq, char charASCII):
////                codes[charASCII] = code;
////                break;
////
////            default:
////                return;
////        }
//    }

    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     *
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     */
    public static String createHeader(int[] freqs) {
        String header = "";
        int index = 0;
        for (int freq: freqs) {
            if (freq != 0) {
                header = header + index + " " + freq + " ";
            }
            index++;
        }
        return header;
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     *   1. A header line (see createHeader), followed by a newline
     *   2. The Huffman-encoded contents of inFile, using the codes from
     *      createCode() -- UNLESS inFile has only one unique character,
     *      see below.
     * If outFile already exists, its old contents are overwritten.
     *
     * Special cases:
     *  - Empty input file: outFile should end up empty too
     *  - Input file with only one unique character: should contain only the
     *    header line (e.g. "97 5") and nothing else - no second line of code bits.
     */
    public static void huffmanEncode(String inFile, String outFile) throws IOException {
        int[] freqs = cntFreq(inFile);

        int unique = 0;
        for (int freq : freqs) {
            if (freq != 0) {
                unique++;
            }
        }

        if (unique == 0) {
            new FileWriter(outFile).close();
            return;
        }

        String header = createHeader(freqs);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        writer.write(header);
        writer.newLine();

        if (unique == 1) {
            writer.close();
            return;
        }

        HuffmanTree root = createHuffTree(freqs);
        String[] codes = createCode(root);

        BufferedReader reader = new BufferedReader(new FileReader(inFile));

        int c;
        while ((c = reader.read()) != -1) {
            writer.write(codes[c]);
        }

        reader.close();
        writer.close();
    }

    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     *
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     */
    public static int[] parseHeader(String headerString) {
        int[] freqs = new int[NUM_CHARS];
        String[] parts = headerString.split(" ");

        for (int i = 0; i < parts.length; i += 2) {
            int index = Integer.parseInt(parts[i]);
            int freq = Integer.parseInt(parts[i + 1]);
            freqs[index] = freq;
        }
        return freqs;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     *
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     *
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     */
    public static void huffmanDecode(String encodedFile, String decodeFile) throws IOException {
        String content = Files.readString(Path.of(encodedFile));

        if (content.isEmpty()) {
            Files.writeString(Path.of(decodeFile), "");
            return;
        }

        int newline = content.indexOf('\n');
        String header = content.substring(0, newline).trim();
        String bits = content.substring(newline + 1).trim();

        int[] freqs = parseHeader(header);
        HuffmanTree root = createHuffTree(freqs);

        HuffmanTree current = root;

        String decoded = "";

        for (int i = 0; i < bits.length(); i++) {

            if (bits.charAt(i) == '0') {
                current = ((HuffmanNode) current).left();
            } else {
                current = ((HuffmanNode) current).right();
            }

            if (current instanceof HuffmanLeaf leaf) {
                decoded += leaf.charASCII();
                current = root;
            }
        }

        Files.writeString(Path.of(decodeFile), decoded);
    }
}
