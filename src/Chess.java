import java.awt.*;
import javax.swing.*;



public class Chess {
    JFrame jf;
    JLabel[][] cells;
    String[][] coins = new String[8][8];
    int selectedRow = -1;
    int selectedCol = -1;
    boolean whiteTurn = true; // White starts the game
    boolean gameOver = false;




    //   ////////////////////////// all methods //////////////////////////////////////////

    public boolean isCurrentPlayersPiece(String piece) {
        return (whiteTurn && isWhite(piece)) || (!whiteTurn && isBlack(piece));// (checking turn is for white and clicked in white piece) or (turn is for black and clicked in black piece)
    }

    public boolean isWhite(String piece) {
        return "♖♘♗♕♔♙".contains(piece);//returns true if piece is white
    }

    public boolean isBlack(String piece) {
        return "♜♞♝♛♚♟".contains(piece);
    }

    public boolean isSameTeam(String p1, String p2) {
        return (isWhite(p1) && isWhite(p2)) || (isBlack(p1) && isBlack(p2));//returns true if first clicked piece and second clicked piece are of same team
    }

// ////////////////////// rules for pown////////////////////
    public boolean isLegalPawnMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int direction = isWhite(piece) ? -1 : 1;
        int startRow = isWhite(piece) ? 6 : 1;
        String destinationPiece = coins[toRow][toCol];

        // Forward by 1. Need to be the same column, to the row just 1(direction) above/below from current position, no other coin should be there in destination cell.
        if (fromCol == toCol && toRow == fromRow + direction && destinationPiece == null) {
            return true;
        }

        // Forward by 2 from start row. Need to be the same column, to the row of 2 cells(direction *2) above/below from current position, no other coin should be there in destination cell, no coins should be infront of it.
        if (fromCol == toCol && fromRow == startRow && toRow == fromRow + 2 * direction
                && destinationPiece == null && coins[fromRow + direction][fromCol] == null) {
            return true;
        }

        // Diagonal capture. Either left or right column(so abs used), to the row just 1 cell(direction) above or below from current position, destination shouldn't be null(there should be a coin to capture), the destination coin shouldn't be same team coin.
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && destinationPiece != null
                && !isSameTeam(piece, destinationPiece)) {
            return true;
        }

        return false;
    }

    public boolean isLegalKnightMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff=Math.abs(fromRow-toRow);
        int colDiff=Math.abs(fromCol-toCol);
        String targetPiece=coins[toRow][toCol];

        boolean isLShape=((rowDiff==2&& colDiff==1)||(rowDiff==1&& colDiff==2));
        return isLShape && (targetPiece ==null || isSameTeam(targetPiece,piece));// target should be L-shape. move to empty cell or capture opponent(not same team).
    }


    public boolean isLegalBishopMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    int rowDiff = Math.abs(toRow - fromRow);// Difference in rows
    int colDiff = Math.abs(toCol - fromCol);// Difference in columns

    // Check if move is diagonal. if rowDiff and colDiff are equal, then it is diagonal move.
    if (rowDiff != colDiff) {
        return false;
    }

    int rowDirection = (toRow > fromRow) ? 1 : -1;//If destination row is greater than source row, then move up(+ve 1), else down(-ve -1).
    int colDirection = (toCol > fromCol) ? 1 : -1;

    // Check all squares between source and destination
    int r = fromRow + rowDirection;//immediate diagonal cell row
    int c = fromCol + colDirection;
    while (r != toRow && c != toCol) {// repeat until we reach destination cell
        // If any square in between is occupied by coin, return false
        if (coins[r][c] != null) {
            return false; // Path is blocked
        }
        r += rowDirection;// Move to the next diagonal square
        c += colDirection;
    }

    // Final destination must be empty or enemy. 
    String destinationPiece = coins[toRow][toCol];
    return destinationPiece == null || !isSameTeam(piece, destinationPiece);
}

public boolean isLegalRookMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    // Rook moves only in straight lines through rows or columns.
    
    boolean isVertical = fromCol == toCol && fromRow != toRow;
    boolean isHorizontal = fromRow == toRow && fromCol != toCol;

    if (!isVertical && !isHorizontal) return false;// Not a valid rook move

    int rowDirection = Integer.compare(toRow, fromRow);// Compare toRow and fromRow to determine direction
    int colDirection = Integer.compare(toCol, fromCol);

    int r = fromRow + rowDirection;
    int c = fromCol + colDirection;

    while (r != toRow || c != toCol) {// check until we reach destination cell
        // If any square in between is occupied by a coin, return false
        if (coins[r][c] != null) return false; // path is blocked
        r += rowDirection;
        c += colDirection;
    }

    // Final destination must be empty or an enemy
    String destinationPiece = coins[toRow][toCol];
    return destinationPiece == null || !isSameTeam(piece, destinationPiece);
}


