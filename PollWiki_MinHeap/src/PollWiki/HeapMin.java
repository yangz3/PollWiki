package PollWiki;

import java.util.Arrays;

public class HeapMin {  
    public int[] Heap;  
    public String[] Name; 
    private int maxsize;  
    private int size;  
  
  
    /*
     * Min Heap
     */
    public HeapMin(int max) {  
        maxsize = max;  
        Heap = new int[maxsize];  
        size = maxsize;  
        Arrays.fill(Heap, Integer.MIN_VALUE);
        
    }  
  
  
    private int leftchild(int pos) {  
        return 2 * pos + 1;  
    }  
  
  
    private int rightchild(int pos) {  
        return 2 * pos + 2;  
    }  
  
    private void swap(int pos1, int pos2) {  
        int tmpI;  
        tmpI = Heap[pos1];  
        Heap[pos1] = Heap[pos2];  
        Heap[pos2] = tmpI;  
        
        String tmpS;  
        tmpS = Name[pos1];  
        Name[pos1] = Name[pos2];  
        Name[pos2] = tmpS; 
    }  
  
  
    public void checkRootAndHeapify(int elem, String name) {  
        
    	if(elem > Heap[0]){
	        Heap[0] = elem;  
	        Name[0] = name; 
	        int current = 0;
	        int toBeSwaped = 0;
	        while (current <= 6 && Heap[current] > Math.min(Heap[leftchild(current)], Heap[rightchild(current)])) { 
	        	toBeSwaped = Heap[leftchild(current)] < Heap[rightchild(current)] ? leftchild(current) : rightchild(current);
	            swap(current, toBeSwaped);  
	            current = toBeSwaped;  
	        }  
    	}
    	
    }  

    public void print() {  
        int i;  
        for (i = 1; i < size; i++)  
            System.out.print(Heap[i] + " ");  
        System.out.println();  
    }  
    

    
    /*
     * Quick Sort
     */
    private int number;

    public void sort() {
      // check for empty or null array
      if (Heap ==null || Heap.length==0){
        return;
      }
      number = Heap.length;
      quicksort(0, number - 1);
    }

    private void quicksort(int low, int high) {
      int i = low, j = high;
      // Get the pivot element from the middle of the list
      int pivot = Heap[low + (high-low)/2];

      // Divide into two lists
      while (i <= j) {
        // If the current value from the left list is smaller then the pivot
        // element then get the next element from the left list
        while (Heap[i] < pivot) {
          i++;
        }
        // If the current value from the right list is larger then the pivot
        // element then get the next element from the right list
        while (Heap[j] > pivot) {
          j--;
        }

        // If we have found a values in the left list which is larger then
        // the pivot element and if we have found a value in the right list
        // which is smaller then the pivot element then we exchange the
        // values.
        // As we are done we can increase i and j
        if (i <= j) {
          exchange(i, j);
          i++;
          j--;
        }
      }
      // Recursion
      if (low < j)
        quicksort(low, j);
      if (i < high)
        quicksort(i, high);
    }

    private void exchange(int i, int j) {
      int temp = Heap[i];
      Heap[i] = Heap[j];
      Heap[j] = temp;
      String tempS = Name[i];
      Name[i] = Name[j];
      Name[j] = tempS;
    }
  
  
}  