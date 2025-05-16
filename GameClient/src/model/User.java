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
    private int game;
    private int win;
    private int score;
    private boolean online;
    private boolean playing;
   
    public User(int ID, String username, String password, String nickname, String avatar, int game, int win, int score) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.game = game;
        this.win = win;
        this.score = score;
    }

    public User(int ID, String username, String password, String nickname, String avatar, int game, int win, int score, boolean online, boolean playing) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.game = game;
        this.win = win;
        this.score = score;
        this.online = online;
        this.playing = playing;
    }

    


    public User(int ID, String nickname) {
        this.ID = ID;
        this.nickname = nickname;
    }

    public User(int ID, String nickname, boolean online, boolean playing) {
        this.ID = ID;
        this.nickname = nickname;
        this.online = online;
        this.playing = playing;
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

    public User(int ID, String nickname, int game, int score) {
        this.ID = ID;
        this.nickname = nickname;
        this.game = game;
        this.score = score;
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
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

    

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean isOnline) {
        this.online = isOnline;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean isPlaying) {
        this.playing = isPlaying;
    }

   
}
