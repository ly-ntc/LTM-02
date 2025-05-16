/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.User;
import model.Result;
import java.sql.Timestamp;
import model.Game;

/**
 *
 * @author Admin
 */
public class GameDAO extends DAO {

    public GameDAO() {
        super();
    }

    public User verifyUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT u.ID, u.username, u.password, u.nickname, u.avatar, " +
                    "COUNT(r.id_game) AS numberOfGames, " +
                    "SUM(CASE WHEN r.win = 1 THEN 1 ELSE 0 END) AS numberOfWins, " +
                    "SUM(r.score) AS totalScore, u.isOnline, u.isplaying " +
                    "FROM user u " +
                    "LEFT JOIN result r ON u.ID = r.id_user " +
                    "WHERE u.username = ? AND u.password = ? " +
                    "GROUP BY u.ID, u.username, u.password, u.nickname, u.avatar, u.isOnline, u.isPlaying"
            );
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt(1),
                       rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        (rs.getInt(9) != 0),
                        (rs.getInt(10) != 0)
                        );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO user(username, password, nickname, avatar)\n"
                    + "VALUES(?,?,?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getNickname());
            preparedStatement.setString(4, user.getAvatar());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkDuplicated(String username) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void updateToOnline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToOffline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToPlaying(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToNotPlaying(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public List<User> getListUserIsOnline(int ID)
    {
        List<User> ListUser = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT User.ID, User.NickName, User.IsOnline, User.IsPlaying\n"
                    + "FROM user\n"
                    + "WHERE \n"
                    +"User.IsOnline = 1 AND User.ID != ?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ListUser.add(new User(rs.getInt(1),
                        rs.getString(2),
                        (rs.getInt(3) == 1),
                        (rs.getInt(4)) == 1));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ListUser;
    }
    

   public List<User> getUserStaticRank() {
    List<User> list = new ArrayList<>();
    try {
        PreparedStatement preparedStatement = con.prepareStatement(
               "SELECT u.ID, u.username, u.password, u.nickname, u.avatar, " +
                "COUNT(r.id_game) AS numberOfGames, " +
                "SUM(CASE WHEN r.win = 1 THEN 1 ELSE 0 END) AS numberOfWins, " +
                "SUM(r.score) AS totalScore, u.isOnline, u.isPlaying " +
                "FROM user u " +
                "LEFT JOIN result r ON u.ID = r.id_user " +
                "GROUP BY u.ID, u.username, u.password, u.nickname, u.avatar, u.isOnline, u.isPlaying " +
                "ORDER BY totalScore DESC LIMIT 8"
        );

        System.out.println(preparedStatement);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            list.add(new User(rs.getInt(1), // u.ID
                    rs.getString(2), // u.username
                    rs.getString(3), // u.password
                    rs.getString(4), // u.nickname
                    rs.getString(5), // u.avatar
                    rs.getInt(6), // numberOfGames
                    rs.getInt(7), // numberOfWins
                    rs.getInt(8), // totalScore
                    rs.getBoolean(9), // isOnline
                    rs.getBoolean(10) // isPlaying
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

    public String getNickNameByID(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.NickName\n"
                    + "FROM user\n"
                    + "WHERE user.ID=?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //get id
    public int getLatestResultId() {
        int latestResultId = -1; // Giá trị mặc định nếu không tìm thấy
        String query = "SELECT MAX(ID) AS latest_id FROM result"; // Truy vấn SQL

        try (PreparedStatement preparedStatement = con.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                latestResultId = resultSet.getInt("latest_id"); // Lấy ID mới nhất
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Xử lý lỗi nếu có
        }

        return latestResultId+1; // Trả về ID mới nhất
    }

    public int getLatestGameId() {
        int latestGameId = -1; // Giá trị mặc định nếu không tìm thấy
        String query = "SELECT MAX(ID) AS latest_id FROM game"; // Truy vấn SQL

        try (PreparedStatement preparedStatement = con.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                latestGameId = resultSet.getInt("latest_id"); // Lấy ID mới nhất
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Xử lý lỗi nếu có
        }

        return latestGameId+1; // Trả về ID mới nhất
    }

    //insert Game
    public void insertGame(Game game)
    {
        String sql = "INSERT INTO game (id, start_time, end_time) VALUES (?, ?, ?)";
        
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, game.getID());
            preparedStatement.setTimestamp(2, game.getStartTime());
            
            // Chuyển đổi LocalDateTime sang Timestamp
            preparedStatement.setTimestamp(3, game.getEndTime());
         
            
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) into game table.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void insertResult(Result result)
    {
        String sql = "INSERT INTO result (id, id_user, id_game, score, win) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, result.getID());
            preparedStatement.setInt(2, result.getID_User());
            preparedStatement.setInt(3, result.getID_Game());
            preparedStatement.setInt(4, result.getScore());
            preparedStatement.setInt(5, result.getWin());

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) into result table.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
