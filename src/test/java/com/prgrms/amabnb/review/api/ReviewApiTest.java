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

        givenGuestAccessToken = ?????????_??????(guest.getName());
        givenReservationId = extractId(??????_??????(givenGuestAccessToken, makeCreateReservationRequest(room)));
    }

    ResultActions ??????_??????(Long reservationId, String userAccessToken,
        CreateReviewRequest createReviewDto) throws Exception {
        return mockMvc.perform(post("/reservations/{reservationId}/reviews", reservationId)
            .header(HttpHeaders.AUTHORIZATION, userAccessToken)
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(toJson(createReviewDto)));
    }

    void ??????_??????_??????(Long reservationId, ReservationStatus status) {
        var reservation = reservationRepository.findById(reservationId).get();
        reservation.changeStatus(status);
        reservationRepository.save(reservation);
    }

    @Nested
    @DisplayName("???????????? ???????????? ????????? ????????? ????????? ??? ?????? #68")
    class CreateReview {
        CreateReviewRequest givenReviewRequest;

        @BeforeEach
        void setAdditionalGiven() {
            givenReviewRequest = new CreateReviewRequest("content", 5);
        }

        @Test
        @DisplayName("????????? ????????? ??? ??????")
        void createReview() throws Exception {
            ??????_??????_??????(givenReservationId, COMPLETED);
            var reserv = reservationRepository.findById(givenReservationId).get();
            ??????_??????(reserv.getId(), givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/reviews/*"))
                .andDo(createReviewDoc());
        }

        private RestDocumentationResultHandler createReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reservationId").description("?????? ?????????")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                ),
                responseHeaders(
                    headerWithName("Location").description("????????? ?????? URI")
                )
            );
        }

        @ParameterizedTest
        @DisplayName("????????? ????????? ??????(COMPLETED)??? ?????? ????????? ????????? ??? ??????")
        @EnumSource(value = ReservationStatus.class, names = {"PENDING", "APPROVED", "GUEST_CANCELED", "HOST_CANCELED"})
        void create_review_exception1(ReservationStatus status) throws Exception {
            var errorMessage = "?????? ?????? ?????? ??? ????????? ????????? ??? ????????????.";
            ??????_??????_??????(givenReservationId, status);

            ??????_??????(givenReservationId, givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("????????? ?????? ??? ?????? ??? ?????? ????????? ??? ??????")
        void create_review_exception2() throws Exception {
            var errorMessage = "?????? ????????? ?????? ??? ?????????.";
            ??????_??????_??????(givenReservationId, COMPLETED);

            var firstReview = ??????_??????(givenReservationId, givenGuestAccessToken, givenReviewRequest);
            firstReview.andExpect(status().isCreated());

            var secondReview = ??????_??????(givenReservationId, givenGuestAccessToken, givenReviewRequest);
            secondReview.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("????????? ????????? ????????? ????????? ??? ??????")
        void create_review_exception3() throws Exception {
            var errorMessage = "????????? ?????? ????????? ???????????? ????????????.";
            ??????_??????_??????(givenReservationId, COMPLETED);

            var illegalToken = ?????????_??????("illegal-user");

            ??????_??????(givenReservationId, illegalToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ????????? ?????? ????????? ????????? ??? ?????? #70")
    class SearchMyReviews {

        List<String> guestTokens = new ArrayList<>();

        ResultActions ??????_??????_??????(
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

                var guestAccessToken = ?????????_??????(guest.getName());

                guestTokens.add(guestAccessToken);

                var reservationId = extractId(??????_??????(guestAccessToken, makeCreateReservationRequest(room)));
                ??????_??????_??????(reservationId, COMPLETED);

                var review = new CreateReviewRequest("content", 1);
                ??????_??????(reservationId, guestAccessToken, review);
            }
        }

        @Test
        @DisplayName("????????? ?????? ????????? ?????? ????????????")
        void searchMyReviews() throws Exception {
            var givenSearchRequest = new SearchReviewRequest(1);
            var givenPageReviewRequest = new PageReviewRequest(0, 10);

            ??????_??????_??????(guestTokens.get(0), givenSearchRequest, givenPageReviewRequest)
                .andExpect(status().isOk())
                .andDo(searchMyReviewsDoc());
        }

        private RestDocumentationResultHandler searchMyReviewsDoc() {
            return document.document(
                requestParameters(
                    parameterWithName("score").description("?????? ??????"),
                    parameterWithName("size").description("????????? ?????????"),
                    parameterWithName("page").description("????????? ??????")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access ??????")
                ),
                responseFields(
                    fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                )
            );
        }

    }

    @Nested
    @DisplayName("???????????? ????????? ????????? ????????? ??? ?????? #67")
    class SearchRoomReviews {
        Room givenRoom;
        String givenUserAccessToken;

        ResultActions ??????_??????_??????(Long roomId, MultiValueMap<String, String> params) throws Exception {
            return mockMvc.perform(get("/rooms/{roomId}/reviews", roomId)
                .header(HttpHeaders.AUTHORIZATION, givenUserAccessToken)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .params(params)
            );
        }


        @BeforeEach
        void setAdditionalGiven() throws Exception {
            var host = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            givenRoom = roomRepository.save(createRoom(host));
            givenUserAccessToken = ?????????_??????("su");
            for (int i = 0; i < 5; i++) {
                writeReview(givenRoom, 5);
            }
            writeReview(givenRoom, 1);
            writeReview(givenRoom, 2);
            writeReview(givenRoom, 3);
        }

        void writeReview(Room room, int score) throws Exception {
            var guest = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            var accessToken = ?????????_??????(guest.getName());
            var reservation = reservationRepository.save(createReservation(room, guest));
            ??????_??????_??????(reservation.getId(), COMPLETED);
            ??????_??????(reservation.getId(), accessToken, new CreateReviewRequest("content", score));
        }

        @Test
        @DisplayName("????????? ?????? (paging & score) ????????? ?????? ????????????")
        void searchRoomReviews() throws Exception {
            var givenSearchRequest = new SearchReviewRequest(5);
            var givenPageReviewRequest = new PageReviewRequest(0, 10);

            ??????_??????_??????(givenRoom.getId(), makeParamMap(givenSearchRequest, givenPageReviewRequest))
                .andExpect(status().isOk())
                .andDo(searchRoomReviewsDoc());
        }
        @Test
        @DisplayName("?????? ????????? (no paging) ?????? ????????? ?????? ????????????")
        void searchRoomReviews2() throws Exception {
            var givenSearchRequest = new SearchReviewRequest(5);
            ??????_??????_??????(givenRoom.getId(), noPagingParam(givenSearchRequest))
                .andExpect(status().isOk())
                .andDo(print());
        }
        @Test
        @DisplayName("?????? ????????? (no score) ?????? ????????? ?????? ????????????")
        void searchRoomReviews3() throws Exception {
            var givenPageReviewRequest = new PageReviewRequest(0, 10);
            ??????_??????_??????(givenRoom.getId(), noScoreParam(givenPageReviewRequest))
                .andExpect(status().isOk())
                .andDo(print());
        }
        MultiValueMap<String, String> makeParamMap(SearchReviewRequest request, PageReviewRequest page){
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("score", String.valueOf(request.getScore()));
            params.add("size", String.valueOf(page.getSize()));
            params.add("page", String.valueOf(page.getPage()));
            return params;
        }
        MultiValueMap<String, String> noPagingParam(SearchReviewRequest request){
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("score", String.valueOf(request.getScore()));
            return params;
        }
        MultiValueMap<String, String> noScoreParam(PageReviewRequest page){
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("size", String.valueOf(page.getSize()));
            params.add("page", String.valueOf(page.getPage()));
            return params;
        }
        private RestDocumentationResultHandler searchRoomReviewsDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("roomId").description("?????? ?????????")
                ),
                requestParameters(
                    parameterWithName("score").description("?????? ??????"),
                    parameterWithName("size").description("????????? ?????????"),
                    parameterWithName("page").description("????????? ??????")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access ??????")
                ),
                responseFields(
                    fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                )
            );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ????????? ????????? ????????? ??? ?????? #81")
    class EditReview {
        Review givenReview;
        EditReviewRequest givenEditDto;

        ResultActions ??????_??????(String userAccessToken, Long reviewId,
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
        @DisplayName("????????? ????????? ??? ??????")
        void editReview() throws Exception {
            ??????_??????(givenGuestAccessToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(givenEditDto.getContent()))
                .andExpect(jsonPath("$.data.score").value(givenEditDto.getScore()))
                .andDo(editReviewDoc());
        }

        private RestDocumentationResultHandler editReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reviewId").description("?????? ?????????")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                ),
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("JWT access ??????")
                ),
                responseFields(
                    fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                )
            );
        }

        @Test
        @DisplayName("????????? ???????????? ?????? ????????? ????????? ??? ??????")
        void noPermission() throws Exception {
            var errorMessage = "????????? ?????? ????????? ???????????? ????????????.";
            var illegalToken = ?????????_??????("illegal-user");

            ??????_??????(illegalToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ????????? ????????? ????????? ??? ?????? #82")
    class DeleteReview {
        Review givenReview;

        ResultActions ??????_??????(String userAccessToken, Long reviewId) throws Exception {
            return mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
                .header(HttpHeaders.AUTHORIZATION, userAccessToken));
        }

        @BeforeEach
        @Transactional
        void setAdditionalGiven() {
            givenReview = reviewRepository.save(new Review("content", 4, new Reservation(givenReservationId)));
        }

        @Test
        @DisplayName("????????? ????????? ??? ??????")
        void deleteReview() throws Exception {
            assertThat(reviewRepository.count()).isOne();

            ??????_??????(givenGuestAccessToken, givenReview.getId())
                .andExpect(status().isNoContent())
                .andDo(deleteReviewDoc());

            assertThat(reviewRepository.count()).isZero();
        }

        private RestDocumentationResultHandler deleteReviewDoc() {
            return document.document(
                pathParameters(
                    parameterWithName("reviewId").description("?????? ?????????")
                )
            );
        }

        @Test
        @DisplayName("????????? ????????? ????????? ????????? ??? ??????")
        void deleteReviewno() throws Exception {
            var errorMessage = "????????? ?????? ????????? ???????????? ????????????.";
            assertThat(reviewRepository.count()).isOne();

            var illegalToken = ?????????_??????("illegal-user");

            ??????_??????(illegalToken, givenReview.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());

            assertThat(reviewRepository.count()).isOne();
        }
    }

}
