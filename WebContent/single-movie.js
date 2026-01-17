function getParameterByName(name) {
    const url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    const regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function fetchMovieData(movieId) {
    console.log("Fetching movie data for movieId:", movieId);
    const apiUrl = `api/single-movie?id=${movieId}`;
    console.log("API URL:", apiUrl);

    fetch(apiUrl)
        .then(response => {
            console.log("Response status:", response.status);
            if (!response.ok) {
                throw new Error(`Network response was not ok: ${response.status} ${response.statusText}`);
            }
            return response.json();
        })
        .then(movieData => {
            console.log("Movie data fetched:", movieData);
            if (movieData.error) {
                throw new Error(movieData.error);
            }
            handleMovieResult(movieData);
        })
        .catch(error => {
            console.error('Error fetching movie data:', error);
            document.getElementById('movie_title').textContent = 'Error loading movie data';
            const errorElement = document.createElement('p');
            errorElement.style.color = 'red';
            errorElement.textContent = `Error: ${error.message}`;
            document.body.appendChild(errorElement);
        });
}

function handleMovieResult(movieData) {
    console.log("Genres data:", movieData.genres);

    document.getElementById('movie_title').textContent = movieData.title;
    document.getElementById('movie_year').textContent = movieData.year;
    document.getElementById('movie_director').textContent = movieData.director;
    document.getElementById('movie_rating').innerHTML = movieData.rating || 'N/A';

    setCartFormValues(movieData);

    const genresElement = document.getElementById('movie_genres');
    const genres = movieData.genres.split(', ');
    genresElement.innerHTML = '';
    genres.forEach(genre => {
        const genreLink = document.createElement('a');
        genreLink.href = `MovieListServlet?genre=${encodeURIComponent(genre)}`;
        genreLink.textContent = genre;
        genresElement.appendChild(genreLink);
        genresElement.appendChild(document.createTextNode(', '));
    });
    if (genresElement.lastChild) {
        genresElement.removeChild(genresElement.lastChild);
    }

    const starsElement = document.getElementById('movie_stars');
    starsElement.innerHTML = '';
    movieData.stars.forEach(star => {
        const starLink = document.createElement('a');
        starLink.href = `single-star.html?id=${star.id}`;
        starLink.textContent = star.name;
        starsElement.appendChild(starLink);
        starsElement.appendChild(document.createTextNode(', '));
    });
    if (starsElement.lastChild) {
        starsElement.removeChild(starsElement.lastChild);
    }
}

function addToCart() {
    const movieId = document.getElementById('movieId').value;
    const movieTitle = document.getElementById('movieTitle').value;
    const price = document.getElementById('price').value;

    $.ajax({
        url: 'CartServlet',
        type: 'POST',
        data: {
            movieId: movieId,
            movieTitle: movieTitle,
            price: price
        },
        success: function(response) {
            $("#cartMessage").show();
            setTimeout(function() {
                $("#cartMessage").hide();
            }, 2000);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // Enhanced error handling
            console.error("Error adding movie to cart:", textStatus, errorThrown);
            alert("Error adding movie to cart. Please try again.");
        }
    });
}

function setCartFormValues(movieData) {
    document.getElementById('movieId').value = movieData.id;
    document.getElementById('movieTitle').value = movieData.title;
}

function setupResultsButton() {
    fetch(`${window.location.origin}/cs122b_project2_war_exploded/api/session`)
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                const queryParams = new URLSearchParams({
                    title: data.searchTitle || '',
                    genre: data.searchGenre || '',
                    year: data.searchYear || '',
                    director: data.searchDirector || '',
                    star: data.searchStar || '',
                    sort: data.searchSort || '',
                    page: data.searchPage || 1,
                    limit: data.searchLimit || 10
                });

                const resultsUrl = `${window.location.origin}/cs122b_project2_war_exploded/SearchServlet?${queryParams.toString()}`;

                console.log("Generated Results URL:", resultsUrl);

                document.getElementById("results-button").href = resultsUrl;
            } else {
                console.error('Error fetching session data:', data.message);
            }
        })
        .catch(error => console.error('Error fetching session data:', error));
}

window.onload = function() {
    const movieId = getParameterByName('id');
    if (movieId) {
        fetchMovieData(movieId);
    } else {
        console.error('No movie id found in URL');
        document.getElementById('movie_title').textContent = 'No movie ID provided';
    }
    setupResultsButton();
};