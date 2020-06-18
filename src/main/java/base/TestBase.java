package base;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.ReadTestProperties;

public class TestBase {
	
	protected final String URL = ReadTestProperties.getProperties("URL");
	protected Logger log = LogManager.getLogger();

}
