package com.prgrms.amabnb.room.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageRequestDto {

    private int page;
    private int size;

}
