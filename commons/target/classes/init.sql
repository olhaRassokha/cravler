drop table if exists review;
drop table if exists movie;
drop table if exists job;

create  table job(
  id INT NOT NULL AUTO_INCREMENT,
  movie_name VARCHAR(40),
  site_name VARCHAR(1500),
  status VARCHAR(20),
  PRIMARY KEY (id)
);

create  table movie (
  id INT NOT NULL AUTO_INCREMENT,
  job_id INT,
  name VARCHAR(50) NOT NULL,
  duration VARCHAR(20),
  director VARCHAR(20),
  PRIMARY KEY (id),
  FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE CASCADE
);


create  table review(
  id INT NOT NULL AUTO_INCREMENT,
  movie_id INT NOT NULL,
  text VARCHAR(1500),
  sentiment VARCHAR(20),
  PRIMARY KEY (id),
  FOREIGN KEY (movie_id) REFERENCES movie(id) ON DELETE CASCADE
);





