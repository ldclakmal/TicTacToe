/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
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
public class GUI_Network extends javax.swing.JFrame {

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
    Home home;
    String player_name;

    JDBC db = new JDBC();

    /**
     * Creates new form GUI
     */
    public GUI_Network(Home h, String name) {
        initComponents();
        board = new char[3][3];
        currentPlayerMark = 'x';
        initializeBoard();
        pnlGame.setVisible(false);
        pnlClientTable.setVisible(false);
        pnlServerIP.setVisible(false);

        df = (DefaultTableModel) tblClients.getModel();
        hmout = new HashMap<String, ObjectOutputStream>();
        try {
            inet = InetAddress.getLocalHost();
            lblCurrentIP.setText(inet.getHostAddress());
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println(e);
        }

        home = h;
        player_name = name;
    }

    private void connectServer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("Waiting...");
                        Socket s = ss.accept();
                        System.out.println("Connected");
                        obin = new ObjectInputStream(s.getInputStream());
                        obout = new ObjectOutputStream(s.getOutputStream());
                        Vector v = new Vector();
                        v.add("" + (++count));
                        v.add(s.getInetAddress().getHostAddress());
                        v.add(s.getInetAddress().getHostName());
                        df.addRow(v);
                        hmout.put("" + count, obout);
                        listenServer(obin, count, s.getInetAddress().getHostName());
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        }).start();
    }

    private void listenServer(final ObjectInputStream obin, final int count, final String host) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Object ob = obin.readObject();
                        switchPanel(ob);
                    } catch (Exception e) {
                        hmout.remove("" + count);
                        for (int i = 0; i < tblClients.getRowCount(); i++) {
                            if (Integer.parseInt(tblClients.getValueAt(i, 0).toString()) == count) {
                                df.removeRow(i);
                                break;
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void connectClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket s = new Socket(InetAddress.getByName(ServerIP), port);
                    obout = new ObjectOutputStream(s.getOutputStream());
                    obin = new ObjectInputStream(s.getInputStream());
                    listenClient(obin, s.getInetAddress().getHostName());
//                    int count = 0;
//                    while (true) {
//                        obout.writeObject("" + (++count));
//                        Thread.sleep(1000);
//                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }

    private void listenClient(final ObjectInputStream obin, final String host) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Object ob = obin.readObject();
                        if (ob.equals("START")) {
                            JOptionPane.showMessageDialog(null, host + " : " + ob.toString() + '\n');
                            pnlGame.setVisible(true);
                        } else {
                            switchPanel(ob);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Oops! Server Down....");
                        break;
                    }
                }
            }
        }).start();
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
            if (status == "Server") {
                JOptionPane.showMessageDialog(null, "Server: You Won! Congrats!");
                try {
                    System.out.println("ava");
                    hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("You Lost!");
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                try {
                    ResultSet rset = db.getData("SELECT * FROM score WHERE name='" + player_name + "'");
                    if (rset.next()) {
                        int score = rset.getInt("score");
                        score++;
                        db.putData("UPDATE score SET score='" + score + "'");
                    } else {
                        db.putData("INSERT INTO score(name, score) VALUES('" + player_name + "','1')");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else if (status == "Client") {
                try {
                    hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("You Lost!");
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                JOptionPane.showMessageDialog(null, "Client: You Won! Congrats!");
                try {
                    ResultSet rset = db.getData("SELECT * FROM score WHERE name='" + player_name + "'");
                    if (rset.next()) {
                        int score = rset.getInt("score");
                        score++;
                        db.putData("UPDATE score SET score='" + score + "' WHERE name='" + player_name + "'");
                    } else {
                        db.putData("INSERT INTO score(name, score) VALUES('" + player_name + "','1')");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
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
        jPanel1 = new javax.swing.JPanel();
        lblIP = new javax.swing.JLabel();
        lblCurrentIP = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        pnlClientTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClients = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        pnlServerIP = new javax.swing.JPanel();
        txtServerIP = new javax.swing.JTextField();
        lblServerIP = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Play over Network");

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

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 255), 2, true));

        lblIP.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        lblIP.setText("Current IP :");

        lblCurrentIP.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        lblCurrentIP.setText("Current IP");

        jComboBox1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Server", "Client" }));

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pnlClientTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Clients"));

        tblClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "IP", "Host Name"
            }
        ));
        jScrollPane1.setViewportView(tblClients);
        if (tblClients.getColumnModel().getColumnCount() > 0) {
            tblClients.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jButton3.setText("START");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlClientTableLayout = new javax.swing.GroupLayout(pnlClientTable);
        pnlClientTable.setLayout(pnlClientTableLayout);
        pnlClientTableLayout.setHorizontalGroup(
            pnlClientTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientTableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClientTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlClientTableLayout.setVerticalGroup(
            pnlClientTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3))
        );

        pnlServerIP.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Details"));

        txtServerIP.setText("192.168.1.2");

        lblServerIP.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        lblServerIP.setText("Server IP :");

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlServerIPLayout = new javax.swing.GroupLayout(pnlServerIP);
        pnlServerIP.setLayout(pnlServerIPLayout);
        pnlServerIPLayout.setHorizontalGroup(
            pnlServerIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlServerIPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblServerIP)
                .addGap(18, 18, 18)
                .addComponent(txtServerIP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );
        pnlServerIPLayout.setVerticalGroup(
            pnlServerIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlServerIPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlServerIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerIP)
                    .addComponent(txtServerIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblIP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCurrentIP)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlClientTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlServerIP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIP)
                    .addComponent(lblCurrentIP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlClientTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnlServerIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(700, 413));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp1MouseClicked
        drawSymbol(jp1, 0, 0);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp1");
            } else if (status == "Client") {
                obout.writeObject("jp1");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp1MouseClicked

    private void jp2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp2MouseClicked
        drawSymbol(jp2, 0, 1);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp2");
            } else if (status == "Client") {
                obout.writeObject("jp2");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp2MouseClicked

    private void jp3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp3MouseClicked
        drawSymbol(jp3, 0, 2);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp3");
            } else if (status == "Client") {
                obout.writeObject("jp3");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp3MouseClicked

    private void jp4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp4MouseClicked
        drawSymbol(jp4, 1, 0);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp4");
            } else if (status == "Client") {
                obout.writeObject("jp4");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp4MouseClicked

    private void jp5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp5MouseClicked
        drawSymbol(jp5, 1, 1);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp5");
            } else if (status == "Client") {
                obout.writeObject("jp5");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp5MouseClicked

    private void jp6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp6MouseClicked
        drawSymbol(jp6, 1, 2);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp6");
            } else if (status == "Client") {
                obout.writeObject("jp6");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp6MouseClicked

    private void jp7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp7MouseClicked
        drawSymbol(jp7, 2, 0);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp7");
            } else if (status == "Client") {
                obout.writeObject("jp7");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp7MouseClicked

    private void jp8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp8MouseClicked
        drawSymbol(jp8, 2, 1);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp8");
            } else if (status == "Client") {
                obout.writeObject("jp8");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp8MouseClicked

    private void jp9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jp9MouseClicked
        drawSymbol(jp9, 2, 2);
        try {
            if (status == "Server") {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("jp9");
            } else if (status == "Client") {
                obout.writeObject("jp9");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jp9MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jComboBox1.getSelectedIndex() == 0) {
            connectServer();
            pnlClientTable.setVisible(true);
            status = "Server";
        } else {
            pnlServerIP.setVisible(true);
            status = "Client";
        }
        jComboBox1.setEnabled(false);
        jButton1.setEnabled(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ServerIP = txtServerIP.getText();
        connectClient();
        txtServerIP.setEnabled(false);
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            client = tblClients.getSelectedRow();
            if (client != -1) {
                hmout.get(tblClients.getValueAt(client, 0).toString()).writeObject("START");
                pnlGame.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(GUI_Network.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_Network.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_Network.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_Network.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new GUI_Network().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel jp1;
    private javax.swing.JPanel jp2;
    private javax.swing.JPanel jp3;
    private javax.swing.JPanel jp4;
    private javax.swing.JPanel jp5;
    private javax.swing.JPanel jp6;
    private javax.swing.JPanel jp7;
    private javax.swing.JPanel jp8;
    private javax.swing.JPanel jp9;
    private javax.swing.JLabel lblCurrentIP;
    private javax.swing.JLabel lblIP;
    private javax.swing.JLabel lblServerIP;
    private javax.swing.JPanel pnlClientTable;
    private javax.swing.JPanel pnlGame;
    private javax.swing.JPanel pnlServerIP;
    private javax.swing.JTable tblClients;
    private javax.swing.JTextField txtServerIP;
    // End of variables declaration//GEN-END:variables
}
