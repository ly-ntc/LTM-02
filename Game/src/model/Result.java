/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Time;
/**
 *
 * @author Admin
 */
public class Result {
    private int ID;
    private int ID_User;
    private int ID_Game;
    private int score;
    private int win;

    public Result() {
    }

    public Result(int ID, int ID_User, int ID_Game, int score, int win) {
        this.ID = ID;
        this.ID_User = ID_User;
        this.ID_Game = ID_Game;
        this.score = score;
        this.win = win;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_User() {
        return ID_User;
    }

    public void setID_User(int ID_User) {
        this.ID_User = ID_User;
    }

    public int getID_Game() {
        return ID_Game;
    }

    public void setID_Game(int ID_Game) {
        this.ID_Game = ID_Game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
    
    
    
}
