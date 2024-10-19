async function openModal(event, action) {
  document.getElementById("action").value = action;
  if (action === "update") {
    let row = event.currentTarget.closest("tr");
    let productIdTd = row.querySelector("td:first-child");
    let productId = productIdTd.textContent.trim();

    let host = window.location.hostname;
    let port = window.location.port;
    let urlPathname = "Commandos/dashboard/products";

    let url = `${window.location.protocol}//${host}:${port}/${urlPathname}?action=get&product_id=${productId}`;

    const data = await getProduct(url);

    console.log(data);
    
  }

  document.getElementById("product_modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("product_modal").classList.add("hidden");
}

function closeMessagePopup(event) {
  let message_container = event.currentTarget;
  message_container.classList.add("hidden");
}

 

async function getProduct(url) {
  const response = await fetch(url, {
    method: "GET",
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();

  return data;
}