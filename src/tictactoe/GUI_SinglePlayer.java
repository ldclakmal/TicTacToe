/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Graphics;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chanaka Lakmal
 */
public class GUI_SinglePlayer extends javax.swing.JFrame {

    InetAddress inet;
    ServerSocket ss;
    int port = 5556;
    DefaultTableModel df;
    int count = 0;
    ObjectInputStream obin;
    ObjectOutputStream obout;
    HashMap<String, ObjectOutputStream> hmout;
    String ServerIP;
    String status;
    int client;
    JPanel[] jp;
    Home home;
    char player_symbol;
    String player_name;

    JDBC db = new JDBC();

    /**
     * Creates new form GUI
     */
    public GUI_SinglePlayer(Home h, char symbol, String name) {
        initComponents();
        board = new char[3][3];
        currentPlayerMark = symbol;
        player_symbol = symbol;
        player_name = name;
        initializeBoard();

        for (int i = 0; i < 9; i++) {
            gameXO[i] = " ";
        }

        jp = new JPanel[]{jp1, jp2, jp3, jp4, jp5, jp6, jp7, jp8, jp9};
        home = h;
    }

    public void switchPanel(Object ob) {
        switch (ob.toString()) {
            case "jp1":
                drawSymbol(jp1, 0, 0);
                break;
            case "jp2":
                drawSymbol(jp2, 0, 1);
                break;
            case "jp3":
                drawSymbol(jp3, 0, 2);
                break;
            case "jp4":
                drawSymbol(jp4, 1, 0);
                break;
            case "jp5":
                drawSymbol(jp5, 1, 1);
                break;
            case "jp6":
                drawSymbol(jp6, 1, 2);
                break;
            case "jp7":
                drawSymbol(jp7, 2, 0);
                break;
            case "jp8":
                drawSymbol(jp8, 2, 1);
                break;
            case "jp9":
                drawSymbol(jp9, 2, 2);
                break;
        }
    }
    private char[][] board;
    private char currentPlayerMark;

    // Set/Reset the board back to all empty values.
    public void initializeBoard() {

        // Loop through rows
        for (int i = 0; i < 3; i++) {

            // Loop through columns
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    // Print the current board (may be replaced by GUI implementation later)
    public void printBoard() {
        System.out.println("-------------");

        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    // Loop through all cells of the board and if one is found to be empty (contains char '-') then return false.
    // Otherwise the board is full.
    public boolean isBoardFull() {
        boolean isFull = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    isFull = false;
                }
            }
        }

        return isFull;
    }

    // Returns true if there is a win, false otherwise.
    // This calls our other win check functions to check the entire board.
    public boolean checkForWin() {
        return (checkRowsForWin() || checkColumnsForWin() || checkDiagonalsForWin());
    }

