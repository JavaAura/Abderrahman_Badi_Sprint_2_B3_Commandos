async function handleChange(event){

    let orderStatus = event.currentTarget.value;

    let row = event.currentTarget.closest("tr");
    let orderIdTd = row.querySelector("td:first-child");
    let orderId = orderIdTd.textContent.trim();

    let host = window.location.hostname;
    let port = window.location.port;
    let urlPathname = "Commandos/dashboard/order";

    let url = `${window.location.protocol}//${host}:${port}/${urlPathname}?action=update_status&order_id=${orderId}&status=${orderStatus}`;
    
    const data = await updateStatus(url);

    if(data.message){
      showMessage(data.message)
    }else if(data.error){
      showErrorMessage(data.error)
    }
}

async function updateStatus(url){
    const response = await fetch(url, {
        method: "POST",
      });
    
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    
      const data = await response.json();
    
      return data;
}

function showMessage(message){
  let messageContainer = document.getElementById("message");
  messageContainer.classList.remove('hidden');
  messageContainer.innerHTML = `
    <p> ${message} </p>
  `;

  setTimeout(hideMessage, 3 * 1000);
}

function hideMessage(){
  let messageContainer = document.getElementById("message");
  if(!messageContainer.classList.contains('hidden')){
    messageContainer.classList.add('hidden');
  }
  messageContainer.innerHTML = '';
}

function showErrorMessage(message){
  let messageContainer = document.getElementById("error_message");
  messageContainer.classList.remove('hidden');
  messageContainer.innerHTML = `
    <p> ${message} </p>
  `;

  setTimeout(hideErrorMessage, 3 * 1000);
}

function hideErrorMessage(){
  let messageContainer = document.getElementById("error_message");
  if(!messageContainer.classList.contains('hidden')){
    messageContainer.classList.add('hidden');
  }
  messageContainer.innerHTML = '';
}