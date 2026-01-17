function getParameterByName(name) {
    const url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    const regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function addToCart(movieId, movieTitle) {
    $.ajax({
        url: 'CartServlet',
        type: 'POST',
        data: {
            movieId: movieId,
            movieTitle: movieTitle,
            price: '9.99'
        },
        success: function(response) {
            // Show confirmation message
            const confirmationElement = document.getElementById(`cart_message_${movieId}`);
            confirmationElement.style.display = "block";
            setTimeout(() => {
                confirmationElement.style.display = "none";
            }, 2000);
        },
        error: function() {
            alert("Error adding movie to cart. Please try again.");
        }
    });
}

function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");

    let starInfoElement = document.getElementById("star_info");
    starInfoElement.innerHTML = "";

    const yearOfBirth = resultData[0]["star_dob"] || "N/A"; // Default to "N/A" if not available
    starInfoElement.innerHTML += `<p><strong>Star Name:</strong> ${resultData[0]["star_name"]}</p>`;
    starInfoElement.innerHTML += `<p>Year of Birth: ${yearOfBirth}</p>`;

    console.log("handleResult: populating movie table from resultData");

    let movieTableBodyElement = document.getElementById("movie_table_body");
    movieTableBodyElement.innerHTML = "";

    const sortedMovies = resultData.sort((a, b) => {
        if (b.movie_year !== a.movie_year) {
            return b.movie_year - a.movie_year;
        } else {
            return a.movie_title.localeCompare(b.movie_title);
        }
    });

    sortedMovies.forEach(movie => {
        let rowHTML = `
            <tr>
                <td><a href='single-movie.html?id=${movie.movie_id}'>${movie.movie_title}</a></td>
                <td>${movie.movie_year}</td>
                <td>${movie.movie_director}</td>
                <td>
                    <button type="button" class="btn btn-custom" onclick="addToCart('${movie.movie_id}', '${movie.movie_title}')">Add to Cart</button>
                    <p id="cart_message_${movie.movie_id}" class="success-message" style="display: none; color: green;">Added to cart!</p>
                </td>
            </tr>`;
        movieTableBodyElement.innerHTML += rowHTML;
    });
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
    let starId = getParameterByName('id');
    if (starId) {
        fetch(`api/single-star?id=${starId}`)
            .then(response => response.json())
            .then(resultData => handleResult(resultData))
            .catch(error => console.error('Error fetching star data:', error));
    } else {
        console.error('No star id found in URL');
    }

    setupResultsButton();
};