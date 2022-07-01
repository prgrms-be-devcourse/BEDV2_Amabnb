package com.prgrms.amabnb.reservation.api;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.time.LocalDate;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.payload.JsonFieldType;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.security.oauth.UserProfile;

class ReservationHostApiTest extends ApiTest {

    private Long roomId;

    private String hostAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        hostAccessToken = 로그인_요청(createHostProfile());
        roomId = 숙소_등록(hostAccessToken, createRoomRequest("별빛밤"));
    }

    @DisplayName("호스트가 예약을 승인한다. 200 - OK")
    @Test
    void approveReservation() throws Exception {
        // given
        MockHttpServletResponse reservationResponse = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(put("/host/reservations/{reservationId}", reservationId)
                .header(HttpHeaders.AUTHORIZATION, hostAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservation-host-approve",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                    fieldWithPath("checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("reservationStatus").type(JsonFieldType.STRING).description("예약 상태")
                )))
            .andReturn().getResponse();

        // then
        ReservationInfoResponse result = objectMapper.readValue(response.getContentAsString(),
            ReservationInfoResponse.class);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(result.getReservationStatus()).isEqualTo(APPROVED)
        );
    }

    @DisplayName("호스트가 예약을 취소한다. 204 - NO CONTENT")
    @Test
    void cancelByHost() throws Exception {
        // given
        MockHttpServletResponse reservationResponse = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(delete("/host/reservations/{reservationId}", reservationId)
                .header(HttpHeaders.AUTHORIZATION, hostAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservation-host-approve",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                )))
            .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Long getReservationId(MockHttpServletResponse response) {
        String[] locations = response.getHeader("Location").split("/");
        return Long.valueOf(locations[locations.length - 1]);
    }

    private MockHttpServletResponse 예약_요청(String accessToken, CreateReservationRequest request) throws Exception {
        return mockMvc.perform(post("/reservations")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andReturn().getResponse();
    }

    private Long 숙소_등록(String accessToken, CreateRoomRequest request) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/rooms")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse();

        String[] location = response.getHeader("Location").split("/");
        return Long.valueOf(location[location.length - 1]);
    }

    private UserProfile createHostProfile() {
        return UserProfile.builder()
            .oauthId("2")
            .provider("host")
            .name("아만드")
            .email("host@gmail.com")
            .profileImgUrl("url")
            .build();
    }

    private UserProfile createSpancerProfile() {
        return UserProfile.builder()
            .oauthId("3")
            .provider("kakao")
            .name("스펜서")
            .email("spancer@gmail.com")
            .profileImgUrl("url")
            .build();
    }

    private CreateRoomRequest createRoomRequest(String name) {
        return CreateRoomRequest.builder()
            .name(name)
            .price(100_000)
            .description("방설명")
            .maxGuestNum(10)
            .zipcode("00000")
            .address("창원")
            .detailAddress("의창구")
            .bedCnt(2)
            .bedRoomCnt(1)
            .bathRoomCnt(1)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("test"))
            .build();
    }

    private CreateReservationRequest createReservationRequest(int totalGuest, int totalPrice, Long roomId) {
        return CreateReservationRequest.builder()
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(3L))
            .totalGuest(totalGuest)
            .totalPrice(totalPrice)
            .roomId(roomId)
            .build();
    }

}

