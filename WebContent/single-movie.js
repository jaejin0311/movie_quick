/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    console.log("getpara")
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {


    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
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
        console.log("aaa");
        let starIds = resultData[i]["star_ids"].split(',');
        let starLinks = "";

        for (let j = 0; j < starNames.length; j++) {
            starLinks += "<a href='single-star.html?id=" + encodeURIComponent(starIds[j].trim()) + "'>" + starNames[j].trim() + "</a>";
            if (j < starNames.length - 1) {
                starLinks += ", ";
            }
        }
        rowHTML += "<th>" + starLinks + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th><button class='add-to-cart' data-movie-id='" + resultData[i]['movie_id'] + "' data-movie-title='" + resultData[i]['movie_title'] + "'>Add</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieID = getParameterByName('id');
console.log("starId:", movieID);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieID, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
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