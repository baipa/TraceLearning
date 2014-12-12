package stl.web.tracelearn;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.core.state.Eventable;
import com.crawljax.plugins.crawloverview.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Sampling {

	private Map<Integer, Integer> labeled;
	private List<Integer> unlabeled;
	
	private List<FeatureVec> vectors;
	private ImmutableMap<String, State> states;
	private ImmutableMap<Integer, Edge> edges;
	
	private static final Logger LOG = LoggerFactory.getLogger(Sampling.class);
	
	public Sampling(List<FeatureVec> vectors, ImmutableMap<String, State> state, ImmutableMap<Integer, Edge> edge){
		this.vectors = vectors;
		
		//initialize labeled & unlabeled sets
		labeled = new TreeMap<>();
		unlabeled = new ArrayList<Integer>();
		for(int i = 0; i < vectors.size(); i = i + 1){
			unlabeled.add(i);
		}
		
		//get json
		states = state;
		edges = edge;
	}
	
	public void getNextTrace(){
		
		//randomly get next trace
		Random randomizer = new Random();
		int id = unlabeled.get(randomizer.nextInt(unlabeled.size()));
		
		//send trace info to client
		String s = getStateJson(vectors.get(id).getTrace());
		String e = getEdgeJson(vectors.get(id).getTrace());
		
		LOG.info("State JSON: " + s);
		LOG.info("Edge JSON: " + e);
		
		//get user label value
		int label_value = 1;
		
		
		
		labeled.put(id, label_value);
		unlabeled.remove(id);
	}
	
	private String getStateJson(List<Eventable> trace){
		Builder<String, State> builder = ImmutableMap.builder();
		builder.put("index", states.get("index"));
		for(Eventable e : trace){
			String key = e.getTargetStateVertex().getName();
			builder.put(key, states.get(key)) ;
		}
		
		return Serializer.toPrettyJson(builder.build());
	}
	
	private String getEdgeJson(List<Eventable> trace){
		ImmutableList.Builder<Edge> builder = ImmutableList.builder();
		for(Eventable e : trace){
			builder.add(edges.get((int) e.getId()));
		}
		return Serializer.toPrettyJson(builder.build());
	}
}
