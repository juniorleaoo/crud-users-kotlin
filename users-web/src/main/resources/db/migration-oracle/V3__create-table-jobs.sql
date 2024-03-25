CREATE TABLE jobs
(
    id          RAW(16) NOT NULL,
    name        VARCHAR2(500) NOT NULL,
    description CLOB,
    salary      NUMBER(10,2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE requirements
(
    id     RAW(16) NOT NULL,
    job_id RAW(16) NOT NULL,
    max    NUMBER(10),
    min    NUMBER(10),
    stack  VARCHAR2(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_requirements_job_id FOREIGN KEY (job_id) REFERENCES jobs (id)
);