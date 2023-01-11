package controller;

import model.domain.User;
import model.general.Header;
import model.general.Status;
import model.request.RequestLine;
import model.response.Response;
import model.response.StatusLine;
import service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class UserController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    private static final String fileParentPath = "src/main/resources/templates/user";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response getResponse(RequestLine requestLine) {
        if(requestLine.getUri().startsWith("/user/create")) return createUser(requestLine);
        else if(requestLine.getUri().equals("/user/form.html")) return getUserFormHtml(requestLine);

        return Response.from(Status.NOT_FOUND);
    }

    private Response createUser(RequestLine requestLine) {
        Map<String, String> userInfo = requestLine.parseQueryString();
        User user = User.from(userInfo.get("userId"), userInfo.get("password"),
                userInfo.get("name"), userInfo.get("email"));
        userService.signUp(user);

        Map<Header, String> headers = new HashMap<>();
        headers.put(Header.of("Location"), "/index.html");

        return Response.of(StatusLine.from(Status.FOUND), headers);
    }

    private Response getUserFormHtml(RequestLine requestLine) {
        Map<Header, String> headers = new HashMap<>();
        headers.put(Header.of("Content-Type"), "text/html;charset=utf-8");

        byte[] body = {};
        try {
            body = Files.readAllBytes(new File(fileParentPath + requestLine.getUri() + "form.html").toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        headers.put(Header.of("Content-Length"), Integer.toString(body.length));

        return Response.of(StatusLine.from(Status.OK), headers, body);
    }
}
