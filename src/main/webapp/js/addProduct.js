// Get modal and buttons
  const productModal = document.getElementById('productModal');
  const openModalBtn = document.querySelector('.open-modal-button'); // Adjust this selector to your button's class or id
  const closeModalBtn = document.getElementById('closeProductModalBtn');

  // Open modal function
  openModalBtn.addEventListener('click', () => {
      productModal.classList.remove('hidden'); // Show the modal
  });

  // Close modal function
  closeModalBtn.addEventListener('click', () => {
      productModal.classList.add('hidden'); // Hide the modal
  });

  // Optional: Close modal when clicking outside the modal content
  productModal.addEventListener('click', (event) => {
      if (event.target === productModal) {
          productModal.classList.add('hidden'); // Hide the modal
      }
  });