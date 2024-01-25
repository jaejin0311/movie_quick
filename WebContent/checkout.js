function placeOrder() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const creditCard = document.getElementById("creditCard").value;
    const expirationDate = document.getElementById("expirationDate").value;

    // Perform client-side validation (basic checks)
    if (!firstName || !lastName || !creditCard || !expirationDate) {
        document.getElementById("errorContainer").textContent = "Please fill in all fields.";
        return;
    }

    // You can add more validation for credit card and expiration date here.

    // Clear any previous error messages
    document.getElementById("errorContainer").textContent = "";

    // Create a data object to send to the server

    // Send a POST request to the server
    jQuery.ajax({
        type: 'POST',
        url: 'api/checkout',
        data: {
            firstName: firstName,
            lastName: lastName,
            creditCard: creditCard,
            expirationDate: expirationDate
        },
        dataType: 'json',
        success: function (response) {
            console.log(response);
            console.log("message: " + response.message);
            if (response.message == "success") {
                // Display the success message
                document.getElementById("errorContainer").textContent = response.text;
                // Redirect to the confirmation page
                window.location.href = 'confirmation.html';
            } else {
                // Handle the case where the payment failed
                document.getElementById("errorContainer").textContent = 'Payment Failed';
            }
        },
        error: function (error) {
            // Handle errors, e.g., display an error message
            document.getElementById("errorContainer").textContent = error.message;
        }
    });
}

