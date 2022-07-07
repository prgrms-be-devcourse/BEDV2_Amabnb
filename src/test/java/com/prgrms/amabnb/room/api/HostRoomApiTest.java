package com.prgrms.amabnb.room.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;

class HostRoomApiTest extends ApiTest {

    @Test
    @DisplayName("숙소 등록 성공 테스트")
    void createRoom() throws Exception {
        //given
        String accessToken = 로그인_요청("host");
        CreateRoomRequest createRoomRequest = createRoomRequest();

        //when
        mockMvc.perform(post("/host/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            //then
            .andExpect(status().isCreated())
            .andDo(document.document(
                tokenRequestHeader(),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("1박 가격"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("숙소 설명"),
                    fieldWithPath("maxGuestNum").type(JsonFieldType.NUMBER).description("최대 게스트 수"),
                    fieldWithPath("zipcode").type(JsonFieldType.STRING).description("우편번호"),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세주소"),
                    fieldWithPath("bedCnt").type(JsonFieldType.NUMBER).description("침대 수"),
                    fieldWithPath("bedRoomCnt").type(JsonFieldType.NUMBER).description("침실 수"),
                    fieldWithPath("bathRoomCnt").type(JsonFieldType.NUMBER).description("욕실 수"),
                    fieldWithPath("roomType").type(JsonFieldType.STRING).description("숙소 유형"),
                    fieldWithPath("roomScope").type(JsonFieldType.STRING).description("숙소 범위"),
                    fieldWithPath("imagePaths").type(JsonFieldType.ARRAY).description("숙소 이미지 경로")
                ),
                responseHeaders(
                    headerWithName("Location").description("숙소 리소스 주소")
                )
            ));
    }

    @Test
    @DisplayName("호스트는 자신이 등록한 방 목록을 가져온다")
    void getHostRooms() throws Exception {
        //given
        String accessToken = 로그인_요청("host");
        saveRoom(accessToken);
        saveRoom(accessToken);

        //when
        mockMvc.perform(get("/host/rooms")
                .header(HttpHeaders.AUTHORIZATION, accessToken))
            //then
            .andExpect(status().isOk())
            .andDo(document.document(
                tokenRequestHeader(),
                responseFields(
                    fieldWithPath("data[].name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("1박 가격"),
                    fieldWithPath("data[].description").type(JsonFieldType.STRING).description("숙소 설명"),
                    fieldWithPath("data[].maxGuestNum").type(JsonFieldType.NUMBER).description("최대 게스트 수"),
                    fieldWithPath("data[].zipcode").type(JsonFieldType.STRING).description("우편번호"),
                    fieldWithPath("data[].address").type(JsonFieldType.STRING).description("주소"),
                    fieldWithPath("data[].detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                    fieldWithPath("data[].bedCnt").type(JsonFieldType.NUMBER).description("침대 수"),
                    fieldWithPath("data[].bedRoomCnt").type(JsonFieldType.NUMBER).description("침실 수"),
                    fieldWithPath("data[].bathRoomCnt").type(JsonFieldType.NUMBER).description("욕실 수"),
                    fieldWithPath("data[].roomType").type(JsonFieldType.STRING).description("숙소 유형"),
                    fieldWithPath("data[].roomScope").type(JsonFieldType.STRING).description("숙소 범위"),
                    fieldWithPath("data[].imagePaths[].imagePath").type(JsonFieldType.STRING).description("숙소 이미지 경로")
                )
            ));
    }

    @Test
    @DisplayName("호스트는 자신이 등록한 숙소를 수정할 수 있다.")
    void modifyRoom() throws Exception {
        //given
        ModifyRoomRequest modifyRequest = createModifyRequest();
        String accessToken = 로그인_요청("host");
        Long roomId = saveRoom(accessToken);

        // when,then
        mockMvc.perform(put("/host/rooms/{roomId}", roomId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            //then
            .andExpect(status().isOk())

            .andDo(document.document(
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("roomId").description("숙소 아이디")
                ),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("bedCnt").type(JsonFieldType.NUMBER).description("침대 수"),
                    fieldWithPath("bedRoomCnt").type(JsonFieldType.NUMBER).description("침실 수"),
                    fieldWithPath("bathRoomCnt").type(JsonFieldType.NUMBER).description("욕실 수"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("숙소 설명"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("1박 가격"),
                    fieldWithPath("maxGuestNum").type(JsonFieldType.NUMBER).description("최대 게스트 수")
                )
            ));
    }

    @Test
    @DisplayName("호스트는 등록한 숙소를 삭제할 수 있다.")
    void deleteRoom() throws Exception{
        //given
        String accessToken = 로그인_요청("host");
        Long roomId = saveRoom(accessToken);
        //when
        mockMvc.perform(delete("/host/rooms/{roomId}", roomId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isOk())
            .andDo(document.document(
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("roomId").description("숙소 아이디")
                )
            ));
    }

    @Test
    @DisplayName("숙소는 userId가 없으면 등록되지 않는다.")
    void nullUserIdTest() throws Exception {
        //given
        CreateRoomRequest createRoomRequest = createRoomRequest();

        //when,then
        mockMvc.perform(post("/host/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Request 하나라도 값이 없다면 수정이 불가능하다")
    void invalidRequestModifyTest() throws Exception {
        //given
        String accessToken = 로그인_요청("host");
        Long roomId = saveRoom(accessToken);

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
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isBadRequest());
        //then

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
}
