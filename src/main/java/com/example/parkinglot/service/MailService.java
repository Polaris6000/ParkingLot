package com.example.parkinglot.service;

import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.enums.AuthKind;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.extern.log4j.Log4j2;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

@Log4j2
public class MailService {

    //싱글톤 작업
    private static MailService INSTANCE;

    public static MailService getINSTANCE() {
        if (INSTANCE == null){
            INSTANCE = new MailService();
        }
        return INSTANCE;
    }

    private String host;
    private String port;
    private String user;
    private String pass;

    //구동시 기본 값들을 가져오기.
    private MailService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            Properties prop = new Properties();

            if (input == null) {return;} //못찾은 경우 해당 내용을 종료

            //데이터가 있으면 로딩해서 값을 가져오기
            prop.load(input);

            this.host = prop.getProperty("mail.host");
            this.port = prop.getProperty("mail.port");
            this.user = prop.getProperty("mail.user");
            this.pass = prop.getProperty("mail.pass");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //signUp, findPw, upgrade

    public boolean sendAuthEmail(String to, AuthKind kind, AdminDTO adminDTO, String uuid) {
        boolean result = false;


        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        log.info("호스트 {}, 포트 {}, 유저 {}, 패스 {}", host, port, user, pass);

        // 로드된 user와 pass를 사용해 인증 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });


        //메일 발송을 시도.
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(kind.getSubject());
            message.setContent(kind.getAuthContext(adminDTO.toString(), uuid),"text/html; charset=UTF-8");


            Transport.send(message);
            result = true;
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
        return result;
    }

    public boolean sendFindEmail(String to, AuthKind kind, String uuid) {
        boolean result = false;

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // 로드된 user와 pass를 사용해 인증 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });


        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(kind.getSubject());
            message.setContent(kind.getFindContext(uuid),"text/html; charset=UTF-8");


            Transport.send(message);
            result = true;
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
        return result;
    }


}
