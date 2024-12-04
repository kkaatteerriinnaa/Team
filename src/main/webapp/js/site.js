document.addEventListener('DOMContentLoaded', function () {
    // var elems = document.querySelectorAll('.modal');

    const span = document.getElementById("currentYear");
    span.textContent = String(new Date().getFullYear());
    // db.jsp
    const createButton = document.getElementById("db-create-button");
    if (createButton) {
        createButton.addEventListener('click', createButtonClick);
    }
    const insertButton = document.getElementById("db-insert-button");
    if (insertButton) {
        insertButton.addEventListener('click', insertButtonClick);
    }
    const readButton = document.getElementById("db-read-button");
    if (readButton) {
        readButton.addEventListener('click', readButtonClick);
    }
    const cardTableDiv = document.getElementById("cardTable");
    if (cardTableDiv) {
        cardTableDiv.hidden = true;
    }
    const generateButton = document.getElementById("number-generate-button");
    if (generateButton) {
        generateButton.addEventListener('click', generateNewNumber);
    }
    const reaAllButton = document.getElementById("db-readall-button");
    if (reaAllButton) {
        reaAllButton.addEventListener('click', readAllButtonClick);
    }
});

function readAllButtonClick() {
    fetch(window.location.href + '?allow-delete=true', {
        method: "COPY"
    }).then(r => r.json())
        .then(j => {
            drawCallsTable(j.calls, true);
        });
}

function readButtonClick() {
    fetch(window.location.href + '?allow-delete=false', {
        method: "COPY"
    }).then(r => r.json())
        .then(j => {
            drawCallsTable(j.calls, false);
        });
}

function drawCallsTable(j, allowDelete = false) {
    if (Array.isArray(j)) {
        document.getElementById("cardTable").hidden = false;
        const orderTable = document.getElementById("orderTable");
        while (orderTable.firstChild) {
            orderTable.removeChild(orderTable.firstChild);
        }
        let table = document.createElement("table");
        let tablehead = `<thead>
                                    <tr>
                                   <th>ID</th>
                                   <th>Name</th>
                                   <th>Phone</th>
                                   <th>Moment</th>
                                   <th>Call Moment</th>
                                   <th>Delete</th>`;
        if (allowDelete) {
            tablehead += `<th>Restore</th>`;
        }
        tablehead += `</tr></thead>`;
        orderTable.appendChild(table);
        table.innerHTML = tablehead;
        const tableBody = document.createElement("tbody");
        while (tableBody.firstChild) {
            tableBody.removeChild(tableBody.firstChild);
        }
        let tr;
        let td;
        for (let call of j) {
            tr = document.createElement("tr");

            td = document.createElement("td");
            td.textContent = call.id;
            tr.appendChild(td);

            td = document.createElement("td");
            td.textContent = call.name;
            tr.appendChild(td);

            td = document.createElement("td");
            td.textContent = call.phone;
            tr.appendChild(td);

            td = document.createElement("td");
            td.textContent = call.moment;
            tr.appendChild(td);

            td = document.createElement("td");
            td.innerHTML = call.callMoment === null ? `<button data-id="${call.id}" class="waves-effect waves-light btn deep-purple darken-4" onclick="callClick(event)"><i class="material-icons right">call</i>call</button>` : call.callMoment;
            tr.appendChild(td);

            td = document.createElement("td");
            td.innerHTML = call.deleteMoment === null ? `<button data-id="${call.id}" class="waves-effect waves-dark btn purple lighten-4" onclick="deleteClick(event)"><i class="material-icons right">clear</i>Delete</button>` : call.deleteMoment;
            tr.appendChild(td);

            if (allowDelete) {
                td = document.createElement("td");
                td.innerHTML = call.deleteMoment !== null ? `<button data-id="${call.id}" class="waves-effect waves-dark btn red lighten-3" onclick="restoreClick(event)"><i class="material-icons right">settings_backup_restore</i>Restore</button>` : '---';
                tr.appendChild(td);
            }

            tableBody.appendChild(tr);
            table.appendChild(tableBody);
        }
    }
}

function insertButtonClick() {
    const nameInput = document.querySelector('[name="user-name"]');
    if (!nameInput) {
        throw '[name="user-name"] not found';
    }
    const phoneInput = document.querySelector('[name="user-phone"]');
    if (!phoneInput) {
        throw '[name="user-phone"] not found';
    }

    fetch(window.location.href, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: nameInput.value,
            phone: phoneInput.value
        })
    }).then(r => r.json()).then(j => {
        if (j.last_id.length !== 0) {
            const lastId = document.getElementById("lastId");
            lastId.textContent = `Last added id: ${j.last_id}`;
        }
    });
}

function createButtonClick() {
    fetch(window.location.href, {
        method: 'PUT'
    }).then(r => r.json()).then(j => {
        console.log(j);
    });
}

