package stl.web.tracelearn;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.plugin.HostInterfaceImpl;
import com.crawljax.plugins.crawloverview.CrawlOverview;

public class Runner {

	private static final long WAIT_TIME_AFTER_EVENT = 200;
	private static final long WAIT_TIME_AFTER_RELOAD = 20;
	private static final String URL = "http://demo.crawljax.com/";
	private static final String OUTPUT_DIR = "C:/lab/demo.crawljax.com/";

	public static void main(String[] args) throws IOException {
		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration
				.builderFor(URL);
		
		builder.setOutputDirectory(new File(OUTPUT_DIR));

		File out = new File(OUTPUT_DIR);
		if (out.exists()) {
			FileUtils.deleteDirectory(out);
		}

		// click these elements
		builder.crawlRules().clickDefaultElements();

		builder.crawlRules().crawlHiddenAnchors(true);
		// builder.crawlRules().insertRandomDataInInputForms(true);

		// Set timeouts
		builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD,	TimeUnit.MILLISECONDS);
		builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);


		// We want to use two browsers simultaneously.
		builder.setBrowserConfig(new BrowserConfiguration(BrowserType.FIREFOX, 1));
		builder.addPlugin(new CrawlOverview(), new TraceGen(new HostInterfaceImpl(out, null)));

		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		crawljax.call();
	}
}
