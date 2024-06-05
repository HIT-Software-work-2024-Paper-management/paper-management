document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('paper-form');
    const paperList = document.getElementById('paper-list');

    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const title = document.getElementById('title').value;
        const author = document.getElementById('author').value;
        const date = document.getElementById('date').value;
        const journal = document.getElementById('journal').value;
        const file = document.getElementById('file').files[0];

        const listItem = document.createElement('li');
        listItem.innerHTML = `
            <span>Title: ${title}</span>,
            <span>Author: ${author}</span>,
            <span>Date: ${date}</span>,
            <span>Journal: ${journal}</span>,
            <button class="edit">Edit</button>
            <button class="delete">Delete</button>
        `;

        // Append the new paper to the list
        paperList.appendChild(listItem);

        // Clear the form after submission
        form.reset();
    });

    paperList.addEventListener('click', (event) => {
        if (event.target.classList.contains('edit')) {
            const listItem = event.target.parentElement;
            const spans = listItem.getElementsByTagName('span');

            // Populate the form with current paper details
            document.getElementById('title').value = spans[0].textContent.replace('Title: ', '');
            document.getElementById('author').value = spans[1].textContent.replace('Author: ', '');
            document.getElementById('date').value = spans[2].textContent.replace('Date: ', '');
            document.getElementById('journal').value = spans[3].textContent.replace('Journal: ', '');

            // Remove the current list item
            listItem.remove();
        }

        if (event.target.classList.contains('delete')) {
            const listItem = event.target.parentElement;
            listItem.remove();
        }
    });
});
