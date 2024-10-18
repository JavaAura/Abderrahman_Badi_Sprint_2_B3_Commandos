async function openModal(event, action) {
  document.getElementById("action").value = action;
  if (action === "update") {
    let row = event.currentTarget.closest("tr");
    let userIdTd = row.querySelector("td:first-child");
    let userId = userIdTd.textContent.trim();

    let host = window.location.hostname;
    let port = window.location.port;
    let urlPathname = "Commandos/dashboard/users";

    let url = `${window.location.protocol}//${host}:${port}/${urlPathname}?action=get&user_id=${userId}`;

    const data = await getUser(url);

    console.log(data);
    
  }

  document.getElementById("user_modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("user_modal").classList.add("hidden");
}

function closeMessagePopup(event) {
  let message_container = event.currentTarget;
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


async function getUser(url) {
  const response = await fetch(url, {
    method: "POST",
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();

  return data;
}