public boolean isLegalQueenMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    // Use bishop or rook logic for queen
    return isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece) ||
           isLegalRookMove(fromRow, fromCol, toRow, toCol, piece);
}

public boolean isLegalKingMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    int rowDiff = Math.abs(toRow - fromRow);
    int colDiff = Math.abs(toCol - fromCol);

    // Move only 1 square in any direction
    if (rowDiff <= 1 && colDiff <= 1) {
        String targetPiece = coins[toRow][toCol];
        return targetPiece == null || !isSameTeam(piece, targetPiece);
    }

    return false;
}


public void restartGame() {
    selectedRow = -1;
    selectedCol = -1;
    whiteTurn = true;
    gameOver = false;

    // Clear board
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            coins[row][col] = null;
            cells[row][col].setText("");
            cells[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    initializeCoins();
    ShowCoins();
}


        public Point findKingPosition(boolean isWhiteKing) {
    String king = isWhiteKing ? "♔" : "♚";

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            if (king.equals(coins[row][col])) {
                return new Point(row, col);
            }
        }
    }
    return null; // king not found
}

public boolean isOpponentPiece(String piece, boolean whiteKing) {
    return (whiteKing && isBlack(piece)) || (!whiteKing && isWhite(piece));
}



                
public boolean isKingInCheck(boolean whiteKing) {
    Point kingPos = findKingPosition(whiteKing);
    if (kingPos == null) return false;

    int kr = kingPos.x;
    int kc = kingPos.y;

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            String piece = coins[row][col];
            if (piece == null) continue;

            if (isOpponentPiece(piece, whiteKing)) {//checking all opponent piece (means not current player's piece)
                if (// Check if the piece can attack the king(if it is pown, pown's toRow,toCol should be the same as king's row and column, then it could lead to check), if it is knight, bishop, rook, queen or king, then check their respective move logics)
                    (piece.equals("♙") || piece.equals("♟")) && isLegalPawnMove(row, col, kr, kc, piece) ||
                    (piece.equals("♘") || piece.equals("♞")) && isLegalKnightMove(row, col, kr, kc, piece) ||
                    (piece.equals("♗") || piece.equals("♝")) && isLegalBishopMove(row, col, kr, kc, piece) ||
                    (piece.equals("♖") || piece.equals("♜")) && isLegalRookMove(row, col, kr, kc, piece) ||
                    (piece.equals("♕") || piece.equals("♛")) && isLegalQueenMove(row, col, kr, kc, piece) ||
                    (piece.equals("♔") || piece.equals("♚")) && isLegalKingMove(row, col, kr, kc, piece)
                ) {
                    return true;
                }
            }
        }
    }

    return false;
}







    //   /////////////////////////////- cell click and after actions

    public void handleCellClick(int row, int col) {
        if (gameOver) {
            JOptionPane.showMessageDialog(jf, "Game is over! Please start a new game.");
            return;
        }
        String currentPiece=coins[row][col];

        if (selectedRow == -1) {


            // At the first click: select a piece and highlight that cell with yellow color
            if (currentPiece != null && isCurrentPlayersPiece(currentPiece)) {
                selectedRow = row;
                selectedCol = col;
                cells[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
        } else {

            // Second click: move the piece
            String selectedPiece = coins[selectedRow][selectedCol];

 // //////////////// checking is 2nd clicking is to the same color coin, if then no action taken, then selection of cell is repeated
            if(currentPiece != null && isSameTeam(currentPiece,selectedPiece)){
                cells[selectedRow][selectedCol].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                selectedRow=-1;
                selectedCol=-1;
                return;
            }
            
    
            if (selectedPiece.equals("♙") || selectedPiece.equals("♟")) {
                if (!isLegalPawnMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal pown move !",
                            "Can't play",
                                JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (selectedPiece.equals("♘") || selectedPiece.equals("♞")) {
                if (!isLegalKnightMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal knight move !",
                            "Can't play",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (selectedPiece.equals("♗") || selectedPiece.equals("♝")) {
                if (!isLegalBishopMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal bishop move !",
                            "Can't play",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (selectedPiece.equals("♖") || selectedPiece.equals("♜")) {
                if (!isLegalRookMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal rook move !",
                            "Can't play",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (selectedPiece.equals("♕") || selectedPiece.equals("♛")) {
                if (!isLegalQueenMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal queen move !",
                            "Can't play",
                            JOptionPane.WARNING_MESSAGE);
                    return;

    }
}

            if (selectedPiece.equals("♔") || selectedPiece.equals("♚")) {
                if (!isLegalKingMove(selectedRow, selectedCol, row, col, selectedPiece)) {
                    JOptionPane.showMessageDialog(jf,
                            "Illegal queen move !",
                            "Can't play",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
       

            // Check if the target cell contains a king coin (either black or white)
            // If so, declare the winner and end the game
            if ("♔".equals(selectedPiece)) {
    JOptionPane.showMessageDialog(jf, "Black wins! ♚🏆\nStarting a new game...");
    try { Thread.sleep(1000); } catch (InterruptedException e) {}// Restart the game after a short delay
    restartGame();
    return;
}
if ("♚".equals(selectedPiece)) {
    JOptionPane.showMessageDialog(jf, "White wins! ♔🏆\nStarting a new game...");
    try { Thread.sleep(1000); } catch (InterruptedException e) {}// Restart the game after a short delay
    restartGame();//
    return;
}


            // ////////////moving the selected coin to other cell
            // Move selectedPiece logic
            coins[row][col] = selectedPiece;
            coins[selectedRow][selectedCol] = null;

            cells[row][col].setText(selectedPiece);
            cells[selectedRow][selectedCol].setText("");
            cells[selectedRow][selectedCol].setBorder(BorderFactory.createLineBorder(Color.BLACK));

            boolean opponentKingInCheck = isKingInCheck(!whiteTurn);

            if (opponentKingInCheck) {
                String checkedSide = whiteTurn ? "Black" : "White";
                JOptionPane.showMessageDialog(jf, checkedSide + " King is in CHECK! ⚠️");// 

            }


            whiteTurn = !whiteTurn;

            // Reset selection
            selectedRow = -1;
            selectedCol = -1;
        }
    }

// ///////////////////////////// Creating Each chess cells/////////////////////////
    public JLabel createLabel(int x, int y, Color color, JFrame frame,int row, int col) {
        JLabel label = new JLabel();
        label.setBounds(x, y, 50, 50);
        label.setOpaque(true);
        label.setBackground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);//setting coin in the middle of each cell
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 32));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));// border for each cell

        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleCellClick(row, col);
            }
        });

        frame.add(label);
        return label;
    }


    // ////////////////////// creating backend logics of coin movements using 2D String matrix/////////////////////
    public void initializeCoins() {
        // Black coins. These are uni-code strings for perform backend action logics
        coins[0][0] = "♜"; coins[0][1] = "♞"; coins[0][2] = "♝"; coins[0][3] = "♛";
        coins[0][4] = "♚"; coins[0][5] = "♝"; coins[0][6] = "♞"; coins[0][7] = "♜";
        for (int i = 0; i < 8; i++)
            coins[1][i] = "♟";

        // White coins
        coins[7][0] = "♖"; coins[7][1] = "♘"; coins[7][2] = "♗"; coins[7][3] = "♕";
        coins[7][4] = "♔"; coins[7][5] = "♗"; coins[7][6] = "♘"; coins[7][7] = "♖";
        for (int i = 0; i < 8; i++)
            coins[6][i] = "♙";
    }


// ////////////////// Display each coin uni-code icon on the chess board for viewing
    public void ShowCoins() {
        // Showing Black coins on cells for the - 1st and 2nd rows
        cells[0][0].setText("♜");
        cells[0][1].setText("♞");
        cells[0][2].setText("♝");
        cells[0][3].setText("♛");
        cells[0][4].setText("♚");
        cells[0][5].setText("♝");
        cells[0][6].setText("♞");
        cells[0][7].setText("♜");

        for (int i = 0; i < 8; i++) {
            cells[1][i].setText("♟");
        }

        //Showing White coins on cells of - 7th and 8th rows
        cells[7][0].setText("♖");
        cells[7][1].setText("♘");
        cells[7][2].setText("♗");
        cells[7][3].setText("♕");
        cells[7][4].setText("♔");
        cells[7][5].setText("♗");
        cells[7][6].setText("♘");
        cells[7][7].setText("♖");

        for (int i = 0; i < 8; i++) {
            cells[6][i].setText("♙");
        }

        // setting empty the remaining squares initially (2 to 5)
        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j].setText("");
                coins[i][j] = null;
            }
        }
    }



    // /////////////////////// Constructor chess//////////////////////////////////
    public Chess(){
        jf=new JFrame("Chess multiplayer");
        jf.setLayout(null);
        jf.setSize(417,440);
        jf.setLocation(400,50);

        cells = new JLabel[8][8]; // For 8 rows and 8 columns

        int[] yPos = {350, 300,250,200,150,100,50,0}; // Y positions for row 1, row 2,...row 8

        for (int row = 0; row<8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = col * 50;
                int y = yPos[row];
                if(row % 2 == 0) {
                    Color color = (col % 2 == 0) ? Color.DARK_GRAY : Color.LIGHT_GRAY;
                    cells[row][col] = createLabel(x, y, color, jf,row,col);
                }else{
                    Color color = (col % 2 == 0) ? Color.LIGHT_GRAY:Color.DARK_GRAY ;
                    cells[row][col] = createLabel(x, y, color, jf,row,col);
                }
            }
        }
        initializeCoins();
        ShowCoins();



        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
