package util;

import constants.Codes;
import lombok.experimental.UtilityClass;
import main.Main;

import javax.json.Json;
import java.io.StringWriter;

@UtilityClass
public class JsonUtil {

    public String convertToJsonMessage(String code, String message){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code",code)
                .add("username", Main.client.getUsername())
                .add("message", message)
                .build());
        return stringWriter.toString();
    }

    public String convertCodeToJson(String code){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code",code)
                .add("username", Main.client.getUsername())
                .build());
        return stringWriter.toString();
    }

    public String convertToJsonMessage(String message){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code", Codes.MESSAGE)
                .add("username", Main.client.getUsername())
                .add("message", message)
                .build());
        return stringWriter.toString();
    }

    public String login(String username, String password, String port){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code", Codes.LOGIN)
                .add("username", username)
                .add("password", password)
                .add("port", port)
                .build());
        return stringWriter.toString();
    }

    public String register(String username, String password){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code", Codes.REGISTER)
                .add("username", username)
                .add("password", password)
                .build());
        return stringWriter.toString();
    }

    public String logout(){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code", Codes.LOGOUT)
                .build());
        return stringWriter.toString();
    }

    public String searchUser(String username){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code", Codes.SEARCH_USER)
                .add("username", username)
                .build());
        return stringWriter.toString();
    }
}
