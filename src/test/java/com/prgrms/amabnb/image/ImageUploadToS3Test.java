package com.prgrms.amabnb.image;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.image.service.ImageUploader;

public class ImageUploadToS3Test extends ApiTest {

    @Autowired
    private ImageUploader awss3Uploader;

    @Nested
    class image_upload {
        @Test
        @WithMockUser
        @DisplayName("파일이 정상적으로 업로드 됐을때")
        void success() throws Exception {
            // given
            var fileResource = new ClassPathResource("imagefile.jpeg");
            MockMultipartFile firstFile = new MockMultipartFile(
                "images",
                fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());

            // when
            mockMvc.perform(MockMvcRequestBuilders
                    .multipart("/room-images")
                    .file(firstFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8")
                )

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value("https://s3.amand.com/0"))
                .andDo(print())

                // docs
                .andDo(document.document(
                    requestParts(
                        partWithName("images").description("이미지 파일 배열")
                    ),
                    responseFields(
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("s3 이미지 경로 배열")
                    )));
        }
    }
}
