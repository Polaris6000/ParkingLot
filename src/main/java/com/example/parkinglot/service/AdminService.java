package com.example.parkinglot.service;

import com.example.parkinglot.dao.AdminDAO;
import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.util.MapperUtil;
import com.example.parkinglot.vo.AdminVO;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class AdminService {

    //싱글톤 생성
    private static AdminService INSTANCE;

    private AdminService() {

    }

    public static AdminService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AdminService();
        }
        return INSTANCE;
    }

    private AdminDAO adminDAO = new AdminDAO();
    private ModelMapper modelMapper = MapperUtil.INSTANCE.getInstance();

    //회원가입 정보를 서버에 등록하는 기능.
    public void signUp(AdminDTO adminDTO) {
        log.info("내가 받은 정보 확인");
        log.info(adminDTO);

        log.info("나에게 온 정보를 서버에 등록");
        adminDAO.insert(modelMapper.map(adminDTO, AdminVO.class));
    }

    //마스터 계정의 정보를 가져오는 서비스. 회원가입할 때 필요함.
    public List<AdminDTO> mastersInfo() {
        List<AdminDTO> adminDTOList = new ArrayList<>();

        List<AdminVO> adminVOList = adminDAO.selectAllMaster();
        for (AdminVO adminVO : adminVOList) {
            adminDTOList.add(modelMapper.map(adminVO, AdminDTO.class));
        }
        return adminDTOList;
    }

    //아이디로 유저의 정보를 찾아오는 메서드
    public AdminDTO findAdminById(String id) {
        AdminDTO adminDTO = null;
        AdminVO adminVO = adminDAO.selectOneById(id);
        if (adminVO != null) {
            adminDTO = modelMapper.map(adminVO, AdminDTO.class);
        }
        return adminDTO;
    }

    //update
    public void changeAdmin(AdminDTO adminDTO) {
        log.info("인증 정보 업데이트 로그인 가능.");

        adminDAO.update(modelMapper.map(adminDTO, AdminVO.class));
    }

    //아이디 찾기, 비밀번호 찾기 기능을 사용하기 위한 서비스
    public AdminDTO findAdminByEmail(String email){
        AdminDTO adminDTO = null;
        AdminVO adminVO = adminDAO.selectOneByEmail(email);
        if (adminVO != null){
            adminDTO = modelMapper.map(adminVO, AdminDTO.class);
        }
        return adminDTO;
    }

    //비밀번호 바꿀 때 사용하는 기능

}
