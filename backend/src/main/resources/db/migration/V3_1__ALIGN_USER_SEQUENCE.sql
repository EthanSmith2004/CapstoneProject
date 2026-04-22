DO $$
    DECLARE
        max_id BIGINT;
    BEGIN
        SELECT COALESCE(MAX(id), 0) INTO max_id FROM "user";
        PERFORM setval('user_seq', max_id + 1, false);
    END;
$$;