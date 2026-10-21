package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.Compatibility.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompatibilityTest {

    @Test
    void protocolOutsideTheRangeIsRejected() {
        assertEquals(Status.MOD_TOO_OLD, Compatibility.check(Compatibility.MIN_PROTOCOL - 1, "9.9.9", ""));
        assertEquals(Status.MOD_TOO_NEW, Compatibility.check(Compatibility.MAX_PROTOCOL + 1, "0.0.1", ""));
        assertEquals(Status.OK, Compatibility.check(Compatibility.MAX_PROTOCOL, "0.2.0+mc26.3", ""));
    }

    @Test
    void minimumVersionFromTheConfig() {
        int protocol = Compatibility.MAX_PROTOCOL;
        assertEquals(Status.MOD_TOO_OLD, Compatibility.check(protocol, "0.2.0+mc26.3", "0.2.1"));
        assertEquals(Status.OK, Compatibility.check(protocol, "0.2.1+mc26.3", "0.2.1"));
        assertEquals(Status.OK, Compatibility.check(protocol, "mc26.3-v0.3.0", "0.2.1"));
        // A pre-release of the minimum version is older than the version itself.
        assertEquals(Status.MOD_TOO_OLD, Compatibility.check(protocol, "0.2.1-rc.1", "0.2.1"));
    }
}
