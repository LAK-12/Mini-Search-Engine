package minisearchengine;

import java.util.ArrayList;
import java.util.HashMap;

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable<V>> ArrayList<K> slowSort (HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

        int N = sortedUrls.size();
        for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) < 0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);					
				}
			}
        }
        return sortedUrls;                    
    }
    
    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
	private static <K, V extends Comparable<V>> void mergesort(ArrayList<K> URLS, HashMap<K, V> results, int leftIndex, int rightIndex) {
		if(leftIndex < rightIndex) {
			int mid = (leftIndex + rightIndex) / 2;
			mergesort(URLS, results, leftIndex, mid);
			mergesort(URLS, results, mid + 1, rightIndex);
			merge(URLS, results, leftIndex, mid, rightIndex);
		}
	}

	private static <K, V extends Comparable<V>> void merge(ArrayList<K> URLS, HashMap<K, V> results, int leftIndex, int mid, int rightIndex) {
		ArrayList<K> list = new ArrayList<>(rightIndex - leftIndex + 1);
		int l = leftIndex;
		int r = mid + 1;

		while (l <= mid && r <= rightIndex) {
			if (results.get(URLS.get(l)).compareTo(results.get(URLS.get(r))) < 0) {
				list.add(URLS.get(r));
				r++;
			} else {
				list.add(URLS.get(l));
				l++;
			}
		}

		while (l <= mid) {
			list.add(URLS.get(l));
			l++;
		}

		while (r <= rightIndex) {
			list.add(URLS.get(r));
			r++;
		}

		for (int i = 0; i < list.size(); i++) {
			URLS.set(leftIndex + i, list.get(i));
		}
	}
	public static <K, V extends Comparable<V>> ArrayList<K> fastSort(HashMap<K, V> results) {
		ArrayList<K> sortedUrls = new ArrayList<>(results.keySet());
		mergesort(sortedUrls, results, 0, sortedUrls.size() - 1);
		return sortedUrls;
	}

}