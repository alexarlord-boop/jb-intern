
function toggleVisibility(index) {
    var element = document.getElementById('commitMessage' + index);
    element.classList.toggle('hidden');
}

$('[id^="jobsModal"]').on('shown.bs.modal', function (e) {
    console.log(`Modal with ID ${this.id} is about to be shown`);
    // Your additional logic here
});