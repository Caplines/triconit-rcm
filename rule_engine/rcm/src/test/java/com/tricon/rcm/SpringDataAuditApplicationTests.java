package com.tricon.rcm;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.security.JwtAuthenticationRequest;
import com.tricon.rcm.util.EncrytedKeyUtil;

@SpringBootTest
@ActiveProfiles("dev")
//@RunWith(SpringJUnit4ClassRunner.class)
public class SpringDataAuditApplicationTests {
	
	
	@Autowired
    private RCMUserRepository rcmUserRepository;
	
	
	private RcmUser user;
	
	@Test
    public void create() {
		/*
		user = new RcmUser();
		user.setUserName("SYSTEM1");
		user.setEmail("SYSTEM1");
		user.setFirstName("SYSTEM1");
		user.setLastName("SYSTEM");
		user.setPassword(EncrytedKeyUtil.encryptKey("SYSTEM"));
		//user.set
       rcmUserRepository.save(user);
       */
        
            
    }
	
	@Test
    public void update() {
		/*user = 	rcmUserRepository.findById("ac083708-551e-4b59-a155-bfd1eed067ce").get();
		user.setUserName("SYSTEMq");
		user.setEmail("SYSTEMq");
		user.setFirstName("SYSTEMq");
		user.setLastName("SYSTEMq");
		user.setPassword("SYSTEMq");
		//user.set
       rcmUserRepository.save(user);*/
        
            
    }
	
	@Test
    public void loginUser() {
		/*user = new RcmUser();
		user.setUserName("SYSTEM");
		user.setEmail("SYSTEM");
		user.setFirstName("SYSTEM");
		user.setLastName("SYSTEM");
		user.setPassword("SYSTEM");
		//user.set
       rcmUserRepository.save(user);*/
		JwtAuthenticationRequest re= new JwtAuthenticationRequest();
		re.setPassword("");
		re.setUsername("");
		
        
            
    }

}
