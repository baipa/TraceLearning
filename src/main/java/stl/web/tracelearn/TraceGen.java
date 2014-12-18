package stl.web.tracelearn;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.ExitNotifier.ExitStatus;
import com.crawljax.core.plugin.HostInterface;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateFlowGraph;
import com.crawljax.plugins.crawloverview.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TraceGen implements PostCrawlingPlugin {

	private File OUTPUT_DIR;
	
	private List<FeatureVec> vectors;
	private OutPutModel result;

	private static final Logger LOG = LoggerFactory.getLogger(TraceGen.class);

	public TraceGen(HostInterface hostInterface) {
		OUTPUT_DIR = hostInterface.getOutputDirectory();
	}

	@Override
	public void postCrawling(CrawlSession session, ExitStatus exitReason) {

		// make testcases dir
		File trace_dir = new File(OUTPUT_DIR, "trace");
		boolean created = trace_dir.mkdir();
		checkArgument(created, "Could not create trace dir");

		//add edge id
		long id = 1;
		Builder<Integer, Edge> builder = ImmutableMap.builder();
		for (Eventable eventable : session.getStateFlowGraph().getAllEdges()) {
			eventable.setId(id);
			builder.put((int) id, new Edge(eventable));
			id++;
		}
		ImmutableMap<Integer, Edge> edges = builder.build();
		
		//handle state flow graph
		StateFlowGraph g = session.getStateFlowGraph();
		LOG.info("Number of States: " + g.getNumberOfStates());
		
		Collection<List<Eventable>> testcases = session.getCrawlPaths();
		
		
		//transfrom trace to vectors, add into list
		vectors = new ArrayList<>();
		for (List<Eventable> p : testcases) {
			FeatureVec v = new FeatureVec(p, id);
			vectors.add(v);
		}
		
		
		//read json
		try {
			result = Serializer.read(new File(OUTPUT_DIR, "result.json"));
		} catch (IOException e) {
			LOG.error("Cannot read result.json");
		}
		
		//do sampling & active learning
		Sampling sampling = new Sampling(vectors, result.getStates(), edges);
		sampling.getNextTrace();
		
		
		
		
		
		
	}

	
	
}
