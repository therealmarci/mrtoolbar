package at.marcelr.mrtoolbar.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Must match Minecraft's FriendlyByteBuf, otherwise mod and plugin cannot talk. */
class WireTest {

    @Test
    void varIntsLikeMinecraft() {
        assertArrayEquals(new byte[]{0}, new Wire.Writer().varInt(0).bytes());
        assertArrayEquals(new byte[]{127}, new Wire.Writer().varInt(127).bytes());
        assertArrayEquals(new byte[]{(byte) 0x80, 0x01}, new Wire.Writer().varInt(128).bytes());
        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x0F},
                new Wire.Writer().varInt(-1).bytes());
    }

    @Test
    void roundTrip() throws IOException {
        byte[] data = new Wire.Writer().varInt(300).string("0.2.0+mc26.3 äö").varInt(-1).bytes();
        Wire.Reader reader = new Wire.Reader(data);
        assertEquals(300, reader.varInt());
        assertEquals("0.2.0+mc26.3 äö", reader.string());
        assertEquals(-1, reader.varInt());
        assertFalse(reader.hasMore());
    }
}
