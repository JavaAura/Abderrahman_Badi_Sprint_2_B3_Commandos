function openModal() {
  document.getElementById("order_modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("order_modal").classList.add("hidden");
}

function closeMessagePopup(event) {
  let message_container = event.currentTarget;
  message_container.classList.add("hidden");
}
