package com.prgrms.amabnb.room.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.service.CreateRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CreateRoomService createRoomService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void createRoom() throws Exception {
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoomRequest))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andDo(document("room-create",
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NULL).description("userId"),
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
                                fieldWithPath("roomScope").type(JsonFieldType.STRING).description("roomScope")
                        )
                ));
    }

    private CreateRoomRequest createCreateRoomRequest() {
        return CreateRoomRequest.builder()
                .price(1)
                .description("방설명")
                .maxGuestNum(1)
                .zipcode("00000")
                .address("창원")
                .detailAddress("의창구")
                .bedCnt(2)
                .bedRoomCnt(1)
                .bathRoomCnt(1)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build();
    }
}