    // Loop through rows and see if any are winners.
    private boolean checkRowsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[i][0], board[i][1], board[i][2]) == true) {
                return true;
            }
        }
        return false;
    }

    // Loop through columns and see if any are winners.
    private boolean checkColumnsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i]) == true) {
                return true;
            }
        }
        return false;
    }

    // Check the two diagonals to see if either is a win. Return true if either wins.
    private boolean checkDiagonalsForWin() {
        return ((checkRowCol(board[0][0], board[1][1], board[2][2]) == true) || (checkRowCol(board[0][2], board[1][1], board[2][0]) == true));
    }

    // Check to see if all three values are the same (and not empty) indicating a win.
    private boolean checkRowCol(char c1, char c2, char c3) {
        return ((c1 != '-') && (c1 == c2) && (c2 == c3));
    }

    // Change player marks back and forth.
    public void changePlayer() {
        if (currentPlayerMark == 'x') {
            currentPlayerMark = 'o';
        } else {
            currentPlayerMark = 'x';
        }
    }

    // Places a mark at the cell specified by row and col with the mark of the current player.
    public boolean placeMark(int row, int col) {

        // Make sure that row and column are in bounds of the board.
        if ((row >= 0) && (row < 3)) {
            if ((col >= 0) && (col < 3)) {
                if (board[row][col] == '-') {
                    board[row][col] = currentPlayerMark;
                    return true;
                }
            }
        }

        return false;
    }

    public void setImage(final JPanel jp) {
        JLabel jl = null;
        if (currentPlayerMark == 'x') {
            final ImageIcon img = new ImageIcon(getClass().getResource("/img/x.png"));

            jl = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
                    g.drawImage(img.getImage(), 0, 0, jp.getWidth(), jp.getHeight(), null);
                }
            };

        } else if (currentPlayerMark == 'o') {
            final ImageIcon img = new ImageIcon(getClass().getResource("/img/o.png"));

            jl = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
                    g.drawImage(img.getImage(), 0, 0, jp.getWidth(), jp.getHeight(), null);
                }
            };

        }
        jl.setSize(jp.getWidth(), jp.getHeight());
        jp.add(jl);
        jp.repaint();
    }

    public void checkWinner() {
        if (checkForWin()) {
            if (currentPlayerMark == player_symbol) {
                JOptionPane.showMessageDialog(null, "You Won! Congrats!");
                try {
                    ResultSet rset = db.getData("SELECT * FROM score WHERE name='" + player_name + "'");
                    if (rset.next()) {
                        int score = rset.getInt("score");
                        score++;
                        db.putData("UPDATE score SET score='" + score + "'");
                    } else {
                        db.putData("INSERT INTO score(name, score) VALUES('" + player_name + "',)");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "You Lost!");
            }
            home.setVisible(true);
            this.dispose();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(null, "Appears we have a draw!");
            home.setVisible(true);
            this.dispose();
        }
        changePlayer();
    }

    public void drawSymbol(JPanel jp, int x, int y) {
        setImage(jp);
        placeMark(x, y);
        checkWinner();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGame = new javax.swing.JPanel();
        jp1 = new javax.swing.JPanel();
        jp2 = new javax.swing.JPanel();
        jp3 = new javax.swing.JPanel();
        jp4 = new javax.swing.JPanel();
        jp5 = new javax.swing.JPanel();
        jp6 = new javax.swing.JPanel();
        jp7 = new javax.swing.JPanel();
        jp8 = new javax.swing.JPanel();
        jp9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Single Player");

        pnlGame.setBackground(new java.awt.Color(255, 255, 255));
        pnlGame.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 2, true));

        jp1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp1.setName(""); // NOI18N
        jp1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp1Layout = new javax.swing.GroupLayout(jp1);
        jp1.setLayout(jp1Layout);
        jp1Layout.setHorizontalGroup(
            jp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp1Layout.setVerticalGroup(
            jp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp2Layout = new javax.swing.GroupLayout(jp2);
        jp2.setLayout(jp2Layout);
        jp2Layout.setHorizontalGroup(
            jp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp2Layout.setVerticalGroup(
            jp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp3Layout = new javax.swing.GroupLayout(jp3);
        jp3.setLayout(jp3Layout);
        jp3Layout.setHorizontalGroup(
            jp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp3Layout.setVerticalGroup(
            jp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp4Layout = new javax.swing.GroupLayout(jp4);
        jp4.setLayout(jp4Layout);
        jp4Layout.setHorizontalGroup(
            jp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp4Layout.setVerticalGroup(
            jp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp5Layout = new javax.swing.GroupLayout(jp5);
        jp5.setLayout(jp5Layout);
        jp5Layout.setHorizontalGroup(
            jp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp5Layout.setVerticalGroup(
            jp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp6Layout = new javax.swing.GroupLayout(jp6);
        jp6.setLayout(jp6Layout);
        jp6Layout.setHorizontalGroup(
            jp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp6Layout.setVerticalGroup(
            jp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp7MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp7Layout = new javax.swing.GroupLayout(jp7);
        jp7.setLayout(jp7Layout);
        jp7Layout.setHorizontalGroup(
            jp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp7Layout.setVerticalGroup(
            jp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp8Layout = new javax.swing.GroupLayout(jp8);
        jp8.setLayout(jp8Layout);
        jp8Layout.setHorizontalGroup(
            jp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp8Layout.setVerticalGroup(
            jp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jp9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jp9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jp9MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jp9Layout = new javax.swing.GroupLayout(jp9);
        jp9.setLayout(jp9Layout);
        jp9Layout.setHorizontalGroup(
            jp9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jp9Layout.setVerticalGroup(
            jp9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlGameLayout = new javax.swing.GroupLayout(pnlGame);
        pnlGame.setLayout(pnlGameLayout);
        pnlGameLayout.setHorizontalGroup(
            pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGameLayout.createSequentialGroup()
                        .addComponent(jp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGameLayout.createSequentialGroup()
                        .addComponent(jp4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGameLayout.createSequentialGroup()
                        .addComponent(jp7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jp9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlGameLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jp1, jp2, jp3, jp4, jp5, jp6, jp7, jp8, jp9});

        pnlGameLayout.setVerticalGroup(
            pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jp4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jp7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jp9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlGameLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jp1, jp2, jp3, jp4, jp5, jp6, jp7, jp8, jp9});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(384, 413));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp1MouseClicked
        drawSymbol(jp1, 0, 0);
        makeMove(0);
    }//GEN-LAST:event_jp1MouseClicked

    private void jp2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp2MouseClicked
        drawSymbol(jp2, 0, 1);
        makeMove(1);
    }//GEN-LAST:event_jp2MouseClicked

    private void jp3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp3MouseClicked
        drawSymbol(jp3, 0, 2);
        makeMove(2);
    }//GEN-LAST:event_jp3MouseClicked

    private void jp4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp4MouseClicked
        drawSymbol(jp4, 1, 0);
        makeMove(3);
    }//GEN-LAST:event_jp4MouseClicked

    private void jp5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp5MouseClicked
        drawSymbol(jp5, 1, 1);
        makeMove(4);
    }//GEN-LAST:event_jp5MouseClicked

    private void jp6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp6MouseClicked
        drawSymbol(jp6, 1, 2);
        makeMove(5);
    }//GEN-LAST:event_jp6MouseClicked

    private void jp7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp7MouseClicked
        drawSymbol(jp7, 2, 0);
        makeMove(6);
    }//GEN-LAST:event_jp7MouseClicked

    private void jp8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp8MouseClicked
        drawSymbol(jp8, 2, 1);
        makeMove(7);
    }//GEN-LAST:event_jp8MouseClicked

    private void jp9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp9MouseClicked
        drawSymbol(jp9, 2, 2);
        makeMove(8);
    }//GEN-LAST:event_jp9MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI_SinglePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_SinglePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_SinglePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_SinglePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new GUI_SinglePlayer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jp1;
    private javax.swing.JPanel jp2;
    private javax.swing.JPanel jp3;
    private javax.swing.JPanel jp4;
    private javax.swing.JPanel jp5;
    private javax.swing.JPanel jp6;
    private javax.swing.JPanel jp7;
    private javax.swing.JPanel jp8;
    private javax.swing.JPanel jp9;
    private javax.swing.JPanel pnlGame;
    // End of variables declaration//GEN-END:variables

    //gameXO is the game
    static String[] gameXO = new String[9];
    //_sdepth is used to control the depth
    static int sdepth;

    public int makeMove(int index) {	/*Step to do in this method
         *1- update gameXO. put X in the gameXO[index]
         *2- test if game is finished (draw or X win)
         *3- call MinMax algorithm and return the score and return the best position for O
         *4- update gameXO. put O in its position
         *5- test if game is finished (draw or O win)
         */
        //return -1 to know that player X wins
        //return -2 to know that the game is draw
        //1

        gameXO[index] = "X";
        //2
        if (gameOver(gameXO)) {
            return -1;
        }
        if (drawGame(gameXO)) {
            return -2;
        }

        //3
        ResultMM res = MinMax(gameXO, "MAX", 0, 0);
        int i = res.getIntrus();
        drawSymbol(jp[i], i / 3, i % 3);
        placeMark(i / 3, i % 3);
        //4
        gameXO[i] = "O";

        //5
        // return i+20 to know that o wins (i used this method for programming issues)
        // retrun i-30 to know that the game is draw (i used this method for programming issues)
        if (gameOver(gameXO)) {
            return i + 20;
        }
        if (drawGame(gameXO)) {
            return i - 30;
        }

        return i;

    }

    public ResultMM MinMax(String[] demo, String level, int fils, int depth) {/*MinMax algorithm
         * 1- generate successor
         * 2- if no successor or game is finished return score 
         * 3- if there is successor
         * 	a) apply MinMax for each successor
         *	b) after recursive call, i return the good score
         */

        //1---------------

        ArrayList<String[]> children = genere_succ(demo, level);
        //2------------------
        if (children == null && sdepth != -1) {
            sdepth = -1;
            depth = depth + 1;
        }

        if (children == null || gameOver(demo)) {
            return new ResultMM(demo, getScore(demo), depth);
        } else {//3------------------
            if (sdepth > children.size()) {
                sdepth = children.size();
                depth = depth + 1;
            }

            ArrayList<ResultMM> listScore = new ArrayList<ResultMM>();
            //pass into each child
            for (int i = 0; i < children.size(); i++) {//3 a)---------------
                listScore.add(MinMax(children.get(i), inverse(level), 1, depth + 1));
            }
            //3 b)----------------
            ResultMM res = getResult(listScore, level);
            if (fils == 1) {
                res.updateMatrix(demo);
            }

            return res;
        }
    }

    public ResultMM getResult(ArrayList<ResultMM> listScore, String level) {//this method is used to get the appropriate score
        //if level is MAX, i search for the higher score in the nearer depth
        //if level is MIN, i search for the lowest score in the nearer depth
        ResultMM result = listScore.get(0);
        if (level.equals("MAX")) {
            for (int i = 1; i < listScore.size(); i++) {
                if ((listScore.get(i).getScore() > result.getScore())
                        || (listScore.get(i).getScore() == result.getScore() && listScore.get(i).depth < result.depth)) {
                    result = listScore.get(i);
                }
            }
        } else {
            for (int i = 1; i < listScore.size(); i++) {
                if ((listScore.get(i).getScore() < result.getScore())
                        || (listScore.get(i).getScore() == result.getScore() && listScore.get(i).depth < result.depth)) {
                    result = listScore.get(i);
                }
            }
        }
        return result;
    }

    public ArrayList<String[]> genere_succ(String[] demo, String level) {//generate successor
        //if level is MAX, generate successor with o ( o in lowerCase)
        //if level is MIN, generate successor with x ( x in lowerCase)
        //if demo has no successor, return null
        ArrayList<String[]> succ = new ArrayList<String[]>();
        for (int i = 0; i < demo.length; i++) {
            if (demo[i].equals(" ")) {
                String[] child = new String[9];
                for (int j = 0; j < 9; j++) {
                    child[j] = demo[j];
                }

                if (level.equals("MAX")) {
                    child[i] = "o";
                } else {
                    child[i] = "x";
                }
                succ.add(child);
            }
        }
        return (succ.size() == 0) ? null : succ;
    }

    public String inverse(String level) { //inverse level from MIN to MAX
        return (level.equals("MIN")) ? "MAX" : "MIN";
    }

    public int getScore(String[] demo) { //return  the score:
        //if X win return -1;
        //if O win return 1;
        //else return 0, this mean draw
        if ((demo[0].equalsIgnoreCase("x") && demo[1].equalsIgnoreCase("x") && demo[2].equalsIgnoreCase("x")) || (demo[3].equalsIgnoreCase("x") && demo[4].equalsIgnoreCase("x") && demo[5].equalsIgnoreCase("x"))
                || (demo[6].equalsIgnoreCase("x") && demo[7].equalsIgnoreCase("x") && demo[8].equalsIgnoreCase("x")) || (demo[0].equalsIgnoreCase("x") && demo[3].equalsIgnoreCase("x") && demo[6].equalsIgnoreCase("x"))
                || (demo[1].equalsIgnoreCase("x") && demo[4].equalsIgnoreCase("x") && demo[7].equalsIgnoreCase("x")) || (demo[2].equalsIgnoreCase("x") && demo[5].equalsIgnoreCase("x") && demo[8].equalsIgnoreCase("x"))
                || (demo[0].equalsIgnoreCase("x") && demo[4].equalsIgnoreCase("x") && demo[8].equalsIgnoreCase("x")) || (demo[2].equalsIgnoreCase("x") && demo[4].equalsIgnoreCase("x") && demo[6].equalsIgnoreCase("x"))) {
            return -1;
        }

        if ((demo[0].equalsIgnoreCase("o") && demo[1].equalsIgnoreCase("o") && demo[2].equalsIgnoreCase("o")) || (demo[3].equalsIgnoreCase("o") && demo[4].equalsIgnoreCase("o") && demo[5].equalsIgnoreCase("o"))
                || (demo[6].equalsIgnoreCase("o") && demo[7].equalsIgnoreCase("o") && demo[8].equalsIgnoreCase("o")) || (demo[0].equalsIgnoreCase("o") && demo[3].equalsIgnoreCase("o") && demo[6].equalsIgnoreCase("o"))
                || (demo[1].equalsIgnoreCase("o") && demo[4].equalsIgnoreCase("o") && demo[7].equalsIgnoreCase("o")) || (demo[2].equalsIgnoreCase("o") && demo[5].equalsIgnoreCase("o") && demo[8].equalsIgnoreCase("o"))
                || (demo[0].equalsIgnoreCase("o") && demo[4].equalsIgnoreCase("o") && demo[8].equalsIgnoreCase("o")) || (demo[2].equalsIgnoreCase("o") && demo[4].equalsIgnoreCase("o") && demo[6].equalsIgnoreCase("o"))) {
            return 1;
        }

        return 0;
    }

    public boolean gameOver(String[] demo) {//if the score of the game is 0 then return false. this mean we have a winner
        return (getScore(demo) != 0) ? true : false;
    }

    public boolean drawGame(String[] demo) {
        //test if the game is draw.
        //if demo is full, this mean that game is draw
        //if demo still has empty square, this mean that the game isn't finished
        for (int i = 0; i < 9; i++) {
            if (demo[i].equals(" ")) {
                return false;
            }
        }
        return true;
    }

}
