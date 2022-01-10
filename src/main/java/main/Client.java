package main;

import constants.Codes;
import lombok.Getter;
import lombok.Setter;
import util.JsonUtil;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

@Setter
@Getter
public class Client extends Thread {
    private ServerSocket serverSocket;
    private Integer port;
    private String username;
    private String password;
    private String messages = "";
    private boolean isOnChat = false;

    @Override
    public void run() {
        port = new Random().nextInt(64534+1000);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JLabel textPane = new JLabel();
            while(true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(()->{
                    BufferedReader reader = null;
                    PrintWriter sender = null;
                    try {
                        reader =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        sender = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (true){
                        JsonObject jsonObject = Json.createReader(reader).readObject();
                        String code = jsonObject.getString("code");
                        if(Codes.CHAT_REQUEST.equalsIgnoreCase(code)){
                            if(Objects.nonNull(Main.peerRequest) || isOnChat){
                                sender.println(JsonUtil.convertCodeToJson(Codes.BUSY));
                            }else{
                                openConfirmScreen(textPane,socket,reader,sender);
                            }
                        }else if(Codes.MESSAGE.equalsIgnoreCase(code)){
                            messages +=  getMessage(jsonObject);
                            textPane.setText("<html>" + messages + "</html>");
                        }
                    }
                });
                thread.start();
            }

        } catch(Exception e ) {
            e.printStackTrace();
        }
    }

    private void openConfirmScreen(JLabel textPane,Socket socket,BufferedReader reader, PrintWriter sender){
        JFrame f = new JFrame("Confirm Screen");
        JLabel chatInputLabel = new JLabel("Do you want chat: ");
        chatInputLabel.setBounds(0,500,150,50);
        f.add(chatInputLabel);

        JButton yes = new JButton("YES");
        yes.setBounds(50,50,60,50);
        yes.addActionListener(a->{
            sender.println(JsonUtil.convertCodeToJson(Codes.OK));
            f.setVisible(false);
            Main.frame.setVisible(false);
            openChatScreen(textPane,sender);
            isOnChat = true;
        });
        f.add(yes);

        JButton no = new JButton("NO");
        no.addActionListener(a->{
            sender.println(JsonUtil.convertCodeToJson(Codes.REJECT));
            f.setVisible(false);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        no.setBounds(110,50,60,50);
        f.add(no);

        f.setSize(300,200);
        f.setLayout(null);
        f.setVisible(true);
    }

    private void openChatScreen(JLabel textPane,PrintWriter sender){
        JFrame f = new JFrame("Chat Screen Current User: " + username);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        textPane.setSize(300,300);
        f.add(textPane);
        JLabel chatInputLabel = new JLabel("Your Message: ");
        chatInputLabel.setBounds(50,500,150,50);
        f.add(chatInputLabel);
        JTextField textField = new JTextField();
        textField.setSize(300,100);
        textField.setBounds(150,500,150,50);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(a->{
            messages += getSendMessage(textField.getText());
            textPane.setText("<html>" + messages + "</html>");
            sender.println(JsonUtil.convertToJsonMessage(textField.getText()));
            textField.setText("");
        });
        sendButton.setBounds(300,500,100,50);
        f.add(sendButton);
        f.add(textField);
        f.add(textPane);
        f.setSize(500,600);
        f.setLayout(null);
        f.setVisible(true);
        Main.frame = f;
    }

    public String getMessage(JsonObject jsonObject){
        StringBuilder sb = new StringBuilder();
        sb.append("- " + jsonObject.getString("username") + ": " + jsonObject.getString("message"));
        sb.append("<br/>");
        return sb.toString();
    }

    public String getSendMessage(String message){
        StringBuilder sb = new StringBuilder();
        sb.append("+ " + username + ": " + message);
        sb.append("<br/>");
        return sb.toString();
    }


}
