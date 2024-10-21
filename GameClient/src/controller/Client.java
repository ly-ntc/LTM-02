/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import model.User;
import view.*;
import java.util.List;
import javax.swing.*;

/**
 * @author Admin
 */
public class Client {
    public static User user;
    public static LoginFrm loginFrm;
    public static RegisterFrm registerFrm;
    public static GameNoticeFrm gameNoticeFrm;
    public static HomePageFrm homePageFrm;
    public static RankFrm rankFrm;
    public static GameClientFrm gameClientFrm;
    public static SocketHandle socketHandle;
    
    public Client() {
    }

    public static JFrame getVisibleJFrame() {
       
        if (rankFrm != null && rankFrm.isVisible()) {
            return rankFrm;
        }
        return homePageFrm;
    }
    public static void openView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm = new LoginFrm();
                    loginFrm.setVisible(true);
                    break;
                case REGISTER:
                    registerFrm = new RegisterFrm();
                    registerFrm.setVisible(true);
                    break;
                 case HOMEPAGE:
                    homePageFrm = new HomePageFrm();
                    homePageFrm.setVisible(true);
                    break;
                case RANK:
                    rankFrm = new RankFrm();
                    rankFrm.setVisible(true);
                    break;
            }
        }
    }
    public static void openView(View viewName, User competitor, int room_ID, int isStart, String competitorIP, List<Integer> shuffledMatrix) {
        if (viewName == View.GAME_CLIENT) {
            // Truyền thêm danh sách shuffledMatrix vào GameClientFrm
            gameClientFrm = new GameClientFrm(competitor, room_ID, isStart, competitorIP, shuffledMatrix);
            gameClientFrm.setVisible(true);
        }
    }

    public static void openView(View viewName, String arg1, String arg2) {
        if (viewName != null) {
            switch (viewName) {
                case GAME_NOTICE:
                    gameNoticeFrm = new GameNoticeFrm(arg1, arg2);
                    gameNoticeFrm.setVisible(true);
                    break;
                case LOGIN:
                    loginFrm = new LoginFrm(arg1, arg2);
                    loginFrm.setVisible(true);
            }
        }
    }

    public static void closeView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm.dispose();
                    break;
                case REGISTER:
                    registerFrm.dispose();
                    break;
                case HOMEPAGE:
                    homePageFrm.dispose();
                    break;
                case GAME_NOTICE:
                    gameNoticeFrm.dispose();
                    break;
            }

        }
    }

    public static void closeAllViews() {
        if (loginFrm != null) loginFrm.dispose();
        if (registerFrm != null) registerFrm.dispose();
        if (homePageFrm != null) homePageFrm.dispose();
        if (gameNoticeFrm != null) gameNoticeFrm.dispose();
    }

    public static void main(String[] args) {
        new Client().initView();
    }

    public void initView() {

        loginFrm = new LoginFrm();
        loginFrm.setVisible(true);
        socketHandle = new SocketHandle();
        socketHandle.run();
    }

    public enum View {
        LOGIN,
        REGISTER,
        HOMEPAGE,
        GAME_NOTICE,
        RANK,
        GAME_CLIENT
    }
}
