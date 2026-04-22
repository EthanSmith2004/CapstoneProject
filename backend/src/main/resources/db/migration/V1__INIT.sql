CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS allergy_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS campus_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS menu_item_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS notification_preference_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS notification_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS order_item_feedback_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS order_item_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS order_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS refresh_token_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS report_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS residence_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS transaction_audit_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_device_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_event_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_profile_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account
(
    id         BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE account_transactions
(
    account_entity_id BIGINT NOT NULL,
    transactions_id   BIGINT NOT NULL
);

CREATE TABLE allergy
(
    id         BIGINT                      NOT NULL,
    allergy    VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_allergy PRIMARY KEY (id)
);

CREATE TABLE audit_transaction_link
(
    audit_id       BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL
);

CREATE TABLE campus
(
    id         BIGINT                      NOT NULL,
    campus     VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_campus PRIMARY KEY (id)
);

CREATE TABLE menu_item
(
    id            BIGINT                      NOT NULL,
    name          VARCHAR(255),
    delivery_date TIMESTAMP WITHOUT TIME ZONE,
    description   VARCHAR(255),
    kcal          BIGINT,
    price         DECIMAL                     NOT NULL,
    edit_by       TIMESTAMP WITHOUT TIME ZONE,
    image_hero    VARCHAR(255),
    image_detail  VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    release_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    order_by      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_menu_item PRIMARY KEY (id)
);

CREATE TABLE menu_item_allergies
(
    allergy_id BIGINT NOT NULL,
    item_id    BIGINT NOT NULL
);

CREATE TABLE notification
(
    id               BIGINT                      NOT NULL,
    user_id          BIGINT                      NOT NULL,
    title            VARCHAR(255)                NOT NULL,
    message          VARCHAR(1000)               NOT NULL,
    type             VARCHAR(255)                NOT NULL,
    status           VARCHAR(255)                NOT NULL,
    priority         INTEGER,
    deep_link_url    VARCHAR(255),
    image_url        VARCHAR(255),
    related_order_id BIGINT,
    scheduled_for    TIMESTAMP WITHOUT TIME ZONE,
    sent_at          TIMESTAMP WITHOUT TIME ZONE,
    delivered_at     TIMESTAMP WITHOUT TIME ZONE,
    read_at          TIMESTAMP WITHOUT TIME ZONE,
    expires_at       TIMESTAMP WITHOUT TIME ZONE,
    retry_count      INTEGER,
    error_message    VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

CREATE TABLE notification_preference
(
    id                   BIGINT                      NOT NULL,
    user_id              BIGINT                      NOT NULL,
    push_enabled         BOOLEAN                     NOT NULL,
    email_enabled        BOOLEAN                     NOT NULL,
    order_updates        BOOLEAN                     NOT NULL,
    menu_updates         BOOLEAN                     NOT NULL,
    account_updates      BOOLEAN                     NOT NULL,
    promotional          BOOLEAN                     NOT NULL,
    system_announcements BOOLEAN                     NOT NULL,
    quiet_hours_start    VARCHAR(255),
    quiet_hours_end      VARCHAR(255),
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notification_preference PRIMARY KEY (id)
);

CREATE TABLE "order"
(
    id             BIGINT                      NOT NULL,
    total          DECIMAL(19, 4),
    status         VARCHAR(255),
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        BIGINT                      NOT NULL,
    transaction_id BIGINT,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

CREATE TABLE order_item
(
    id            BIGINT                      NOT NULL,
    name          VARCHAR(255),
    delivery_date TIMESTAMP WITHOUT TIME ZONE,
    description   VARCHAR(255),
    kcal          BIGINT,
    price         DECIMAL                     NOT NULL,
    edit_by       TIMESTAMP WITHOUT TIME ZONE,
    image_hero    VARCHAR(255),
    image_detail  VARCHAR(255),
    quantity      INTEGER                     NOT NULL,
    order_id      BIGINT,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_order_item PRIMARY KEY (id)
);

CREATE TABLE order_item_allergies
(
    allergy_id BIGINT NOT NULL,
    item_id    BIGINT NOT NULL
);

CREATE TABLE order_item_feedback
(
    id            BIGINT                      NOT NULL,
    order_item_id BIGINT,
    rating        INTEGER,
    feedback      VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_order_item_feedback PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id         BIGINT                      NOT NULL,
    token      VARCHAR(255)                NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked    BOOLEAN                     NOT NULL,
    revoked_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    BIGINT                      NOT NULL,
    last_used  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

CREATE TABLE report
(
    id                      BIGINT                      NOT NULL,
    name                    VARCHAR(255)                NOT NULL,
    description             VARCHAR(255),
    type                    VARCHAR(255)                NOT NULL,
    status                  VARCHAR(255)                NOT NULL,
    requested_by            BIGINT                      NOT NULL,
    file_url                VARCHAR(255),
    file_name               VARCHAR(255),
    file_size               BIGINT,
    mime_type               VARCHAR(255),
    date_from               TIMESTAMP WITHOUT TIME ZONE,
    date_to                 TIMESTAMP WITHOUT TIME ZONE,
    scheduled               BOOLEAN,
    processing_started_at   TIMESTAMP WITHOUT TIME ZONE,
    processing_completed_at TIMESTAMP WITHOUT TIME ZONE,
    error_message           VARCHAR(255),
    last_downloaded_at      TIMESTAMP WITHOUT TIME ZONE,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_report PRIMARY KEY (id)
);

CREATE TABLE report_parameter
(
    report_id       BIGINT       NOT NULL,
    parameter_value VARCHAR(255),
    parameter_key   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_report_parameter PRIMARY KEY (report_id, parameter_key)
);

CREATE TABLE residence
(
    id         BIGINT                      NOT NULL,
    residence  VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_residence PRIMARY KEY (id)
);

CREATE TABLE transaction
(
    id               BIGINT                      NOT NULL,
    debit            DECIMAL(19, 4)              NOT NULL,
    credit           DECIMAL(19, 4)              NOT NULL,
    running_balance  DECIMAL(19, 4)              NOT NULL,
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description      VARCHAR                     NOT NULL,
    account_id       BIGINT,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

CREATE TABLE transaction_audit
(
    id             BIGINT                      NOT NULL,
    user_id        BIGINT                      NOT NULL,
    initiated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    loaded_content VARCHAR                     NOT NULL,
    type           VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_transaction_audit PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id                      BIGINT                      NOT NULL,
    first_name              VARCHAR(255)                NOT NULL,
    last_name               VARCHAR(255)                NOT NULL,
    email                   VARCHAR(255)                NOT NULL,
    password                VARCHAR(255)                NOT NULL,
    account_non_expired     BOOLEAN                     NOT NULL,
    account_non_locked      BOOLEAN                     NOT NULL,
    credentials_non_expired BOOLEAN                     NOT NULL,
    enabled                 BOOLEAN                     NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_device
(
    id         BIGINT                      NOT NULL,
    user_id    BIGINT                      NOT NULL,
    endpoint   VARCHAR(512)                NOT NULL,
    p256dh     VARCHAR(256)                NOT NULL,
    auth       VARCHAR(256)                NOT NULL,
    user_agent VARCHAR(255),
    is_active  BOOLEAN                     NOT NULL,
    last_used  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_device PRIMARY KEY (id)
);

CREATE TABLE user_event
(
    id          BIGINT                      NOT NULL,
    login_event TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id     BIGINT                      NOT NULL,
    event_type  VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_user_event PRIMARY KEY (id)
);

CREATE TABLE user_profile
(
    enabled           BOOLEAN                     NOT NULL,
    id                BIGINT                      NOT NULL,
    credential_number VARCHAR(255)                NOT NULL,
    user_id           BIGINT                      NOT NULL,
    campus_id         BIGINT                      NOT NULL,
    residence_id      BIGINT,
    account_id        BIGINT                      NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_profile PRIMARY KEY (id)
);

CREATE TABLE user_profile_allergy
(
    user_profile_entity_id BIGINT NOT NULL,
    allergy_id             BIGINT NOT NULL
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    roles   VARCHAR(255)
);

ALTER TABLE account_transactions
    ADD CONSTRAINT uc_account_transactions_transactions UNIQUE (transactions_id);

ALTER TABLE allergy
    ADD CONSTRAINT uc_allergy_allergy UNIQUE (allergy);

ALTER TABLE audit_transaction_link
    ADD CONSTRAINT uc_audit_transaction_link_transaction UNIQUE (transaction_id);

ALTER TABLE campus
    ADD CONSTRAINT uc_campus_campus UNIQUE (campus);

ALTER TABLE notification_preference
    ADD CONSTRAINT uc_notification_preference_user UNIQUE (user_id);

ALTER TABLE order_item_feedback
    ADD CONSTRAINT uc_order_item_feedback_order_item UNIQUE (order_item_id);

ALTER TABLE "order"
    ADD CONSTRAINT uc_order_transaction UNIQUE (transaction_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

ALTER TABLE residence
    ADD CONSTRAINT uc_residence_residence UNIQUE (residence);

ALTER TABLE user_device
    ADD CONSTRAINT uc_user_device_endpoint UNIQUE (endpoint);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_account UNIQUE (account_id);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_credential_number UNIQUE (credential_number);

ALTER TABLE user_profile
    ADD CONSTRAINT uc_user_profile_user UNIQUE (user_id);

CREATE INDEX idx_account_created_at ON account (created_at);

CREATE INDEX idx_account_updated_at ON account (updated_at);

CREATE INDEX idx_allergy_created_at ON allergy (created_at);

CREATE INDEX idx_campus_created_at ON campus (created_at);

CREATE INDEX idx_feedback_created_at ON order_item_feedback (created_at);

CREATE INDEX idx_feedback_rating ON order_item_feedback (rating);

CREATE INDEX idx_notif_pref_created_at ON notification_preference (created_at);

CREATE INDEX idx_notif_pref_push_enabled ON notification_preference (push_enabled);

CREATE INDEX idx_notification_created_at ON notification (created_at);

CREATE INDEX idx_notification_scheduled ON notification (scheduled_for);

CREATE INDEX idx_notification_status ON notification (status);

CREATE INDEX idx_notification_type ON notification (type);

CREATE INDEX idx_notification_user_status ON notification (user_id, status);

CREATE INDEX idx_order_created_at ON "order" (created_at);

CREATE INDEX idx_order_item_created_at ON order_item (created_at);

CREATE INDEX idx_order_status ON "order" (status);

CREATE INDEX idx_order_updated_at ON "order" (updated_at);

CREATE INDEX idx_order_user_status ON "order" (user_id, status);

CREATE INDEX idx_refresh_token_expires ON refresh_token (expires_at);

CREATE INDEX idx_refresh_token_revoked ON refresh_token (revoked);

CREATE INDEX idx_refresh_token_user_revoked ON refresh_token (user_id, revoked);

CREATE INDEX idx_report_created_at ON report (created_at);

CREATE INDEX idx_report_date_range ON report (date_from, date_to);

CREATE INDEX idx_report_status ON report (status);

CREATE INDEX idx_report_type ON report (type);

CREATE INDEX idx_residence_created_at ON residence (created_at);

CREATE INDEX idx_transaction_credit ON transaction (credit);

CREATE INDEX idx_transaction_date ON transaction (transaction_date);

CREATE INDEX idx_transaction_debit ON transaction (debit);

CREATE INDEX idx_user_created_at ON "user" (created_at);

CREATE INDEX idx_user_device_active ON user_device (is_active);

CREATE INDEX idx_user_device_last_used ON user_device (last_used);

CREATE INDEX idx_user_enabled ON "user" (enabled);

CREATE INDEX idx_user_event_time ON user_event (login_event);

CREATE INDEX idx_user_event_type ON user_event (event_type);

CREATE INDEX idx_user_event_user_type ON user_event (user_id, event_type);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_RELATED_ORDER FOREIGN KEY (related_order_id) REFERENCES "order" (id);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_notification_user ON notification (user_id);

ALTER TABLE notification_preference
    ADD CONSTRAINT FK_NOTIFICATION_PREFERENCE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE order_item_feedback
    ADD CONSTRAINT FK_ORDER_ITEM_FEEDBACK_ON_ORDER_ITEM FOREIGN KEY (order_item_id) REFERENCES order_item (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);

CREATE INDEX idx_order_item_order ON order_item (order_id);

ALTER TABLE "order"
    ADD CONSTRAINT FK_ORDER_ON_TRANSACTION FOREIGN KEY (transaction_id) REFERENCES transaction (id);

ALTER TABLE "order"
    ADD CONSTRAINT FK_ORDER_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_order_user ON "order" (user_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_REQUESTED_BY FOREIGN KEY (requested_by) REFERENCES "user" (id);

CREATE INDEX idx_report_requested_by ON report (requested_by);

ALTER TABLE transaction_audit
    ADD CONSTRAINT FK_TRANSACTION_AUDIT_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE user_device
    ADD CONSTRAINT FK_USER_DEVICE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_user_device_user ON user_device (user_id);

ALTER TABLE user_event
    ADD CONSTRAINT FK_USER_EVENT_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_user_event_user ON user_event (user_id);

ALTER TABLE user_profile
    ADD CONSTRAINT FK_USER_PROFILE_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE user_profile
    ADD CONSTRAINT FK_USER_PROFILE_ON_CAMPUS FOREIGN KEY (campus_id) REFERENCES campus (id);

ALTER TABLE user_profile
    ADD CONSTRAINT FK_USER_PROFILE_ON_RESIDENCE FOREIGN KEY (residence_id) REFERENCES residence (id);

ALTER TABLE user_profile
    ADD CONSTRAINT FK_USER_PROFILE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

CREATE INDEX idx_user_profile_user ON user_profile (user_id);

ALTER TABLE account_transactions
    ADD CONSTRAINT fk_acctra_on_account_entity FOREIGN KEY (account_entity_id) REFERENCES account (id);

ALTER TABLE account_transactions
    ADD CONSTRAINT fk_acctra_on_transaction_entity FOREIGN KEY (transactions_id) REFERENCES transaction (id);

ALTER TABLE audit_transaction_link
    ADD CONSTRAINT fk_audtra_on_transaction_audit_entity FOREIGN KEY (audit_id) REFERENCES transaction_audit (id);

ALTER TABLE audit_transaction_link
    ADD CONSTRAINT fk_audtra_on_transaction_entity FOREIGN KEY (transaction_id) REFERENCES transaction (id);

ALTER TABLE menu_item_allergies
    ADD CONSTRAINT fk_meniteall_on_allergy_entity FOREIGN KEY (allergy_id) REFERENCES allergy (id);

ALTER TABLE menu_item_allergies
    ADD CONSTRAINT fk_meniteall_on_menu_item_entity FOREIGN KEY (item_id) REFERENCES menu_item (id);

ALTER TABLE order_item_allergies
    ADD CONSTRAINT fk_orditeall_on_allergy_entity FOREIGN KEY (allergy_id) REFERENCES allergy (id);

ALTER TABLE order_item_allergies
    ADD CONSTRAINT fk_orditeall_on_order_item_entity FOREIGN KEY (item_id) REFERENCES order_item (id);

ALTER TABLE report_parameter
    ADD CONSTRAINT fk_report_parameter_on_report_entity FOREIGN KEY (report_id) REFERENCES report (id);

ALTER TABLE user_profile_allergy
    ADD CONSTRAINT fk_useproall_on_allergy_entity FOREIGN KEY (allergy_id) REFERENCES allergy (id);

ALTER TABLE user_profile_allergy
    ADD CONSTRAINT fk_useproall_on_user_profile_entity FOREIGN KEY (user_profile_entity_id) REFERENCES user_profile (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user_entity FOREIGN KEY (user_id) REFERENCES "user" (id);