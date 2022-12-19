package com.tricon.rcm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricon.rcm.security.JwtAuthenticationRequest;
import com.tricon.rcm.security.api.controller.AuthenticationRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AuthenticationRestController.class)
class RegisterRestControllerTest {
	
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper  objectMapper;

  //@MockBean
  //private RegisterUseCase registerUseCase;

  //@Test
  void whenValidInput_thenReturns200() throws Exception {
	  
	  JwtAuthenticationRequest re= new JwtAuthenticationRequest();
		re.setPassword("SYSTEM1");
		re.setUsername("SYSTEM1");
    
     mockMvc.perform(post("/account/login")
          .contentType("application/json")
          .param("sendWelcomeMail", "true")
          .content(objectMapper.writeValueAsString(re)))
          .andExpect(status().isOk());
         
  }

}