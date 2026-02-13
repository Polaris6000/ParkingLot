package com.example.parkinglot.controller;

import com.example.parkinglot.enums.AuthKind;
import com.example.parkinglot.service.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
@WebServlet(name = "mailController", value = "/mail")
public class MailController extends HttpServlet {
    MailService mailService = MailService.getINSTANCE();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mailService.sendFindEmail("kangthink7@gmail.com", AuthKind.FINDID,"12345");
        resp.sendRedirect("/web/Stastics.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
