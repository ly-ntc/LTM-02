package view;


import controller.Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import model.User;
import java.sql.Timestamp;
import java.time.Instant;

// Các biến hiện có của bạn

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Admin
 */
public class GameClientFrm extends javax.swing.JFrame {

    private final User competitor;
    private final int size = 4; // Kích thước lưới 4x4
    private Timer timer;
    private Integer second;
    private Integer minute;
    private int numberOfMatch;
    private int timeLeft = 90; 
    
    private int userWin;
    private int competitorWin;
    private final String competitorIP;

    // Mảng các nút đại diện cho ô trong ma trận
    private JButton[] buttons;
    private Timer flipBackTimer;
    private JButton firstSelectedButton = null;
    private JButton secondSelectedButton = null;
    
    private int playerScore = 0;
    private int competitorScore = 0;
    
    private Timestamp startTime;
    private Timestamp endTime;
    
    public GameClientFrm(User competitor, int room_ID, int isStart, String competitorIP, List<Integer> shuffledMatrix) {
        initComponents();
        numberOfMatch = isStart;
        this.competitor = competitor;
        this.competitorIP = competitorIP;
        
        // Khởi tạo tỉ số
        userWin = 0;
        competitorWin = 0;
        
        startTime = Timestamp.from(Instant.now());
        System.out.println("Thời gian bắt đầu (Timestamp): " + startTime);
    
        this.setTitle("Trò chơi trí nhớ nhóm 2");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.getContentPane().setLayout(null);

        // Set layout dạng lưới cho panel chứa button
        gamePanel.setLayout(new GridLayout(size, size));
        scoreLabel.setText("Tỉ số: 0-0");
        //Setup UI
        playerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        competitorLabel.setFont(new Font("Arial", Font.BOLD, 15));
        roomNameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        roomNameLabel.setAlignmentX(JLabel.CENTER);
        
        playerNicknameValue.setText(Client.user.getNickname());
        playerNumberOfGameValue.setText(Integer.toString(Client.user.getGame()));
        playerNumberOfWinValue.setText(Integer.toString(Client.user.getWin()));
      
        roomNameLabel.setText("Phòng: " + room_ID);
       
        competitorNicknameValue.setText(competitor.getNickname());
        competotorNumberOfGameValue.setText(Integer.toString(competitor.getGame()));
        competitorNumberOfWinValue.setText(Integer.toString(competitor.getWin()));
        scoreLabel.setText("Tỉ số: " + playerScore + " - " + competitorScore);
//        drawRequestButton.setVisible(false);
     
      
        scoreLabel.setText("Tỉ số: 0-0");
        
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cập nhật nhãn thời gian
                countDownLabel.setText("Thời gian còn lại: " + timeLeft + " giây");
                timeLeft--;

                // Kiểm tra nếu hết thời gian
                if (timeLeft < 0) {
                    timer.stop(); // Dừng bộ đếm thời gian
                    //cho thằng có điểm cao hơn gửi thông điệp, nếu hai người điểm bằng nhau thì lấy ng có id bé hoen để gửi
                    if(playerScore > competitorScore)
                    {
                        notifyServerGameTimeOut();  // Gửi thông báo về việc hết thời gian
                        return;
                    }else if( playerScore == competitorScore && Client.user.getID() > competitor.getID()){
                        notifyServerGameTimeOut();  // Gửi thông báo về việc hết thời gian
                        return;
                    }
                    
                    //lưu điểm , trở lại trang xếp hạng
                    
                }
            }
        });
        timer.start(); // Bắt đầu đếm ngược

        buttons = new JButton[size * size];
        List<JButton> selectedButtons = new ArrayList<>();  // Danh sách chứa 2 nút đã chọn

        for (int i = 0; i < size * size; i++) {
            buttons[i] = new JButton();
            buttons[i].setActionCommand(String.valueOf(i));  // Đặt chỉ số làm action command
            buttons[i].setIcon(new ImageIcon("src/assets/images/default.jpg"));  // Ảnh mặc định

            // Sự kiện khi bấm nút
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton buttonClicked = (JButton) e.getSource();
                    int index = Integer.parseInt(buttonClicked.getActionCommand());

                    // Lật ảnh
                    showImage(buttonClicked, shuffledMatrix.get(index));

                    // Thêm nút đã chọn vào danh sách
                    selectedButtons.add(buttonClicked);

                    // Nếu đã chọn đủ 2 nút
                    if (selectedButtons.size() == 2) {
                        JButton firstButton = selectedButtons.get(0);
                        JButton secondButton = selectedButtons.get(1);

                        int firstIndex = Integer.parseInt(firstButton.getActionCommand());
                        int secondIndex = Integer.parseInt(secondButton.getActionCommand());

                        // Kiểm tra nếu 2 nút đã chọn có hình giống nhau
                        if (shuffledMatrix.get(firstIndex).equals(shuffledMatrix.get(secondIndex))) {
                            // Cộng điểm cho người chơi
                            playerScore++;
                            scoreLabel.setText("Tỉ số: " + playerScore + " - " + competitorScore);
                            notifyServerScoreUpdate();
                            // Khóa hai nút này lại (không cho lật nữa)
                            firstButton.setEnabled(false);
                            secondButton.setEnabled(false);
                            
                            
                            // Kiểm tra nếu người chơi đã lật hết các cặp ảnh
                            if (playerScore == 8 ) {
                                timer.stop();  // Dừng đồng hồ đếm ngược
//                                JOptionPane.showMessageDialog(null, "Bạn đã hoàn thành trò chơi!");
                                notifyServerGameFinished();  // Gửi thông báo đến server rằng người chơi đã hoàn thành
                                
                            }
                            // khi hết thời gian mà hai người vẫn chưa xong
                            
                            
                            
                        } else {
                            // Nếu hai ảnh không giống nhau, úp lại ảnh sau 1 giây
                            Timer flipBackTimer = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    firstButton.setIcon(new ImageIcon("src/assets/images/default.jpg"));
                                    secondButton.setIcon(new ImageIcon("src/assets/images/default.jpg"));
                                }
                            });
                            flipBackTimer.setRepeats(false);  // Đặt để chỉ chạy một lần
                            flipBackTimer.start();  // Bắt đầu đếm giờ úp ảnh
                        }

                        // Xóa danh sách các nút đã chọn để chuẩn bị cho lần chọn mới
                        selectedButtons.clear();
                    }
                }
            });

            gamePanel.add(buttons[i]);  // Thêm nút vào panel
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });

