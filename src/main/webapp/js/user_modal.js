async function openModal(event, action) {
  document.getElementById("action").value = action;
  if (action === "update") {
    document.getElementById("role_field").innerHTML = "";

    let row = event.currentTarget.closest("tr");
    let userIdTd = row.querySelector("td:first-child");
    let userId = userIdTd.textContent.trim();

    let host = window.location.hostname;
    let port = window.location.port;
    let urlPathname = "Commandos/dashboard/users";

    let url = `${window.location.protocol}//${host}:${port}/${urlPathname}?action=get&user_id=${userId}`;

    const data = await getUser(url);

    console.log(data);

    if (data) {
      document.getElementById("firstName").value = data.firstName;
      document.getElementById("lastName").value = data.lastName;
      document.getElementById("user_email").value = data.email;
      document.getElementById("userId").value = data.id;

      if (data.role === "CLIENT") {
        await showFields(null);

        document.getElementById("addressDelivery").value = data.addressDelivery;
        document.getElementById("paymentMethod").value = data.paymentMethod;
      } else {
        document.getElementById("role_field").innerHTML = "";
      }
    }
  } else if (action === "add") {
    document.getElementById("firstName").value = "";
    document.getElementById("lastName").value = "";
    document.getElementById("user_email").value = "";
    document.getElementById("fields").innerHTML = "";
    await populateRoleField();
  }
  document.getElementById("user_modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("user_modal").classList.add("hidden");
}

async function showFields(event) {
  if (event === null) {
    document.getElementById("fields").innerHTML = `
          <div class="flex flex-col items-start gap-1 w-full">
            <label for="email" class="mb-2 text-sm font-medium">Delivery address</label>
            <input type="text" name="addressDelivery" id="addressDelivery"
              class="bg-gray-50 border border-blue-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg"
              required />
          </div>
          <div class="flex flex-col items-start gap-1 w-full">
            <label for="email" class="mb-2 text-sm font-medium">Payment Method</label>
            <input type="text" name="paymentMethod" id="paymentMethod"
              class="bg-gray-50 border border-blue-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg"
              required />
          </div>
    `;
    return;
  }

  let selectElement = event.currentTarget;

  switch (selectElement.value) {
    case "ADMIN":
      document.getElementById("fields").innerHTML = "";
      break;
    case "CLIENT":
      document.getElementById("fields").innerHTML = `
          <div class="flex flex-col items-start gap-1 w-full">
            <label for="email" class="mb-2 text-sm font-medium">Delivery address</label>
            <input type="text" name="addressDelivery" id="addressDelivery"
              class="bg-gray-50 border border-blue-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg"
              required />
          </div>
          <div class="flex flex-col items-start gap-1 w-full">
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

async function populateRoleField() {
  document.getElementById("role_field").innerHTML = `
  <label for="role" class="mb-2 text-sm font-medium">Role</label>
  <select onchange="showFields(event)" name="role" id="role"
    class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 shadow-lg">
    <option value="" selected disabled hidden>Select role...</option>
    <option value="CLIENT">Client</option>
    <option value="ADMIN">Admin</option>
  </select>
  `;
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
