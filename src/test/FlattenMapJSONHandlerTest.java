/**
 * Created by Felipe on 21/12/16.
 */

import com.konkerlabs.analytics.ingestion.handler.FlattenMapJSONHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;

public class FlattenMapJSONHandlerTest {

    FlattenMapJSONHandler _flattenMapJSONHandler;
    @Before
    public void runBeforeTests() {
        _flattenMapJSONHandler = new FlattenMapJSONHandler();
    }

    @Test
    public void eventsShouldBeCaught()
    {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn("application/json");

        String testJson = "{\"test\":\"test\"}";
        InputStream stream = new ByteArrayInputStream(testJson.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        try {
            when(request.getReader()).thenReturn(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert(_flattenMapJSONHandler.getEvents(request).size() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
