package com.prgrms.amabnb.room.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.prgrms.amabnb.config.ApiTest;

class RoomApiTest extends ApiTest {

    @Test
    @DisplayName("필터 검색을 할 수 있다.")
    void filterSearchRooms() throws Exception {
        //given
        String accessToken = 로그인_요청("host");
        saveRoom(accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("minBeds", "1");
        params.add("minBedrooms", "1");
        params.add("minBathrooms", "1");
        params.add("minPrice", "1");
        params.add("maxPrice", "10000000");
        params.add("roomTypes", "APARTMENT");
        params.add("roomScopes", "PRIVATE");
        params.add("size", "10");
        params.add("page", "1");

        // when
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
            //then
            .andExpect(status().isOk())

            .andDo(document.document(
                requestParameters(
                    parameterWithName("minBeds").optional().description("최수 침대 수"),
                    parameterWithName("minBedrooms").optional().description("최수 침실 수"),
                    parameterWithName("minBathrooms").optional().description("최소 욕실 수"),
                    parameterWithName("minPrice").optional().description("최소 1박 가격"),
                    parameterWithName("maxPrice").optional().description("최대 1박 가격"),
                    parameterWithName("roomTypes").optional().description("숙소 유형"),
                    parameterWithName("roomScopes").optional().description("숙소 범위"),
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
    @DisplayName("숙소 상세정보를 가져온다.")
    void getRoomDetail() throws Exception {
        //given
        String accessToken = 로그인_요청("host");
        Long roomId = saveRoom(accessToken);

        //when
        mockMvc.perform(get("/rooms/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isOk())

            .andDo(document.document(
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
    @DisplayName("필터를 설정하지 않아도 숙소를 들고온다.")
    void noFilterSearchTest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "10");
        params.add("size", "10");

        // when
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
            //then
            .andExpect(status().isOk());

    }

    @Test
    @DisplayName("등록되지 않은 숙소 상세정보를 가져오지 못한다.")
    void getRoomDetailFailTest() throws Exception {
        //given
        Long notSavedRoomId = 3712893721L;

        //when
        mockMvc.perform(get("/rooms/" + notSavedRoomId)
                .contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isNotFound());

    }

    private Long saveRoom(String accessToken) throws Exception {
        String location = mockMvc.perform(post("/host/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest())))
            .andReturn().getResponse().getHeader("Location");

        String saveRoomId = location.replaceAll("[^0-9]", "");
        return Long.valueOf(saveRoomId);
    }

}
