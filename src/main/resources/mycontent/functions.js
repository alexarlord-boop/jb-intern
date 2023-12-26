
function toggleVisibility(index) {
    var element = document.getElementById('commitMessage' + index);
    element.classList.toggle('hidden');
}

$('[id^="jobsModal"]').on('shown.bs.modal', function (e) {
    console.log(`Modal with ID ${this.id} is about to be shown`);
    console.log(`Btn with ID ${this.id} is clicked`);
    // Your additional logic here
    let jobsUrl = $(this).attr("jobsUrl");
    let token = $(this).attr("authToken");
    console.log(jobsUrl);
    let apiResponse = getRunJobs(jobsUrl, authToken=token);
    console.log(apiResponse);

    let modalBody = $(this).find('.modal-body');
    modalBody.html('<div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>');

    apiResponse.then(apiResponse => {
                           console.log(apiResponse);

                           // Update the modal body with the API response
                           let jobsContent = visualizeJobs(apiResponse);
                           modalBody.html(jobsContent);
                       })
                       .catch(error => {
                           // Handle errors if needed
                           console.error(error);
                       })
                       .finally(() => {
                                     // Hide loader/spinner (whether the request succeeds or fails)
                                     modalBody.find('.spinner-border').hide();
                                 });;

});


function getRunJobs(jobsUrl, authToken = "") {
    return fetch(jobsUrl, {
        method: "GET",
        headers: {
            "Accept": "application/vnd.github.v3+json",
            "Authorization": "Bearer " + authToken
        }
    }).then(function (response) {
        if (!response.ok) {
            // Handle error
            throw new Error("Failed to fetch workflow run log. HTTP status code: " + response.status);
        }
        // Successfully fetched the data
        return response.json();
    }).then(function (data) {
        return JSON.stringify(data);
    });
}

function visualizeJobs(jsonData) {
        // Parse JSON data
        const data = JSON.parse(jsonData);

        // Extract jobs array from the JSON
        const jobs = data.jobs;

        // Init succeed jobs counter
        let jobsN = jobs.length;
       var succeedCounter = 0;

       // Create a container for the job tabs and content
       const container = document.createElement('div');

       // Create the tab navigation
       const tabNav = document.createElement('ul');
       tabNav.className = 'nav nav-tabs';
       tabNav.setAttribute('id', 'jobTabs');

       // Create the tab content
       const tabContent = document.createElement('div');
       tabContent.className = 'tab-content';



       // Iterate through each job and create a tab for each
       jobs.forEach((job, index) => {
           if (job.conclusion == "success") { succeedCounter += 1; }

           // Create a unique ID for the tab and content
           const tabId = `tab-${index}`;
           const contentId = `content-${index}`;

           // Create the tab link
           const tabLink = document.createElement('a');
           tabLink.className = 'nav-link';
           tabLink.setAttribute('id', tabId);
           tabLink.setAttribute('data-bs-toggle', 'tab');
           tabLink.setAttribute('href', `#${contentId}`);
           tabLink.textContent = job.name;

           // Create the tab pane for the content
           const tabPane = document.createElement('div');
           tabPane.className = 'tab-pane fade';
           tabPane.setAttribute('id', contentId);

           // Set the content of the tab pane
           tabPane.innerHTML = `
               <div class="card border-secondary mb-3">
                   <div class="card-body">
                       <h5 class="card-title">${job.name}</h5>
                       <p class="card-text">Status: <strong>${job.status}</strong>&nbsp;&nbsp;&nbsp; Conclusion: ${getConclusionIcon(job.conclusion)}</p>
                       <p class="card-text"></p>
                       <ul class="list-group">
                           ${job.steps.map(step => `<li class="list-group-item">${getConclusionIcon(step.conclusion)} ${step.name}</li>`).join('')}
                       </ul>
                   </div>
               </div>
           `;

           // Append the tab link and content to the respective containers
           tabNav.appendChild(tabLink);
           tabContent.appendChild(tabPane);

           // Set the first tab as active
           if (index === 0) {
               tabLink.classList.add('active');
               tabPane.classList.add('show', 'active');
           }
       });

       // Create a header indicating the count of successful jobs
       const header = document.createElement('div');
       header.textContent = `${succeedCounter}/${jobsN} Successful Jobs`;
       container.appendChild(header);

       // Append the tab navigation and content to the main container
       container.appendChild(tabNav);
       container.appendChild(tabContent);


       return container;
    }

 function getConclusionIcon(conclusion) {
        return conclusion === 'success' ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>';
    }
