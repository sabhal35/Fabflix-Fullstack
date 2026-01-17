function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
}

function handleMovieResult(resultData) {
    let movieTableBodyElement = document.getElementById("movie_table_body");

    const path = getContextPath();

    for (let i = 0; i < resultData.length; i++) {
        let row = "<tr>";

        row += "<td><a href='" + path + "/single-movie.html?id=" + resultData[i]['movie_id'] + "'>" + resultData[i]["title"] + "</a></td>";

        row += "<td>" + resultData[i]["year"] + "</td>";

        row += "<td>" + resultData[i]["director"] + "</td>";

        // Genres
        let genres = resultData[i]["genres"].split(',');
        let genreLinks = "";
        for (let j = 0; j < genres.length && j < 3; j++) {
            if (j > 0) genreLinks += ", ";
            genreLinks += '<a href="' + path + '/BrowseByGenre.jsp?genre=' + genres[j] + '">' + genres[j] + '</a>';
        }
        row += "<td>" + genreLinks + "</td>";

        let s = "";
        let stars = resultData[i]["stars"];
        if (stars && stars.length > 0) {
            for (let j = 0; j < stars.length && j < 3; j++) {
                if (j > 0) s += ", ";
                s += '<a href="' + path + '/single-star.html?id=' + stars[j]['id'] + '">' + stars[j]['name'] + '</a>';
            }
        } else {
            s = "N/A";
        }
        row += "<td>" + s + "</td>";
        row += "<td>" + (resultData[i]["rating"] || "N/A") + "</td>";
        row += "</tr>";

        movieTableBodyElement.innerHTML += row;
    }
}

fetch('api/movies')
    .then(response => response.json())
    .then(resultData => handleMovieResult(resultData))
    .catch(error => console.error('Error fetching movie data:', error));