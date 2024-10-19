function openPopup() {
  document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

function toggleMenu() {
  document.getElementById("menu").classList.toggle("hidden");
  document.getElementById("arrow_icon").classList.toggle("rotate-180")
}

function closeMessagePopup(event) {
  let message_container = event.currentTarget;
  message_container.classList.add("hidden");
}