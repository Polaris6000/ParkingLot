package com.example.parkinglot.dao;

import com.example.parkinglot.vo.AdminVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminDAOTest {

    private AdminDAO adminDAO;

    @BeforeEach
    public void ready(){
        adminDAO = new AdminDAO();
    }

    @Test
    public void insertTest(){
        AdminVO adminVO = AdminVO.builder()
                .id("test4")
                .password("qwe123")
                .name("실험자4")
                .email("warmice8226@gmail.com")
                .authorization("master")
                .authentication(false)
                .build();

        adminDAO.insert(adminVO);

        AdminVO adminVO2 = AdminVO.builder()
                .id("test1")
                .password("qwe123")
                .name("실험자1")
                .email("kangthink7@gmail.com")
                .authorization("master")
                .authentication(true)
                .build();

        adminDAO.insert(adminVO2);
    }

}