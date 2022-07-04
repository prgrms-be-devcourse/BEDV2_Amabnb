package com.prgrms.amabnb.reservation.api;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prgrms.amabnb.common.exception.ErrorResponse;
import com.prgrms.amabnb.common.model.ApiResponse;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationUpdateRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.security.oauth.UserProfile;

class ReservationGuestApiTest extends ApiTest {

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
                    getReservationResponseForGuestDescriptor()
                )))
            .andReturn().getResponse();

        // then
        ApiResponse<ReservationResponseForGuest> apiResponse = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(apiResponse.data().getReservation().getId()).isPositive()
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
    void xcreate_reservation_invalid_price() throws Exception {
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
        LocalDate now = now();
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
                    fieldWithPath("data[].checkIn").description("예약 체크인 날짜"),
                    fieldWithPath("data[].checkOut").description("예약 체크아웃 날짜")
                )))
            .andReturn().getResponse();

        // then
        ApiResponse<List<ReservationDateResponse>> apiResponse = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(apiResponse.data()).extracting("checkIn", "checkOut")
                .containsExactly(tuple(now, now.plusDays(2L)))
        );
    }

    @DisplayName("게스트가 예약을 취소한다. 204 - NO CONTENT")
    @Test
    void cancelByGuest() throws Exception {
        String accessToken = 로그인_요청(createSpancerProfile());
        MockHttpServletResponse reservationResponse = 예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(delete("/guest/reservations/{reservationId}", reservationId)
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

    @DisplayName("게스트가 예약 정보를 단건 조회한다. 200 - OK")
    @Test
    void getReservation() throws Exception {
        // given
        String accessToken = 로그인_요청(createSpancerProfile());
        MockHttpServletResponse reservationResponse = 예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);

        // when
        mockMvc.perform(get("/guest/reservations/{reservationId}", reservationId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

            // then
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data.reservation.id").value(reservationId)
            )

            // docs
            .andDo(document("reservation-guest-findOne",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                ),
                responseFields(
                    getReservationResponseForGuestDescriptor()
                )));
    }

    @DisplayName("게스트가 예약 정보들을 조회한다. 200 - OK")
    @Test
    void getReservations() throws Exception {
        // given
        String accessToken = 로그인_요청(createSpancerProfile());
        for (int i = 0; i < 2; i++) {
            예약_요청(accessToken, createReservationRequestByDay(i));
        }

        // when
        mockMvc.perform(get("/guest/reservations")
                .param("pageSize", "2")
                .param("status", "PENDING")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())

            // then
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data", hasSize(2)),
                jsonPath("$.data[0].reservation.reservationStatus", PENDING).exists()
            )

            // docs
            .andDo(document("reservation-guest-find",
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
                    fieldWithPath("data[].host.id").type(JsonFieldType.NUMBER).description("호스트 아이디"),
                    fieldWithPath("data[].host.name").type(JsonFieldType.STRING).description("호스트 이름"),
                    fieldWithPath("data[].host.email").type(JsonFieldType.STRING).description("호스트 이메일")
                )));
    }

    @DisplayName("게스트가 예약 정보를 수정한다. 200 - OK")
    @Test
    void modifyReservation() throws Exception {
        // given
        String accessToken = 로그인_요청(createUserProfile());
        MockHttpServletResponse reservationResponse = 예약_요청(accessToken, createReservationRequest(3, 300_000, roomId));
        Long reservationId = getReservationId(reservationResponse);
        ReservationUpdateRequest request = new ReservationUpdateRequest(now().plusDays(5L), 5, 200_000);

        // when
        mockMvc.perform(put("/reservations/{reservationId}", reservationId)
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andDo(print())

            // then
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data.reservation.checkOut").value(now().plusDays(5L).toString()),
                jsonPath("$.data.reservation.totalGuest").value(5),
                jsonPath("$.data.reservation.totalPrice").value(500_000)
            )

            // docs
            .andDo(document("reservation-update",
                tokenRequestHeader(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                ),
                requestFields(
                    fieldWithPath("checkOut").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("totalGuest").type(JsonFieldType.NUMBER).description("총 인원수"),
                    fieldWithPath("paymentPrice").type(JsonFieldType.NUMBER).description("추가 가격")
                ),
                responseFields(
                    getReservationResponseForGuestDescriptor()
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

        return getReservationId(response);
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
            .checkIn(now())
            .checkOut(now().plusDays(3L))
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

    private List<FieldDescriptor> getReservationResponseForGuestDescriptor() {
        return List.of(
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
            fieldWithPath("data.host.id").type(JsonFieldType.NUMBER).description("호스트 아이디"),
            fieldWithPath("data.host.name").type(JsonFieldType.STRING).description("호스트 이름"),
            fieldWithPath("data.host.email").type(JsonFieldType.STRING).description("호스트 이메일")
        );
    }
}