//        for(int x : shuffledMatrix)
//        {
//            System.out.println(x);
//        }
    }
    

    private void notifyServerGameFinished() {
        try {
            endTime = Timestamp.from(Instant.now());
            Client.socketHandle.write("win1," + playerScore +","+ competitorScore+","+startTime.toString()+","+endTime.toString());  
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void notifyServerGameTimeOut(){
        try{
            endTime = Timestamp.from(Instant.now());
            Client.socketHandle.write("timeout," + playerScore+ ","+competitorScore+","+startTime.toString()+","+endTime.toString());
        }catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    public void exitGame() {
        try {
            timer.stop();
            endTime = Timestamp.from(Instant.now());
            Client.socketHandle.write("left-room,"+playerScore+","+competitorScore+","+startTime.toString()+","+endTime.toString());
            Client.closeAllViews();
            Client.openView(Client.View.HOMEPAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
        Client.closeAllViews();
        Client.openView(Client.View.HOMEPAGE);
    }
    public void updateScore(int newScore) {
        competitorScore = newScore; // Cập nhật điểm số của đối thủ 
        scoreLabel.setText("Tỉ số: " + playerScore + " - " + competitorScore); // Cập nhật UI
    }
    private void showImage(JButton button, int imageIndex) {
        String imagePath = "src/assets/images/" + imageIndex + ".png";
        button.setIcon(new ImageIcon(imagePath));
    }
    private void notifyServerScoreUpdate() {
        try {
            // Gửi thông điệp lên server với điểm số của người chơi
            Client.socketHandle.write("score-update," + playerScore);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //xử lý kết thúc trò chơi
    
    
    }
      public void stopTimer() {
        timer.stop();
    }

    public void CheckWin(String s){
         JOptionPane.showMessageDialog(rootPane, "Kết quả: " + s +"\n"+ "Điểm của bạn là" + playerScore);
         try {
            timer.stop();
            Client.socketHandle.write("finish,");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
    }

    public void stopAllThread() {
        timer.stop();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jFrame3 = new javax.swing.JFrame();
        jFrame4 = new javax.swing.JFrame();
        playerNumberOfWinLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        playerNicknameLabel = new javax.swing.JLabel();
        playerNumberOfGameLabel = new javax.swing.JLabel();
        competitorNumberOfWinLabel = new javax.swing.JLabel();
        competitorNicknameLabel = new javax.swing.JLabel();
        competotorNumberOfGameLabel = new javax.swing.JLabel();
        playerNicknameValue = new javax.swing.JLabel();
        playerNumberOfGameValue = new javax.swing.JLabel();
        playerNumberOfWinValue = new javax.swing.JLabel();
        competitorNicknameValue = new javax.swing.JLabel();
        competotorNumberOfGameValue = new javax.swing.JLabel();
        competitorNumberOfWinValue = new javax.swing.JLabel();
        gamePanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        playerLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        competitorLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        roomNameLabel = new javax.swing.JLabel();
        scoreLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        drawRequestButton = new javax.swing.JButton();
        countDownLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame4Layout = new javax.swing.GroupLayout(jFrame4.getContentPane());
        jFrame4.getContentPane().setLayout(jFrame4Layout);
        jFrame4Layout.setHorizontalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame4Layout.setVerticalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);

        playerNumberOfWinLabel.setText("Số ván thắng");

        playerNicknameLabel.setText("Nickname");

        playerNumberOfGameLabel.setText("Số ván chơi");

        competitorNumberOfWinLabel.setText("Số ván thắng");

        competitorNicknameLabel.setText("Nickname");

        competotorNumberOfGameLabel.setText("Số ván chơi");

        playerNicknameValue.setText("{nickname}");

        playerNumberOfGameValue.setText("{sovanchoi}");

        playerNumberOfWinValue.setText("{sovanthang}");

        competitorNicknameValue.setText("{nickname}");

        competotorNumberOfGameValue.setText("{sovanchoi}");

        competitorNumberOfWinValue.setText("{sovanthang}");

        gamePanel.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout gamePanelLayout = new javax.swing.GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 568, Short.MAX_VALUE)
        );
        gamePanelLayout.setVerticalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 473, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));

        playerLabel.setForeground(new java.awt.Color(255, 255, 255));
        playerLabel.setText("Bạn");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setForeground(new java.awt.Color(102, 102, 102));

        competitorLabel.setForeground(new java.awt.Color(255, 255, 255));
        competitorLabel.setText("Đối thủ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(competitorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(competitorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));

        roomNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        roomNameLabel.setText("{Tên Phòng}");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roomNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roomNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        scoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scoreLabel.setText("Tỉ số:  0-0");

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 387, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );

        drawRequestButton.setBackground(new java.awt.Color(102, 102, 102));
        drawRequestButton.setForeground(new java.awt.Color(255, 255, 255));
        drawRequestButton.setText("Thoát game");
        drawRequestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawRequestButtonActionPerformed(evt);
            }
        });

        countDownLabel.setText("Thoi gian: ");

        helpMenu.setText("Help");

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        helpMenuItem.setText("Trợ giúp");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(playerNumberOfWinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(playerNumberOfWinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(competitorNicknameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(39, 39, 39)
                                                .addComponent(competitorNicknameValue, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(competotorNumberOfGameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(competitorNumberOfWinLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(27, 27, 27)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(competotorNumberOfGameValue, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(competitorNumberOfWinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(playerNumberOfGameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(playerNicknameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(playerNicknameValue, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(playerNumberOfGameValue)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(146, 146, 146)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 160, Short.MAX_VALUE))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(countDownLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(drawRequestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116)))
                .addComponent(gamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNicknameLabel)
                    .addComponent(playerNicknameValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNumberOfGameLabel)
                    .addComponent(playerNumberOfGameValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNumberOfWinLabel)
                    .addComponent(playerNumberOfWinValue))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(competitorNicknameLabel)
                    .addComponent(competitorNicknameValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(competotorNumberOfGameLabel)
                    .addComponent(competotorNumberOfGameValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(competitorNumberOfWinLabel)
                    .addComponent(competitorNumberOfWinValue))
                .addGap(19, 19, 19)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel7)
                .addGap(11, 11, 11)
                .addComponent(scoreLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(countDownLabel)
                .addGap(18, 18, 18)
                .addComponent(drawRequestButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(gamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        //for(int i=0; i<5; i++){
            //    for(int j=0;j<5;j++){
                //        gamePanel.add(button[i][j]);
                //    }
            //}

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void drawRequestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawRequestButtonActionPerformed

        exitGame();
        
    }//GEN-LAST:event_drawRequestButtonActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(rootPane, "Luật chơi: o	Sever sẽ xáo trộn các hình ảnh trong ma trận (4x4), có 16 ảnh và có 8 cặp ảnh giống nhau.\n" +
"Mỗi màn hình người chơi sẽ hiện lên ma trận ảnh giống nhau, hai người sẽ cùng bắt đầu chơi một lúc.\n" +
"Trò chơi sẽ chỉ diễn ra trong 90 giây. Người chơi được quyền chọn lật hai ảnh mỗi lần, khi hai ảnh giống nhau thì chúng sẽ ngửa còn hai ảnh khác nhau thì lại úp lại. \n" +
"Trò chơi kết thúc khi có một người chơi hoàn thành trò chơi tức là lật hết các cặp ảnh. Người hoàn thành trước sẽ chiến thắng. Người chưa hoàn thành kia sẽ cũng dừng chơi.\n" +
"Hoặc khi hết thời gian mà không ai hoàn thành hết các ảnh thì người nào nhiều điểm hơn thì người đó thắng.\n" +
"Điểm được tính như sau: mỗi người chơi lật đúng một cặp sẽ được một điểm.\n" +
"Nếu hai người chơi hoàn thành cùng một lúc thì sẽ hòa nhau.");
    }//GEN-LAST:event_helpMenuItemActionPerformed

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(rootPane, message);
    } 
    
    
    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel competitorLabel;
    private javax.swing.JLabel competitorNicknameLabel;
    private javax.swing.JLabel competitorNicknameValue;
    private javax.swing.JLabel competitorNumberOfWinLabel;
    private javax.swing.JLabel competitorNumberOfWinValue;
    private javax.swing.JLabel competotorNumberOfGameLabel;
    private javax.swing.JLabel competotorNumberOfGameValue;
    private javax.swing.JLabel countDownLabel;
    private javax.swing.JButton drawRequestButton;
    private javax.swing.JPanel gamePanel;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel playerLabel;
    private javax.swing.JLabel playerNicknameLabel;
    private javax.swing.JLabel playerNicknameValue;
    private javax.swing.JLabel playerNumberOfGameLabel;
    private javax.swing.JLabel playerNumberOfGameValue;
    private javax.swing.JLabel playerNumberOfWinLabel;
    private javax.swing.JLabel playerNumberOfWinValue;
    private javax.swing.JLabel roomNameLabel;
    private javax.swing.JLabel scoreLabel;
    // End of variables declaration//GEN-END:variables


}
