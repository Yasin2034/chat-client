package main;

import constants.Codes;
import constants.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import util.JsonUtil;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;


@RequiredArgsConstructor
@Getter
@Setter
public class PeerRequest extends Thread{
    private final Client client;
    private Socket socket;
    private final Integer port;
    private PrintWriter sender;
    private BufferedReader reader;
    String messages = "";


    @Override
    public void run() {
        try {
            socket = new Socket(Constants.LOCALHOST,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sender = new PrintWriter(socket.getOutputStream(), true);

            sender.println(JsonUtil.convertCodeToJson(Codes.CHAT_REQUEST));
            JLabel textPane = new JLabel();
            while (true){
                JsonObject jsonObject = Json.createReader(reader).readObject();
                String code = jsonObject.getString("code");

                if(Objects.nonNull(Main.peerRequest)){
                    sender.println( JsonUtil.convertCodeToJson(Codes.BUSY));
                    socket.close();
                }if(Codes.REJECT.equalsIgnoreCase(code)){
                    JOptionPane.showMessageDialog(null, "Request Rejected");
                    socket.close();
                }else if (Codes.OK.equalsIgnoreCase(code)){
                    Main.frame.setVisible(false);
                    openChatScreen(textPane);
                }else if(Codes.MESSAGE.equalsIgnoreCase(code)){
                    messages +=  getMessage(jsonObject);
                    textPane.setText("<html>" + messages + "</html>");
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void openChatScreen(JLabel textPane){
        JFrame f = new JFrame("Chat Screen Current User: " + client.getUsername());
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
        sendButton.setBounds(300,500,100,50);
        sendButton.addActionListener(a->{
            messages += getSendMessage(textField.getText());
            textPane.setText("<html>" + messages + "</html>");
            sender.println(JsonUtil.convertToJsonMessage(textField.getText()));
            textField.setText("");
        });
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
        sb.append("+ " + client.getUsername() + ": " + message);
        sb.append("<br/>");
        return sb.toString();
    }

}
