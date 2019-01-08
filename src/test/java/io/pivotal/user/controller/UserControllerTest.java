package io.pivotal.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.user.domain.RegistrationRequest;
import io.pivotal.user.domain.User;
import io.pivotal.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the UserController.
 * @author David Ferreira Pinto
 * @author Simon Rowe
 *
 */

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class, secure = false)
public class UserControllerTest {

	@MockBean
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();


	@Test
	public void getUser() throws Exception {
		String id = "id";

		User expectedUser = getUser(id);

		when(userService.get(id)).thenReturn(expectedUser);

		mockMvc.perform(get("/users/" + id)
				       .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
				.andExpect(jsonPath("$.givenNames").value(expectedUser.getGivenNames()))
				.andExpect(jsonPath("$.surname").value(expectedUser.getSurname()));
	}

	private User getUser(String id) {
		User expectedUser = new User();
		expectedUser.setId(id);
		expectedUser.setEmail("john@doe.com");
		expectedUser.setGivenNames("John");
		expectedUser.setSurname("Doe");
		return expectedUser;
	}

	@Test
	public void registerWithValidaitonErrors() throws Exception{
		RegistrationRequest request = new RegistrationRequest();

		mockMvc.perform(post("/users/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void register() throws Exception {
		User expectedUser = getUser("newlyRegistered");

		RegistrationRequest request = new RegistrationRequest();
		request.setGivenNames("Jane");
		request.setSurname("Doe");
		request.setEmail("jane@doe.com");
		request.setPassword("pass123");
		request.setConfirmPassword("pass123");

		ArgumentCaptor<RegistrationRequest> registrationRequestArgumentCaptor = ArgumentCaptor.forClass(RegistrationRequest.class);
		when(userService.register(registrationRequestArgumentCaptor.capture())).thenReturn(expectedUser);

		mockMvc.perform(post("/users/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(expectedUser.getId()))
				.andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
				.andExpect(jsonPath("$.givenNames").value(expectedUser.getGivenNames()))
				.andExpect(jsonPath("$.surname").value(expectedUser.getSurname()));;

		RegistrationRequest actualRequestMade = registrationRequestArgumentCaptor.getValue();
		assertEquals(request, actualRequestMade);
	}

}
