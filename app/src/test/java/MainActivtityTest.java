import android.content.Context;
import android.test.AndroidTestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by VIanoshchuk on 16.03.2015.
 */


public class MainActivtityTest extends AndroidTestCase {
    @Mock
    Context context;

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


}