package main;

import util.JsonUtil;

import javax.swing.*;

public class Main {

    public static Client client;
    public static PeerRequest peerRequest;
    public static ServerConnection serverConnection;
    public static JFrame frame;

    public static void main(String[] args) {
        client = new Client();
        client.start();
        serverConnection = new ServerConnection(client);
        serverConnection.start();

        frame =new JFrame("Chatter");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        openLoginFrame(frame);
        openRegister(frame);
        frame.setSize(400,200);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private static void openRegister(JFrame f) {
        JButton openRegisterFrame=new JButton("Register");
        openRegisterFrame.setBounds(130,50,95,30);
        f.add(openRegisterFrame);

        openRegisterFrame.addActionListener(actionEvent-> {
            JFrame newFrame = new JFrame("Register");
            Main.frame = newFrame;
            newFrame.setSize(400, 200);
            JTextField username = new JTextField();
            JLabel usernameLabel = new JLabel("Username: ");
            usernameLabel.setBounds(0, 50, 100, 30);
            username.setBounds(100, 50, 120, 30);
            JTextField password = new JTextField();
            JLabel passwordLabel = new JLabel("Password: ");
            passwordLabel.setBounds(0, 120, 100, 30);
            password.setBounds(100, 120, 120, 30);
            newFrame.add(username);
            newFrame.add(usernameLabel);
            newFrame.add(password);
            newFrame.add(passwordLabel);
            newFrame.setSize(400, 300);
            newFrame.setLayout(null);
            newFrame.setVisible(true);

            JButton register = new JButton("Register");

            register.addActionListener(a->{
                serverConnection.getSender().println(JsonUtil.register(username.getText(),password.getText()));
            });

            newFrame.add(register);
            register.setBounds(100, 180, 120, 30);
        });
    }

    private static void openLoginFrame(JFrame f) {
        JButton loginButton=new JButton("Login");
        loginButton.setBounds(130,80,95,30);
        f.add(loginButton);

        loginButton.addActionListener(actionEvent->{
            f.setVisible(false);
            JFrame newFrame = new JFrame("Login");
            newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Main.frame = newFrame;
            newFrame.setSize(400,200);
            JTextField username = new JTextField();
            JLabel usernameLabel = new JLabel("Username: ");
            usernameLabel.setBounds(0,50, 100,30);
            username.setBounds(100,50, 120,30);
            JTextField password = new JTextField();
            JLabel passwordLabel = new JLabel("Password: ");
            passwordLabel.setBounds(0,120, 100,30);
            password.setBounds(100,120, 120,30);
            newFrame.add(username);
            newFrame.add(usernameLabel);
            newFrame.add(password);
            newFrame.add(passwordLabel);
            newFrame.setSize(400,300);
            newFrame.setLayout(null);
            newFrame.setVisible(true);

            JButton login = new JButton("Login");
            newFrame.add(login);
            login.setBounds(100,180, 120,30);

            try {
                login.addActionListener(a-> {
                    serverConnection.getSender().println(JsonUtil.login(username.getText(),password.getText(),client.getPort().toString()));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
