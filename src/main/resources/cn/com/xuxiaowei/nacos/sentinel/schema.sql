CREATE TABLE discovery (
    id varchar(100) NOT NULL,
    service_name varchar(100) NOT NULL,
    ip varchar(100) NOT NULL,
    port int NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (service_name, ip, port)
);
