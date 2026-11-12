
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HuffmanTest {

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0};

        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i],
                    "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testSortFrequencies() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        HuffmanLeaf[] sorted = Huffman.sortFrequencies(freqList);

        assertEquals(256, sorted.length);

        for (int i = 1; i < sorted.length; i++) {
            assertTrue(sorted[i - 1].freq() <= sorted[i].freq(),
                    "frequencies not sorted at index " + i);
        }
    }

    @Test
    public void testCreateHuffTree() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        HuffmanTree root = Huffman.createHuffTree(freqList);

        assertNotNull(root);
        assertEquals(32, root.freq());
    }

    @Test
    public void testCreateCode() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        HuffmanTree root = Huffman.createHuffTree(freqList);
        String[] codes = Huffman.createCode(root);

        assertEquals(256, codes.length);

        for (int i = 0; i < 256; i++) {
            if (freqList[i] > 0) {
                assertFalse(codes[i].isEmpty(),
                        "missing code for ASCII " + i);
            } else {
                assertEquals("", codes[i],
                        "unexpected code at ASCII " + i);
            }
        }
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");

        assertEquals("97 2 98 4 99 8 100 16 102 2 ",
                Huffman.createHeader(freqList));
    }

    @Test
    public void testParseHeader() {
        int[] freqList = Huffman.parseHeader(
                "97 2 98 4 99 8 100 16 102 2");

        int[] ansList = {2, 4, 8, 16, 0, 2, 0};

        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i],
                    "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testHuffmanEncode() throws IOException {
        Huffman.huffmanEncode(
                "src/test/java/file2.txt",
                "src/test/java/encoded.txt");

        String encoded = Files.readString(
                Path.of("src/test/java/encoded.txt"));

        assertTrue(encoded.startsWith(
                "97 2 98 4 99 8 100 16 102 2 \n"));
    }

    @Test
    public void testHuffmanDecode() throws IOException {
        Huffman.huffmanEncode(
                "src/test/java/file2.txt",
                "src/test/java/encoded.txt");

        Huffman.huffmanDecode(
                "src/test/java/encoded.txt",
                "src/test/java/decoded.txt");

        String original = Files.readString(
                Path.of("src/test/java/file2.txt"));

        String decoded = Files.readString(
                Path.of("src/test/java/decoded.txt"));

        assertEquals(original, decoded);
    }

    @Test
    public void testEmptyFile() throws IOException {
        Files.writeString(
                Path.of("src/test/java/empty.txt"), "");

        int[] freqs = Huffman.cntFreq("src/test/java/empty.txt");

        for (int freq : freqs) {
            assertEquals(0, freq);
        }

        Huffman.huffmanEncode(
                "src/test/java/empty.txt",
                "src/test/java/encoded.txt");

        assertEquals("", Files.readString(
                Path.of("src/test/java/encoded.txt")));
    }

    @Test
    public void testSingleCharacter() throws IOException {
        Files.writeString(
                Path.of("src/test/java/single.txt"), "aaaaa");

        Huffman.huffmanEncode(
                "src/test/java/single.txt",
                "src/test/java/encoded.txt");

        assertEquals("97 5 \n", Files.readString(
                Path.of("src/test/java/encoded.txt")));

        Huffman.huffmanDecode(
                "src/test/java/encoded.txt",
                "src/test/java/decoded.txt");
    }
}
