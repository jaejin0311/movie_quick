$(document).ready(function () {
    // Function to load cart contents
    function loadCart() {
        $.ajax({
            type: 'GET',
            url: 'api/cart',
            dataType: 'json',
            success: function (response) {
                if (response && response.cartItems) {
                    const cartItems = response.cartItems;
                    const tableBody = document.getElementById('movie_table_body');
                    tableBody.innerHTML = ''; // Clear previous contents

                    let totalAmount = 0;

                    if (Array.isArray(cartItems)) {
                        for (let i = 0; i < cartItems.length; i++) {
                            const cartItem = cartItems[i];
                            const row = tableBody.insertRow(-1);

                            // Create table cells and populate them
                            const cell1 = row.insertCell(0);
                            cell1.textContent = cartItem.movieTitle;

                            const cell2 = row.insertCell(1);
                            cell2.appendChild(createQuantityControls(cartItem.movieId, cartItem.quantity));

                            const cell3 = row.insertCell(2);
                            const deleteButton = document.createElement('button');
                            deleteButton.textContent = 'Delete';
                            deleteButton.addEventListener('click', function () {
                                deleteCartItem(cartItem.movieId);
                            });
                            cell3.appendChild(deleteButton);

                            const cell4 = row.insertCell(3);
                            cell4.textContent = '$6'; // Replace with the actual price

                            const cell5 = row.insertCell(4);
                            cell5.textContent = '$' + (cartItem.quantity * 6);
                            totalAmount += cartItem.quantity * 6;
                        }
                        const checkoutRow = tableBody.insertRow(-1);
                        const checkoutCell = checkoutRow.insertCell(0);
                        const checkoutButton = document.createElement('button');
                        checkoutButton.textContent = 'Proceed to Checkout';
                        checkoutButton.addEventListener('click', function() {
                            window.location.href = 'checkout.html';
                        });
                        checkoutCell.appendChild(checkoutButton);
                        const empty1 = checkoutRow.insertCell(1);
                        const empty2 = checkoutRow.insertCell(2);
                        const empty3 = checkoutRow.insertCell(3);
                        const totalCell = checkoutRow.insertCell(4);
                        totalCell.innerHTML = '<b>$' + totalAmount + '</b>';
                    }
                }
            },
            error: function (error) {
                console.error('Error loading cart:', error);
            }
        });
    }

    // Function to delete an item from the cart
    function deleteCartItem(movieId) {
        $.ajax({
            type: 'POST',
            url: 'api/cart',
            data: { movieId: movieId, method: "delete"},
            dataType: 'json',
            success: function (response) {
                if (response && response.message) {
                    if (response) {
                        // Reload the cart contents after deleting
                        loadCart();
                    } else {
                        console.error('Error removing movie ID from the cart:', response.message);
                    }
                }
            },
            error: function (error) {
                console.error('Error deleting movie ID from the cart:', error);
            }
        });
    }

    function createQuantityControls(movieId, quantity) {
        const controlDiv = document.createElement('div');

        // Create decrease button
        const decreaseButton = document.createElement('button');
        decreaseButton.textContent = '-';
        decreaseButton.addEventListener('click', function () {
            if (quantity > 1) {
                console.log("id: " + movieId + "quantity: " + quantity);
                updateCartItemQuantity(movieId, quantity, "-");
            }
        });
        controlDiv.appendChild(decreaseButton);

        // Display the quantity
        const quantityDisplay = document.createElement('span');
        quantityDisplay.textContent = quantity;
        controlDiv.appendChild(quantityDisplay);

        // Create increase button
        const increaseButton = document.createElement('button');
        increaseButton.textContent = '+';
        increaseButton.addEventListener('click', function () {
            updateCartItemQuantity(movieId, quantity, "+");
        });
        controlDiv.appendChild(increaseButton);

        return controlDiv;
    }

    // Function to update the quantity of an item in the cart
    function updateCartItemQuantity(movieId, quantity, method) {
        $.ajax({
            type: 'POST',
            url: 'api/cart',
            data: { method: method, movieId: movieId, quantity: quantity },
            dataType: 'json',
            success: function (response) {
                if (response) {
                    // Reload the cart contents after updating the quantity
                    loadCart();
                } else {
                    console.error('Error updating item quantity:', JSON.stringify(response));
                }
            },
            error: function (xhr, status, error) {
                console.error('Error updating item quantity. Status:', status, 'Error:', error, 'Response:', xhr.responseText);
            }
        });
    }
    // Load the initial cart contents when the page loads
    loadCart();
});
