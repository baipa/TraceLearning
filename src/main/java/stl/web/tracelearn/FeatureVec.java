package stl.web.tracelearn;

import java.util.*;

import com.crawljax.core.state.Eventable;

public class FeatureVec {

	private List<Eventable> trace;
	private Map<Integer, Integer> featureVector;
	private long NumberOfTotalEdges; 
	
	public FeatureVec(List<Eventable> p, long NumberOfEdges){
		trace = p;
		NumberOfTotalEdges = NumberOfEdges;
		featureVector = new TreeMap<>();
		addTransition();	//edge transition, index: 0 ~ total # edges - 1
		addTraceInfo();		//# edges, same state, ..., index: total # edges ~
	}
	
	private void addTraceInfo() {
		featureVector.put((int) NumberOfTotalEdges, trace.size());
		featureVector.put(hasSameState(), trace.size() + 1);
	}

	private Integer hasSameState() {
		List<Integer> state = new ArrayList<Integer>();
		state.add(0);
		for(Eventable e : trace){
			state.add(e.getTargetStateVertex().getId());
		}
		Set<Integer> set = new HashSet<Integer>(state);
		
		if(set.size() < state.size()){
			return 1;
		}else{
			return 0;
		}
	}

	private void addTransition() {

		for(Eventable e : trace){
			featureVector.put((int) e.getId(), 1);
		}
	}
	
	public List<Eventable> getTrace(){
		return trace;
	}
	
	public Map<Integer, Integer> getVector(){
		return featureVector;
	}
	
	public String toString(){
		String str = "";
		for(Integer key : featureVector.keySet()){
			str += key + ":" + featureVector.get(key) + " ";
		}
		
		return str;
	}
	
}