function validateFields(nameInput, phoneInput) {
    if (nameInput.classList.contains("invalid")) {
        nameInput.classList.remove("invalid");
    }
    if (phoneInput.classList.contains("invalid")) {
        phoneInput.classList.remove("invalid");
    }
    let isValid = true;
    const nameError = document.getElementById("nameError");
    const phoneError = document.getElementById("phoneError");
    //const namePattern = new RegExp("^[а-яА-Яa-zA-ZіІїЇ]+$");
    const phonePattern = new RegExp("^\\+38\\s?(\\(\\d{3}\\)|\\d{3})\\s?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2}$");
    if (nameInput.value.trim() === '') {
        isValid = false;
        nameInput.classList.add("invalid");
        nameError.setAttribute("data-error", "Ім'я не може бути порожнім");
        // nameError.setAttribute("data-error", "Ім'я не може бути порожнім");
    }
    // } else if (!namePattern.test(nameInput.value)) {
    //     isValid = false;
    //     nameInput.classList.add("invalid");
    //     nameError.setAttribute("data-error", "Ім'я не відповідає шаблону, тільки букви, без пробілів");
    // }
    if (phoneInput.value.trim() === '') {
        isValid = false;
        phoneInput.classList.add("invalid");
        phoneError.setAttribute("data-error", "Телефон не може бути порожнім");
    } else if (!phonePattern.test(phoneInput.value)) {
        isValid = false;
        phoneInput.classList.add("invalid");
        phoneError.setAttribute("data-error", "Телефон не відповідає шаблону: +38099xxxxxxx або +38(099)xxx-xx-xx");
    }
    return isValid;
}

function callClick(e) {
    const callId = e.target.getAttribute("data-id")
    const result = confirm(`Do you really want to call the user:${callId}?`);
    if (result) {
        // old-version
        /*fetch(window.location.href, {
            method: "LINK",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({"call-id": callId})
        }).then(r => r.json())
            .then(j => {
                if (j.status === "204") {
                    window.location.reload();
                }
            })*/
        fetch(window.location.href + `?call-id=${callId}`, {
            method: "PATCH",
        }).then(r => r.json()).then(j => {
            if (typeof j.callMoment == 'undefined') { // j - текст помилки
                alert(j);
            } else {
                // прибираємо кнопку та ставимо дату
                e.target.parentNode.innerHTML = j.callMoment;
            }
        })

    }
    //alert('CALLING id= ' + e.target.getAttribute("data-id"));
}

function generateNewNumber() {
    let number = '+380';
    for (let i = 0; i < 9; i++) {
        number += Math.floor(Math.random() * 10)
    }
    document.getElementById("user-phone").value = number;
}

function deleteClick(e) {
    const btn = e.target.closest('button');
    const callId = btn.getAttribute("data-id")
    const result = confirm(`Do you really want to delete the user order: ${callId}?`);
    if (result) {
        fetch(window.location.href + `?call-id=${callId}`, {
            method: "DELETE",
        }).then(r => {
            if (r.status === 202) { // успішне видалення
                let tr = btn   // button
                    .parentNode                 // td
                    .parentNode;                // tr
                tr.parentNode.removeChild(tr);
            } else {
                r.json().then(alert);
            }
        })

    }
}

function restoreClick(e) {
    const btn = e.target.closest('button');
    const callId = btn.getAttribute("data-id")
    const result = confirm(`Do you really want to restore the user order: ${callId}?`);
    if (result) {
        fetch(window.location.href + `?call-id=${callId}`, {
            method: "LINK",
        }).then(r => r.json()).then(j => {
            if (typeof j.deleteMoment == 'undefined') {
                alert(j);
            } else {
                const tr = btn.parentNode.parentNode;
                while (tr.firstChild) {
                    tr.removeChild(tr.firstChild);
                }
                let td = document.createElement("td");
                td.textContent = j.id;
                tr.appendChild(td);

                td = document.createElement("td");
                td.textContent = j.name;
                tr.appendChild(td);

                td = document.createElement("td");
                td.textContent = j.phone;
                tr.appendChild(td);

                td = document.createElement("td");
                td.textContent = j.moment;
                tr.appendChild(td);

                td = document.createElement("td");
                td.innerHTML = j.callMoment === null ? `<button data-id="${j.id}" class="waves-effect waves-light btn deep-purple darken-4" onclick="callClick(event)"><i class="material-icons right">call</i>call</button>` : j.callMoment;
                tr.appendChild(td);

                td = document.createElement("td");
                td.innerHTML = j.deleteMoment === null ? `<button data-id="${j.id}" class="waves-effect waves-dark btn purple lighten-4" onclick="deleteClick(event)"><i class="material-icons right">clear</i>Delete</button>` : j.deleteMoment;
                tr.appendChild(td);

                td = document.createElement("td");
                td.innerHTML = j.deleteMoment !== null ? `<button data-id="${j.id}" class="waves-effect waves-dark btn red lighten-3" onclick="restoreClick(event)"><i class="material-icons right">settings_backup_restore</i>Restore</button>` : '---';
                tr.appendChild(td);

            }
        })

    }
}
