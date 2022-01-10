package main;

import constants.Codes;
import constants.Constants;
import lombok.Getter;
import lombok.SneakyThrows;
import util.JsonUtil;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.*;
import java.io.*;
import java.net.*;

@Getter
public class ServerConnection extends Thread{
    private final Client client;
    private Socket socket;
    private PrintWriter sender;
    private BufferedReader reader;
    private DatagramSocket udpSocket;
    private Thread loginUpdaterThread;

    public ServerConnection(Client client) {
        this.client = client;
    }

    @SneakyThrows
    @Override
    public void run() {


        socket = new Socket(Constants.LOCALHOST,Constants.SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sender = new PrintWriter(socket.getOutputStream(), true);
        udpSocket = new DatagramSocket();


        Thread listenThread = new Thread(() -> {
            while (true) {
                try {
                    JsonObject jsonObject = Json.createReader(reader).readObject();
                    String code = jsonObject.getString("code");
                    if(code.equalsIgnoreCase(Codes.LOGIN_FAULT)){
                        JOptionPane.showMessageDialog(null, "Not logged in");
                    }else if(code.equalsIgnoreCase(Codes.LOGIN_SUCCESS)){
                        Main.client.setUsername(jsonObject.getString("username"));
                        Main.frame.setVisible(false);

                        JFrame frame = new JFrame("Main");
                        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        frame.setSize(400,300);
                        frame.setLayout(null);
                        Main.frame = frame;

                        JTextField username = new JTextField();
                        JLabel usernameLabel = new JLabel("Username: ");
                        usernameLabel.setBounds(20, 20, 100, 30);
                        username.setBounds(120, 20, 120, 30);
                        frame.add(username);
                        frame.add(usernameLabel);

                        JButton request=new JButton("Request");
                        request.setBounds(130,60,95,30);
                        request.addActionListener(a->{
                            sender.println(JsonUtil.searchUser(username.getText()));
                        });
                        frame.add(request);

                        JButton logoutButton=new JButton("Logout");
                        logoutButton.setBounds(250,220,95,30);
                        logoutButton.addActionListener(a->{
                            sender.println(JsonUtil.logout());
                            frame.setVisible(false);
                            System.exit(0);
                        });
                        frame.add(logoutButton);
                        frame.setVisible(true);
                        createUpdaterLoginThread();
                    }else if(code.equalsIgnoreCase(Codes.REGISTER_FAULT)){
                        JOptionPane.showMessageDialog(null, "REGISTER FAULT");
                    }else if(code.equalsIgnoreCase(Codes.REGISTER_SUCCESS)){
                        JOptionPane.showMessageDialog(null, "REGISTER SUCCESS");
                    }else if(code.equalsIgnoreCase(Codes.ONLINE_USER_NOT_FOUND)){
                        JOptionPane.showMessageDialog(null, "Online User Not Found");
                    }else if(code.equalsIgnoreCase(Codes.ONLINE_USER_FOUND)){
                        String port = jsonObject.getString("port");

                        PeerRequest peerRequest = new PeerRequest(Main.client,Integer.valueOf(port));
                        peerRequest.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listenThread.start();


    }

    public void createUpdaterLoginThread(){
        loginUpdaterThread = new Thread(()->{
            try {
                String hello = JsonUtil.convertToJsonMessage(Codes.HELLO,client.getUsername());
                InetAddress ip = InetAddress.getLocalHost();
                DatagramPacket dp = new DatagramPacket(hello.getBytes(), hello.getBytes().length, ip, 3000);
                while (true){
                    sleep(6000);
                    udpSocket.send(dp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loginUpdaterThread.start();
    }
}