package com.prgrms.amabnb.review.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.dto.request.PageReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchReviewRequest;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.repository.ReviewRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReviewApiTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepository roomRepository;

    private String givenGuestAccessToken;
    private Long givenReservationId;

    @BeforeEach
    void setBasicGiven() throws Exception {
        var guest = userRepository.save(createUser("guest"));
        var host = userRepository.save(createUser("host"));
        var room = roomRepository.save(createRoom(host));

        givenGuestAccessToken = 로그인_요청(guest.getName());
        givenReservationId = extractId(예약_요청(givenGuestAccessToken, makeCreateReservationRequest(room)));
    }

    ResultActions 리뷰_작성(Long reservationId, String userAccessToken,
        CreateReviewRequest createReviewDto) throws Exception {
        return mockMvc.perform(post("/reservations/{reservationId}/reviews", reservationId)
            .header(HttpHeaders.AUTHORIZATION, userAccessToken)
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(toJson(createReviewDto)));
    }

    void 예약_상태_변경(Long reservationId, ReservationStatus status) {
        var reservation = reservationRepository.findById(reservationId).get();
        reservation.changeStatus(status);
        reservationRepository.save(reservation);
    }

    @Nested
    @DisplayName("게스트는 예약했던 숙소에 리뷰를 작성할 수 있다 #68")
    class CreateReview {
        CreateReviewRequest givenReviewRequest;

        @BeforeEach
        void setAdditionalGiven() {
            givenReviewRequest = new CreateReviewRequest("content", 5);
        }

        @Test
        @DisplayName("리뷰를 작성할 수 있다")
        void createReview() throws Exception {
            예약_상태_변경(givenReservationId, COMPLETED);
            var reserv = reservationRepository.findById(givenReservationId).get();
            리뷰_작성(reserv.getId(), givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/reviews/*"))
                .andDo(createReviewDoc());
        }

        private RestDocumentationResultHandler createReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reservationId").description("예약 아이디")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰 점수"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용 본문")
                ),
                responseHeaders(
                    headerWithName("Location").description("생성된 리뷰 URI")
                )
            );
        }

        @ParameterizedTest
        @DisplayName("숙소를 방문을 완료(COMPLETED)한 후에 리뷰를 작성할 수 있다")
        @EnumSource(value = ReservationStatus.class, names = {"PENDING", "APPROVED", "GUEST_CANCELED", "HOST_CANCELED"})
        void create_review_exception1(ReservationStatus status) throws Exception {
            var errorMessage = "숙소 방문 완료 후 리뷰를 작성할 수 있습니다.";
            예약_상태_변경(givenReservationId, status);

            리뷰_작성(givenReservationId, givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("리뷰는 예약 한 건당 한 개만 작성할 수 있다")
        void create_review_exception2() throws Exception {
            var errorMessage = "이미 작성한 예약 건 입니다.";
            예약_상태_변경(givenReservationId, COMPLETED);

            var firstReview = 리뷰_작성(givenReservationId, givenGuestAccessToken, givenReviewRequest);
            firstReview.andExpect(status().isCreated());

            var secondReview = 리뷰_작성(givenReservationId, givenGuestAccessToken, givenReviewRequest);
            secondReview.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("예약자 본인만 리뷰를 작성할 수 있다")
        void create_review_exception3() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";
            예약_상태_변경(givenReservationId, COMPLETED);

            var illegalToken = 로그인_요청("illegal-user");

            리뷰_작성(givenReservationId, illegalToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰 목록을 조회할 수 있다 #70")
    class SearchMyReviews {

        List<String> guestTokens = new ArrayList<>();

        ResultActions 본인_리뷰_조회(
            String guestUserAccessToken, SearchReviewRequest request, PageReviewRequest page) throws Exception {

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("score", String.valueOf(request.getScore()));
            params.add("size", String.valueOf(page.getSize()));
            params.add("page", String.valueOf(page.getPage()));

            return mockMvc.perform(get("/reviews")
                .header(HttpHeaders.AUTHORIZATION, guestUserAccessToken)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .params(params));
        }

        @BeforeEach
        void setMultiValues() throws Exception {
            for (int i = 0; i < 5; i++) {
                var guest = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
                var host = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
                var room = roomRepository.save(createRoom(host));

                var guestAccessToken = 로그인_요청(guest.getName());

                guestTokens.add(guestAccessToken);

                var reservationId = extractId(예약_요청(guestAccessToken, makeCreateReservationRequest(room)));
                예약_상태_변경(reservationId, COMPLETED);

                var review = new CreateReviewRequest("content", 1);
                리뷰_작성(reservationId, guestAccessToken, review);
            }
        }

        @Test
        @DisplayName("조건에 맞는 리뷰를 전부 가져온다")
        void searchMyReviews() throws Exception {
            var givenSearchRequest = new SearchReviewRequest(1);
            var givenPageReviewRequest = new PageReviewRequest(0, 10);

            본인_리뷰_조회(guestTokens.get(0), givenSearchRequest, givenPageReviewRequest)
                .andExpect(status().isOk())
                .andDo(searchMyReviewsDoc());
        }

        private RestDocumentationResultHandler searchMyReviewsDoc() {
            return document.document(
                requestParameters(
                    parameterWithName("score").description("리뷰 점수"),
                    parameterWithName("size").description("페이지 사이즈"),
                    parameterWithName("page").description("페이지 번호")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access 토큰")
                ),
                responseFields(
                    fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("리뷰 점수"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용 본문")
                )
            );
        }

    }

    @Nested
    @DisplayName("게스트는 숙소의 리뷰를 조회할 수 있다 #67")
    class SearchRoomReviews {

        Room givenRoom;
        String givenUserAccessToken;

        ResultActions 숙소_리뷰_조회(
            Long roomId, SearchReviewRequest request, PageReviewRequest page) throws Exception {

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("score", String.valueOf(request.getScore()));
            params.add("size", String.valueOf(page.getSize()));
            params.add("page", String.valueOf(page.getPage()));

            return mockMvc.perform(get("/rooms/{roomId}/reviews", roomId)
                .header(HttpHeaders.AUTHORIZATION, givenUserAccessToken)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .params(params));
        }

        @BeforeEach
        void setAdditionalGiven() throws Exception {
            var host = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            givenRoom = roomRepository.save(createRoom(host));
            givenUserAccessToken = 로그인_요청("su");
            for (int i = 0; i < 5; i++) {
                writeReview(givenRoom, 5);
            }
            writeReview(givenRoom, 1);
            writeReview(givenRoom, 2);
            writeReview(givenRoom, 3);
        }

        void writeReview(Room room, int score) throws Exception {
            var guest = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            var accessToken = 로그인_요청(guest.getName());
            var reservation = reservationRepository.save(createReservation(room, guest));
            예약_상태_변경(reservation.getId(), COMPLETED);
            리뷰_작성(reservation.getId(), accessToken, new CreateReviewRequest("content", score));
        }

        @Test
        @DisplayName("조건에 맞는 리뷰를 전부 가져온다")
        void searchRoomReviews() throws Exception {
            var givenSearchRequest = new SearchReviewRequest(5);
            var givenPageReviewRequest = new PageReviewRequest(0, 10);

            숙소_리뷰_조회(givenRoom.getId(), givenSearchRequest, givenPageReviewRequest)
                .andExpect(status().isOk())
                .andDo(searchRoomReviewsDoc());
        }

        private RestDocumentationResultHandler searchRoomReviewsDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("roomId").description("숙소 아이디")
                ),
                requestParameters(
                    parameterWithName("score").description("리뷰 점수"),
                    parameterWithName("size").description("페이지 사이즈"),
                    parameterWithName("page").description("페이지 번호")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access 토큰")
                ),
                responseFields(
                    fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("리뷰 점수"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용 본문")
                )
            );
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 수정할 수 있다 #81")
    class EditReview {
        Review givenReview;
        EditReviewRequest givenEditDto;

        ResultActions 리뷰_수정(String userAccessToken, Long reviewId,
            EditReviewRequest editReviewDto) throws Exception {
            return mockMvc.perform(post("/reviews/{reviewId}", reviewId)
                .header(HttpHeaders.AUTHORIZATION, userAccessToken)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(toJson(editReviewDto)));
        }

        @BeforeEach
        void setAdditionalGiven() {
            givenReview = reviewRepository.save(new Review(1L, "content", 4, new Reservation(givenReservationId)));
            givenEditDto = new EditReviewRequest("edit-content", 2);
        }

        @Test
        @DisplayName("리뷰를 수정할 수 있다")
        void editReview() throws Exception {
            리뷰_수정(givenGuestAccessToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(givenEditDto.getContent()))
                .andExpect(jsonPath("$.data.score").value(givenEditDto.getScore()))
                .andDo(editReviewDoc());
        }

        private RestDocumentationResultHandler editReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 아이디")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰 점수"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용 본문")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access 토큰")
                ),
                responseFields(
                    fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("리뷰 점수"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용 본문")
                )
            );
        }

        @Test
        @DisplayName("본인이 작성하지 않은 리뷰는 수정할 수 없다")
        void noPermission() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";
            var illegalToken = 로그인_요청("illegal-user");

            리뷰_수정(illegalToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 삭제할 수 있다 #82")
    class DeleteReview {
        Review givenReview;

        ResultActions 리뷰_삭제(String userAccessToken, Long reviewId) throws Exception {
            return mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
                .header(HttpHeaders.AUTHORIZATION, userAccessToken));
        }

        @BeforeEach
        @Transactional
        void setAdditionalGiven() {
            givenReview = reviewRepository.save(new Review("content", 4, new Reservation(givenReservationId)));
        }

        @Test
        @DisplayName("리뷰를 삭제할 수 있다")
        void deleteReview() throws Exception {
            assertThat(reviewRepository.count()).isOne();

            리뷰_삭제(givenGuestAccessToken, givenReview.getId())
                .andExpect(status().isNoContent())
                .andDo(deleteReviewDoc());

            assertThat(reviewRepository.count()).isZero();
        }

        private RestDocumentationResultHandler deleteReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 아이디")
                )
            );
        }

        @Test
        @DisplayName("예약자 본인만 리뷰를 삭제할 수 있다")
        void deleteReviewno() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";
            assertThat(reviewRepository.count()).isOne();

            var illegalToken = 로그인_요청("illegal-user");

            리뷰_삭제(illegalToken, givenReview.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());

            assertThat(reviewRepository.count()).isOne();
        }
    }

}
