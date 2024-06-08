//document.addEventListener('DOMContentLoaded', () => {
//    const form = document.getElementById('paper-form');
//    const paperList = document.getElementById('paper-list');
//    const viewPapersButton = document.getElementById('view-papers-button');
//
//    // Fetch and display all papers when "View Uploaded Papers" button is clicked
//    viewPapersButton.addEventListener('click', () => {
//        paperList.innerHTML = ''; // Clear the current list
//        fetch('/api/papers')
//            .then(response => response.json())
//            .then(data => {
//                data.forEach(paper => {
//                    const listItem = createPaperListItem(paper);
//                    paperList.appendChild(listItem);
//                });
//            })
//            .catch(error => console.error('Error fetching papers:', error));
//    });
//
//    form.addEventListener('submit', (event) => {
//        event.preventDefault();
//
//        const formData = new FormData(form);
//
//        fetch('/api/papers/upload', {
//            method: 'POST',
//            body: formData
//        })
//        .then(response => {
//            if (!response.ok) {
//                throw new Error('Network response was not ok!!!');
//            }
//            return response.json();
//        })
//        .then(data => {
//            const listItem = createPaperListItem(data);
//            paperList.appendChild(listItem);
//            form.reset();
//            alert('Paper uploaded successfully!');
//        })
//        .catch(error => console.error('Error uploading paper:', error));
//    });
//
//    paperList.addEventListener('click', (event) => {
//        const listItem = event.target.parentElement;
//        const paperId = listItem.getAttribute('data-id');
//
//        if (event.target.classList.contains('edit')) {
//            const spans = listItem.getElementsByTagName('span');
//            document.getElementById('title').value = spans[0].textContent.replace('Title: ', '');
//            document.getElementById('authors').value = spans[1].textContent.replace('Authors: ', '');
//            document.getElementById('date').value = spans[2].textContent.replace('Date: ', '');
//            document.getElementById('journal').value = spans[3].textContent.replace('Journal: ', '');
//            document.getElementById('categoryId').value = spans[4].textContent.replace('Category: ', '');
//            document.getElementById('type').value = spans[5].textContent.replace('Type: ', '');
//            document.getElementById('impactFactor').value = spans[6].textContent.replace('Impact Factor: ', '');
//            document.getElementById('authorRank').value = spans[7].textContent.replace('Author Rank: ', '');
//
//            form.addEventListener('submit', function updatePaper(event) {
//                event.preventDefault();
//
//                const updatedFormData = new FormData(form);
//                const updatedPaper = {
//                    title: updatedFormData.get('title'),
//                    authors: updatedFormData.get('authors').split(';'),
//                    date: updatedFormData.get('date'),
//                    journal: updatedFormData.get('journal'),
//                    fileUrl: listItem.querySelector('.download').getAttribute('data-file-url'),
//                    categoryId: updatedFormData.get('categoryId'),
//                    type: updatedFormData.get('type'),
//                    impactFactor: updatedFormData.get('impactFactor'),
//                    authorRank: updatedFormData.get('authorRank')
//                };
//
//                fetch(`/api/papers/${paperId}`, {
//                    method: 'PUT',
//                    body: JSON.stringify(updatedPaper),
//                    headers: {
//                        'Content-Type': 'application/json'
//                    }
//                })
//                .then(response => response.json())
//                .then(data => {
//                    spans[0].textContent = `Title: ${data.title}`;
//                    spans[1].textContent = `Authors: ${data.authors.join('; ')}`;
//                    spans[2].textContent = `Date: ${data.date}`;
//                    spans[3].textContent = `Journal: ${data.journal}`;
//                    spans[4].textContent = `Category: ${data.category.name}`;
//                    spans[5].textContent = `Type: ${data.type}`;
//                    spans[6].textContent = `Impact Factor: ${data.impactFactor}`;
//                    spans[7].textContent = `Author Rank: ${data.authorRank}`;
//
//                    form.removeEventListener('submit', updatePaper);
//                    form.reset();
//                })
//                .catch(error => console.error('Error updating paper:', error));
//            }, { once: true });
//        }
//
//        if (event.target.classList.contains('delete')) {
//            fetch(`/api/papers/${paperId}`, {
//                method: 'DELETE'
//            })
//            .then(() => {
//                listItem.remove();
//            })
//            .catch(error => console.error('Error deleting paper:', error));
//        }
//
//        if (event.target.classList.contains('download')) {
//            const fileUrl = listItem.querySelector('.download').getAttribute('data-file-url');
//            window.open(fileUrl, '_blank');
//        }
//    });
//
//    function createPaperListItem(paper) {
//        const listItem = document.createElement('li');
//        listItem.setAttribute('data-id', paper.id);
//        listItem.innerHTML = `
//            <span>Title: ${paper.title}</span>,
//            <span>Authors: ${paper.authors.join('; ')}</span>,
//            <span>Date: ${paper.date}</span>,
//            <span>Journal: ${paper.journal}</span>,
//            <span>Category: ${paper.category.name}</span>,
//            <span>Type: ${paper.type}</span>,
//            <span>Impact Factor: ${paper.impactFactor}</span>,
//            <span>Author Rank: ${paper.authorRank}</span>,
//            <button class="edit">Edit</button>
//            <button class="delete">Delete</button>
//            <button class="download" data-file-url="${paper.fileUrl}">Download</button>
//        `;
//        return listItem;
//    }
//});
