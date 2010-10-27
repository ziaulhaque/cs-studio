package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.rdb.RDBArchive;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the engine model
 *  <p>
 *  RDBArchive configuration (schema) might need info from
 *  Eclipse preferences, hence Plug-in test.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineModelHeadlessTest
{
    @Test
    public void testReadConfig() throws Exception
    {
    	final TestProperties settings = new TestProperties();
    	final String url = settings.getString("archive_rdb_url");
    	if (url == null)
    	{
    		System.out.println("Skipping, no archive test settings");
    		return;
    	}
    	final String user = settings.getString("archive_rdb_user");
    	final String password = settings.getString("archive_rdb_password");
    	final String config = settings.getString("archive_config");
    	final int port = settings.getInteger("archive_port", 4812);
    	
        final RDBArchive rdb = RDBArchive.connect(url, user, password);
        final BenchmarkTimer timer = new BenchmarkTimer();
        final EngineModel model = new EngineModel(rdb);
        model.readConfig(config, port);
        timer.stop();
        final int count = model.getChannelCount();
        System.out.println("Channel count: " + count);
        System.out.println("Runtime      : " + timer);
        System.out.println("Channels/sec : " + count/timer.getSeconds());
        rdb.close();
        assertTrue(count > 0);
    }
}