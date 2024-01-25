let star_form = $("#star_form");

/**
 * Handle the data returned by AddStarServlet.java
 * @param resultDataString JSON response from the servlet
 */
function handleAddStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add star response");
    console.log(resultDataJson);

    // Check if the server response contains an "errorMessage"
    if (resultDataJson.hasOwnProperty("errorMessage")) {
        // If there's an error, display it in a message div
        $("#add_star_message").text(resultDataJson["errorMessage"]);
    } else {
        // If the operation is successful, you can redirect to a success page or do something else
        console.log("Star added successfully!");
        // Redirect to a success page if needed
        // window.location.replace("success.html");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        {
            type: "POST",
            url: "api/addStar",
            // Serialize the star_form data to be sent by POST request
            data: star_form.serialize(),
            success: handleAddStarResult
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitStarForm);
