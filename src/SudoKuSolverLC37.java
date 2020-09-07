
public class SudoKuSolverLC37 {
    public static final int EMPTY = 0; // EMPTY CELL
    public static final int SIZE = 9; // SIZE OF SUDOKU GRID
    private int[][] board= new int[SIZE][SIZE];;
    public SudoKuSolverLC37(int[][] board) {
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                this.board[i][j]=board[i][j];
            }
        }
    }


    private void print() {
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                System.out.print(this.board[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
    private boolean solved(){
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(board[i][j] == EMPTY){
                    for(int num=1;num<=SIZE;num++){
                        if (isValid(i,j,num)){
                            board[i][j]=num;
                            if(solved()){
                                return true;
                                } else {
                                board[i][j]=EMPTY;
                            }
                        }
                    }
                    return false; // return false
                }
            }
        }
        print();
        return true; //sudoku is solved.
    }

    private boolean isValid(int row, int col,int num) {
        return (!isinRowConstraint(row,num) &&  !isinColumnConstraint(col,num) && !isinSubSectionConstraint(row,col,num));
    }

    private boolean isinSubSectionConstraint(int row, int col,int num) { //check if possible number is in subsection
        int sqrt=(int)Math.sqrt(board.length);
        int rowConstraint=row-row % sqrt;
        int colConstraint=col-col % sqrt;
        for(int i=rowConstraint;i<rowConstraint+sqrt;i++){
            for(int j=colConstraint;j<colConstraint+sqrt;j++){
                if(board[i][j] == num)
                    return true;
            }
        }
        return false;
    }

    private boolean isinColumnConstraint(int col, int num) { // check if possible number is in this column
        for(int i=0;i<SIZE;i++){
            if(board[i][col]==num){
                return true;
            }
        }
        return false;
    }

    private boolean isinRowConstraint(int row,int num) { // check if possible number is in this row

        for(int i=0;i<SIZE;i++){
            if(board[row][i]==num){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        /*
        int[][]board={
                {9,0,0,1,0,0,0,0,5},
                {0,0,5,0,9,0,2,0,1},
                {8,0,0,0,4,0,0,0,0},
                {0,0,0,0,8,0,0,0,0},
                {0,0,0,7,0,0,0,0,0},
                {0,0,0,0,2,6,0,0,9},
                {2,0,0,3,0,0,0,0,6},
                {0,0,0,2,0,0,9,0,0},
                {0,0,1,9,0,4,5,7,0}
        };
        */
        int[][]board={
                {5,3,0,0,7,0,0,0,0},
                {6,0,0,1,9,5,0,0,0},
                {0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3},
                {4,0,0,8,0,3,0,0,1},
                {7,0,0,0,2,0,0,0,6},
                {0,6,0,0,0,0,2,8,0},
                {0,0,0,4,1,9,0,0,5},
                {0,0,0,0,8,0,0,7,9}
        };
        SudoKuSolverLC37 a =  new SudoKuSolverLC37(board);
        a.print();
        System.out.println("-----------Solution starts------------------");
        a.solved();
    }
}
