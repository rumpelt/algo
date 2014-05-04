import java.lang.NumberFormatException;
/**
 * QuickSelect to select Kth smallet number.
 * Input the number on the command line with the last number as the Kth number to be selected
 * e.g java QuickSelect 2 5 4 3  1
 * above will select 1st smallest number from 2 5 4 3 i.e 2
 * 
 */
public class QuickSelect {
    public static void swap(Integer[] values, int findex, int sindex) {
        if(findex == sindex)
            return;
        int temp = values[findex];
        values[findex] = values[sindex];
        values[sindex] = temp;
    }

    public static int partition(Integer[] values , int low, int high) {
        int pivot = high;
        int highIndex = low;
        for(int index = low; index <= high; index++) {
            if(values[index] < values[pivot]) {
                swap(values, index, highIndex); 
                highIndex++;  
            } 
        }

        swap(values, highIndex, pivot);
        return highIndex;

    }

    /**
     * Recursive version
     */
    public static int qSelectRecursive(Integer[] values, int low, int high, int k) {
        if(low > high)
            return -1;
        int pivot = partition(values, low, high);
        if(pivot == k - 1)
            return values[pivot];
        else if(pivot < k - 1)
            return qSelectRecursive(values, pivot + 1, high, k);
        else
            return qSelectRecursive(values, low, pivot -1, k);  
        
    }
    /**
     * Non recursive version
     */
    public static int qSelect(Integer[] values, int low, int high, int k) {
        while(low <= high) {
            int pivot = partition(values, low, high);
            if(pivot == k - 1) 
                return values[pivot];
            if(pivot < k - 1) {
                low = pivot + 1;
            }
            else
                high = pivot - 1;
        }
        return -1;   
    }

    public static void main(String[] args) throws NumberFormatException{
        
        Integer[] values = new Integer[args.length -1];
        for(int index = 0; index < args.length - 1; index++) {
            values[index] = Integer.parseInt(args[index]);
        }
        int select = Integer.parseInt(args[args.length - 1]);
        System.out.println(qSelectRecursive(values, 0, values.length - 1, select));
    }
}