function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Use a regular expression to find the parameter value
    let regex = new RegExp("[?&]" + target + "=([^&#]*)");
    let results = regex.exec(url);

    if (!results) return null;

    let parameterValue = decodeURIComponent(results[1].replace(/\+/g, " "));

    // Check for empty values
    if (parameterValue === "") {
        return null;
    }

    return parameterValue;
}

function sortMovies() {
    console.log("sortmovies")
    let selectedSortOption = document.getElementById("sortForPage").value;
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let starName = getParameterByName('starName');
    let moviesPerPage = document.getElementById("moviesPerPage").value;
    console.log(selectedSortOption)
    console.log(moviesPerPage)

    // Make an AJAX request to fetch the data based on the selected options
    let apiUrl = "api/search?";
    if (title) {
        apiUrl += "title=" + title;
    }
    if (year) {
        apiUrl += "&year=" + year;
    }
    if (director) {
        apiUrl += "&director=" + director;
    }
    if (starName) {
        apiUrl += "&starName=" + starName;
    }

    if (selectedSortOption) { // Use selectedSortOption instead of sortOption
        apiUrl += "&sort=" + selectedSortOption;
    }

    if (moviesPerPage) {
        apiUrl += "&limit=" + moviesPerPage;
    }

    // Use jQuery to make an AJAX GET request
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: apiUrl,
        success: (resultData) => handleResult(resultData)
    });
}


function handleResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let moviesPerPage = parseInt(document.getElementById("moviesPerPage").value);
    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty();

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(moviesPerPage, resultData.length); i++) {
        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        let genres = resultData[i]["genres"].split(',');
        let three_genres = "";

        const genreList = [
            "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime",
            "Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music",
            "Musical", "Mystery", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Thriller",
            "War", "Western"
        ];

        for (let a = 0; a < genres.length && a < 3; a++) {
            if (genres[a]) { // Check if the genre is defined
                const genre = genres[a].trim();
                const genreIndex = genreList.indexOf(genre) + 1; // Find the index of the genre
                if (genreIndex > 0) {
                    three_genres += `<a href="list.html?genre=${genreIndex}">${genre}</a>`;
                } else {
                    three_genres += genre; // If the genre is not found in the list, add it as is
                }
                if (a < genres.length - 1 && a < 2) {
                    three_genres += ", ";
                }
            }
        }
        rowHTML += "<th>" + three_genres + "</th>";

        let starNames = (resultData[i]["star_names"] || "").split(','); // Split star names
        let starIds = (resultData[i]["star_ids"] || "").split(',');
        let starLinks = "";

        for (let j = 0; j < starNames.length && j < 3; j++) {
            starLinks += "<a href='single-star.html?id=" + encodeURIComponent(starIds[j].trim()) + "'>" + starNames[j].trim() + "</a>";
            if (j < starNames.length - 1 && j < 2) {
                starLinks += ", ";
            }
        }

        rowHTML += "<th>" + starLinks + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th><button class='add-to-cart' data-movie-id='" + resultData[i]['movie_id'] + "' data-movie-title='" + resultData[i]['movie_title'] + "'>Add</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body
        movieTableBodyElement.append(rowHTML);
    }
}

// Usage example:
let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let starName = getParameterByName('starName');
let sortOption = getParameterByName('sort');

console.log("Title:", title);
console.log("Year:", year);
console.log("Director:", director);
console.log("Star Name:", starName);

// Construct the URL in the Java code and pass it to the JavaScript
let apiUrl = "api/search?";

// Use the URL constructed by the Java code
if (title) {
    apiUrl += "&title=" + title;
}
if (year) {
    apiUrl += "&year=" + year;
}
if (director) {
    apiUrl += "&director=" + director;
}
if (starName) {
    apiUrl += "&starName=" + starName;
}

if (sortOption) {
    apiUrl += "&sort=" + sortOption;
}

document.getElementById("updateButton").addEventListener('click', function () {
    sortMovies(); // Fetch and display the updated movies when the "Update" button is clicked

    // Save the selected sort option to local storage
    let selectedSortOption = document.getElementById("sortForPage").value;
    localStorage.setItem('sortOption', selectedSortOption);
});


// Make the AJAX request with the constructed URL
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: apiUrl, // Use the constructed URL
    success: (resultData) => handleResult(resultData)
});

function addToCart(movieId, movieTitle) {
    // Send a request to the cartServlet to add or update the item in the cart
    $.ajax({
        type: 'POST',
        url: 'api/cart', // Replace with the actual URL for your cartServlet
        data: { movieId: movieId, movieTitle: movieTitle },
        success: function (response) {
            // Handle the response if needed
            console.log('Movie added to cart:', response);
            alert("Success!")
        },
        error: function (error) {
            // Handle the error if the request fails
            console.error('Error adding movie to cart:', error);
        }
    });
}

// Add a click event to the "Add" buttons
$(document).on('click', '.add-to-cart', function () {
    // Get the movie ID and title from the data attributes
    let movieId = $(this).data('movie-id');
    let movieTitle = $(this).data('movie-title');
    console.log(movieTitle)

    // Call the addToCart function with the movie ID and title
    addToCart(movieId, movieTitle);
});