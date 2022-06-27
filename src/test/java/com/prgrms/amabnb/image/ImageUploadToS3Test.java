package com.prgrms.amabnb.image;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.prgrms.amabnb.infra.s3.AWSS3Uploader;

@SpringBootTest
public class ImageUploadToS3Test {

    private MockMvc mockMvc;

    @MockBean
    private AWSS3Uploader awss3Uploader;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();
    }

    @Nested
    class image_upload {
        @Test
        @DisplayName("파일이 정상적으로 업로드 됐을때")
        void 성공() throws Exception {
            var fileResource = new ClassPathResource("imagefile.jpeg");
            MockMultipartFile firstFile = new MockMultipartFile(
                "file", fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());

            when(awss3Uploader.upload(List.of(), "testDir")) // when: 특정 메서드가 호출되면 해당 데이터 반환
                .thenReturn(List.of("something url1"));

            mockMvc.perform(MockMvcRequestBuilders
                    .multipart("/room-images")
                    .file("images", firstFile.getBytes())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk());
        }
    }
}
