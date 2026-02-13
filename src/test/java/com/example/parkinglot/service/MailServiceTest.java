package com.example.parkinglot.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

@Log4j2
class MailServiceTest {

    @Test
    public void testmail(){
    }

    @Test
    public void password(){
        log.info("비번 확인 : 1 : ,{}", BCrypt.hashpw("1",BCrypt.gensalt()));
    }

}