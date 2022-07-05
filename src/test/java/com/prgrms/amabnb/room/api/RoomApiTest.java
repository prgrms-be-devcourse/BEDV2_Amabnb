package com.prgrms.amabnb.room.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.security.oauth.UserProfile;

class RoomApiTest extends ApiTest {

    @Autowired
    OAuthService oAuthService;

    @Test
    @WithMockUser
    @DisplayName("필터 검색을 할 수 있다.")
    void filterSearchTest() throws Exception {
        //given
        String accessToken = 로그인_요청();
        saveRoom(accessToken);

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

        // when
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
            //then
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("room-filter-search",
                requestParameters(
                    parameterWithName("minBeds").description("최수 침대 수"),
                    parameterWithName("minBedrooms").description("최수 침실 수"),
                    parameterWithName("minBathrooms").description("최소 욕실 수"),
                    parameterWithName("minPrice").description("최소 1박 가격"),
                    parameterWithName("maxPrice").description("최대 1박 가격"),
                    parameterWithName("roomTypes").description("숙소 유형"),
                    parameterWithName("roomScopes").description("숙소 범위"),
                    parameterWithName("size").description("페이지 사이즈"),
                    parameterWithName("page").description("페이지 번호")
                ),
                responseFields(
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("숙소 아이디"),
                    fieldWithPath("data[].name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("1박 가격"),
                    fieldWithPath("data[].imagePaths").type(JsonFieldType.ARRAY).description("숙소 이미지 경로")
                )
            ));

    }

    @Test
    @WithMockUser
    @DisplayName("숙소 상세정보를 가져온다.")
    void getRoomDetail() throws Exception {
        //given
        String accessToken = 로그인_요청();
        Long roomId = saveRoom(accessToken);

        //when
        mockMvc.perform(get("/rooms/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("room-detail",
                pathParameters(
                    parameterWithName("roomId").description("숙소 아이디")
                ),
                responseFields(
                    fieldWithPath("data.name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("1박 가격"),
                    fieldWithPath("data.description").type(JsonFieldType.STRING).description("숙소 설명"),
                    fieldWithPath("data.maxGuestNum").type(JsonFieldType.NUMBER).description("최대 게스트 수"),
                    fieldWithPath("data.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                    fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                    fieldWithPath("data.detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                    fieldWithPath("data.bedCnt").type(JsonFieldType.NUMBER).description("침대 수"),
                    fieldWithPath("data.bedRoomCnt").type(JsonFieldType.NUMBER).description("침실 수"),
                    fieldWithPath("data.bathRoomCnt").type(JsonFieldType.NUMBER).description("욕실 수"),
                    fieldWithPath("data.roomType").type(JsonFieldType.STRING).description("숙소 유형"),
                    fieldWithPath("data.roomScope").type(JsonFieldType.STRING).description("숙소 범위"),
                    fieldWithPath("data.imagePaths[].imagePath").type(JsonFieldType.STRING).description("숙소 이미지 경로")
                )
            ));

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
}
