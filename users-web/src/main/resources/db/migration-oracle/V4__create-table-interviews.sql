CREATE TABLE interviews
(
    id      RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    job_id  RAW(16) NOT NULL,
    interview_date    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_interviews_job_id FOREIGN KEY (job_id) REFERENCES jobs (id),
    CONSTRAINT fk_interviews_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);