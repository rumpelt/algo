import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * SpreadSheet formulae calculator
 * Cells in spreadsheet are arthimetic expressions in RPN notaion such as  " 3 4 + " for "3 + 4"
 * Cells containing expression can refer to other cell for example "A1 4 + ". This is sum of content in A1 cell of spreadsheet with
 * 4. 
 * It also detects cyclic references.
 * I will updae later on sample input and output
 * As reported it currently fails for big spreadsheet file :(. But the idea of using stack and recursive evaluation is good.
 * to compilte : javac SpreadSheet.java
 * to run : cat <spreadsheet_filename> | java SpreadSheet
 * 
 */
public class SpreadSheet {
    public enum ROWNAMES {A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z};
    public enum EXPTYPE {REFERENCE, OPERATOR, VALUE};
    Cell[][] sheet;
    static int numRow; // number of rows in spreadsheet
    static int numCol; // Number of columns in spreadsheet
    public SpreadSheet() {
        this.sheet = new Cell[SpreadSheet.numRow][SpreadSheet.numCol];
    }
  
    public static class Cell {
        List<Expression> expList; // A cell has list of expression (opertor/Reference/Value), It will always have one expression (The value expression);
         
        int rowIndex;  // The row number of cell in spreadsheet, this starts from zero
        int colIndex;  // the col number of cell in spreadsheet, this start from zero
        int cellId; // a cell is the basically the index in single dimesnional array of cells represent by this spreadsheet. We use it for debug purpose
        double cellValue; // The value of cell when it evaluated completely
        boolean noReference; // flag when this cell does not contain any reference
        public Cell() {
            this.expList = new ArrayList<Expression>(SpreadSheet.numCol);
            this.noReference = true; // this is set to false when we find there is reference in this cell
        }

        public static class  Expression {
            EXPTYPE expType;
            int rowIndex; // If this expression is reference then this points to Cell.rowIndex.
            int colIndex; // If this expression is reference then this points to Cell.colIndex.
            String exp; // The acutal expression when we read from input
            double value; // The expression may be evaluated to some value later on
        
        };
       
        /**
         * This is very simple evaluation method coded initially, this is  redundant code.
         * As the Cell.applyOperator is more suitable. 
         */
        public double evaluateSimple(Expression operator, double fOperand, double sOperand) {
            if(operator.exp.equals("*")) {
                return fOperand * sOperand;
            }
            else if(operator.exp.equals("/")) {
                return fOperand/sOperand; 
            }
            else if(operator.exp.equals("+")) {
                return fOperand + sOperand;          
            }
            else{
                return fOperand - sOperand;
            }
        }

        /**
         * Applies an operator to two expressions
         */
        public double applyOperator(Expression operator, Expression fOperand, Expression sOperand) {
            if(operator.exp.equals("*")) {
                return fOperand.value * sOperand.value;
            }
            else if(operator.exp.equals("/")) {
                return fOperand.value /sOperand.value; 
            }
            else if(operator.exp.equals("+")) {
                return fOperand.value + sOperand.value;          
            }
            else{
                return fOperand.value - sOperand.value;
            }
        }
 
        /**
         * When we know that a cell has no refrence then we evaluate this quickly
         */
        public void evaluateSimpleExpression() {
            Deque<Double> stack = new ArrayDeque<Double>(this.expList.size());
            for(Expression exp : this.expList) {
                if(exp.expType == EXPTYPE.OPERATOR) {
                    double  secondOp = stack.pop();
                    double firstOp = stack.pop();
                    double result = this.evaluateSimple(exp, firstOp, secondOp);
                    stack.push(result);
                } else {
                    stack.push((double)exp.value);
                } 
            }
            this.cellValue = stack.pop();
            this.expList.clear(); 
            Expression result = new Expression();
            result.expType = EXPTYPE.VALUE;
            result.value = this.cellValue;
            this.expList.add(result);
            this.noReference = true;
        }

        /**
         * the main recursive call which resolve reference recursively
         * visitedCells : This set is there to keep track for cyclic references. We keep on updating this to see if have visited a cell earlier.
         */
        public Expression recursiveEvaluation(Set<Integer> visitedCells, SpreadSheet spreadSheet) {
            if(visitedCells.contains(this.cellId)) {
                System.out.println("Cyclic dependency found ");
                System.exit(-1); 
            }
            visitedCells.add(this.cellId);

            Deque<Expression> stack = new ArrayDeque<Expression>(this.expList.size());  // A stack to process the RPN expression, On stack the expression will be either A reference or value
            for(Expression exp : this.expList) { 
                if(exp.expType == EXPTYPE.OPERATOR) {
                    // we found an operator, pop the two opearand on stack
                    Expression  secondOp = stack.pop(); 
                    if(secondOp.expType == EXPTYPE.REFERENCE) {
                        Cell cell = spreadSheet.sheet[secondOp.rowIndex][secondOp.colIndex];
                        secondOp = cell.recursiveEvaluation(visitedCells, spreadSheet);
                    }

                    Expression firstOp = stack.pop();
                    if(firstOp.expType == EXPTYPE.REFERENCE) {
                        Cell cell = spreadSheet.sheet[firstOp.rowIndex][firstOp.colIndex];
                        firstOp = cell.recursiveEvaluation(visitedCells, spreadSheet);
                    }

                    if(firstOp != null && secondOp != null) {
                        double result = this.applyOperator(exp, firstOp, secondOp);
                        Expression resExp = new Expression();
                        resExp.expType = EXPTYPE.VALUE;
                        resExp.value = result;
                        stack.push(resExp);
                    }
                    else 
                        return null;
                } else if(exp.expType == EXPTYPE.REFERENCE) {
                    // we found a reference, resolve it and push it on stack
                    Cell cell = spreadSheet.sheet[exp.rowIndex][exp.colIndex]; // get the cell corresponding to this reference
                    Expression result = cell.recursiveEvaluation(visitedCells, spreadSheet);
                    if(result != null) {
                        stack.push(result); 
                    }
                }
                else {
                    stack.push(exp); // ok we found an expression which is value just push on the stack
                }            
            }

            
            if(stack.size() == 1) {
                // There was one value on stack an so this should be a value we have evaluated
                 Expression result  = stack.pop(); 
                if(result.expType == EXPTYPE.VALUE) {
                    if(!this.noReference) { 
                        // Ok this is first time we are resolving this cell completely
                        this.expList.clear(); // clear the expression list of cell
                        this.cellValue = result.value; // update cell value
                        this.expList.add(result); // Push a single expression which is value on cell
                        this.noReference = true; // There are no more references in the cell
                    } 
                    return result; // Return the result
                }
                else
                    return null;
            }
            return null;
        }
        
        /**
         * This is while we are reading spreadsheet and building the dataset
         * If we find and expression which has no reference then we evaluate immediately
         */        
        public void immediateEvaluation() {
            if(this.noReference) {
                this.evaluateSimpleExpression();
            }
        }

        public Cell parseExpression(String expression) {
            String[] expSplit = expression.split("\\s+");
            for(String exp : expSplit) {
                if(exp.equalsIgnoreCase("+") ||  exp.equalsIgnoreCase("-")  || exp.equalsIgnoreCase("/")  || exp.equalsIgnoreCase("*") ) {
                   Cell.Expression operator = new Cell.Expression();
                   operator.expType = EXPTYPE.OPERATOR;
                   operator.exp = exp;
                   this.expList.add(operator);
                }
                else {
                    try {
                        int value = Integer.parseInt(exp);
                        Cell.Expression primitive = new Cell.Expression();
                        primitive.expType = EXPTYPE.VALUE;
                        primitive.exp = exp;
                        primitive.value = value;
                        this.expList.add(primitive);
                   } catch(NumberFormatException ne) {
                       Cell.Expression reference = new Cell.Expression();
                       this.noReference = false;
                       reference.expType = EXPTYPE.REFERENCE;
                       reference.exp = exp;
                       SpreadSheet.decodeCellIndex(reference, reference.exp);
                       this.expList.add(reference);
                    }
                }
            }
            this.immediateEvaluation(); // If this has no reference then this will evaluate soon 
            return this;
        }
    };

    /**
     * If a Expression is reference then Expression.rowIndex and Expression.colIndex should be * update to reflect the cell in sheet;
     */  
    public static void decodeCellIndex(Cell.Expression exp, String cellString) {
        int index = 0;
        String row = cellString.substring(0, 1);
        exp.rowIndex = ROWNAMES.valueOf(row).ordinal(); // row value from 0
        int colNum = Integer.parseInt(cellString.substring(1));  // colnum from 1;
        exp.colIndex = colNum -1;
    }
    
    /**
     * This where we calculate the cell values;
     */
    public void decodeSheet() {
        boolean allDecoded = false; // set to false to enter loop once
        Set<Integer> visitedSet = new HashSet<Integer>();
        while(!allDecoded) {
            allDecoded = true; // We set it to true assuming that every cell value has been claculated.
            for(int rowIdx = 0; rowIdx < SpreadSheet.numRow; rowIdx++) {
                for(int colIdx = 0; colIdx < SpreadSheet.numCol; colIdx++) {
                 Cell cell = this.sheet[rowIdx][colIdx];
                 if(!cell.noReference ) {
                     allDecoded = false; // This cell had reference and so we need to loop once more
                     visitedSet.clear();
                     cell.recursiveEvaluation(visitedSet, this);
                    
                  }
                }
            }
        }
        // Print out the results
        System.out.println(SpreadSheet.numCol+ " " +SpreadSheet.numRow);
        for(int rIdx = 0; rIdx < SpreadSheet.numRow; rIdx++) {
            for(int cIdx = 0; cIdx < SpreadSheet.numCol; cIdx++) {
                System.out.println(String.format("%.5f",this.sheet[rIdx][cIdx].cellValue)); 
            } 
        }
     }

    public static void main(String[] args) throws IOException{
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String[] dimension = br.readLine().split("\\s+");
        SpreadSheet.numRow = Integer.parseInt(dimension[1]);
        SpreadSheet.numCol = Integer.parseInt(dimension[0]);
        //System.out.println("num rows " + SpreadSheet.numRow + " " + " num col " + SpreadSheet.numCol);
        SpreadSheet st = new SpreadSheet();
        String line = null;
        int colIndex = 0;
        int rowIndex = 0;
        while((line = br.readLine()) != null) {
            line = line.trim();
        //System.out.println("Processiong " + line);
            if(colIndex == SpreadSheet.numCol) {
                rowIndex = rowIndex + 1;
                colIndex = 0;
            }
            Cell cell = new Cell();
            cell.cellId = (rowIndex * SpreadSheet.numCol) + colIndex;
            //System.out.println(cell.cellId);
            cell.parseExpression(line.trim());
            st.sheet[rowIndex][colIndex] = cell;
            colIndex++;
        }
        st.decodeSheet();
        br.close();
    }

}
