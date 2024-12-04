<%@ page contentType="text/html;charset=UTF-8" %>
<h1>WebSocket</h1>
<div class="row">
    <div class="col s5">
        <strong id="chat-user">Connecting...</strong>
        <input type="text" id="user-message" value="Hello">
        <button class="btn deep-purple darken-4" onclick="sendClick()">Send</button>
        <ul class="collection" id="chat-container">
        </ul>
    </div>
    <div class="col s7">
        <p>
            WebSocket - протокол (приблизно рівня HTTP), відомий схемами
            <b>ws://</b> та <b>wss://</b>. Для прикладного програмування
            головная відмінність - у дуплексному режимі передавання даних
            за якого як і клієнт може ініціювати передачу, так і сервер.
            Реалізується це механзізмом подій та їх утворенням як на клієнті,
            так і на сервері за однаковими принципами.
        </p>
        <p>
            Оскільки це інший протокол, він реалізується окремим сервером,
            який існує або окремо, або паралельно з основним (HTTP). Веб-
            сервери (на кшталт Tomcat) здатні обслуговувати обидва типи
            протоколів.
        </p>
        <p>
            Для роботи з вебсокетом додаємо залежність (javax.websocket-api),
            оголошуємо клас серверу (див. WebsocketServer). Оскільки сервер
            забезпечує дуплексний зв'язок, він повинен зберігати масив
            (колекцію) активних підключень (сесій) та реалізовувати засоби
            повідомлення усіх підключених клієнтів при надходженні даних
            від одного з них.
        </p>
        <p>
            Вебсокет, як об'єкт (Java) утворюється для одного HTTP-об'єкта,
            у той же час у межах одного HTTP відбувається багаторазовий
            обмін даними. Іншими словами, багато веб-сокет пакетів "прив'язані"
            до одного HTTP. Для збереження відомостей про HTTP (у якому,
            зокрема, є відомості про токен авторизації) необхідно впроваджувати
            конфігуратор.
        </p>
    </div>
</div>


<script>
    document.addEventListener('DOMContentLoaded', () => {
        const token = window.localStorage.getItem('token');
        if (token) {
            initWebSocket();
        } else {
            document.getElementById("chat-user").innerText = `Авторизуйтесь для того щоб користуватись чатом`;
        }
    })

    function addMessage(txt, error = false) {
        const li = document.createElement("li");
        li.className = 'collection-item';
        if (error) {
            li.classList.add('red-text')
        }
        li.appendChild(document.createTextNode(txt));
        document.getElementById('chat-container').appendChild(li);
    }

    function sendClick() {
        window.websocket.send(
            JSON.stringify({
                command: 'chat',
                data: document.getElementById('user-message').value
            })
        );
    }

    function initWebSocket() {
        const host = window.location.host + getAppContext(); // localhost
        window.websocket = new WebSocket(`ws://${host}/chat`);
        window.websocket.onopen = onWsOpen;
        window.websocket.onclose = onWsClose;
        window.websocket.onmessage = onWsMessage;
        window.websocket.onerror = onWsError;
    }

    function onWsOpen(e) {
        // console.log("onWsOpen: ", e)
        const token = window.localStorage.getItem('token');
        window.websocket.send(JSON.stringify({
            command: 'auth',
            data: token
        }))

    }

    function onWsClose(e) {
        // console.log("onWsClose: ", e)
        addMessage('Chat deactivated');
    }

    function onWsMessage(e) {
        // console.log("onWsMessage: ", e)
        const message = JSON.parse(e.data);
        switch (message.status) {
            case 201:
                addMessage(message.data);
                break;
            case 202: // токен прийнято - вилучаємо з нього SUB та виводимо фрагмент
                const item = JSON.parse(atob(window.localStorage.getItem('token')));
                const json = message.data;
                const dataObject = JSON.parse(json);
                document.getElementById("chat-user").innerText = dataObject.data;
                addMessage('Chat activated');
                break;
            case 401:
            case 403:
                document.getElementById("chat-user").innerText = "Повторіть авторизацію";
                break;
            default:
                console.log(message);

        }
    }

    function onWsError(e) {
        console.log("onWsError: ", e)
        addMessage(e.data, true);
    }

    function getAppContext() {
        return '/' + window.location.pathname.split('/')[1];
    }
</script>