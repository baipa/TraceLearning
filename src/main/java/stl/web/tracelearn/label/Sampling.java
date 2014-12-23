package stl.web.tracelearn.label;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stl.web.tracelearn.feature.FeatureVec;

public class Sampling {

	private Map<Integer, Integer> labeled;
	private List<Integer> unlabeled;

	private List<FeatureVec> vectors;
	

	private static final Logger LOG = LoggerFactory.getLogger(Sampling.class);

	private static final String TRACE_DIR = "C:/lab/demo.crawljax.com/trace/";
	
	public Sampling() {

		// read feature vectors
		vectors = new ArrayList<>();
		readFile();
		
		// initialize labeled & unlabeled sets
		labeled = new TreeMap<>();
		unlabeled = new ArrayList<Integer>();
		for (FeatureVec v : vectors) {
			unlabeled.add(v.getId());
		}

	}


	private void readFile() {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File(TRACE_DIR, "vector_list")));
			String line;
			while ((line = br.readLine()) != null) {
				FeatureVec v = new FeatureVec(line);
				vectors.add(v);
			}
			br.close();
			
		} catch (IOException e) {
			LOG.error("Cannot read file: vector_list");
		}
	}

	public String getNextTrace() {

		// randomly get next trace
		int id = randAlgo();

		// send trace info to client
		return TRACE_DIR + id + ".json";
	}
	
	public void sendValue(String json, int value){
		//parse the id from file name
		String[] str = json.split("/");
		String[] s = str[str.length - 1].split(".");
		int id = Integer.parseInt(s[0]);
		
		labeled.put(id, value);
		unlabeled.remove(id);
	}

	private int randAlgo(){
		
		Random randomizer = new Random();
		int id = unlabeled.get(randomizer.nextInt(unlabeled.size()));
		return id;
	}
	
}
