CREATE TABLE menu_template (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    delivery_offset_minutes INTEGER NOT NULL,
    release_offset_minutes INTEGER NOT NULL,
    order_by_offset_minutes INTEGER NOT NULL,
    preset_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_menu_template_preset_name ON menu_template(preset_name);