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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.PhoneNumber;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.room.service.GuestRoomService;
import com.prgrms.amabnb.room.service.HostRoomService;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.security.oauth.UserProfile;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.repository.UserRepository;

class RoomApiTest extends ApiTest {

    @Autowired
    HostRoomService hostRoomService;

    @Autowired
    GuestRoomService guestRoomService;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OAuthService oAuthService;

    @Test
    @DisplayName("숙소 등록 성공 테스트")
    void createRoom() throws Exception {
        String accessToken = 로그인_요청();
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();

        mockMvc.perform(post("/host/rooms")
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
        //given
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        User user = userRepository.save(createUser());
        Room room = createRoomRequest.toRoom(user);
        roomRepository.save(room);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("minBeds", "1");
        params.add("minBedrooms", "1");
        params.add("minBathrooms", "1");
        params.add("minPrice", "1");
        params.add("maxPrice", "10000000");
        params.add("roomTypes", "HOUSE");
        params.add("roomScopes", "PRIVATE");
        params.add("size", "10");
        params.add("page", "1");

        // when, then
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
            .andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("필터를 설정하지 않아도 숙소를 들고온다.")
    void noFilterSearchTest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "10");
        params.add("size", "10");

        // when, then
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
            .andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("숙소 상세정보를 가져온다.")
    void getRoomDetail() throws Exception {
        //given
        String accessToken = 로그인_요청();
        Long saveRoomId = saveRoom(accessToken);

        //when, then
        mockMvc.perform(get("/rooms/" + saveRoomId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("등록되지 않은 숙소 상세정보를 가져오지 못한다.")
    void getRoomDetailFailTest() throws Exception {
        //given
        Long notSavedRoomId = 3712893721L;

        //when, then
        mockMvc.perform(get("/rooms/" + notSavedRoomId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());

    }

    @Test
    @DisplayName("호스트는 자신이 등록한 방 목록을 가져온다")
    void getHostRooms() throws Exception {
        //given
        String accessToken = 로그인_요청();
        saveRoom(accessToken);
        saveRoom(accessToken);

        //when, then
        mockMvc.perform(get("/host/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("호스트는 자신이 등록한 숙소를 수정할 수 있다.")
    void modifyTest() throws Exception {
        //given
        ModifyRoomRequest modifyRequest = createModifyRequest();
        String accessToken = 로그인_요청();
        Long roomId = saveRoom(accessToken);

        // when,then
        mockMvc.perform(put("/host/rooms/" + roomId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("Request 하나라도 값이 없다면 수정이 불가능하다")
    void name() throws Exception {
        //given
        User user = createUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        Long roomId = hostRoomService.createRoom(userId, createCreateRoomRequest());

        ModifyRoomRequest modifyRequest = ModifyRoomRequest.builder()
            .bedCnt(11)
            .bedRoomCnt(11)
            .bathRoomCnt(11)
            .description("수정된 방 설명")
            .price(111111)
            .maxGuestNum(1111)
            .build();
        //when
        mockMvc.perform(put("/host/rooms/" + roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isBadRequest());
        //then

    }

    private String 로그인_요청() {
        return "Bearer" + oAuthService.register(createUserProfile()).accessToken();
    }

    private Long saveRoom(String accessToken) throws Exception {
        String location = mockMvc.perform(post("/host/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCreateRoomRequest())))
            .andReturn().getResponse().getHeader("Location");

        String saveRoomId = location.replaceAll("[^0-9]", "");
        return Long.valueOf(saveRoomId);
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
            .roomType(RoomType.HOUSE)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("aaa", "bbb"))
            .build();
    }

    private ModifyRoomRequest createModifyRequest() {
        return ModifyRoomRequest.builder()
            .name("수정된 이름")
            .bedCnt(11)
            .bedRoomCnt(11)
            .bathRoomCnt(11)
            .description("수정된 방 설명")
            .price(111111)
            .maxGuestNum(1111)
            .build();
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
}
