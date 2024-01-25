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
                    let salesId = 1234
                    if (Array.isArray(cartItems)) {
                        for (let i = 0; i < cartItems.length; i++) {
                            // Instruction was unclear about salesID, should ask them later

                            const cartItem = cartItems[i];
                            const row = tableBody.insertRow(-1);
                            const sale = row.insertCell(0);
                            sale.textContent = String(salesId);
                            ++salesId;
                            // Create table cells and populate them
                            const cell1 = row.insertCell(-1); // Insert at the end of the row
                            cell1.textContent = cartItem.movieTitle;

                            const cell2 = row.insertCell(-1); // Insert at the end of the row
                            cell2.textContent = cartItem.quantity;

                            const cell3 = row.insertCell(-1); // Insert at the end of the row
                            cell3.textContent = '$' + (cartItem.quantity * 6);
                            totalAmount += cartItem.quantity * 6;
                        }
                        const checkoutRow = tableBody.insertRow(-1);
                        const empty1 = checkoutRow.insertCell(-1);
                        const empty2 = checkoutRow.insertCell(-1);
                        const empty3 = checkoutRow.insertCell(-1);
                        const totalCell = checkoutRow.insertCell(-1);
                        totalCell.innerHTML = '<b>$' + totalAmount + '</b>';
                    }
                }
            },
            error: function (error) {
                console.error('Error loading cart:', error);
            }
        });
    }
    loadCart();
});
