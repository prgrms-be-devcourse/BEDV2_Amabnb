package com.prgrms.amabnb.config;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.common.exception.ErrorResponse;
import com.prgrms.amabnb.config.util.DatabaseCleanup;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.security.oauth.UserProfile;

@Import(InfraConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class ApiTest {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected OAuthService oAuthService;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }

    protected RequestHeadersSnippet tokenRequestHeader() {
        return requestHeaders(
            headerWithName(AUTHORIZATION).description("JWT access 토큰")
        );
    }

    protected String 로그인_요청(UserProfile userProfile) {
        return "Bearer" + oAuthService.register(userProfile).accessToken();
    }

    protected String 로그인_요청(String name) {
        return "Bearer" + oAuthService.register(createUserProfile(name)).accessToken();
    }

    protected MockHttpServletResponse 예약_요청(String accessToken, CreateReservationRequest request) throws Exception {
        return mockMvc.perform(post("/reservations")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andReturn().getResponse();
    }

    protected Long 숙소_등록(String accessToken, CreateRoomRequest request) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/host/rooms")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse();

        return extractId(response);
    }

    protected Long extractId(MockHttpServletResponse response) {
        String[] locations = response.getHeader("Location").split("/");
        return Long.valueOf(locations[locations.length - 1]);
    }

    protected ErrorResponse extractErrorResponse(MockHttpServletResponse response) throws IOException {
        return objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);
    }

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}
