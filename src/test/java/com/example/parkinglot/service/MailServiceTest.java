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
//    11:33:48.962 [Test worker] INFO  com.example.parkinglot.service.MailServiceTest - 비번 확인 : 1 : ,$2a$10$X.kTVDAkQlNdRX0eaSWz/.MyH2hm49PsVnIWnAFJ0z2TmAbPpjBqy
//    11:34:44.199 [Test worker] INFO  com.example.parkinglot.service.MailServiceTest - 비번 확인 : 1 : ,$2a$10$DWRbLChINFOVoESHee2/Z.w0biq9rv4yOuoAdiRvh.ixmT8GhQHJO


}