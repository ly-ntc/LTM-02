/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.GameDAO;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;
import model.Game;
import model.Result;
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
    private final GameDAO gameDAO;
    private final String clientIP;
    private boolean isTimeoutProcessed = false;

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        gameDAO = new GameDAO();
        isClosed = false;
        room = null;

        if (this.socketOfServer.getInetAddress().getHostAddress().equals("10.21.33.7")) {
            clientIP = "10.21.33.7";
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
                user1.getAvatar() + "," + user1.getGame()+ "," +
                user1.getWin() + "," + user1.getScore();
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
                    User user1 = gameDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null)
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    else if (!user1.getIsOnline()) {
                        write("login-success," + getStringFromUser(user1));
                        this.user = user1;
                        gameDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getNickname() + " đang online");
                        Server.admin.addMessage("[" + user1.getID() + "] " + user1.getNickname() + " đang online");
                    } else if (user1.getIsOnline()) {
                        write("dupplicate-login," + messageSplit[1] + "," + messageSplit[2]);
                    } 
                }
                //Xử lý lấy danh sách user khi đăng ký xong
                if (messageSplit[0].equals("get-user-list")) {
                    System.out.println(message);
                    List<User> friends = gameDAO.getListUserIsOnline(this.user.getID());
                    StringBuilder res = new StringBuilder("return-user-list,");
                    for (User friend : friends) {
                        res.append(friend.getID()).append(",").append(friend.getNickname()).append(",").append(friend.getIsOnline() ? 1 : 0).append(",").append(friend.getIsPlaying() ? 1 : 0).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = gameDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) write("duplicate-username,");
                    else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3], messageSplit[4]);
                        gameDAO.addUser(userRegister);
                        this.user = gameDAO.verifyUser(userRegister);
                        gameDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đang online");
                        write("login-success," + getStringFromUser(this.user));
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    gameDAO.updateToOffline(this.user.getID());
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
                    List<User> ranks = gameDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
               
                //Xử lý khi gửi yêu cầu thách đấu 
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
                    goToOwnRoom();
                    gameDAO.updateToPlaying(this.user.getID());
                    gameDAO.updateToPlaying(ID_User2);
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }
                // Xử lý update điểm mỗi lần
                if (messageSplit[0].equals("score-update")) {
                            int newScore = Integer.parseInt(messageSplit[1]);
                            System.out.println(newScore);
                            // Gửi thông báo điểm mới đến client còn lại
                            room.getCompetitor(clientNumber).write("update-score," + newScore);
                }
                //Xử lí khi win và lưu diểm
                if (messageSplit[0].equals("win1")) {
                    int score = Integer.parseInt(messageSplit[1]);
//                        System.out.println(score);
                    int competitorScore = Integer.parseInt(messageSplit[2]);
                    String startTime = messageSplit[3];
                    String endTime = messageSplit[4];
                    //luu du lieu
                    int id_game = gameDAO.getLatestGameId();
                    Game game = new Game(id_game, Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
                    gameDAO.insertGame(game);
                    
                    int id_result = gameDAO.getLatestResultId();
                    Result re = new Result(id_result, this.user.getID(), id_game, score, 1);
                    gameDAO.insertResult(re);
                    Result re1 = new Result(id_result+1, room.getCompetitorID(clientNumber), id_game, competitorScore, 0);
                    gameDAO.insertResult(re1);
                    write("won1," + score + "," + competitorScore);
                    room.getCompetitor(clientNumber).write("lose1," + competitorScore + "," + score);
                    

                }
                //Xử lý hết thời gian và lưu điểm
                if (messageSplit[0].equals("timeout")&& !isTimeoutProcessed) {
                    isTimeoutProcessed = true;
                    int score = Integer.parseInt(messageSplit[1]);
                    int competitorScore = Integer.parseInt(messageSplit[2]);
                    String startTime = messageSplit[3];
                    String endTime = messageSplit[4];
                    int id_game = gameDAO.getLatestGameId();
                    Game game = new Game(id_game, Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
                    gameDAO.insertGame(game);
                    if(score == competitorScore)
                    {
                        int id_result = gameDAO.getLatestResultId();
                        Result re = new Result(id_result, this.user.getID(), id_game, score, 2);
                        gameDAO.insertResult(re);
                        write("draw1," + score + "," + competitorScore);
                        Result re1 = new Result(id_result+1, room.getCompetitorID(clientNumber), id_game, competitorScore, 2);
                        gameDAO.insertResult(re1);
                        room.getCompetitor(clientNumber).write("draw1," + competitorScore + "," + score);
                    }else{
                        int id_result = gameDAO.getLatestResultId();
                        Result re = new Result(id_result, this.user.getID(), id_game, score, 1);
                        gameDAO.insertResult(re);
                        write("won1," + score + "," + competitorScore);
                        Result re1 = new Result(id_result+1, room.getCompetitorID(clientNumber), id_game, competitorScore, 0);
                        gameDAO.insertResult(re1);
                        room.getCompetitor(clientNumber).write("lose1," + competitorScore + "," + score);
                    }
                }
                //xử lý khi có 1 người chơi rời khởi phòng
                if (messageSplit[0].equals("left-room")) {
                    if (room != null) {
                        room.setUsersToNotPlaying();
                        
                        int score = Integer.parseInt(messageSplit[1]);
                        int competitorScore = Integer.parseInt(messageSplit[2]);
                        String startTime = messageSplit[3];
                        String endTime = messageSplit[4];
                         int id_game = gameDAO.getLatestGameId();
                        Game game = new Game(id_game, Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
                        gameDAO.insertGame(game);
                    
                        int id_result = gameDAO.getLatestResultId();
                        Result re = new Result(id_result, this.user.getID(), id_game, 0, 0);
                        gameDAO.insertResult(re);
                        
                        Result re1 = new Result(id_result + 1, room.getCompetitorID(clientNumber), id_game, competitorScore, 1);
                        gameDAO.insertResult(re1);
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }
                //Xử lý hoàn thành ván đấu
                if (messageSplit[0].equals("finish")) {
                    if (room != null) {
                        room.setUsersToNotPlaying();
                        room.getCompetitor(clientNumber).write("finish,");
                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }
                //Xử lý lấy bảng xếp hạng
                 if (messageSplit[0].equals("get-rank-charts")) {
                    List<User> ranks = gameDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
            }
        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                gameDAO.updateToOffline(this.user.getID());
                gameDAO.updateToNotPlaying(this.user.getID());
                Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
            }

            //remove thread khỏi bus
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            if (room != null) {
                try {
                    if (room.getCompetitor(clientNumber) != null) {
//                        room.decreaseNumberOfGame();
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
