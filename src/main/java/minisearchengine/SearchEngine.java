package minisearchengine;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, ArrayList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does an exploration of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {

		if(internet.getVisited(url)) {
			return;
		}

		internet.setVisited(url, true);
		internet.addVertex(url);

		ArrayList<String> data = parser.getContent(url);

		for(String word : data) {
			String foundWord = word.toLowerCase();
			if(!wordIndex.containsKey(foundWord)) {
				wordIndex.put(foundWord, new ArrayList<>()); //?
			}
			if(!wordIndex.get(foundWord).contains(url)) {
				wordIndex.get(foundWord).add(url);
			}
		}


		ArrayList<String> hyperLinks = parser.getLinks(url);
		for(String link : hyperLinks) {
			internet.addVertex(link);
			internet.addEdge(url, link);
			crawlAndIndex(link);

		}
	}



	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		ArrayList<String> vertices = new ArrayList<>(internet.getVertices());
		ArrayList<Double> pageRanks = computeRanks(vertices);

		boolean converged = false;
		for(int i= 0; i < vertices.size(); i++) {
			pageRanks.add(1.0); // set all pr(vi) to 1
			internet.setPageRank(vertices.get(i),1.0);
		}

		while (!converged) {
			ArrayList<Double> updatedPageRanks = new ArrayList<>(pageRanks);

			for (int i = 0; i < vertices.size(); i++) {
				String vertex = vertices.get(i);
				double total = 0.0;


				ArrayList<String> inVertices = internet.getEdgesInto(vertex);
				for (String inVertex : inVertices) {
					int outDegree = internet.getOutDegree(inVertex);
					double pr_w = pageRanks.get(vertices.indexOf(inVertex));

					total += (pr_w / outDegree);

				}

				double pr_v = (1-0.5) + (0.5) * (total);
				updatedPageRanks.add(i,pr_v);
			}

			converged = true;
			for (int i = 0; i < vertices.size(); i++) {
				if (Math.abs(updatedPageRanks.get(i) - pageRanks.get(i)) >= epsilon) {
					converged = false;
					break;
				}
			}

			// Update page ranks for the next iteration
			pageRanks = updatedPageRanks;
		}

		// Assign final page ranks to vertices in the web graph
		for (int i = 0; i < vertices.size(); i++) {
			String vertex = vertices.get(i);
			internet.setPageRank(vertex, pageRanks.get(i));
		}

	}


	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls.
	 * Note that the double in the output list is matched to the url in the input list using
	 * their position in the list.
	 *
	 * This method will probably fit in about 20 lines.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		ArrayList<Double> pageRank = new ArrayList<>();


		for(int i = 0; i < vertices.size(); i++) {
			String vertex = vertices.get(i);
			double sum = 0.0;
			ArrayList<String> inVertices = internet.getEdgesInto(vertex);

			for (String inVertex : inVertices) {
				double pr_w = internet.getPageRank(inVertex);
				int outDegree = internet.getOutDegree(inVertex);


				sum += (pr_w / outDegree);

			}

			double pr_v = (1-0.5) + (0.5*sum);
			pageRank.add(i, pr_v);
		}
		return pageRank;
	}

	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method will probably fit in about 10-15 lines.
	 */
	public ArrayList<String> getResults(String query) {
		String q = query.toLowerCase();
		ArrayList<String> result = new ArrayList<>();

		if (wordIndex.containsKey(q)){
			ArrayList<String> queryURL = wordIndex.get(q);

			HashMap<String, Double> ranks = new HashMap<>();
			for (String url : queryURL) {
				double rank = internet.getPageRank(url);
				ranks.put(url, rank);
			}

			ArrayList<String> orderedURLS = Sorting.fastSort(ranks); // Use external fastSort method
			result.addAll(orderedURLS);
		}

		return result;

	}
}
