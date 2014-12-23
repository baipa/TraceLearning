package stl.web.tracelearn.feature;

import java.util.*;

import com.crawljax.core.state.Eventable;

public class FeatureVec {

	private List<Eventable> trace;
	private Map<Integer, Integer> featureVector;
	private long NumberOfTotalEdges;
	private int id;
	
	public FeatureVec(List<Eventable> p, long NumberOfEdges, int id){
		trace = p;
		this.id = id;
		NumberOfTotalEdges = NumberOfEdges;
		featureVector = new TreeMap<>();
		addTransition();	//edge transition, index: 0 ~ total # edges - 1
		addTraceInfo();		//# edges, same state, ..., index: total # edges ~
	}
	
	public FeatureVec(String str){
		
		featureVector = new TreeMap<>();
		
		String[] index = str.split(" ");
		
		for(int i = 0; i < index.length; i++){
			if(i == 0){
				id = Integer.valueOf(index[i]);
			}
			else{
			String[] key = index[i].split(":");
			featureVector.put(Integer.valueOf(key[0]), Integer.valueOf(key[1]));
			}
		}
	}
	
	private void addTraceInfo() {
		featureVector.put((int) NumberOfTotalEdges, trace.size());
		featureVector.put((int) NumberOfTotalEdges + 1, hasSameState());
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
	
	public int getId(){
		return id;
	}
	
	public List<Eventable> getTrace(){
		return trace;
	}
	
	public Map<Integer, Integer> getVector(){
		return featureVector;
	}
	
	public String toString(){
		String str = id + " ";
		for(Integer key : featureVector.keySet()){
			str += key + ":" + featureVector.get(key) + " ";
		}
		
		return str;
	}
	
}
