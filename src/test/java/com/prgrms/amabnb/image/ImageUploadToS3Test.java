package com.prgrms.amabnb.image;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.prgrms.amabnb.common.infra.s3.AWSS3Uploader;
import com.prgrms.amabnb.config.ApiTest;

public class ImageUploadToS3Test extends ApiTest {
    
    @MockBean
    private AWSS3Uploader awss3Uploader;

    @Nested
    class image_upload {
        @Test
        @DisplayName("파일이 정상적으로 업로드 됐을때")
        void 성공() throws Exception {
            var fileResource = new ClassPathResource("imagefile.jpeg");
            MockMultipartFile firstFile = new MockMultipartFile(
                "file",
                fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());

            when(awss3Uploader.uploadImage(List.of())).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders
                    .multipart("/room-images")
                    .file("images", firstFile.getBytes())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andDo(print())
                .andDo(document("room-images",
                    requestPartBody("images"),
                    responseFields(
                        fieldWithPath("[]").description("List of s3 image path").optional()
                    ))
                );
        }

        @Test
        @DisplayName("파일이 업로드가 실패했을 때")
        void 실패() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .multipart("/room-images")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8")
                )
                .andExpect(status().is5xxServerError());
        }
    }
}
