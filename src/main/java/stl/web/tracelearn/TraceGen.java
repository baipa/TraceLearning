package stl.web.tracelearn;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stl.web.tracelearn.feature.FeatureVec;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.ExitNotifier.ExitStatus;
import com.crawljax.core.plugin.HostInterface;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateFlowGraph;
import com.crawljax.plugins.crawloverview.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TraceGen implements PostCrawlingPlugin {

	private File OUTPUT_DIR;
	private File TRACE_DIR;
	
	private List<FeatureVec> vectors;

	private ImmutableMap<String, State> states;
	private ImmutableMap<Integer, Edge> edges;
	
	private static final Logger LOG = LoggerFactory.getLogger(TraceGen.class);

	public TraceGen(HostInterface hostInterface) {
		OUTPUT_DIR = hostInterface.getOutputDirectory();
	}

	@Override
	public void postCrawling(CrawlSession session, ExitStatus exitReason) {

		// make testcases dir
		TRACE_DIR = new File(OUTPUT_DIR, "trace");
		boolean created = TRACE_DIR.mkdir();
		checkArgument(created, "Could not create trace dir");

		//add edge id & edge json
		long id = 1;
		Builder<Integer, Edge> builder = ImmutableMap.builder();
		for (Eventable eventable : session.getStateFlowGraph().getAllEdges()) {
			eventable.setId(id);
			builder.put((int) id, new Edge(eventable));
			id++;
		}
		edges = builder.build();
		
		//get states json
		try {
			states = Serializer.read(new File(OUTPUT_DIR, "result.json")).getStates();
		} catch (IOException e) {
			LOG.error("Cannot read result.json");
		}
		
		
		//handle state flow graph
		StateFlowGraph g = session.getStateFlowGraph();
		LOG.info("Number of States: " + g.getNumberOfStates());
		
		Collection<List<Eventable>> testcases = session.getCrawlPaths();
		
		
		//transform trace to vectors, add into list
		vectors = new ArrayList<>();
		int i = 1;
		for (List<Eventable> p : testcases) {
			FeatureVec v = new FeatureVec(p, id, i);
			vectors.add(v);
			
			String s = "{\n  \"states\" : " + getStateJson(p)
					+ ",\n  \"edges\" : " + getEdgeJson(p) + "\n}";
			
			writeFile(i + ".json", s);
			i++;
		}
		
		//feature extraction/selection
		
		
		//write vector_list file
		String vecstr = vecListToString();
		writeFile("vector_list", vecstr);
		
	}

	private void writeFile(String filename, String str){
		try {
			FileWriter out = new FileWriter(new File(TRACE_DIR, filename));
			out.write(str);
			out.flush();
			out.close();
		} catch (IOException e) {
			LOG.error("Cannot write file: " + filename);
		}
	}
	
	private String vecListToString(){
		String vecstr = "";
		for(FeatureVec v : vectors){
			vecstr += v.toString() + "\n";
		}
		return vecstr;
	}
	
	private String getStateJson(List<Eventable> trace) {
		Builder<String, State> builder = ImmutableMap.builder();
		List<String> names = new ArrayList<>();
		
		builder.put("index", states.get("index"));
		names.add("index");
		for (Eventable e : trace) {
			String key = e.getTargetStateVertex().getName();
			if(!names.contains(key)){
				builder.put(key, states.get(key));
				names.add(key);
			}
		}

		return Serializer.toPrettyJson(builder.build());
	}

	private String getEdgeJson(List<Eventable> trace) {
		ImmutableList.Builder<Edge> builder = ImmutableList.builder();
		for (Eventable e : trace) {
			builder.add(edges.get((int) e.getId()));
		}
		return Serializer.toPrettyJson(builder.build());
	}
	
	
}
