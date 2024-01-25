let movie_form = $("#movie_form");

/**
 * Handle the data returned by AddStarServlet.java
 * @param resultDataString JSON response from the servlet
 */
function handleAddMovieResult(resultDataString) {
    console.log("results?")
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson);

    // Check if the server response contains an "errorMessage"
    if (resultDataJson.hasOwnProperty("errorMessage")) {
        // If there's an error, display it in a message div
        console.log("failed")
        $("#add_movie_message").text(resultDataJson["errorMessage"]);
    } else {
        // If the operation is successful, you can redirect to a success page or do something else
        console.log("Movie added successfully!");
        // Redirect to a success page if needed
        // window.location.replace("success.html");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        {
            type: "POST",
            url: "../api/addMovie",
            // Serialize the star_form data to be sent by POST request
            data: movie_form.serialize(),
            success: handleAddMovieResult
        }
    );
}

// Bind the submit action of the form to a handler function
movie_form.submit(submitMovieForm);
