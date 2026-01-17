DELIMITER //

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100),
    IN p_star_name VARCHAR(100),
    IN p_birth_year INT,
    IN p_genre_name VARCHAR(32),
    OUT out_movie_id VARCHAR(10),
    OUT out_star_id VARCHAR(10),
    OUT out_genre_id INT
)
BEGIN
    DECLARE existing_star_id VARCHAR(10);
    DECLARE existing_genre_id INT;
    DECLARE new_movie_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);
    DECLARE max_movie_id VARCHAR(10);
    DECLARE max_star_id VARCHAR(10);
    DECLARE id_num INT;
    DECLARE star_id_num INT;

    START TRANSACTION;

    IF EXISTS (
        SELECT 1 FROM movies WHERE title = p_title AND year = p_year AND director = p_director
    ) THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate movie';
    END IF;

    SELECT id INTO existing_genre_id FROM genres WHERE name = p_genre_name LIMIT 1;

    IF existing_genre_id IS NULL THEN
        INSERT INTO genres (name) VALUES (p_genre_name);
        SET existing_genre_id = LAST_INSERT_ID();
    END IF;
    SET out_genre_id = existing_genre_id;

    SELECT id INTO existing_star_id FROM stars
    WHERE name = p_star_name AND (birthYear = p_birth_year OR p_birth_year IS NULL)
    LIMIT 1;

    IF existing_star_id IS NULL THEN
        SELECT MAX(id) INTO max_star_id FROM stars WHERE id LIKE 'nm%';
        IF max_star_id IS NOT NULL THEN
            SET star_id_num = CAST(SUBSTRING(max_star_id, 3) AS UNSIGNED) + 1;
        ELSE
            SET star_id_num = 1;
        END IF;
        SET new_star_id = CONCAT('nm', LPAD(star_id_num, 7, '0'));

        INSERT INTO stars (id, name, birthYear) VALUES (new_star_id, p_star_name, p_birth_year);
        SET existing_star_id = new_star_id;
    END IF;
    SET out_star_id = existing_star_id;

    SELECT MAX(id) INTO max_movie_id FROM movies WHERE id LIKE 'tt%';
    IF max_movie_id IS NOT NULL THEN
        SET id_num = CAST(SUBSTRING(max_movie_id, 3) AS UNSIGNED) + 1;
    ELSE
        SET id_num = 1;
    END IF;
    SET new_movie_id = CONCAT('tt', LPAD(id_num, 7, '0'));

    INSERT INTO movies (id, title, year, director) VALUES (new_movie_id, p_title, p_year, p_director);
    SET out_movie_id = new_movie_id;

    INSERT INTO stars_in_movies (starId, movieId) VALUES (existing_star_id, new_movie_id);

    INSERT INTO genres_in_movies (genreId, movieId) VALUES (existing_genre_id, new_movie_id);

    COMMIT;
END //

DELIMITER ;