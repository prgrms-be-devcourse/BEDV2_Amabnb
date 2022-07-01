package com.prgrms.amabnb.reservation.api;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.prgrms.amabnb.common.exception.ErrorResponse;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDatesResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.security.oauth.UserProfile;

class ReservationControllerTest extends ApiTest {

    @Autowired
    private OAuthService oAuthService;

    private Long roomId;

    private String hostAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        hostAccessToken = 로그인_요청(createHostProfile());
        roomId = 숙소_등록(hostAccessToken, createRoomRequest("별빛밤"));
    }

    @DisplayName("유저는 숙소를 예약할 수 있다. 201 - CREATED")
    @Test
    void create_reservation() throws Exception {
        // given
        String accessToken = 로그인_요청(createUserProfile());
        CreateReservationRequest request = createReservationRequest(1, 300_000, roomId);

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/reservations")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(document("reservation-create",
                tokenRequestHeader(),
                requestFields(
                    fieldWithPath("checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("숙소 아이디")
                ),
                responseHeaders(
                    headerWithName("Location").description("예약 리소스 주소")
                ),
                responseFields(
                    fieldWithPath("reservation.id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("reservation.checkIn").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("reservation.checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("reservation.totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("reservation.totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                    fieldWithPath("reservation.reservationStatus").type(JsonFieldType.STRING).description("예약 상태"),
                    fieldWithPath("room.roomId").type(JsonFieldType.NUMBER).description("숙소 아이디"),
                    fieldWithPath("room.roomAddress.zipcode").type(JsonFieldType.STRING)
                        .description("숙소 우편번호"),
                    fieldWithPath("room.roomAddress.address").type(JsonFieldType.STRING)
                        .description("숙소 주소"),
                    fieldWithPath("room.roomAddress.detailAddress").type(JsonFieldType.STRING)
                        .description("숙소 상세주소"),
                    fieldWithPath("host.hostId").type(JsonFieldType.NUMBER).description("호스트 아이디"),
                    fieldWithPath("host.name").type(JsonFieldType.STRING).description("호스트 이름"),
                    fieldWithPath("host.email").type(JsonFieldType.STRING).description("호스트 이메일")
                )))
            .andReturn().getResponse();

        // then
        ReservationResponseForGuest reservationResponseForGuest = objectMapper.readValue(response.getContentAsString(),
            ReservationResponseForGuest.class);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(reservationResponseForGuest.getReservation().getId()).isPositive()
        );

    }

    @DisplayName("로그인을 하지 않고 예약 할 수 없다. 401 - UNAUTHORIZED ")
    @Test
    void create_reservation_not_login() throws Exception {
        // given
        CreateReservationRequest request = createReservationRequest(10, 100_000, 1L);

        // when
        MockHttpServletResponse response = 예약_요청("not login", request);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("로그인이 필요합니다.")
        );
    }

    @DisplayName("숙소가 지정한 인원 수보다 예약인원이 많을 경우 예약 할 수 없다. 400 - BAD REQUEST")
    @Test
    void create_reservation_over_max_guest_num() throws Exception {
        // given
        String accessToken = 로그인_요청(createUserProfile());
        CreateReservationRequest request = createReservationRequest(100, 300_000, roomId);

        // when
        MockHttpServletResponse response = 예약_요청(accessToken, request);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("숙소의 최대 인원을 넘을 수 없습니다.")
        );
    }

    @DisplayName("숙소 가격이 일치하지 않으면 예약할 수 없다. 400 - BAD REQUEST")
    @Test
    void create_reservation_invalid_price() throws Exception {
        String accessToken = 로그인_요청(createUserProfile());
        CreateReservationRequest request = createReservationRequest(1, 200_000, roomId);

        // when
        MockHttpServletResponse response = 예약_요청(accessToken, request);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("숙소 가격이 일치하지 않습니다.")
        );
    }

    @DisplayName("예약 기간에 이미 예약된 숙소는 예약할 수 없다. 400 - BAD REQUEST")
    @Test
    void create_reservation_already_reserved_room() throws Exception {
        CreateReservationRequest request = createReservationRequest(3, 300_000, roomId);
        예약_요청(로그인_요청(createSpancerProfile()), request);

        String accessToken = 로그인_요청(createUserProfile());

        // when
        MockHttpServletResponse response = 예약_요청(accessToken, request);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("해당 숙소가 이미 예약된 기간입니다.")
        );
    }

    @DisplayName("예약 기간에 다른 예약을 한 유저는 예약할 수 없다. 400 - BAD REQUEST")
    @Test
    void create_reservation_already_reserved_user() throws Exception {
        // given
        String accessToken = 로그인_요청(createUserProfile());
        예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));

        Long anotherRoomId = 숙소_등록(hostAccessToken, createRoomRequest("검은밤"));

        // when
        CreateReservationRequest invalidRequest = createReservationRequest(3, 300_000, anotherRoomId);
        MockHttpServletResponse response = 예약_요청(accessToken, invalidRequest);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("귀하가 이미 숙소를 예약한 기간입니다.")
        );
    }

    @DisplayName("존재하지 않는 숙소를 예약할 수 없다. 404 - NOT FOUND")
    @Test
    void create_reservation_room_not_found() throws Exception {
        // given
        String accessToken = 로그인_요청(createUserProfile());
        CreateReservationRequest request = createReservationRequest(10, 100_000, 1L);

        // when
        MockHttpServletResponse response = 예약_요청(accessToken, request);

        // then
        ErrorResponse errorResponse = extractErrorResponse(response);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()),
            () -> assertThat(errorResponse.getMessage()).isEqualTo("존재하지 않는 숙소입니다")
        );
    }

    @DisplayName("예약 불가능한 날짜를 조회한다. 200 - OK")
    @Test
    void getReservationDates() throws Exception {
        // given
        String accessToken = 로그인_요청(createSpancerProfile());
        LocalDate now = LocalDate.now();
        예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startDate", now.toString());
        params.add("endDate", now.plusMonths(1L).toString());

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/rooms/{roomId}/reservations-date", roomId)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .params(params)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservation-date",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("roomId").description("숙소 아이디")
                ),
                requestParameters(
                    parameterWithName("startDate").description("조회 시작 날짜"),
                    parameterWithName("endDate").description("조회 끝 날짜")
                ),
                responseFields(
                    fieldWithPath("reservationDates[].checkIn").description("예약 체크인 날짜"),
                    fieldWithPath("reservationDates[].checkOut").description("예약 체크아웃 날짜")
                )))
            .andReturn().getResponse();

        // then
        ReservationDatesResponse result = objectMapper.readValue(response.getContentAsString(),
            ReservationDatesResponse.class);
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(result.getReservationDates()).extracting("checkIn", "checkOut")
                .containsExactly(
                    tuple(now, now.plusDays(2L))
                )
        );
    }

    @DisplayName("호스트가 예약을 승인한다. 200 - OK")
    @Test
    void approveReservation() throws Exception {
        // given
        MockHttpServletResponse reservationResponse = 예약_요청(로그인_요청(createSpancerProfile()),
            createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(put("/hosts/reservations/{reservationId}", reservationId)
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
        MockHttpServletResponse response = mockMvc.perform(delete("/hosts/reservations/{reservationId}", reservationId)
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

    @DisplayName("게스트가 예약을 취소한다. 204 - NO CONTENT")
    @Test
    void cancelByGuest() throws Exception {
        String accessToken = 로그인_요청(createSpancerProfile());
        MockHttpServletResponse reservationResponse = 예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(delete("/guests/reservations/{reservationId}", reservationId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservation-guest-cancel",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                )))
            .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private RequestHeadersSnippet tokenRequestHeader() {
        return requestHeaders(
            headerWithName(AUTHORIZATION).description("JWT access 토큰")
        );
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

    private String 로그인_요청(UserProfile userProfile) {
        return "Bearer" + oAuthService.register(userProfile).accessToken();
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

    private UserProfile createUserProfile() {
        return UserProfile.builder()
            .oauthId("1")
            .provider("kakao")
            .name("아만드")
            .email("asdasd@gmail.com")
            .profileImgUrl("url")
            .build();
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

    private ErrorResponse extractErrorResponse(MockHttpServletResponse response) throws IOException {
        return objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);
    }

}

