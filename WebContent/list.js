function sortMovies() {
    let selectedSortOption = document.getElementById("sortForPage").value;
    let genreID = getParameterByName('genre');
    let prefix = getParameterByName('prefix');
    let moviesPerPage = document.getElementById("moviesPerPage").value;

    // Make an AJAX request to fetch the data based on the selected options
    let url;
    if (genreID) {
        url = `api/genre?genre=${genreID}&sort=${selectedSortOption}&limit=${moviesPerPage}`;
    } else if (prefix) {
        url = `api/prefix?prefix=${prefix}&sort=${selectedSortOption}&limit=${moviesPerPage}`;
    }

    // Use jQuery to make an AJAX GET request
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: url,
        success: (resultData) => handleResult(resultData)
    });
}

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Use a regular expression to find the parameter value
    let regex = new RegExp("[?&]" + target + "=([^&#]*)");
    let results = regex.exec(url);

    if (!results) return null;

    // Return the decoded parameter value
    return decodeURIComponent(results[1].replace(/\+/g, " "));
}

/**
 * Function to handle the data returned by the API and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    // Get the number of movies to display per page from the dropdown
    let moviesPerPage = parseInt(document.getElementById("moviesPerPage").value);

    // Clear the table body
    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty();

    // Concatenate the HTML tags with resultData jsonObject to create table rows
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

        let starNames = resultData[i]["star_names"].split(','); // Split star names
        let starIds = resultData[i]["star_ids"].split(',');
        let starLinks = "";

        for (let j = 0; j < starNames.length && j < 3; j++) {
            if (starNames[j]) { // Check if the star name is defined
                starLinks += "<a href='single-star.html?id=" + encodeURIComponent(starIds[j].trim()) + "'>" + starNames[j].trim() + "</a>";
                if (j < starNames.length - 1 && j < 2) {
                    starLinks += ", ";
                }
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

// Get genre ID from the URL
let genreID = getParameterByName('genre');
let prefix = getParameterByName('prefix');

// Check if a sort option is already stored in local storage
let savedSortOption = localStorage.getItem('sortOption');
if (savedSortOption) {
    // Set the sort option to the saved option
    document.getElementById("sortForPage").value = savedSortOption;
}

// Add a click event to the "Update" button
document.getElementById("updateButton").addEventListener('click', function () {
    sortMovies(); // Fetch and display the updated movies when the "Update" button is clicked

    // Save the selected sort option to local storage
    let selectedSortOption = document.getElementById("sortForPage").value;
    localStorage.setItem('sortOption', selectedSortOption);
});

// Use jQuery to make an AJAX GET request based on genre or prefix
if (genreID) {
    // Genre-based AJAX request
    let url = "api/genre?genre=" + genreID + "&sort=" + savedSortOption;
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: url,
        success: (resultData) => handleResult(resultData)
    });
} else if (prefix) {
    // Prefix-based AJAX request
    let sort = getParameterByName('sort');
    let url = "api/prefix?prefix=" + prefix + "&sort=" + savedSortOption;
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: url,
        success: (resultData) => handleResult(resultData)
    });
}

// Function to handle adding items to the cart
function addToCart(movieId, movieTitle) {
    // Send a request to the cartServlet to add or update the item in the cart
    $.ajax({
        type: 'POST',
        url: 'api/cart', // Replace with the actual URL for your cartServlet
        data: { movieId: movieId, movieTitle: movieTitle },
        success: function (response) {
            // Handle the response if needed
            alert("Success!")
            console.log('Movie added to cart:', response);
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

    // Call the addToCart function with the movie ID and title
    addToCart(movieId, movieTitle);
});
