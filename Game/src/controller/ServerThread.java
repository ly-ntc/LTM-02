/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDAO;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Admin
 */
public class ServerThread implements Runnable {

    private User user;
    private final Socket socketOfServer;
    private final int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    private Room room;
    private final UserDAO userDAO;
    private final String clientIP;

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDAO = new UserDAO();
        isClosed = false;
        room = null;

        if (this.socketOfServer.getInetAddress().getHostAddress().equals("192.168.38.59")) {
            clientIP = "192.168.38.59";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }

    }

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getClientIP() {
        return clientIP;
    }

    public String getStringFromUser(User user1) {
        return user1.getID() + "," + user1.getUsername()
                + "," + user1.getPassword() + "," + user1.getNickname() + "," +
                user1.getAvatar() + "," + user1.getNumberOfGame() + "," +
                user1.getNumberOfWin() + "," + user1.getNumberOfDraw() + "," + user1.getRank();
    }
    public String generateShuffledMatrix() {
        List<Integer> matrix = new ArrayList<>();

        // Thêm các số từ 1 đến 8, mỗi số 2 lần (tạo thành 8 cặp)
        for (int i = 1; i <= 8; i++) {
            matrix.add(i);
            matrix.add(i);
        }

        // Xáo trộn ma trận
        Collections.shuffle(matrix);

        // Chuyển ma trận thành chuỗi
        String res = "";
        for (int num : matrix) {
            res+= num + ",";
        }

        // Xóa dấu phẩy cuối cùng nếu có
        if (res.endsWith(",")) {
            res = res.substring(0, res.length() - 1);
        }

        return res; // Trả về chuỗi kết quả

    }

    
    public void goToOwnRoom() throws IOException {
    // Tạo ma trận ngẫu nhiên (shuffled matrix)
        String shuffledMatrix = generateShuffledMatrix();
        
        // Gửi thông tin cho người chơi 1 (chính là this)
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",1," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()) + "," + shuffledMatrix);

        // Gửi thông tin cho người chơi 2 (competitor của this)
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",0," + getStringFromUser(user) + "," + shuffledMatrix);
        System.out.println(shuffledMatrix);
     }


    

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);
            write("server-send-id" + "," + this.clientNumber);
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                //Xác minh
                if (messageSplit[0].equals("client-verify")) {
                    System.out.println(message);
                    User user1 = userDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null)
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    else if (!user1.getIsOnline() ) {
                        write("login-success," + getStringFromUser(user1));
                     
                        this.user = user1;
                        userDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getNickname() + " đang online");
                        Server.admin.addMessage("[" + user1.getID() + "] " + user1.getNickname() + " đang online");
                    } 
                }
                //Xử lý lấy danh sách user khi đăng ký xong
                if (messageSplit[0].equals("get-user-list")) {
                    System.out.println(message);
                    List<User> friends = userDAO.getListUserIsOnline(this.user.getID());
                    StringBuilder res = new StringBuilder("return-user-list,");
                    for (User friend : friends) {
                        res.append(friend.getID()).append(",").append(friend.getNickname()).append(",").append(friend.getIsOnline() ? 1 : 0).append(",").append(friend.getIsPlaying() ? 1 : 0).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) write("duplicate-username,");
                    else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3], messageSplit[4]);
                        userDAO.addUser(userRegister);
                        this.user = userDAO.verifyUser(userRegister);
                        userDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đang online");
                        write("login-success," + getStringFromUser(this.user));
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    userDAO.updateToOffline(this.user.getID());
                    Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                    this.user = null;
                }
               
                //Xử lý chat toàn server
                if (messageSplit[0].equals("chat-server")) {
                    Server.serverThreadBus.boardCast(clientNumber, messageSplit[0] + "," + user.getNickname() + " : " + messageSplit[1]);
                    Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " : " + messageSplit[1]);
                }
             
                
                //Xử lý lấy danh sách bảng xếp hạng
                if (messageSplit[0].equals("get-rank-charts")) {
                    List<User> ranks = userDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
               
                
                //Xử lý lấy thông tin kết bạn và rank
                if (messageSplit[0].equals("check-friend")) {
                    String res = "check-friend-response,";
                    res += (userDAO.checkIsFriend(this.user.getID(), Integer.parseInt(messageSplit[1])) ? 1 : 0);
                    write(res);
                }
               
                
                //Xử lý khi gửi yêu cầu thách đấu tới bạn bè
                if (messageSplit[0].equals("duel-request")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]),
                            "duel-notice," + this.user.getID() + "," + this.user.getNickname());
                }
                //Xử lý khi đối thủ đồng ý thách đấu
                if (messageSplit[0].equals("agree-duel")) {
                    this.room = new Room(this);
                    int ID_User2 = Integer.parseInt(messageSplit[1]);
                    ServerThread user2 = Server.serverThreadBus.getServerThreadByUserID(ID_User2);
                    room.setUser2(user2);
                    user2.setRoom(room);
                    room.increaseNumberOfGame();
                    goToOwnRoom();
                    userDAO.updateToPlaying(this.user.getID());
                    userDAO.updateToPlaying(ID_User2);
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }
                //Xử lý khi người chơi đánh 1 nước
                // Giả sử đây là nơi xử lý thông điệp từ client
                if (messageSplit[0].equals("score-update")) {
                            int newScore = Integer.parseInt(messageSplit[1]);
                            System.out.println(newScore);
                            // Gửi thông báo điểm mới đến client còn lại
                            room.getCompetitor(clientNumber).write("update-score," + newScore);
                        }

                if (messageSplit[0].equals("caro")) {
                    room.getCompetitor(clientNumber).write(message);
                }
                
                if (messageSplit[0].equals("win")) {
                    userDAO.addWinGame(this.user.getID());
                    room.increaseNumberOfGame();
                    room.getCompetitor(clientNumber).write("caro," + messageSplit[1] + "," + messageSplit[2]);
                    room.boardCast("new-game,");
                }
                if (messageSplit[0].equals("lose")) {
                    userDAO.addWinGame(room.getCompetitor(clientNumber).user.getID());
                    room.increaseNumberOfGame();
                    room.getCompetitor(clientNumber).write("competitor-time-out");
                    write("new-game,");
                }
//                if (messageSplit[0].equals("draw-request")) {
//                    room.getCompetitor(clientNumber).write(message);
//                }
//                if (messageSplit[0].equals("draw-confirm")) {
//                    room.increaseNumberOfDraw();
//                    room.increaseNumberOfGame();
//                    room.boardCast("draw-game,");
//                }
//                if (messageSplit[0].equals("draw-refuse")) {
//                    room.getCompetitor(clientNumber).write("draw-refuse,");
//                }
//                if (messageSplit[0].equals("voice-message")) {
//                    room.getCompetitor(clientNumber).write(message);
//                }
                
                if (messageSplit[0].equals("left-room")) {
                    if (room != null) {
                        room.setUsersToNotPlaying();
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }
            }
        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                userDAO.updateToOffline(this.user.getID());
                userDAO.updateToNotPlaying(this.user.getID());
                Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
            }

            //remove thread khỏi bus
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            if (room != null) {
                try {
                    if (room.getCompetitor(clientNumber) != null) {
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                    }
                    this.room = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
}
