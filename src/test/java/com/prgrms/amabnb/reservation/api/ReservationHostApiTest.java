package com.prgrms.amabnb.reservation.api;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.prgrms.amabnb.common.model.ApiResponse;
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
        MockHttpServletResponse ReservationResponseForGuest = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(ReservationResponseForGuest);

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
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                    fieldWithPath("data.checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("data.checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("data.totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("data.reservationStatus").type(JsonFieldType.STRING).description("예약 상태")
                )))
            .andReturn().getResponse();

        // then
        ApiResponse<ReservationInfoResponse> apiResponse = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(apiResponse.data().getReservationStatus()).isEqualTo(APPROVED)
        );
    }

    @DisplayName("호스트가 예약을 취소한다. 204 - NO CONTENT")
    @Test
    void cancelByHost() throws Exception {
        // given
        MockHttpServletResponse ReservationResponseForGuest = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(ReservationResponseForGuest);

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

    @DisplayName("호스트가 예약 정보를 단건 조회한다. 200 - OK")
    @Test
    void getReservation() throws Exception {
        // given
        MockHttpServletResponse ReservationResponseForGuest = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(ReservationResponseForGuest);

        // when
        mockMvc.perform(get("/host/reservations/{reservationId}", reservationId)
                .header(HttpHeaders.AUTHORIZATION, hostAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

            // then
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data.reservation.id").value(reservationId),
                jsonPath("$.data.reservation.totalGuest").value(3),
                jsonPath("$.data.reservation.totalPrice").value(300_000),
                jsonPath("$.data.room.roomId").value(roomId)
            )

            // docs
            .andDo(document("reservation-host-findOne",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                ),
                responseFields(
                    fieldWithPath("data.reservation.id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("data.reservation.checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("data.reservation.checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("data.reservation.totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("data.reservation.totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("data.reservation.reservationStatus").type(JsonFieldType.STRING).description("예약 상태"),
                    fieldWithPath("data.room.roomId").type(JsonFieldType.NUMBER).description("숙소 아이디"),
                    fieldWithPath("data.room.name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("data.room.roomAddress.zipcode").type(JsonFieldType.STRING)
                        .description("숙소 우편번호"),
                    fieldWithPath("data.room.roomAddress.address").type(JsonFieldType.STRING)
                        .description("숙소 주소"),
                    fieldWithPath("data.room.roomAddress.detailAddress").type(JsonFieldType.STRING)
                        .description("숙소 상세주소"),
                    fieldWithPath("data.guest.id").type(JsonFieldType.NUMBER).description("게스트 아이디"),
                    fieldWithPath("data.guest.name").type(JsonFieldType.STRING).description("게스트 이름"),
                    fieldWithPath("data.guest.email").type(JsonFieldType.STRING).description("게스트 이메일")
                )));
    }

    @DisplayName("호스트가 예약 정보들을 조회한다. 200 - OK")
    @Test
    void getReservations() throws Exception {
        // given
        String accessToken = 로그인_요청(createSpancerProfile());
        for (int i = 0; i < 2; i++) {
            예약_요청(accessToken, createReservationRequestByDay(i));
        }

        // when
        mockMvc.perform(get("/host/reservations")
                .param("pageSize", "2")
                .param("status", "PENDING")
                .header(HttpHeaders.AUTHORIZATION, hostAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

            // then
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data", hasSize(2)),
                jsonPath("$.data[0].reservation.reservationStatus", PENDING).exists()
            )

            // docs
            .andDo(document("reservation-host-find",
                tokenRequestHeader(),
                requestParameters(
                    parameterWithName("pageSize").description("페이지 사이즈"),
                    parameterWithName("status").description("예약 상태").optional(),
                    parameterWithName("lastReservationId").description("마지막 예약 아이디").optional()
                ),
                responseFields(
                    fieldWithPath("data[].reservation.id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("data[].reservation.checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("data[].reservation.checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("data[].reservation.totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("data[].reservation.totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("data[].reservation.reservationStatus").type(JsonFieldType.STRING)
                        .description("예약 상태"),
                    fieldWithPath("data[].room.roomId").type(JsonFieldType.NUMBER).description("숙소 아이디"),
                    fieldWithPath("data[].room.name").type(JsonFieldType.STRING).description("숙소 이름"),
                    fieldWithPath("data[].room.roomAddress.zipcode").type(JsonFieldType.STRING)
                        .description("숙소 우편번호"),
                    fieldWithPath("data[].room.roomAddress.address").type(JsonFieldType.STRING)
                        .description("숙소 주소"),
                    fieldWithPath("data[].room.roomAddress.detailAddress").type(JsonFieldType.STRING)
                        .description("숙소 상세주소"),
                    fieldWithPath("data[].guest.id").type(JsonFieldType.NUMBER).description("게스트 아이디"),
                    fieldWithPath("data[].guest.name").type(JsonFieldType.STRING).description("게스트 이름"),
                    fieldWithPath("data[].guest.email").type(JsonFieldType.STRING).description("게스트 이메일")
                )));
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
        MockHttpServletResponse response = mockMvc.perform(post("/host/rooms")
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

    private CreateReservationRequest createReservationRequestByDay(int day) {
        return CreateReservationRequest.builder()
            .checkIn(now().plusDays(day))
            .checkOut(now().plusDays(day + 1))
            .totalGuest(1)
            .totalPrice(100_000)
            .roomId(roomId)
            .build();
    }

}

