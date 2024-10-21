/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author Admin
 */
public class SocketHandle implements Runnable {

    private BufferedWriter outputWriter;
    private Socket socketOfClient;

    public List<User> getListUser(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 4) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2].equals("1"),
                    message[i + 3].equals("1")));
        }
        return friend;
    }

    public List<User> getListRank(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 9) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2],
                    message[i + 3],
                    message[i + 4],
                    Integer.parseInt(message[i + 5]),
                    Integer.parseInt(message[i + 6]),
                    Integer.parseInt(message[i + 7]),
                    Integer.parseInt(message[i + 8])));
        }
        return friend;
    }

    public User getUserFromString(int start, String[] message) {
        return new User(Integer.parseInt(message[start]),
                message[start + 1],
                message[start + 2],
                message[start + 3],
                message[start + 4],
                Integer.parseInt(message[start + 5]),
                Integer.parseInt(message[start + 6]),
                Integer.parseInt(message[start + 7]),
                Integer.parseInt(message[start + 8]));
    }

    @Override
    public void run() {
        try {
            socketOfClient = new Socket("26.143.1.133", 7777);
            System.out.println("Kết nối thành công!");
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while (true) {
                message = inputReader.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                if (messageSplit[0].equals("server-send-id")) {
                    int serverId = Integer.parseInt(messageSplit[1]);
                }
                //Đăng nhập thành công
                if (messageSplit[0].equals("login-success")) {
                    System.out.println("Đăng nhập thành công");
                    Client.closeAllViews();
                    Client.user = getUserFromString(1, messageSplit);
                    //lây danh sách người dùng từ đối tượng

                    Client.openView(Client.View.HOMEPAGE);
                }
                //Thông tin tài khoản sai
                if (messageSplit[0].equals("wrong-user")) {
                    System.out.println("Thông tin sai");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản hoặc mật khẩu không chính xác");
                }
                //Tài khoản đã đăng nhập ở nơi khác
                if (messageSplit[0].equals("dupplicate-login")) {
                    System.out.println("Đã đăng nhập");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản đã đăng nhập ở nơi khác");
                }

                //Xử lý register trùng tên
                if (messageSplit[0].equals("duplicate-username")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "Tên tài khoản đã được người khác sử dụng");
                }
                //Xử lý lấy danh sách
                if (messageSplit[0].equals("return-user-list")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.setDataToTable(getListUser(messageSplit));
                    }
                }
                //Xử lý nhận thông tin, chat từ toàn server
                if (messageSplit[0].equals("chat-server")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.addMessage(messageSplit[1]);
                    }
                }


                //Xử lý khi nhận được yêu cầu thách đấu
                if (messageSplit[0].equals("duel-notice")) {
                    int res = JOptionPane.showConfirmDialog(Client.getVisibleJFrame(), "Bạn nhận được lời thách đấu của " + messageSplit[2] + " (ID=" + messageSplit[1] + ")", "Xác nhận thách đấu", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        Client.socketHandle.write("agree-duel," + messageSplit[1]);
                    } else {
                        Client.socketHandle.write("disagree-duel," + messageSplit[1]);
                    }
                }
                //Xử lý không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Đối thủ không đồng ý thách đấu");
                }
                if (messageSplit[0].equals("go-to-room")) {
                    System.out.println("Vào phòng");
                    int roomID = Integer.parseInt(messageSplit[1]);
                    String competitorIP = messageSplit[2];
                    int isStart = Integer.parseInt(messageSplit[3]);

                    // Lấy thông tin người chơi đối thủ
                    User competitor = getUserFromString(4, messageSplit);

                    // Lấy ma trận từ chuỗi sau thông tin người chơi
//                    System.out.println("aaaaaa:"+messageSplit[1]);
                    String[] matrixData = Arrays.copyOfRange(messageSplit, messageSplit.length - 16, messageSplit.length);
                    // Xử lý chuỗi ma trận (ở đây chuỗi matrixData chứa các phần tử của ma trận)
                    List<Integer> shuffledMatrix = new ArrayList<>();
                    for (String matrixElement : matrixData) {
                        shuffledMatrix.add(Integer.parseInt(matrixElement));
//                        System.out.println(matrixElement);
                    }

                    Client.closeAllViews();

                    System.out.println("Đã vào phòng: " + roomID);

                    // Mở giao diện chơi game và truyền ma trận đã nhận được
                    Client.openView(Client.View.GAME_CLIENT, competitor, roomID, isStart, competitorIP, shuffledMatrix);
                }
                if (messageSplit[0].equals("update-score")) {
                    int newScore = Integer.parseInt(messageSplit[1]);
                    System.out.println(newScore);
                    Client.gameClientFrm.updateScore(newScore);
                    
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

}
