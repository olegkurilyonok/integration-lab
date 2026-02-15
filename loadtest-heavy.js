import http from "k6/http"; // HTTP-клиент для запросов
import { check, sleep } from "k6"; // check: проверки, sleep: пауза между итерациями

export const options = {
  scenarios: {
    spike_write: {
      executor: "constant-vus", // запуск с фиксированным числом виртуальных пользователей
      vus: 200, // число виртуальных пользователей в пике
      duration: "2m", // длительность пика
    },
    steady_write: {
      executor: "ramping-vus", // плавное изменение числа виртуальных пользователей
      startVUs: 0, // стартовое число VU до начала наращивания
      stages: [
        { duration: "30s", target: 50 }, // рост до 50 VU
        { duration: "2m", target: 150 }, // удержание на 150 VU
        { duration: "30s", target: 0 }, // спад до 0 VU
      ],
      startTime: "0s", // когда сценарий стартует (относительно начала теста)
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<800"], // 95% запросов должны быть быстрее 800 мс
    http_req_failed: ["rate<0.02"], // доля ошибок должна быть меньше 2%
  },
};

let localCounter = 0;

export default function () {
  const url = "http://localhost:8080/messages"; // целевой эндпоинт
  const payload = JSON.stringify({
    message: `heavy-load-${Date.now()}-${++localCounter}`
  }); // тело запроса
  const params = { headers: { "Content-Type": "application/json" } }; // HTTP-заголовки

  const res = http.post(url, payload, params); // отправка POST-запроса
  check(res, { "status is 2xx": (r) => r.status >= 200 && r.status < 300 }); // проверка успешного ответа
  sleep(0.05); // короткая пауза между итерациями
}


