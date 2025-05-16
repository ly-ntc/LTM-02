/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class User {
    private int ID;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
 
    private boolean isOnline;
    private boolean isPlaying;
    private int score;
    private int win;
    private int game;
    
    public User() {
    }

    public User(int ID, String username, String password, String nickname, String avatar, boolean isOnline, boolean isPlaying) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        
    }

    public User(int ID, String nickname, boolean isOnline, boolean isPlaying) {
        this.ID = ID;
        this.nickname = nickname;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String nickname, String avatar) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
    }
     public User(int ID, String username, String password, String nickname, String avatar, int game,int win, int score, boolean isOnline, boolean isPlaying) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.score = score;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        this.win = win;
        this.game = game;
        
    }
      public User(int ID, String username, String password, String nickname, String avatar, int score, boolean isOnline, boolean isPlaying) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.score = score;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        
        
    }
    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

   

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    

}
