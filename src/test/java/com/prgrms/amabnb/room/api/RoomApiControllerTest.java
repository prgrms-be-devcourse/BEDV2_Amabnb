package com.prgrms.amabnb.room.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.PhoneNumber;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.PageRequestDto;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.service.CreateRoomService;
import com.prgrms.amabnb.room.service.SearchRoomService;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.security.oauth.UserProfile;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CreateRoomService createRoomService;

    @Autowired
    SearchRoomService searchRoomService;

    @Autowired
    OAuthService oAuthService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("숙소 등록 성공 테스트")
    void createRoom() throws Exception {
        String accessToken = 로그인_요청();
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();

        mockMvc.perform(post("/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("room-create",
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("roomName"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("price"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("description"),
                    fieldWithPath("maxGuestNum").type(JsonFieldType.NUMBER).description("maxGuestNum"),
                    fieldWithPath("zipcode").type(JsonFieldType.STRING).description("zipcode"),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("address"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("detailAddress"),
                    fieldWithPath("bedCnt").type(JsonFieldType.NUMBER).description("bedCnt"),
                    fieldWithPath("bedRoomCnt").type(JsonFieldType.NUMBER).description("bedRoomCnt"),
                    fieldWithPath("bathRoomCnt").type(JsonFieldType.NUMBER).description("bathRoomCnt"),
                    fieldWithPath("roomType").type(JsonFieldType.STRING).description("roomType"),
                    fieldWithPath("roomScope").type(JsonFieldType.STRING).description("roomScope"),
                    fieldWithPath("imagePaths").type(JsonFieldType.ARRAY).description("roomImagePath")
                )
            ));
    }

    @Test
    @DisplayName("숙소는 userId가 없으면 등록되지 않는다.")
    void nullUserIdTest() throws Exception {
        //given
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();

        //when,then
        mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("필터 검색을 할 수 있다.")
    void filterSearchTest() throws Exception {
        // when, then
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minBeds", "1")
                .param("minBedrooms", "1")
                .param("minBathrooms", "1")
                .param("minPrice", "1")
                .param("maxPrice", "10000000")
                .param("roomTypes", "HOUSE")
                .param("roomScopes", "PRIVATE")
                .content(objectMapper.writeValueAsString(new PageRequestDto(0, 10))))
            .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    @DisplayName("필터를 설정하지 않아도 숙소를 들고온다.")
    void noFilterSearchTest() throws Exception {
        // when, then
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PageRequestDto(0, 10))))
            .andExpect(status().isOk());

    }

    private User createUser() {
        return User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();
    }

    private String 로그인_요청() {
        return "Bearer" + oAuthService.register(createUserProfile()).accessToken();
    }

    private UserProfile createUserProfile() {
        return UserProfile.builder()
            .oauthId("1")
            .provider("kakao")
            .name("아만드")
            .email("asdasd@gmail.com")
            .profileImgUrl("url")
            .build();
    }

    private PageRequest createPageRequest() {
        return PageRequest.of(0, 10);
    }

    private CreateRoomRequest createCreateRoomRequest() {
        return CreateRoomRequest.builder()
            .name("방이름")
            .price(1)
            .description("방설명")
            .maxGuestNum(1)
            .zipcode("00000")
            .address("창원")
            .detailAddress("의창구")
            .bedCnt(2)
            .bedRoomCnt(1)
            .bathRoomCnt(1)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("aaa", "bbb"))
            .build();
    }

    private SearchRoomFilterCondition createFilterCondition() {
        return SearchRoomFilterCondition.builder()
            .minBeds(0)
            .minBedrooms(0)
            .minBathrooms(0)
            .minPrice(100)
            .maxPrice(100000000)
            .roomScopes(List.of(RoomScope.PUBLIC))
            .roomTypes(List.of(RoomType.HOUSE))
            .build();
    }

}
