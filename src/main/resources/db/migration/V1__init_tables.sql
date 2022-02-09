CREATE TABLE Words
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(7) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    size TINYINT UNSIGNED                          NOT NULL,
    mode TINYINT UNSIGNED                          NOT NULL,
    UNIQUE KEY idx_words_unique_word (word)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE WordStats
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    word_id       INT NOT NULL,
    shown_times   INT NOT NULL DEFAULT 0,
    guessed_times INT NOT NULL DEFAULT 0,
    fail_times    INT NOT NULL DEFAULT 0,
    FOREIGN KEY (word_id) REFERENCES Words (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE Users
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    u_key VARCHAR(20) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    UNIQUE KEY idx_users_unique_u_key (u_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE UserStats
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT              NOT NULL,
    last_word_id  INT              NOT NULL,
    size          TINYINT UNSIGNED NOT NULL,
    mode          TINYINT UNSIGNED NOT NULL,
    fail_times    INT              NOT NULL DEFAULT 0,
    success_times INT              NOT NULL DEFAULT 0,
    is_current    TINYINT UNSIGNED NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES Users (id),
    UNIQUE KEY idx_user_config_set (user_id, size, mode)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

