import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.NumberFormatException;
import java.lang.Math;
import java.io.IOException;
import java.util.Arrays;
/**
 * Implementation of problem from book ProgrammingChallenges.
 * To see problem online : http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110201&format=html
 * Input and output as described in problem.
 * This code passed the test although I was not happy with execution time.
 */
public class JollyJumpers {
    
    public static void main(String[] args) throws IOException, NumberFormatException {
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        String line = null;
        while((line = br.readLine()) != null) {
            String[] numbers = line.split("\\s+");
            
            int max = Integer.parseInt(numbers[0]);
            Integer[] inNumber = new Integer[max];
             
            int index = 0;
            for(int idx=1; idx < numbers.length; idx++) {
                int value = Integer.parseInt(numbers[idx]);
                inNumber[index] = value; 
		index++;
	    }
            
            if(inNumber.length == 1) {
                System.out.println("Jolly");
                continue;
	    }
        
 
	    //            Arrays.sort(inNumber);               
            Integer[] diff = new Integer[inNumber.length - 1];
            int firstNum = inNumber[0];
            index = 1;
            int anotherIndex = 0;
            while(index < inNumber.length) {
                int nub = inNumber[index];
                diff[anotherIndex++] = Math.abs(nub - firstNum);
                firstNum = nub; 
                index++;
	    } 

            Arrays.sort(diff); 

            if(diff[0] != 1) {
                System.out.println("Not jolly");
                continue;
	    }
            if(diff[diff.length - 1] != (max -1) ) {
                System.out.println("Not jolly");
                continue;
	    }
            
            int firstDiff = diff[0];
            boolean foundJolly = true;
            for(int idx = 1; idx  < diff.length; idx++) {
                int difference = diff[idx];
                if((difference - firstDiff) != 1 ) {
                    foundJolly = false;
                    break;
		}
                firstDiff = difference;    
	    }
	    

            if(foundJolly)
                System.out.println("Jolly");
            else
                System.out.println("Not jolly"); 
	}          
    }
}