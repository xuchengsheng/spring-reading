CREATE TABLE `scores`
(
    `id`    bigint        NOT NULL,
    `score` decimal(5, 2) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin
  ROW_FORMAT = DYNAMIC;

CREATE TABLE `scores_log`
(
    `id`          bigint   NOT NULL,
    `score_id`    bigint   NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin
  ROW_FORMAT = DYNAMIC;
