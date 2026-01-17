<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/devbridge/jQuery-Autocomplete@1.4.10/dist/jquery.autocomplete.css">


    <style>
        body {
            background-color: #f0f4f8;
            font-family: 'Montserrat', sans-serif;
            color: #333;
        }
        .container {
            margin-top: 50px;
        }
        .browse-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: #4b6cb7;
            text-align: center;
            margin-bottom: 40px;
        }
        .search-form {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
        }
        .browse-links a {
            display: block;
            margin: 10px 0;
            font-size: 1.2rem;
        }
        .logout-button {
            margin-bottom: 20px;
        }

        .autocomplete-suggestions {
            background-color: white;
            border: 1px solid #ccc;
            max-height: 400px;
            overflow-y: auto;
            z-index: 9999;
        }

        .autocomplete-suggestion {
            background-color: white;
            padding: 8px 12px;
            cursor: pointer;
            display: block;
        }

        .autocomplete-suggestion:hover,
        .autocomplete-suggestion.autocomplete-selected {
            background-color: #d4e2ff;
            color: #000;
        }

        .autocomplete-suggestion strong {
            color: #000;
        }

        .autocomplete-suggestions::-webkit-scrollbar {
            width: 0px;
            background: transparent;
        }
        .autocomplete-suggestions {
            scrollbar-width: none;
            -ms-overflow-style: none;
        }

    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg" style="background-color: #182848; padding: 20px; font-size: 1.2rem;">
    <a class="navbar-brand text-white" href="MainPage.jsp" style="font-size: 1.5rem;">Fabflix</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link text-light" href="Top20Servlet">Top20</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-light" href="#">Results</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-light" href="shopping-cart.jsp">Checkout</a>
            </li>
        </ul>

        <form id="autocomplete-form" class="form-inline" action="SearchServlet" method="get">
            <input id="autocomplete-search" class="form-control mr-sm-2" type="text" name="query"
                   placeholder="Full-text Search Movies..." style="width: 250px;"
                   aria-autocomplete="list" aria-haspopup="true" aria-expanded="false" aria-controls="autocomplete-list">
            <button class="btn btn-outline-light my-2 my-sm-0" type="submit">Search</button>
        </form>

        <script>
            function validateAutocompleteSearch() {
                const query = document.getElementById('autocomplete-search').value.trim();
                if (query === '') {
                    alert('Please enter at least one search field.');
                    return false;
                }
                return true;
            }
        </script>

        <div class="ml-2">
            <a href="LogoutServlet" class="btn btn-danger">Logout</a>
        </div>
    </div>
</nav>


<div class="container mt-5">
    <h2 class="browse-title">Browse Movies</h2>

    <div class="text-center mb-4">
        <form id="advanced-search-form" class="form-inline justify-content-center" action="SearchServlet" method="get">
            <input class="form-control mr-sm-2 mb-2" type="text" name="title" placeholder="Title" aria-label="Title" style="width: 150px;">
            <input class="form-control mr-sm-2 mb-2" type="text" name="genre" placeholder="Genre" aria-label="Genre" style="width: 150px;">
            <input class="form-control mr-sm-2 mb-2" type="number" name="year" placeholder="Year" aria-label="Year" style="width: 120px;" min="1800" max="2100">
            <input class="form-control mr-sm-2 mb-2" type="text" name="director" placeholder="Director" aria-label="Director" style="width: 160px;">
            <input class="form-control mr-sm-2 mb-2" type="text" name="star" placeholder="Star" aria-label="Star" style="width: 150px;">
            <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">Search</button>
        </form>
    </div>

    <div class="row text-center">
        <div class="col-md-6 offset-md-3">
            <a href="Browse?type=genre" class="btn btn-info btn-lg btn-block" style="background-color: #38b2ac; border: none;">Browse by Genre</a>
            <a href="Browse?type=title" class="btn btn-info btn-lg btn-block mt-3" style="background-color: #38b2ac; border: none;">Browse by Title</a>
        </div>
    </div>
</div>

<footer style="background-color: #f0f4f8; padding: 20px; text-align: center; position: fixed; bottom: 0; left: 0; right: 0; width: 100%; z-index: 1000;">
    <p style="color: #38b2ac; margin: 0;">
        &copy; 2024 Lara&Sona - All rights reserved.
    </p>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script src="https://cdn.jsdelivr.net/gh/devbridge/jQuery-Autocomplete@1.4.10/dist/jquery.autocomplete.min.js"></script>

<script>
    $(document).ready(function() {
        const autocompleteCache = {};

        $('#autocomplete-search').autocomplete({
            serviceUrl: 'autocomplete',
            minChars: 3,
            deferRequestBy: 300,
            maxHeight: 400,
            width: 'auto',
            zIndex: 9999,
            lookupLimit: 10,
            triggerSelectOnValidInput: false,

            lookup: function(query, done) {
                query = $.trim(query);
                if (query.length < 3) {
                    return;
                }

                if (autocompleteCache[query]) {
                    console.log("Using cached results for query:", query);
                    done({ suggestions: autocompleteCache[query] });
                } else {
                    console.log("Sending AJAX request for query:", query);
                    $.ajax({
                        url: 'autocomplete',
                        type: 'GET',
                        dataType: 'json',
                        data: { query: query },
                        success: function(data) {
                            autocompleteCache[query] = data;
                            console.log("Suggestions from server:", data);
                            done({ suggestions: data });
                        },
                        error: function(xhr, status, error) {
                            console.error("Autocomplete AJAX error:", error);
                            done({ suggestions: [] });
                        }
                    });
                }
            },

            onSelect: function(suggestion) {
                window.location.href = 'single-movie.html?id=' + suggestion.data;
            },

            formatResult: function(suggestion, currentValue) {
                const re = new RegExp("(" + currentValue + ")", "gi");
                return suggestion.value.replace(re, "<strong>$1</strong>");
            }
        });

        $('#autocomplete-search').on('autocomplete.suggest', function(event, suggestions) {
            console.log("Suggestion list displayed:", suggestions);
        });
    });


</script>

</body>
</html>
