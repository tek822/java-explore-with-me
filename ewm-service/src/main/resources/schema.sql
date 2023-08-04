CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT email_unique UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT category_name_unique UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    pinned BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    title VARCHAR(120),
    annotation VARCHAR(2000),
    description VARCHAR(7000),
    location_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    state VARCHAR(15) NOT NULL,
    CONSTRAINT events_locations_location_id_fk FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE,
    CONSTRAINT events_users_initiator_id_fk FOREIGN KEY (initiator_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT events_categories_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(15),
    CONSTRAINT requests_events_event_id_fk FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT requests_events_requester_id_fk FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_event_requester UNIQUE(event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    event_id BIGINT NOT NULL,
    CONSTRAINT compilations_events_compilations_compilation_id_fk FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
    CONSTRAINT compilations_events_events_event_id_fk FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT unique_compilation_event UNIQUE(compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS areas (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    radius FLOAT NOT NULL,
    location_id BIGINT NOT NULL,
    CONSTRAINT areas_locations_location_id_fk FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
DECLARE
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        -- переводим градусы широты в радианы
        rad_lat1 = pi() * lat1 / 180;
        -- переводим градусы долготы в радианы
        rad_lat2 = pi() * lat2 / 180;
        -- находим разность долгот
        theta = lon1 - lon2;
        -- переводим градусы в радианы
        rad_theta = pi() * theta / 180;
        -- находим длину ортодромии
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        -- переводим радианы в градусы
        dist = dist * 180 / pi();
        -- переводим градусы в километры
        dist = dist * 60 * 1.8524;
        call raise_notice(dist);
        RETURN dist;
    END IF;
END;
'
LANGUAGE plpgsql;

create procedure raise_notice (s float) language plpgsql as
'
begin
    raise info ''%'', s;
end;
';
