/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
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

        for (let a = 0; a < genres.length && a < 3; a++) {
            three_genres += genres[a];
            if (a < genres.length - 1 && a < 2) {
                three_genres += ", ";
            }
        }

        // rowHTML += "<th>" + resultData[i]["genres"] + "</th>";

        rowHTML += "<th>" + three_genres + "</th>";

        let starNames = resultData[i]["star_names"].split(','); // Split star names
        console.log("aaa");
        let starIds = resultData[i]["star_ids"].split(',');
        let starLinks = "";

        for (let j = 0; starNames.length && j < 3; j++) {
            starLinks += "<a href='single-star.html?id=" + encodeURIComponent(starIds[j].trim()) + "'>" + starNames[j].trim() + "</a>";
            if (j < starNames.length - 1 && j < 2) {
                starLinks += ", ";
            }
        }
        rowHTML += "<th>" + starLinks + "</th>";

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});