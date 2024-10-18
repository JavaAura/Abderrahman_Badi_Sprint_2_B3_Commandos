function openModal(action) {
  document.getElementById("action").value = action;
  if (action === "update") {
  }

  document.getElementById("user_modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("user_modal").classList.add("hidden");
}

function closeMessagePopup(event) {
  let message_container = event.currentTarget;
  console.log(message_container);
  
  message_container.classList.add("hidden");
}

function showFields(event) {
  let selectElement = event.currentTarget;
  switch (selectElement.value) {
    case "ADMIN":
      document.getElementById("fields").innerHTML = "";
      break;
    case "CLIENT":
      document.getElementById("fields").innerHTML = `
        <div class="flex flex-col items-start">
                  <label for="email" class="mb-2 text-sm font-medium">Delivery address</label>
                  <input type="text" name="addressDelivery" id="addressDelivery"
                    class="bg-gray-50 border border-blue-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg"
                    required />
                </div>
                <div class="flex flex-col items-start">
                  <label for="email" class="mb-2 text-sm font-medium">Payment Method</label>
                  <input type="text" name="paymentMethod" id="paymentMethod"
                    class="bg-gray-50 border border-blue-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg"
                    required />
        </div>
        `;
      break;
    default:
      document.getElementById("fields").innerHTML = "";
      break;
  }
}
