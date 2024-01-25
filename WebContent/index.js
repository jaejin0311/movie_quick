$(document).ready(function () {
    // Add event listener to the search form
    $("#searchForm").submit(function (event) {
        event.preventDefault();

        // Redirect to the list page with search parameters as query parameters
        const queryParams = {
            title: $("#title").val(),
            year: $("#year").val(),
            director: $("#director").val(),
            starName: $("#starName").val(),
        };

        const queryString = $.param(queryParams);
        const listPageURL = "search.html?" + queryString;

        // const listPageURL = "list.html";
        console.log(listPageURL);
        window.location.href = listPageURL;
    });

    $("#fullTextSearch").click(function (event) {
        event.preventDefault();

        // Redirect to the list page with search parameters as query parameters
        const queryParams = {
            title: $("#autocomplete").val(),
        };

        const queryString = $.param(queryParams);
        const listPageURL = "search.html?" + queryString;

        // const listPageURL = "list.html";
        console.log(listPageURL);
        window.location.href = listPageURL;
    });

    $(".genre-button").click(function (event) {
        // Get the genre from the data attribute of the clicked button
        const genre = $(this).data("genre");

        // Construct the URL with the genre parameter
        const listPageURL = "list.html?" + "genre=" + genre;

        // Redirect to the list page with the selected genre
        window.location.href = listPageURL;
    });

    $(".prefix-button").click(function (event) {
        // Get the genre from the data attribute of the clicked button
        const prefix = $(this).data("prefix");

        // Construct the URL with the genre parameter
        const listPageURL = "list.html?" + "prefix=" + prefix;

        // Redirect to the list page with the selected genre
        window.location.href = listPageURL;
    });
});

// Auto Search & Full text Search

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    if (query.length < 3) {
        console.log("Query length is less than 3 characters. Skipping Autocomplete search.");
        return;
    }

    const cachedData = localStorage.getItem(query);
    if (cachedData) {
        console.log("Using cached results");
        handleLookupAjaxSuccess(cachedData, query, doneCallback);
    } else {
        console.log("Sending AJAX request to backend Java Servlet");
        // TODO: if you want to check past query results first, you can do it here
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "auto-complete?query=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
                localStorage.setItem(query, data);
            },
            "error": function (errorData) {
                console.log("lookup ajax error")
                console.log(errorData.responseText)
            }
        })
    }
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData);

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
    const listPageURL = "single-movie.html?id=" + suggestion["data"]["movieID"];
    window.location.href = listPageURL;

}


$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here

    const listPageURL = "search.html?title=" + query;
    window.location.href = listPageURL;
}

$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})
