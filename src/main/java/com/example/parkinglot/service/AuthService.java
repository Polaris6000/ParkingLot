package com.example.parkinglot.service;

import com.example.parkinglot.dao.AdminDAO;
import com.example.parkinglot.dao.AuthDAO;
import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.dto.AuthDTO;
import com.example.parkinglot.util.MapperUtil;
import com.example.parkinglot.vo.AdminVO;
import com.example.parkinglot.vo.AuthVO;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Objects;

@Log4j2
public class AuthService {
    //싱글톤 작업
    private static AuthService INSTANCE;

    private AuthService() {

    }

    public static AuthService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AuthService();
        }
        return INSTANCE;
    }

    private AuthDAO authDAO = new AuthDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private ModelMapper modelMapper = MapperUtil.INSTANCE.getInstance();

    /***************************************************/
    //인증을 받는 서비스
    public AdminDTO loginPass(AdminDTO adminDTO) {
        AdminDTO findAdmin = null;
        log.info("dao에서 값을 찾아오기");
        AdminVO adminVO = adminDAO.selectOneById(adminDTO.getId()); //db에서 받은 값,

        log.info("받은 비번 : {}", adminDTO.getPassword());
        //암호화
        log.info("디비 비번 : {}", adminVO.getPassword());
//        String hashPw = BCrypt.hashpw(adminVO.getPassword(),BCrypt.gensalt());
//        log.info(hashPw);
        log.info("BCrypt.checkpw(adminDTO.getPassword(),adminVO.getPassword()) : {}", BCrypt.checkpw(adminDTO.getPassword(), adminVO.getPassword()));
//        log.info("BCrypt.checkpw(adminVO.getPassword(),adminDTO.getPassword()) : {}" ,BCrypt.checkpw(hashPw,adminDTO.getPassword()));
        log.info(!Objects.isNull(adminVO));
        log.info(adminVO.isAuthentication());

        log.info("아이디가 존재하고 비밀번호가 일치하는 경우 해당 값을 통과");
        if (!Objects.isNull(adminVO) && //아이디가 있는지
                BCrypt.checkpw(adminDTO.getPassword(), adminVO.getPassword()) && //비밀번호가 일치하는지
                adminVO.isAuthentication() //인증을 받았는지.
        ) {
            findAdmin = modelMapper.map(adminVO, AdminDTO.class);
        }
        return findAdmin;
    }


    //인증정보 확인
    public AuthDTO findAuthInfo(String uuid) {
        AuthDTO authDTO = null;
        AuthVO authVO = authDAO.selectOne(uuid);
        log.info("찾은 값 확인 : {}", authVO);
        log.info("현재시간 확인 : {}", LocalDateTime.now());
        log.info("시간 비교 : {}",LocalDateTime.now().isBefore(authVO.getExpiryTime()));

        //존재 검사 / 만료시간 검사 / 사용여부 검사
        if ((authVO != null) &&
                (LocalDateTime.now().isBefore(authVO.getExpiryTime())) &&
                (authVO.isCanUse())
        ) {
            authDTO = modelMapper.map(authVO, AuthDTO.class);
        }

        return authDTO;
    }

    //인증정보를 등록
    public void addAuthToken(AuthDTO authDTO) {
        //시간에 대한 정보는 원래 없다. 서버 등록시간 기준으로 할거니까
        log.info("인증 토큰을 등록");
        //uuid 신청자 id 그리고 용도가 정해져서 와야함.
        log.info("회원 가입 uuid를 토큰 db에 등록 : {}", authDTO);

        authDAO.insert(modelMapper.map(authDTO, AuthVO.class));
    }


    public void useUUID(String uuid) {
        authDAO.updateUse(uuid);
    }
}
