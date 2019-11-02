package edu.udacity.java.nano.chat.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class MessageController {
    /**
     * Login Page
     */
    @GetMapping("/")
    public ModelAndView login() {
        //System.out.println(">> ModelAndView Login()");
        return new ModelAndView("/login"); // return the file name
    }

    /**
     * Chatroom Page
     */
    @GetMapping("/index")
    public ModelAndView index(@RequestParam String username, HttpServletRequest request) throws UnknownHostException {
        //System.out.println(">> ModelAndView Index");
        //System.out.println("username : " + username);

        ModelAndView mv = new ModelAndView("/chat");
        mv.addObject("username", username);
        mv.addObject("webSocketUrl", "ws://" + InetAddress.getLocalHost().getHostAddress() + ":" + request.getServerPort() + request.getContextPath() + "/chat");

        //System.out.println(InetAddress.getLocalHost().getHostAddress() );
        //System.out.println(request.getServerPort());
        //System.out.println(request.getContextPath());

        return mv;
    }
}
