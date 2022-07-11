DROP TABLE IF EXISTS token;

DROP TABLE IF EXISTS room_image;

DROP TABLE IF EXISTS review;

DROP TABLE IF EXISTS reservation;

DROP TABLE IF EXISTS room;

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id              bigint       NOT NULL auto_increment,
    oauth_id        varchar(100) NOT NULL,
    provider        varchar(20)  NOT NULL,
    name            varchar(20)  NOT NULL,
    email           varchar(100) NOT NULL,
    profile_img_url varchar(255) NOT NULL,
    user_role       varchar(10)  NOT NULL,
    created_at      datetime     NOT NULL,
    updated_at      datetime     NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table users
    add unique key (email);

CREATE TABLE room
(
    id             bigint       NOT NULL auto_increment,
    name           varchar(255) NOT NULL,
    price          int          NOT NULL,
    address        varchar(255) NOT NULL,
    detail_address varchar(255) NULL,
    bed_cnt        int          NOT NULL,
    bed_room_cnt   int          NOT NULL,
    bath_room_cnt  int          NOT NULL,
    description    longtext     NOT NULL,
    zipcode        varchar(10)  NOT NULL,
    max_guest_num  int          NOT NULL,
    room_type      varchar(255) NOT NULL,
    room_scope     varchar(255) NOT NULL,
    host_id        bigint       NOT NULL,
    created_at     datetime     NOT NULL,
    updated_at     datetime     NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table room
    add constraint fk_room_to_user
        foreign key (host_id) references users (id) on DELETE cascade;


CREATE TABLE reservation
(
    id          bigint      NOT NULL auto_increment,
    check_in    date        NOT NULL,
    check_out   date        NOT NULL,
    total_guest int         NOT NULL,
    status      varchar(20) NOT NULL,
    total_price int         NOT NULL,
    created_at  datetime    NOT NULL,
    updated_at  datetime    NOT NULL,
    room_id     bigint      NOT NULL,
    user_id     bigint      NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table reservation
    add constraint fk_reservation_to_reservation
        foreign key (room_id) references room (id) on DELETE cascade;

alter table reservation
    add constraint fk_reservation_to_user
        foreign key (user_id) references users (id) on DELETE cascade;


CREATE TABLE `review`
(
    id             bigint       NOT NULL auto_increment,
    content        varchar(255) NOT NULL,
    score          int          NOT NULL,
    reservation_id bigint       NOT NULL,
    created_at     datetime     NOT NULL,
    updated_at     datetime     NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table review
    add constraint fk_review_to_reservation
        foreign key (reservation_id) references reservation (id) on DELETE cascade;;

CREATE TABLE room_image
(
    id         bigint       NOT NULL auto_increment,
    image_path varchar(255) NOT NULL,
    room_Id    bigint       NOT NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table room_image
    add constraint fk_room_images_to_room
        foreign key (room_id) references room (id) on DELETE cascade;

CREATE TABLE token
(
    id            bigint       NOT NULL auto_increment,
    refresh_token varchar(255) NOT NULL,
    user_id       bigint       NOT NULL,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;
