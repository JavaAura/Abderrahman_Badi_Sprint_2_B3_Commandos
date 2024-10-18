let breadCrumbs = document.getElementById("bread_crumbs");

let host = window.location.hostname;
let port = window.location.port;
let pathname = window.location.pathname;

// Remove the first `/` in the pathname
pathname = pathname.substring(pathname.indexOf("/") + 1);

let pathnames = pathname.split("/");

let url = `${window.location.protocol}//${host}:${port}`;

breadCrumbs.innerHTML = `
<div class="flex items-center flex-wrap">
    <ul class="flex items-center">

        ${pathnames
          .map((path, index) => {
            url = url + '/' + path;
            if (index === 0) {
              return ` 
                        <li class="inline-flex items-center">
                            <a href="${url}" class="hover:text-blue-500">
                                <svg class="w-5 h-auto fill-current " xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                                    fill="#000000">
                                    <path d="M0 0h24v24H0V0z" fill="none" />
                                    <path
                                        d="M10 19v-5h4v5c0 .55.45 1 1 1h3c.55 0 1-.45 1-1v-7h1.7c.46 0 .68-.57.33-.87L12.67 3.6c-.38-.34-.96-.34-1.34 0l-8.36 7.53c-.34.3-.13.87.33.87H5v7c0 .55.45 1 1 1h3c.55 0 1-.45 1-1z" />
                                </svg>
                            </a>
                            <span class="mx-4 h-auto text-gray-400 font-medium">/</span>
                        </li>`;
            } else if(index === pathnames.length - 1){
              return `
                        <li class="inline-flex items-center">
                            <a href="${url}" class="hover:text-blue-500">${path.charAt(0).toUpperCase()}${path.slice(1)}</a>
                        </li>
                    `;
            }else {
                return `
                        <li class="inline-flex items-center">
                            <a href="${url}" class="hover:text-blue-500">${path.charAt(0).toUpperCase()}${path.slice(1)}</a>
                            <span class="mx-4 h-auto text-gray-400 font-medium">/</span>
                        </li>
                    `;
            }
          })
          .join("")}
    </ul>
</div>
`;
